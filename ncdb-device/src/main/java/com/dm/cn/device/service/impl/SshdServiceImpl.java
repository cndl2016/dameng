package com.dm.cn.device.service.impl;

import cn.hutool.json.JSONUtil;
import com.dm.cn.common.config.InfoConfig;
import com.dm.cn.common.config.NcdbApiConfig;
import com.dm.cn.common.core.constant.*;
import com.dm.cn.common.core.exception.ServiceException;
import com.dm.cn.common.core.utils.SpringUtils;
import com.dm.cn.common.core.utils.StringUtils;
import com.dm.cn.common.enums.ArchTypeEnum;
import com.dm.cn.common.enums.EncodeModeEnum;
import com.dm.cn.common.utils.CmdUtil;
import com.dm.cn.common.utils.I18nMessageUtil;
import com.dm.cn.common.utils.ThreadPoolManager;
import com.dm.cn.common.utils.io.IoUtils;
import com.dm.cn.common.utils.server.DiskIo;
import com.dm.cn.common.utils.server.Server;
import com.dm.cn.common.utils.server.SysFileServer;
import com.dm.cn.common.utils.ssh.pool.sshj.SshBuilder;
import com.dm.cn.device.entity.server.Device;
import com.dm.cn.device.service.DeviceService;
import com.dm.cn.device.service.SshdService;
import com.dm.cn.system.config.security.service.CacheService;
import com.dm.cn.system.constant.MessageTipConstant;
import com.dm.framework.common.exception.AppException;
import com.dm.framework.common.util.BeanUtil;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.ServerException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ssh服务实现类
 *
 * @author DAMENG
 */
@Configuration
public class SshdServiceImpl implements SshdService {

    private static final Logger log = LoggerFactory.getLogger(SshdServiceImpl.class);

    @Resource
    private NcdbApiConfig config;

    @Resource
    private InfoConfig infoConfig;

    @Resource
    private CacheService cacheService;

    /**
     * 获取ssh客户端
     *
     * @param deviceIp 设备ip
     * @return {@link SshBuilder}
     */
    private SshBuilder sshPoolBuilder(String deviceIp) {
        // 从缓存获取设备信息
        Device device = cacheService.getCacheObject(String.format(Constants.CACHE_DEVICE_INFO, deviceIp));
        if (ObjectUtils.isEmpty(device)) {
            // 查询设备
            device = SpringUtils.getBean(DeviceService.class).lambdaQuery().select(Device::getPort, Device::getDeviceIp, Device::getDeviceSshPwd, Device::getDeviceSshUsr, Device::getScriptVersion, Device::getArchType).eq(Device::getDeviceIp, deviceIp).one();
            if (ObjectUtils.isEmpty(device)) {
                throw new AppException("获取连接失败，请确认是否维护该设备：" + deviceIp);
            }
            // 缓存设备信息
            SpringUtils.getBean(CacheService.class).setCacheObject(String.format(Constants.CACHE_DEVICE_INFO, deviceIp), device, NumberConstants.NUMBER_TWO_INTEGER, TimeUnit.DAYS);
        }
        return new SshBuilder(device.getDeviceIp(), device.getDeviceSshUsr(), device.getPort(), CmdUtil.handleEncodePass(device.getDeviceSshPwd(), EncodeModeEnum.DECODE.name()));
    }

    /**
     * 等待，防止执行太快报错
     *
     * @throws InterruptedException
     */
    public void waitMoment() throws InterruptedException {
        Thread.sleep(800);
    }

    /**
     * 修改配置文件内容
     *
     * @param host     设备ip
     * @param filePath 配置文件地址
     * @param content  修改内容
     */
    @Override
    public void writeOrReplaceFile(String host, String filePath, Map<String, String> content) {
        SshBuilder sshBuilder = sshPoolBuilder(host);
        List<String> cmd = new LinkedList<>();
        try {
            // 遍历修改参数内容
            for (Map.Entry<String, String> entry : content.entrySet()) {
                // 若map中参数值为空，则直接echo写入文件
                if (StringUtils.isBlank(entry.getValue())) {
                    if (!StringUtils.isBlank(entry.getKey())) {
                        cmd.add(String.format(CommandConstants.ECHO, entry.getKey(), filePath));
                    }
                    continue;
                }
                // 若参数为save，需要删除所有save配置并写入新的save参数
                if (Constants.SAVE.equals(entry.getKey())) {
                    String[] split = entry.getValue().split(SymbolConstants.WRAP);
                    cmd.add(String.format(CommandConstants.SED_I, entry.getKey(), filePath));
                    for (int i = NumberConstants.ZERO; i < split.length; i++) {
                        cmd.add(String.format(CommandConstants.ECHO, split[i], filePath));
                    }
                } else {
                    // 其他参数使用文件修改脚本
                    cmd.add(Constants.BASH_SPACE + getDeviceHomeDir(host) + SymbolConstants.SLASH + config.writeFile + Constants.BLANK_SPACE + entry.getKey() + Constants.BLANK_SPACE + entry.getValue() + Constants.BLANK_SPACE + filePath);
                }
            }
            // 执行命令
            sshBuilder.getExec(String.join(SymbolConstants.AND_AND, cmd));
        } catch (Exception e) {
            log.error("writeOrReplaceFile exception: {}", e);
            throw new RuntimeException(e);
        } finally {
            sshBuilder.close();
        }
    }

    /**
     * 写入配置文件内容
     *
     * @param filePath 配置文件地址
     * @param host     设备ip
     * @param content  修改内容
     */
    @Override
    public void writeFileTxt(String host, String content, String filePath) {
        SshBuilder sshBuilder = sshPoolBuilder(host);
        try {
            sshBuilder.getExec(String.format(CommandConstants.ECHO_TEE, content, filePath));
        } catch (Exception e) {
            log.error("writeFileTxt exception: {}", e);
            throw new RuntimeException(e);
        } finally {
            sshBuilder.close();
        }
    }

    /**
     * 执行命令返回结果
     *
     * @param host 设备ip
     * @param cmd  命令
     * @return {@link String}
     */
    @Override
    public String execCmd(String host, String cmd) {
        SshBuilder builder = sshPoolBuilder(host);
        try {
            log.info("execCmd:" + cmd);
            // 执行命令
            return builder.getExec(cmd);
        } catch (Exception e) {
            log.error("execCmd:{}", e.getMessage());
        } finally {
            builder.close();
        }
        return SymbolConstants.BLANK;
    }

    @Override
    public String execPtyCmd(String host, String cmd) {
        SshBuilder builder = sshPoolBuilder(host);
        try {
            log.info("execPtyCmd:" + cmd);
            // 执行命令
            return builder.getPtyExec(cmd);
        } catch (Exception e) {
            log.error("execPtyCmd:{}", e.getMessage());
        } finally {
            builder.close();
        }
        return SymbolConstants.BLANK;
    }

    /**
     * 命令行操作 不读取返回
     *
     * @param cmd  shell命令
     * @param host 设备ip
     */
    @Override
    public void execCmdNoResult(String host, String cmd) {
        SshBuilder builder = sshPoolBuilder(host);
        try {
            log.info("execCmd:" + cmd);
            // 执行命令
            builder.execCmdNoResult(cmd);
        } catch (Exception e) {
            log.error("execCmd:{}", e.getMessage());
        } finally {
            builder.close();
        }
    }

    /**
     * 删除文件
     *
     * @param host     设备ip
     * @param filePath 文件路径
     */
    @Override
    public void deleteFile(String host, String filePath) {
        SshBuilder builder = sshPoolBuilder(host);
        try {
            builder.getExec(String.format(CommandConstants.RM_RF, filePath));
        } catch (Exception e) {
            log.error("deleteFile exception: {}", e);
        } finally {
            builder.close();
        }
    }

    @Override
    public void deleteFiles(String host, List<String> filePaths) {
        SshBuilder builder = sshPoolBuilder(host);
        List<String> cmd = new LinkedList<>();
        filePaths.forEach(path -> {
            cmd.add(String.format(CommandConstants.RM_RF, path));
        });
        try {
            builder.getExec(String.join(SymbolConstants.AND_AND, cmd));
        } catch (Exception e) {
            log.error("deleteFiles exception: {}", e);
        } finally {
            builder.close();
        }
    }

    @Override
    public void deleteFileText(String host, String filePath, String content) {
        SshBuilder builder = sshPoolBuilder(host);
        try {
            // 转义空格
            content = content.replaceAll(Constants.BLANK_SPACE, SymbolConstants.ESCAPE_SPACE);
            builder.getExec(String.format(CommandConstants.SED_I, content, filePath));
        } catch (Exception e) {
            log.error("deleteFileText exception: {}", e);
        } finally {
            builder.close();
        }
    }

    /**
     * 新增设备后 将本地config 目录下的文件拷贝到新的设备上 projectPath/config 下
     *
     * @param device 设备
     */
    @Override
    public boolean copyConfig(Device device) {
        String deviceSshUsr = device.getDeviceSshUsr();
        String deviceSshPwd = CmdUtil.handleEncodePass(device.getDeviceSshPwd(), EncodeModeEnum.DECODE.name());
        SshBuilder sshBuilder = new SshBuilder(device.getDeviceIp(), deviceSshUsr, device.getPort(), deviceSshPwd);
        String cmd;
        // 创建目录，具有root权限用户 对config目录权限及所有人进行权限变更
        String path = getDeviceHomeDir(device.getDeviceIp()) + SymbolConstants.SLASH + config.scriptPath;
        cmd = String.format(CommandConstants.MKDIR_P, path);
        try {
            sshBuilder.exec(cmd, null);
            waitMoment();
            sshBuilder.sftpServer(scp -> {
                // 找到本地 config 目录下所有的文件
                Stream<Path> paths = null;
                try {
                    paths = Files.list(Paths.get(config.getAppPathConfig()));
                } catch (IOException e) {
                    log.error("scp.upload exception: {}", e);
                }
                // 统计文件列表
                List<Path> fileNames = paths.filter(Files::isRegularFile).collect(Collectors.toList());
                log.info(device.getDeviceIp() + "scp.upload " + fileNames);
                for (Path filePath : fileNames) {
                    try {
                        scp.getFileTransfer().upload(String.valueOf(filePath), path);
                    } catch (IOException e) {
                        log.error("scp.upload exception: {}", e);
                    }
                }
                try {
                    String chmod;
                    waitMoment();
                    // 修改文件权限
                    chmod = String.format(CommandConstants.CHMOD_R_777, path);
                    sshBuilder.exec(chmod, null);
                } catch (InterruptedException e) {
                    log.error("scp.upload InterruptedException: {}", e);
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    log.error("scp.upload exception: {}", e);
                }
                try {
                    scp.close();
                } catch (Exception e) {
                    log.error("scp.upload exception: {}", e);
                }
            });
            // 更新脚本版本信息
            SpringUtils.getBean(DeviceService.class).lambdaUpdate().set(Device::getScriptVersion, infoConfig.getVersion()).eq(Device::getDeviceIp, device.getDeviceIp()).update();
            device.setScriptVersion(infoConfig.getVersion());
            SpringUtils.getBean(CacheService.class).setCacheObject(String.format(Constants.CACHE_DEVICE_INFO, device.getDeviceIp()), device, NumberConstants.NUMBER_TWO_INTEGER, TimeUnit.DAYS);
            log.info(device.getDeviceIp() + "拷贝脚本结束");
            return true;
        } catch (InterruptedException e) {
            log.error("copyConfig InterruptedException: {}", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("copyConfig exception: {}", e);
        } finally {
            sshBuilder.close();
        }
        return false;
    }

    @Override
    public boolean putRemoteFiles(Device device, String sourceLocalPath, String targetRemotePath) {
        String deviceSshUsr = device.getDeviceSshUsr();
        String deviceSshPwd = CmdUtil.handleEncodePass(device.getDeviceSshPwd(), EncodeModeEnum.DECODE.name());
        SshBuilder sshBuilder = new SshBuilder(device.getDeviceIp(), deviceSshUsr, device.getPort(), deviceSshPwd);
        try {
            File localFile = new File(sourceLocalPath);
            List<Path> fileNames;
            if (localFile.isDirectory()) {
                // 找到目录下所有的文件
                Stream<Path> paths = null;
                try {
                    paths = Files.list(Paths.get(sourceLocalPath));
                } catch (IOException e) {
                    log.error("scp.upload exception: {}", e);
                }
                // 统计文件列表
                fileNames = paths.filter(Files::isRegularFile).collect(Collectors.toList());
            } else {
                fileNames = Collections.singletonList(Paths.get(sourceLocalPath));
            }
            // 创建目录
            String cmd = String.format(CommandConstants.MKDIR_P, targetRemotePath);
            sshBuilder.exec(cmd, null);
            waitMoment();
            sshBuilder.sftpServer(scp -> {
                log.info(device.getDeviceIp() + "scp.upload " + fileNames);
                for (Path filePath : fileNames) {
                    try {
                        scp.getFileTransfer().upload(String.valueOf(filePath), targetRemotePath);
                    } catch (IOException e) {
                        log.error("scp.upload exception: {}", e);
                    }
                }
                try {
                    scp.close();
                } catch (Exception e) {
                    log.error("scp.upload exception: {}", e);
                }
            });
            log.info(device.getDeviceIp() + "上传文件结束");
            return true;
        } catch (InterruptedException e) {
            log.error("putFiles InterruptedException: {}", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("putFiles exception: {}", e);
        } finally {
            sshBuilder.close();
        }
        return false;
    }

    @Override
    public boolean getRemoteFiles(Device device, String sourceRemotePath, String targetLocalPath) {
        String deviceSshUsr = device.getDeviceSshUsr();
        String deviceSshPwd = CmdUtil.handleEncodePass(device.getDeviceSshPwd(), EncodeModeEnum.DECODE.name());
        SshBuilder sshBuilder = new SshBuilder(device.getDeviceIp(), deviceSshUsr, device.getPort(), deviceSshPwd);
        try {
            // 创建目录
            String cmd = String.format(CommandConstants.MKDIR_P, targetLocalPath);
            CmdUtil.execute(cmd);
            waitMoment();
            sshBuilder.sftpServer(scp -> {
                log.info(device.getDeviceIp() + "scp.download " + sourceRemotePath);
                try {
                    scp.getFileTransfer().download(sourceRemotePath, targetLocalPath);
                } catch (IOException e) {
                    log.error("scp.download exception: {}", e);
                }
                try {
                    scp.close();
                } catch (Exception e) {
                    log.error("scp.download exception: {}", e);
                }
            });
            log.info(device.getDeviceIp() + "下载文件结束");
            return true;
        } catch (InterruptedException e) {
            log.error("putFiles InterruptedException: {}", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("putFiles exception: {}", e);
        } finally {
            sshBuilder.close();
        }
        return false;
    }

    /**
     * 设备空余磁盘
     *
     * @param host 设备ip
     */
    @Override
    public Double deviceFreeDisk(String host) {
        SshBuilder sshBuilder = sshPoolBuilder(host);
        double freeDisk = NumberConstants.ZERO_DOUBLE;
        List<String> list = new ArrayList<>();
        // 执行命令获取剩余磁盘大小
        try {
            sshBuilder.exec(CommandConstants.FREE_DISK, lines -> {
                list.addAll(lines);
            });
            for (String line : list) {
                log.info(line);
                String[] result = line.split(SymbolConstants.COLON_DELIMITER);
                String key = result[NumberConstants.ZERO];
                String value = result[NumberConstants.ONE];
                // 返回结果中筛选FREE_DISK磁盘余量
                if (Constants.MONITOR_FREE_DISK.equals(key)) {
                    freeDisk = Double.parseDouble(value);
                    break;
                }
            }
        } catch (Exception e) {
            log.error("deviceFreeDisk exception: {}" + e);
            throw new RuntimeException(e);
        } finally {
            sshBuilder.close();
        }
        return freeDisk;
    }

    /**
     * 设备监控信息
     *
     * @param host 设备ip
     */
    @Override
    public Server deviceMonitor(String host) {
        SshBuilder sshBuilder = sshPoolBuilder(host);
        Server server = new Server();
        List<SysFileServer> fileServers = new LinkedList<>();
        DecimalFormat format = new DecimalFormat(SymbolConstants.DECIMAL_FORMAT_ZERO_PATTERN);
        // 执行命令且调用脚本 "MEM_USE" 内存使用率  "DISK_USED" 磁盘使用率
        try {
            sshBuilder.exec(Constants.BASH_SPACE + getDeviceHomeDir(host) + SymbolConstants.SLASH + config.deviceMonitor + Constants.BLANK_SPACE + host, lines -> {
                lines.forEach(line -> {
                    log.info(line + host);
                    // 磁盘IO
                    if (line.contains(Constants.KEY_DISK_IO)) {
                        List<DiskIo> diskIoList = BeanUtil.wrapBeanList(DiskIo.class, line);
                        server.setDiskIoList(diskIoList);
                    } else if (line.contains(SymbolConstants.COLON_DELIMITER)) {
                        String[] result = line.split(SymbolConstants.COLON_DELIMITER);
                        String key = result[NumberConstants.ZERO];
                        String value = result[NumberConstants.ONE].trim();
                        Double longValue = Double.parseDouble(value);
                        // cpu 使用率
                        if (Constants.CPU_USE.equals(key)) {
                            server.getCpu().setUsed(longValue);
                            // cpu 核心
                        } else if (Constants.CPU_CORE.equals(key)) {
                            server.getCpu().setCpuNum(Integer.valueOf(result[NumberConstants.ONE].trim()));
                            // 总内存
                        } else if (Constants.MEM_TOTAL.equals(key)) {
                            server.getMem().setTotal(longValue);
                            // 已使用内存
                        } else if (Constants.MEM_USED.equals(key)) {
                            server.getMem().setUsed(longValue);
                            // 上行
                        } else if (Constants.TRAFFIC_IN.equals(key)) {
                            server.getNetWork().setBytesRecv(Double.parseDouble(format.format(longValue / 1024)));
                            // 下行
                        } else if (Constants.TRAFFIC_OUT.equals(key)) {
                            server.getNetWork().setBytesSent(Double.parseDouble(format.format(longValue / 1024)));
                            // 空闲内存
                        } else if (Constants.MEM_FREE.equals(key)) {
                            server.getMem().setFree(longValue);
                            //  内存使用率
                        } else if (Constants.MEM_USE.equals(key)) {
                            server.getMem().setUsage(longValue);
                        } else if (Constants.DISK_USED.equals(key)) {
                            server.setDiskUsed(longValue);
                        }
                    }
                });
            })
                    // 获取磁盘使用情况
                    .exec(CommandConstants.DF_H_TAIL, lines -> {
                        for (String line : lines) {
                            SysFileServer fileServer = new SysFileServer();
                            List<String> list = new LinkedList<>();
                            log.info(line + host);
                            String[] disk = line.split(Constants.BLANK_SPACE);
                            for (int i = NumberConstants.ZERO; i < disk.length; i++) {
                                if (StringUtils.isNotBlank(disk[i])) {
                                    list.add(disk[i]);
                                }
                            }
                            // 盘符路径
                            String dirName = list.get(NumberConstants.FIVE);
                            fileServer.setDirName(dirName);
                            // 文件类型
                            String typeName = list.get(NumberConstants.ZERO);
                            fileServer.setTypeName(typeName);
                            // 磁盘剩余大小
                            String free = list.get(NumberConstants.THREE);
                            fileServer.setFree(free);
                            // 磁盘已经使用量
                            String used = list.get(NumberConstants.TWO);
                            fileServer.setUsed(used);
                            // 磁盘总大小
                            String total = list.get(NumberConstants.ONE);
                            fileServer.setTotal(total);
                            // 计算磁盘用量
                            fileServer.setUsage(Double.parseDouble(list.get(NumberConstants.FOUR).replace(SymbolConstants.PERCENTAGE, SymbolConstants.BLANK)));
                            fileServers.add(fileServer);
                        }
                    });
        } catch (Exception e) {
            log.error("deviceMonitor exception: {}" + e);
            throw new RuntimeException(e);
        } finally {
            sshBuilder.close();
        }
        server.setSysFiles(fileServers);
        server.getSys().setComputerIp(host);
        return server;
    }

    /**
     * 测试设备连接
     *
     * @param host     设备ip
     * @param username 用户名
     * @param port     连接端口
     * @param pass     密码
     */
    @Override
    public void connTest(String host, String username, int port, String... pass) {
        if (!validatePort(host, port)) {
            throw new RuntimeException(String.format("%s%s", host, I18nMessageUtil.getMessage(MessageTipConstant.DEVICE_CONNECT_PORT_FAILED)));
        }
        String pwd = StringUtils.isNotBlank(pass[NumberConstants.ZERO]) ? pass[NumberConstants.ZERO] : SymbolConstants.BLANK;
        boolean login;
        login = new SshBuilder().connTest(host, username, port, pwd);
        if (!login) {
            throw new RuntimeException(String.format("%s%s", host, I18nMessageUtil.getMessage(MessageTipConstant.DEVICE_PASS_FAILED)));
        }
    }

    @Override
    public void connTestNewPwd(String host, String username, int port, String pass) {
        boolean login = true;
        try (SSHClient sshClient = new SSHClient();) {
            sshClient.addHostKeyVerifier(new PromiscuousVerifier());
            sshClient.connect(host, port);
            sshClient.authPassword(username, pass);
            sshClient.useCompression();
            sshClient.setConnectTimeout(3000);
            Session session = sshClient.startSession();
            if (!session.isOpen()) {
                throw new ServerException("不能获得session连接");
            }
        }catch (IOException e) {
            log.error(" ClientSession createClient connTestNewPwd" + e);
            login = false;
        }
        if (!login) {
            throw new RuntimeException(String.format("%s%s", host, I18nMessageUtil.getMessage(MessageTipConstant.DEVICE_PASS_FAILED)));
        }
    }

    /**
     * 获取设备架构信息
     *
     * @param host     设备ip
     * @param username 用户名
     * @param port     连接端口
     * @param pass     密码
     */
    @Override
    public String getArchInfo(String host, String username, int port, String... pass) {
        String pwd = StringUtils.isNotBlank(pass[NumberConstants.ZERO]) ? pass[NumberConstants.ZERO] : SymbolConstants.BLANK;
        SshBuilder sshBuilder = new SshBuilder(host, username, port, pwd);
        String archInfo;
        try {
            // 执行命令获取架构信息
            archInfo = sshBuilder.getExec(CommandConstants.UNAME_M).replaceAll(SymbolConstants.WRAP, SymbolConstants.BLANK);
        } catch (Exception e) {
            throw new ServiceException("get arch info error: " + e);
        } finally {
            sshBuilder.close();
        }
        return getDeviceArchType(archInfo);
    }

    /**
     * 获取CPU核心数量信息
     *
     * @param host     设备ip
     * @param username 用户名
     * @param port     连接端口
     * @param pass     密码
     */
    @Override
    public int getCpuCoreInfo(String host, String username, int port, String... pass) {
        String pwd = StringUtils.isNotBlank(pass[NumberConstants.ZERO]) ? pass[NumberConstants.ZERO] : SymbolConstants.BLANK;
        SshBuilder sshBuilder = new SshBuilder(host, username, port, pwd);
        String coreInfo;
        try {
            coreInfo = sshBuilder.getExec(CommandConstants.N_PROC).replaceAll(SymbolConstants.WRAP, SymbolConstants.BLANK);
        } catch (Exception e) {
            throw new ServiceException("get cpu core info error: " + e);
        } finally {
            sshBuilder.close();
        }
        return Integer.parseInt(coreInfo);
    }


    @Override
    public String getOsInfo(String host, String username, int port, String... pass) {
        String pwd = StringUtils.isNotBlank(pass[NumberConstants.ZERO]) ? pass[NumberConstants.ZERO] : SymbolConstants.BLANK;
        SshBuilder sshBuilder = new SshBuilder(host, username, port, pwd);
        String osInfo;
        try {
            osInfo = sshBuilder.getExec(CommandConstants.GET_OS_INFO);
        } catch (Exception e) {
            throw new ServiceException("get os info error: " + e);
        } finally {
            sshBuilder.close();
        }
        return osInfo;
    }

    @Override
    public String getCpuDescInfo(String host, String username, int port, String... pass) {
        String pwd = StringUtils.isNotBlank(pass[NumberConstants.ZERO]) ? pass[NumberConstants.ZERO] : SymbolConstants.BLANK;
        SshBuilder sshBuilder = new SshBuilder(host, username, port, pwd);
        String osInfo;
        try {
            for (String command : CommandConstants.GET_CPU_DESC_INFO_LIST){
                osInfo = sshBuilder.getExec(command);
                if (StringUtils.isNotBlank(osInfo)){
                    return osInfo.trim();
                }
            }
        } catch (Exception e) {
            throw new ServiceException("get cpu desc info error: " + e);
        } finally {
            sshBuilder.close();
        }
        return SymbolConstants.BLANK;
    }

    /**
     * 获取内存总量信息
     *
     * @param host     设备ip
     * @param username 用户名
     * @param port     连接端口
     * @param pass     密码
     */
    @Override
    public String getMemTotalInfo(String host, String username, int port, String... pass) {
        String pwd = StringUtils.isNotBlank(pass[NumberConstants.ZERO]) ? pass[NumberConstants.ZERO] : SymbolConstants.BLANK;
        SshBuilder sshBuilder = new SshBuilder(host, username, port, pwd);
        String memInfo;
        try {
            memInfo = sshBuilder.getExec(CommandConstants.GET_MEM_TOTAL).replaceAll(SymbolConstants.WRAP, SymbolConstants.BLANK);
        } catch (Exception e) {
            throw new ServiceException("get mem total info error: " + e);
        } finally {
            sshBuilder.close();
        }
        return String.format(FileConstants.MEM_TOTAL_FORMAT, memInfo);
    }

    /**
     * 获取磁盘总量信息
     *
     * @param host     设备ip
     * @param username 用户名
     * @param port     连接端口
     * @param pass     密码
     */
    @Override
    public String getDiskTotalInfo(String host, String username, int port, String... pass) {
        String pwd = StringUtils.isNotBlank(pass[NumberConstants.ZERO]) ? pass[NumberConstants.ZERO] : SymbolConstants.BLANK;
        SshBuilder sshBuilder = new SshBuilder(host, username, port, pwd);
        String diskInfo;
        try {
            diskInfo = sshBuilder.getExec(CommandConstants.GET_DISK_TOTAL).replaceAll(SymbolConstants.WRAP, SymbolConstants.BLANK);
        } catch (Exception e) {
            throw new ServiceException("get disk total info error: " + e);
        } finally {
            sshBuilder.close();
        }
        return String.format(FileConstants.MEM_TOTAL_FORMAT, diskInfo);
    }

    /**
     * 校验端口连接
     *
     * @param ipAddr 设备ip
     * @param port   端口
     * @return boolean
     */
    private boolean validatePort(String ipAddr, int port) {
        // sonar扫描误判：校验连接 cn
        TelnetClient client = new TelnetClient();
        try {
            client.setDefaultTimeout(1000);
            client.setConnectTimeout(1000);
            client.connect(ipAddr, port);
            if (client.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            log.error("validatePort exception: {}" + e);
        } finally {
            try {
                client.disconnect();
            } catch (IOException e) {
                log.error("validatePort exception: {}" + e);
            }
        }
        return false;
    }

    /**
     * 使用 exec 调用shell脚本
     *
     * @param shellPath 脚本路径
     * @param params    参数
     * @param host      设备ip
     * @return boolean 返回值
     */
    @Override
    public boolean callShellByExec(String host, String shellPath, String[] params) {
        SshBuilder builder = sshPoolBuilder(host);
        String result;
        try {
            String exec = Constants.BASH_SPACE + shellPath + Constants.BLANK_SPACE + String.join(Constants.BLANK_SPACE, params);
            result = builder.getExec(exec);
            log.info("callShellByExec Common {} result {}", exec, result);
        } catch (Exception e) {
            log.error("callShellByExec exception: {}", e);
            return false;
        } finally {
            builder.close();
        }
        return true;
    }

    /**
     * 钉钉告警发送
     *
     * @param shellPath 脚本路径
     * @param params    脚本参数
     * @return boolean
     */
    @Override
    public boolean callShellByExecWithDingTalk(String shellPath, String[] params) {
        // 组装命令
        StringJoiner joiner = new StringJoiner(Constants.BLANK_SPACE);
        joiner.add(Constants.BLANK_SPACE);
        for (String param : params) {
            joiner.add(param);
        }
        BufferedReader input = null;
        try {
            log.info("callShellByExecWithDingTalk Common {}", Constants.BASH_SPACE + shellPath + joiner);
            // 执行命令
            Process process = Runtime.getRuntime().exec(Constants.BASH_SPACE + shellPath + Constants.BLANK_SPACE + String.join(Constants.BLANK_SPACE, params));
            ThreadPoolManager.getInstance().execute(() -> {
                new Thread(new PrintProcessLog(params, process)).start();
            });
            // 读取返回
            input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                log.info("-----------send to DingTalk-----------callShellByExecWithDingTalk {}", line);
                if (line.contains(MgConstants.DING_TALK_SUCCESS_MSG)) {
                    log.info("-----------send to DingTalk-----------callShellByExecWithDingTalk success: {}", line);
                    return true;
                }
            }
        } catch (IOException e) {
            log.error("callShellByExecWithDingTalk exception: {}", e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                log.error("callShellByExecWithDingTalk exception: {}", e);
            }
        }
        return false;
    }

    /**
     * 指定directive 执行
     *
     * @param shellPath 脚本路径
     * @param params    参数
     * @param directive 命令
     * @return {@link String}
     */
    @Override
    public String callLocalShellByExec(String shellPath, List<String> params, String directive) {
        String result;
        BufferedReader input = null;
        InputStreamReader inputStreamReader = null;
        StringJoiner joiner = new StringJoiner(SymbolConstants.COMMA);
        try {
            params.add(NumberConstants.ZERO, Constants.SH);
            params.add(NumberConstants.ONE, shellPath);
            params.add(directive);
            // 组装命令
            String[] directives = params.toArray(new String[NumberConstants.ZERO]);
            log.info("callLocalShellByExec:" + Arrays.toString(directives));
            // 执行命令
            Process process = Runtime.getRuntime().exec(directives);
            process.waitFor();
            // 读取执行结果
            inputStreamReader = new InputStreamReader(process.getInputStream());
            input = new BufferedReader(inputStreamReader);
            int lineNo = NumberConstants.ZERO;
            while (true) {
                lineNo++;
                result = input.readLine();
                if (lineNo > NumberConstants.TEN) {
                    break;
                }
                if (StringUtils.isNotEmpty(result)) {
                    joiner.add(result);
                }
            }
        } catch (InterruptedException | IOException e) {
            log.error("callLocalShellByExec exception: {}", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            IoUtils.close(inputStreamReader, input);
        }
        return joiner.toString();
    }

    /**
     * 使用 exec 调用shell脚本
     *
     * @param shellPath 脚本路径
     * @param params    参数
     * @return boolean 返回值
     */
    @Override
    public boolean callLocalShellByExec(String shellPath, String[] params) {
        try {
            log.info("callLocalShellByExec Common {}", Constants.BASH_SPACE + shellPath + String.join(Constants.BLANK_SPACE, params));
            Process process = Runtime.getRuntime().exec(Constants.BASH_SPACE + shellPath + Constants.BLANK_SPACE + String.join(Constants.BLANK_SPACE, params));
            ThreadPoolManager.getInstance().execute(() -> {
                new Thread(new PrintProcessLog(params, process)).start();
            });
        } catch (IOException e) {
            log.error("callLocalShellByExec exception: {}", e);
            return false;
        }
        return true;
    }

    /**
     * 执行ping命令判断ip地址是否能联通
     *
     * @param ipAddress ip地址
     * @return boolean
     */
    @Override
    public boolean pingExec(String ipAddress) {
        // 当返回值是true时，说明host是可用的，false则不可。
        try {
            return InetAddress.getByName(ipAddress).isReachable(500);
        } catch (IOException e) {
            log.error("pingExec exception: {}", e);
        }
        return false;
    }

    /**
     * 创建目录
     *
     * @param host 设备ip
     * @param path 目录
     */
    @Override
    public void mkdirs(String host, String path) {
        // 创建目录
        SshBuilder sshBuilder = sshPoolBuilder(host);
        try {
            sshBuilder.getExec(String.format(CommandConstants.MKDIR_P, path));
        } catch (Exception e) {
            log.error("mkdirs exception: {}", e);
            throw new RuntimeException(e);
        } finally {
            sshBuilder.close();
        }
    }

    /**
     * 创建目录并执行后续命令
     *
     * @param host 设备ip
     * @param path 目录
     * @param cmd  后续命令
     */
    @Override
    public void mkdirsAndOthers(String host, String path, String cmd) {
        // 创建目录
        SshBuilder sshBuilder = sshPoolBuilder(host);
        // 创建目录
        try {
            sshBuilder.exec(String.format(CommandConstants.MKDIR_P, path) + SymbolConstants.AND_AND + cmd, null);
        } catch (Exception e) {
            log.error("mkdirsAndOthers exception: {}", e);
            throw new RuntimeException(e);
        } finally {
            sshBuilder.close();
        }
    }

    @Override
    public List<String> findFileText(String host, String filePath, String content) {
        List<String> list = new LinkedList<>();
        SshBuilder builder = sshPoolBuilder(host);
        try {
            // 转义空格
            content = content.replaceAll(Constants.BLANK_SPACE, SymbolConstants.ESCAPE_SPACE);
            builder.exec(String.format(CommandConstants.SED_N, content, filePath), lines -> list.addAll(lines));
        } catch (Exception e) {
            log.error("findFileText exception: {}", e);
            Thread.currentThread().interrupt();
        } finally {
            builder.close();
        }

        return list;
    }

    /**
     * 获取设备空余端口
     *
     * @param host      设备ip
     * @param startPort 起始端口
     * @param endPort   终止端口
     * @return int
     */
    @Override
    public int getDevicePort(String host, int startPort, int endPort) {
        int port = NumberConstants.ZERO;
        List<String> list = new ArrayList<>();
        SshBuilder sshBuilder = null;
        // 执行命令且调用脚本
        try {
            log.info("开始获取端口: {} {} {}" , host, startPort, endPort);
            sshBuilder = sshPoolBuilder(host);
            sshBuilder.exec(Constants.BASH_SPACE + getDeviceHomeDir(host) + SymbolConstants.SLASH + config.portRange + Constants.BLANK_SPACE + startPort + Constants.BLANK_SPACE + endPort, list::addAll);
            log.info("获取端口结束: {}" + JSONUtil.toJsonStr(list));
            String[] split = list.get(NumberConstants.ZERO).split(SymbolConstants.COLON_DELIMITER);
            port = Integer.parseInt(split[NumberConstants.ONE]);
            log.info("端口号为: {}" + port);
        } catch (Exception e) {
            log.error("devicePort exception: {}" + e);
        } finally {
            if (!ObjectUtils.isEmpty(sshBuilder)) {
                sshBuilder.close();
            }
        }
        return port;
    }

    /**
     * 获取设备空余内存
     *
     * @param host 设备ip
     * @return {@link Double}
     */
    @Override
    public Double deviceFreeMem(String host) {
        SshBuilder sshBuilder = sshPoolBuilder(host);
        double freeMem = NumberConstants.ZERO_DOUBLE;
        List<String> list = new ArrayList<>();
        // 执行命令且调用脚本
        try {
            sshBuilder.exec(CommandConstants.FREE_M, list::addAll);
            for (String line : list) {
                log.info(line);
                String[] result = line.split(SymbolConstants.COLON_DELIMITER);
                String key = result[NumberConstants.ZERO];
                String value = result[NumberConstants.ONE];
                if (Constants.MEM_FREE.equals(key)) {
                    freeMem = Double.parseDouble(value);
                    break;
                }
            }
        } catch (Exception e) {
            log.error("deviceFreeMem exception: {}" + e);
            throw new RuntimeException(e);
        } finally {
            sshBuilder.close();
        }
        return freeMem;
    }

    /**
     * 获取设备磁盘和内存剩余空间
     * @param host 设备ip
     * @return 剩余量
     */
    @Override
    public Map<String, Double> deviceFreeResource(String host) {
        SshBuilder sshBuilder = sshPoolBuilder(host);
        List<String> list = new ArrayList<>();
        Map<String, Double> resultMap = new HashMap<>(NumberConstants.TWO);
        // 执行资源查询命令
        try {
            String cmd = String.format(CommandConstants.DEVICE_FREE_RESOURCE, CommandConstants.FREE_M, CommandConstants.FREE_DISK, CommandConstants.GET_CPU_USE);
            sshBuilder.exec(cmd, list::addAll);
            for (String line : list) {
                log.info(line);
                String[] result = line.split(SymbolConstants.COLON_DELIMITER);
                String key = result[NumberConstants.ZERO];
                String value = result[NumberConstants.ONE];
                if (Constants.MEM_FREE.equals(key) || Constants.DISK_FREE.equals(key) || Constants.CPU_USE.equals(key)) {
                    resultMap.put(key, Double.parseDouble(value));
                }
            }
        } catch (Exception e) {
            log.error("deviceFreeMem exception: {}" + e);
            throw new RuntimeException(e);
        } finally {
            sshBuilder.close();
        }
        return resultMap;
    }

    @Override
    public String getDeviceHomeDir(String host) {
        return execCmd(host, CommandConstants.GET_DEVICE_USER_HOME).trim();
    }

    @Override
    public List<String> getAbsoluteFilePathList(String host, String path) {
        List<String> resultList = new ArrayList<>();
        String fileListStr = execCmd(host, String.format(CommandConstants.LS, path));
        if (StringUtils.isBlank(fileListStr)){
            return new ArrayList<>();
        }
        Arrays.asList(fileListStr.split(SymbolConstants.WRAP)).forEach(file -> {
            if (StringUtils.isNotBlank(file)){
                resultList.add(path + SymbolConstants.SLASH + file);
            }
        });
        return resultList;
    }

    @Override
    public boolean checkRemoteFileExist(String host, String path){
        String fileStr = execCmd(host, String.format(CommandConstants.LS, path));
        return StringUtils.isNotBlank(fileStr);
    }

    @Override
    public boolean checkRemoteDirExist(String host, String path){
        String dirStr = execCmd(host, String.format(CommandConstants.TEST_D, path));
        return StringUtils.isNotBlank(dirStr);
    }

    @Override
    public long getRemoteFileSize(String host, String path){
        long fileSize;
        String fileSizeStr = execCmd(host, String.format(CommandConstants.GET_REMOTE_FILE_SIZE, path)).trim();
        try {
            fileSize = Long.parseLong(fileSizeStr);
        } catch (NumberFormatException e){
            throw new ServiceException(String.format(MgConstants.GET_REMOTE_FILE_SIZE_FAILED, fileSizeStr));
        }
        return fileSize;
    }

    /**
     * 打印过程日志
     *
     * @author cn
     * @date 2024/10/30
     */
    class PrintProcessLog implements Runnable {
        String[] params;
        Process process;

        public PrintProcessLog(String[] params, Process process) {
            this.params = params;
            this.process = process;
        }

        @Override
        public void run() {
            BufferedReader input = null;
            try {
                // 读取日志输出
                input = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = input.readLine()) != null) {
                    // 当读取到成功标志时退出读取
                    if (line.contains("success")) {
                        break;
                    }
                }
            } catch (IOException e) {
                log.error("callShellByExec exception: {}", e);
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        log.error("input.close() exception: {}", e);
                    }
                }
            }
        }
    }

    @Override
    public String getDeviceArchType() {
        // 获取本机系统架构，架构不同校验工具也不同
        String archInfo = CmdUtil.execute(CommandConstants.UNAME_M);
        if (config.x86FlagList.stream().anyMatch(flag -> archInfo.toLowerCase().contains(flag))) {
            return ArchTypeEnum.X86.getValue();
        } else if (config.armFlagList.stream().anyMatch(flag -> archInfo.toLowerCase().contains(flag))) {
            return ArchTypeEnum.ARM.getValue();
        } else {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.BASE_UNSUPPORTED_ARCH_TYPE, archInfo));
        }
    }

    @Override
    public String remoteTransferFile(Device source, Device target, String sourceFilePath, String targetFilePath) {
        SshBuilder sshBuilder = new SshBuilder();
        String sourceKey = source.getDeviceIp() + SymbolConstants.COLON_DELIMITER + source.getDeviceSshUsr() + SymbolConstants.COLON_DELIMITER + CmdUtil.handleEncodePass(source.getDeviceSshPwd(), EncodeModeEnum.DECODE.name()) + SymbolConstants.COLON_DELIMITER + source.getPort();
        String targetKey = target.getDeviceIp() + SymbolConstants.COLON_DELIMITER + target.getDeviceSshUsr() + SymbolConstants.COLON_DELIMITER + CmdUtil.handleEncodePass(target.getDeviceSshPwd(), EncodeModeEnum.DECODE.name()) + SymbolConstants.COLON_DELIMITER + target.getPort();
        return sshBuilder.doRemoteTransferFile(sourceKey, targetKey, sourceFilePath, targetFilePath);
    }

    /**
     * 根据架构标识区分系统的架构
     *
     * @param archInfo 获取架构命令返回的结果
     * @return
     */
    private String getDeviceArchType(String archInfo) {
        if (config.x86FlagList.stream().anyMatch(flag -> archInfo.toLowerCase().contains(flag))) {
            return ArchTypeEnum.X86.getValue();
        } else if (config.armFlagList.stream().anyMatch(flag -> archInfo.toLowerCase().contains(flag))) {
            return ArchTypeEnum.ARM.getValue();
        } else {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.BASE_UNSUPPORTED_ARCH_TYPE, archInfo));
        }
    }

}