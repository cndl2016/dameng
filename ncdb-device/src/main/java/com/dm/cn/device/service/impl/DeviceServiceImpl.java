package com.dm.cn.device.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dm.cn.common.config.NcdbApiConfig;
import com.dm.cn.common.core.constant.*;
import com.dm.cn.common.core.enums.AlarmConditionEnum;
import com.dm.cn.common.core.exception.ServiceException;
import com.dm.cn.common.core.utils.IdWorker;
import com.dm.cn.common.core.utils.SpringUtils;
import com.dm.cn.common.core.utils.StringUtils;
import com.dm.cn.common.core.utils.bean.DbTypeBean;
import com.dm.cn.common.core.web.domain.AjaxResult;
import com.dm.cn.common.enums.DeviceEnum;
import com.dm.cn.common.enums.EncodeModeEnum;
import com.dm.cn.common.param.DeviceMonitorParam;
import com.dm.cn.common.param.DeviceParam;
import com.dm.cn.common.param.EnableParam;
import com.dm.cn.common.security.utils.SecurityUtils;
import com.dm.cn.common.utils.CmdUtil;
import com.dm.cn.common.utils.I18nMessageUtil;
import com.dm.cn.common.utils.ThreadPoolManager;
import com.dm.cn.common.utils.ip.IpUtils;
import com.dm.cn.common.utils.server.NetWork;
import com.dm.cn.common.utils.server.Server;
import com.dm.cn.device.entity.enums.DeviceMonitorEnum;
import com.dm.cn.device.entity.server.Device;
import com.dm.cn.device.entity.vo.*;
import com.dm.cn.device.mapper.DeviceMapper;
import com.dm.cn.device.service.DeviceService;
import com.dm.cn.device.service.SshdService;
import com.dm.cn.system.config.security.service.CacheService;
import com.dm.cn.system.constant.MessageTipConstant;
import com.dm.cn.system.mapper.SysMenuMapper;
import com.dm.cn.system.service.ISysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 针对【T_DEVICE（设备表）】的数据库操作Service实现
 *
 * @author dameng
 * @date 2024/10/15
 */
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device>
        implements DeviceService {

    private static final Logger log = LoggerFactory.getLogger(DeviceServiceImpl.class);

    @Resource
    private NcdbApiConfig config;

    @Resource
    private DbTypeBean dbTypeBean;

    @Resource
    private CacheService cacheService;

    @Resource
    private SshdService sshdService;

    @Resource
    private DeviceMapper deviceMapper;

    /**
     * 查询设备列表
     *
     * @param deviceParam 设备查询参数
     * @return {@link IPage}<{@link Device}>
     */
    @Override
    public IPage<Device> getListDevice(DeviceParam deviceParam) {
        // 初始化分页对象
        IPage page = new Page<>(deviceParam.getPageNum(), deviceParam.getPageSize());
        // 获取条件查询时间参数
        String beginTime = String.valueOf(deviceParam.getParams().get(Constants.QUERY_PARAM_BEGIN_TIME));
        String endTime = String.valueOf(deviceParam.getParams().get(Constants.QUERY_PARAM_END_TIME));
        // 获取设备所属设备节点的所有后代节点id
        if (!ObjectUtils.isEmpty(deviceParam.getNodeId())) {
            deviceParam.setIdList(deviceMapper.getChildIdList(deviceParam.getNodeId(), dbTypeBean.isH2()));
        }
        // 获取设备分页列表
        IPage<Device> iPage = deviceMapper.getDeviceList(page, deviceParam, beginTime, endTime);
        for (Device device : iPage.getRecords()) {
            // 若设备禁用，则清除缓存中的监控信息
            if (DeviceEnum.DISABLE.getValue().equals(device.getEnableStatus())) {
                cacheService.deleteObject(String.format(Constants.CACHE_DEVICE_MONITOR, device.getDeviceIp()));
            }
            // 从缓存中获取设备监控信息
            Map<String, String> deviceInfo = cacheService.getCacheObject(String.format(Constants.CACHE_DEVICE_MONITOR, device.getDeviceIp()));
            if (DeviceEnum.ENABLE.getValue().equals(device.getEnableStatus()) && !ObjectUtils.isEmpty(deviceInfo)) {
                device.setCpuUsing(deviceInfo.get(Constants.CPU_USING_FIELD));
                device.setMemUsing(deviceInfo.get(Constants.MEM_USING_INFO_FIELD));
                device.setDiskUsing(deviceInfo.get(Constants.DISK_USING_FIELD));
                device.setNetworkOut(deviceInfo.get(Constants.NETWORK_OUT_FIELD));
                device.setNetworkIn(deviceInfo.get(Constants.NETWORK_IN_FIELD));
            }
            // 设置所属节点全路径
            device.setDeviceArea(getDevicePath(device.getNodeId()));
            device.setDeviceSshPwd(CmdUtil.handleEncodePass(device.getDeviceSshPwd(), EncodeModeEnum.DECODE.name()));
        }
        // 刷新设备 cpu/内存/磁盘等信息
        refreshDeviceInfo();
        return iPage;
    }

    /**
     * 设备启用禁用
     *
     * @param enableParam 启停用参数
     * @return boolean
     * @author cn 2024/07/10
     */
    @Override
    public boolean enableDevice(EnableParam enableParam) {
        return lambdaUpdate().set(Device::getEnableStatus, enableParam.getStatus())
                .eq(Device::getId, enableParam.getId()).update();
    }

    /**
     * 删除设备
     *
     * @param id 设备id
     * @return {@link AjaxResult}
     * @author cn 2024/07/10
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDeviceById(Long id) {
        if (ObjectUtils.isEmpty(id)) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.COMMON_NOT_SELECT));
        }
        Device device = getById(id);
        if (ObjectUtils.isEmpty(device)) {
            return true;
        }
        // 获取路径下脚本文件并删除
        String path = sshdService.getDeviceHomeDir(device.getDeviceIp()) + SymbolConstants.SLASH + config.scriptPath;
        List<String> scriptList = sshdService.getAbsoluteFilePathList(device.getDeviceIp(), path)
                .stream().filter(file -> StringUtils.isNotBlank(file) && file.toLowerCase(Locale.ROOT).endsWith(FileConstants.SH_FILE_SUFFIX))
                .collect(Collectors.toList());
        if (!scriptList.isEmpty()) {
            sshdService.deleteFiles(device.getDeviceIp(), scriptList);
        }
        // 删除设备
        boolean isSuccess = removeById(id);
        // 更新设备缓存数据
        if (isSuccess) {
            cacheService.setCacheObject(TokenConstants.DEVICE_LIST_KEY, lambdaQuery().list());
        }
        return isSuccess;
    }

    @Override
    public boolean saveOrUpdateDevice(Device device) {
        device.setDeviceSshPwd(CmdUtil.handleEncodePass(device.getDeviceSshPwd(), EncodeModeEnum.ENCODE.name()));
        // 校验用户名密码是否联通
        sshdService.connTest(device.getDeviceIp(), device.getDeviceSshUsr(), device.getPort(), CmdUtil.handleEncodePass(device.getDeviceSshPwd(), EncodeModeEnum.DECODE.name()));
        // 判断 保存 or 新增
        if (device.getId() == null) {
            // 设置id
            device.setId(IdWorker.getId().nextId());
            // 设置创建时间
            device.setCreateTime(new Date(System.currentTimeMillis() / NumberConstants.ONE_THOUSAND * NumberConstants.ONE_THOUSAND));
            // 获取创建人
            device.setCreateUser(SecurityUtils.getUsername());
            device.setConnStatus(DeviceEnum.CONN.getValue());
            // 校验ip是否重复
            long count = lambdaQuery().eq(Device::getDeviceIp, device.getDeviceIp()).count();
            if (count > NumberConstants.ZERO) {
                throw new ServiceException(String.format(MgConstants.DEVICE_IP_EXIST, device.getDeviceIp()));
            }
        } else {
            // 判断密码是否修改
            Device oldDevice = getById(device.getId());
            String oldPwd = CmdUtil.handleEncodePass(oldDevice.getDeviceSshPwd(), EncodeModeEnum.DECODE.name());
            String newPwd = CmdUtil.handleEncodePass(device.getDeviceSshPwd(), EncodeModeEnum.DECODE.name());
            if (StringUtils.isNotEmpty(oldPwd) && !oldPwd.equals(newPwd)) {
                // 修改了密码 校验新密码
                sshdService.connTestNewPwd(device.getDeviceIp(), device.getDeviceSshUsr(), device.getPort(), newPwd);
            }
            // 校验ip是否重复
            long count = lambdaQuery().eq(Device::getDeviceIp, device.getDeviceIp()).ne(Device::getId, device.getId()).count();
            if (count > NumberConstants.ZERO) {
                throw new ServiceException(String.format(MgConstants.DEVICE_IP_EXIST, device.getDeviceIp()));
            }
            device.setUpdateUser(SecurityUtils.getUsername());
            // 设置修改时间
            device.setUpdateTime(new Date(System.currentTimeMillis() / NumberConstants.ONE_THOUSAND * NumberConstants.ONE_THOUSAND));
        }
        // 设备架构信息
        device.setArchType(sshdService.getArchInfo(device.getDeviceIp(), device.getDeviceSshUsr(), device.getPort(), CmdUtil.handleEncodePass(device.getDeviceSshPwd(), EncodeModeEnum.DECODE.name())));
        device.setCpuCore(sshdService.getCpuCoreInfo(device.getDeviceIp(), device.getDeviceSshUsr(), device.getPort(), CmdUtil.handleEncodePass(device.getDeviceSshPwd(), EncodeModeEnum.DECODE.name())));
        device.setMemTotal(sshdService.getMemTotalInfo(device.getDeviceIp(), device.getDeviceSshUsr(), device.getPort(), CmdUtil.handleEncodePass(device.getDeviceSshPwd(), EncodeModeEnum.DECODE.name())));
        device.setDiskTotal(sshdService.getDiskTotalInfo(device.getDeviceIp(), device.getDeviceSshUsr(), device.getPort(), CmdUtil.handleEncodePass(device.getDeviceSshPwd(), EncodeModeEnum.DECODE.name())));

        try {
            // 操作系统信息
            String osInfo = sshdService.getOsInfo(device.getDeviceIp(), device.getDeviceSshUsr(), device.getPort(), CmdUtil.handleEncodePass(device.getDeviceSshPwd(), EncodeModeEnum.DECODE.name()));
            if (!StringUtils.isEmpty(osInfo)) {
                String[] split = osInfo.split("\n");
                String[] osName = split[0].split(SymbolConstants.EQUAL);
                String[] osVersion = split[1].split(SymbolConstants.EQUAL);
                device.setOsName(osName[1].replaceAll("\"", ""));
                device.setOsVersion(osVersion[1].replaceAll("\"", ""));
            }
            // cpu描述信息
            String cpuDescInfo = sshdService.getCpuDescInfo(device.getDeviceIp(), device.getDeviceSshUsr(), device.getPort(), CmdUtil.handleEncodePass(device.getDeviceSshPwd(), EncodeModeEnum.DECODE.name()));
            if (!StringUtils.isEmpty(cpuDescInfo)) {
                if (cpuDescInfo.contains(SymbolConstants.COLON_DELIMITER)){
                    String[] split = cpuDescInfo.split(SymbolConstants.COLON_DELIMITER);
                    if (split.length > 1) {
                        device.setCpuDesc(split[1].trim());
                    }
                } else {
                    device.setCpuDesc(cpuDescInfo);
                }
            }
        } catch (Exception e) {
            log.error("获取主机详细信息失败：{}", e);
        }

        // 记录设备新增修改情况
        boolean isSuccess = saveOrUpdate(device);
        // 上传脚本文件
        ThreadPoolManager.getInstance().execute(() -> sshdService.copyConfig(device));
        // 更新设备缓存数据
        if (isSuccess) {
            SpringUtils.getBean(CacheService.class).setCacheObject(String.format(Constants.CACHE_DEVICE_INFO, device.getDeviceIp()), device, NumberConstants.NUMBER_TWO_INTEGER, TimeUnit.DAYS);
            cacheService.setCacheObject(TokenConstants.DEVICE_LIST_KEY, lambdaQuery().list());
        }
        return isSuccess;
    }

    /**
     * 获取设备监控列表
     *
     * @param param 查询参数
     * @return {@link AjaxResult}
     * @author cn 2024/07/11
     */
    @Override
    public List<DeviceMonitorInfoVO> getMonitorList(DeviceMonitorParam param) {
        // 设置数据库类型
        param.setH2(dbTypeBean.isH2());
        List<DeviceMonitorInfoVO> list = new ArrayList<>();
        // 获取设备列表
        List<Device> deviceList = deviceMapper.getMonitorList(param);
        // 给设备所属区域字段赋值
        deviceList.forEach(device -> {
            DeviceMonitorInfoVO vo = new DeviceMonitorInfoVO();
            BeanUtils.copyProperties(device, vo);
            vo.setDeviceArea(getDevicePath(vo.getNodeId()));
            list.add(vo);
        });
        return list;
    }

    /**
     * 根据设备节点id查询设备所属组织全路径
     *
     * @param deviceNodeId 设备节点id
     * @return {@link String}
     */
    @Override
    public String getDevicePath(long deviceNodeId) {
        return deviceMapper.getDevicePath(deviceNodeId, dbTypeBean.isH2());
    }

    /**
     * 根据当前设备节点id，查询其所有后代节点的id
     *
     * @param deviceNodeId 设备节点id
     * @param isH2         数据源是否为h2
     * @return {@link List}<{@link String}>
     */
    @Override
    public List<String> getChildIdList(long deviceNodeId, boolean isH2) {
        return deviceMapper.getChildIdList(deviceNodeId, isH2);
    }

    @Override
    public boolean checkPortIsExist(String ip, Integer port) {
        String cmd = sshdService.execCmd(ip, String.format(CommandConstants.NCDB_OPERATE_PORT, port));
        return !StringUtils.isBlank(cmd);
    }

    @Override
    public boolean checkPortIsExistRetry(String ip, Integer port) {
        int i = NumberConstants.ZERO;
        // 监听进程
        while (checkPortIsExist(ip, port)) {
            i++;
            if (i > NumberConstants.THREE) {
                // 3s内端口存在则返回true
                return true;
            }
            try {
                Thread.sleep(NumberConstants.ONE_THOUSAND);
            } catch (InterruptedException e) {
                log.error("devicePort InterruptedException {}", e);
                Thread.currentThread().interrupt();
            }
        }
        return false;
    }

    @Override
    public boolean checkProgressIsExist(String ip, String fileName) {
        String str = sshdService.execCmd(ip, String.format(CommandConstants.NCDB_OPERATE_FILE, fileName));
        return !StringUtils.isBlank(str);
    }

    /**
     * 刷新设备 cpu/内存/磁盘等信息
     */
    private void refreshDeviceInfo() {
        List<Device> devices = lambdaQuery().select(Device::getId, Device::getDeviceIp, Device::getDeviceSshUsr, Device::getDeviceSshPwd, Device::getPort, Device::getConnStatus)
                .eq(Device::getEnableStatus, DeviceEnum.ENABLE.getValue())
                .eq(Device::getConnStatus, DeviceEnum.CONN.getValue())
                .list();
        if (!CollectionUtils.isEmpty(devices)) {
            devices.forEach(device -> {
                // 新建任务：刷新设备信息
                ThreadPoolManager.getInstance().execute(() -> {
                    if (DeviceEnum.CONN.getValue().equals(device.getConnStatus())) {
                        try {
                            Server server = sshdService.deviceMonitor(device.getDeviceIp());
                            getSysInfo(server);
                        } catch (Exception e) {
                            log.error("getDeviceInfo exception: {}", e);
                        }
                    }
                });
            });
        }
    }

    /**
     * 缓存设备信息
     *
     * @param server 服务器信息对象
     */
    private void getSysInfo(Server server) {
        try {
            // 构造map
            NetWork netWork = server.getNetWork();
            Map<String, String> deviceInfo = new HashMap<>(NumberConstants.SIX);
            deviceInfo.put(Constants.CPU_USING_FIELD, server.getCpu().getUsed() + SymbolConstants.PERCENTAGE);
            deviceInfo.put(Constants.MEM_USING_INFO_FIELD, server.getMem().getUsage() + SymbolConstants.PERCENTAGE);
            deviceInfo.put(Constants.DISK_USING_FIELD, server.getDiskUsed() + SymbolConstants.PERCENTAGE);
            deviceInfo.put(Constants.NETWORK_IN_FIELD, netWork.getBytesRecv() + Constants.NETWORK_UNIT);
            deviceInfo.put(Constants.NETWORK_OUT_FIELD, netWork.getBytesSent() + Constants.NETWORK_UNIT);
            deviceInfo.put(Constants.FREE_MEMORY_SIZE_FIELD, String.valueOf(server.getMem().getFree()));
            // 存入缓存
            cacheService.setCacheObject(String.format(Constants.CACHE_DEVICE_MONITOR, server.getSys().getComputerIp()), deviceInfo, NumberConstants.ONE_HUNDRED, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("getSysInfo exception {}", e);
        }
    }

    @Override
    public List<String> getNodeDeviceIpList() {
        return deviceMapper.getNodeDeviceList().stream().map(Device::getDeviceIp).collect(Collectors.toList());
    }

    /**
     * 检查指定的设备是否被禁用
     *
     * @param deviceList 设备ip列表
     */
    @Override
    public void verifyDeviceInitMessage(List<String> deviceList) {
        if (!CollectionUtils.isEmpty(deviceList)) {
            StringJoiner joiner = new StringJoiner(SymbolConstants.COMMA);
            for (String device : deviceList) {
                if (!lambdaQuery()
                        .eq(Device::getDeviceIp, device)
                        .eq(Device::getEnableStatus, DeviceEnum.ENABLE.getValue())
                        .eq(Device::getConnStatus, DeviceEnum.CONN.getValue())
                        .exists()
                ) {
                    joiner.add(device);
                }
            }
            if (StringUtils.isNotBlank(joiner.toString())) {
                throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.DEVICE_DISABLED_FOR_TASK, joiner));
            }
        }
    }

    /**
     * 检查IP
     *
     * @param deviceList 设备IP列表
     * @param ipStart    起始ip
     * @param ipEnd      终止ip
     * @param deviceRoom 机房信息
     * @return {@link List}<{@link String}>
     */
    @Override
    public List<String> verifyIpInitMessage(List<String> deviceList, String ipStart, String ipEnd, Long... deviceRoom) {
        boolean deviceExist = lambdaQuery()
                .eq(Device::getEnableStatus, DeviceEnum.ENABLE.getValue())
                .eq(Device::getConnStatus, DeviceEnum.CONN.getValue())
                .exists();
        if (!deviceExist) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.DEVICE_SELECT_ENABLE));
        }
        // 只能选择一个模式
        boolean isNotSingle = !CollectionUtils.isEmpty(deviceList) && (StringUtils.isNotBlank(ipEnd) || StringUtils.isNotBlank(ipStart));
        if (isNotSingle) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.DEVICE_SELECT_ONE_MODEL, deviceList, ipStart, ipEnd));
        }
        // 如果没有指定设备，那么就是Ip段
        boolean isIp = CollectionUtils.isEmpty(deviceList) && (StringUtils.isNotBlank(ipStart) && StringUtils.isNotBlank(ipEnd));
        if (isIp) {
            // 获取系统内部已经启用的设备
            List<Device> enabledDevices = lambdaQuery()
                    .select(Device::getDeviceIp, Device::getNodeId)
                    .eq(Device::getEnableStatus, DeviceEnum.ENABLE.getValue())
                    .eq(Device::getConnStatus, DeviceEnum.CONN.getValue())
                    .list();
            // 两地三中心机房筛选逻辑
            if (deviceRoom.length != NumberConstants.ZERO) {
                enabledDevices = enabledDevices.stream().filter(device -> device.getNodeId().equals(deviceRoom[NumberConstants.ZERO])).collect(Collectors.toList());
            }
            List<Device> devices = enabledDevices.stream().filter(device -> IpUtils.compareIp(device.getDeviceIp(), ipStart) > NumberConstants.MINUS_ONE
                    && IpUtils.compareIp(device.getDeviceIp(), ipEnd) < NumberConstants.ONE).collect(Collectors.toList());
            // 如果设备都被禁用 或不存在
            if (CollectionUtils.isEmpty(devices)) {
                throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.DEVICE_NOT_EXIST_IN_RANGE, ipStart, ipEnd));
            }
            // 启用的设备
            return devices.stream().map(Device::getDeviceIp).collect(Collectors.toList());
        }
        return deviceList;
    }

    @Override
    public void verifyPortInitMessage(Integer portStart, Integer portEnd) {
        if (portStart == null || portEnd == null) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.DEVICE_PORT_START_END, portStart, portEnd));
        }
    }

    @Override
    public List<Device> getDeviceIpInfoList(List<String> deviceList) {
        // 在线的设备集合
        List<Device> onlineDevices = new LinkedList<>();
        List<Device> devices = deviceMapper.getDeviceIpInfoList(deviceList);
        for (Device device : devices) {
            if (sshdService.pingExec(device.getDeviceIp())) {
                onlineDevices.add(device);
            }
        }
        return onlineDevices;
    }

    /**
     * 处理SQL返回集合，获取设备数量和各类实例数量
     *
     * @param dataList 数据集合
     * @return
     */
    private DeviceInstanceNum dealDeviceInstanceNum(List<DeviceInstanceNumVO> dataList) {
        DeviceInstanceNum deviceInstanceNum = new DeviceInstanceNum();
        dataList.forEach(item -> {
            int count = item.getCount();
            // 设备：-1,单机：0,主备：1,分布式：2
            switch (item.getType()) {
                case -1:
                    deviceInstanceNum.setDeviceCount(count);
                    break;
                case 0:
                    deviceInstanceNum.setSingleInstanceCount(count);
                    break;
                case 1:
                    deviceInstanceNum.setSentinelInstanceCount(count);
                    break;
                case 2:
                    deviceInstanceNum.setClusterInstanceCount(count);
                    break;
                default:
                    break;
            }
        });

        return deviceInstanceNum;
    }

    @Override
    public DeviceTopChartVO getDeviceTopChart() {
        DeviceTopChartVO result = new DeviceTopChartVO();
        // CPU数据
        List<SysJobLogVO> cpuDataList = new ArrayList<>();
        // 內存数据
        List<SysJobLogVO> memoryDataList = new ArrayList<>();
        // 磁盘数据
        List<SysJobLogVO> diskDataList = new ArrayList<>();
        // 从缓存中获取设备信息
        List<DeviceOptionVO> deviceOptionList = getDeviceInfo();
        if (!CollectionUtils.isEmpty(deviceOptionList)) {
            deviceOptionList.forEach(deviceOption -> {
                if (!StringUtils.isEmpty(deviceOption.getInfo())) {
                    String[] split = deviceOption.getInfo().split(",");
                    for (String item : split) {
                        String[] info = item.split(":");
                        // 判断info对象是否为空
                        if (!StringUtils.isEmpty(info) && info.length > 1) {
                            String name = info[0];
                            String value = info[1];
                            SysJobLogVO sysJobLog = new SysJobLogVO();
                            sysJobLog.setJobMessage(deviceOption.getValue());
                            if (!DeviceMonitorEnum.UPSTREAM.getDesc().equals(name) && !DeviceMonitorEnum.DOWNSTREAM.getDesc().equals(name)) {
                                sysJobLog.setMonitorValues(Float.parseFloat(value.replace("%", "")));
                            }
                            switch (name) {
                                case Constants.CPU:
                                    cpuDataList.add(sysJobLog);
                                    break;
                                case Constants.MEMORY:
                                    memoryDataList.add(sysJobLog);
                                    break;
                                case Constants.DISK:
                                    diskDataList.add(sysJobLog);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                } else {
                    // 缓存中无数据,读取数据库
                    String ip = deviceOption.getLabel();
                    // 这个地方无需判断为空，ip是从设备表查询出来的
                    Long deviceId = lambdaQuery().eq(Device::getDeviceIp, ip).one().getId();
                    // 根据告警项和在线设备获取前十设备信息
                    List<SysJobLogVO> sysJobLogList = deviceMapper.getDeviceMessageById(deviceId);
                    sysJobLogList.forEach(sysJobLog -> {
                        sysJobLog.setJobMessage(ip);
                        if (StringUtils.isNotEmpty(sysJobLog.getAlarmCondition())) {
                            if (sysJobLog.getAlarmCondition().equals(AlarmConditionEnum.DEVICE_CPU.getValue())) {
                                cpuDataList.add(sysJobLog);
                            } else if (sysJobLog.getAlarmCondition().equals(AlarmConditionEnum.DEVICE_MEM.getValue())) {
                                memoryDataList.add(sysJobLog);
                            } else if (sysJobLog.getAlarmCondition().equals(AlarmConditionEnum.DEVICE_DISK.getValue())) {
                                diskDataList.add(sysJobLog);
                            }
                        }
                    });
                }
            });
        }
        result.setCpuDataList(cpuDataList);
        result.setMemoryDataList(memoryDataList);
        result.setDiskDataList(diskDataList);

        return result;
    }

    @Override
    public boolean testPing(List<Device> deviceList) {
        for (Device device : deviceList) {
            sshdService.connTest(device.getDeviceIp(), device.getDeviceSshUsr(), device.getPort(), device.getDeviceSshPwd());
        }
        return true;
    }

    /**
     * 获取设备 cpu/内存/磁盘等信息
     *
     * @return
     */
    private List<DeviceOptionVO> getDeviceInfo() {
        List<Device> deviceList = lambdaQuery().select(Device::getId, Device::getDeviceIp, Device::getDeviceSshUsr, Device::getPort, Device::getConnStatus)
                .eq(Device::getEnableStatus, DeviceEnum.ENABLE.getValue())
                .eq(Device::getConnStatus, DeviceEnum.CONN.getValue())
                .list();
        List<DeviceOptionVO> deviceOptionList = new LinkedList<>();
        if (!CollectionUtils.isEmpty(deviceList)) {
            deviceList.forEach(device -> {
                DeviceOptionVO option = new DeviceOptionVO();
                option.setLabel(device.getDeviceIp());
                option.setValue(device.getDeviceIp());
                // 从缓存中获取设备信息
                Map<String, String> deviceInfo = cacheService.getCacheObject(String.format(Constants.CACHE_DEVICE_MONITOR, device.getDeviceIp()));
                if (!ObjectUtils.isEmpty(deviceInfo)) {
                    String cpuUsing = deviceInfo.get(Constants.CPU_USING_FIELD);
                    String memUsingInfo = deviceInfo.get(Constants.MEM_USING_INFO_FIELD);
                    String diskUsing = deviceInfo.get(Constants.DISK_USING_FIELD);
                    String networkIn = deviceInfo.get(Constants.NETWORK_IN_FIELD);
                    String networkOut = deviceInfo.get(Constants.NETWORK_OUT_FIELD);
                    String info = String.format(MgConstants.DEVICE_INFO, cpuUsing, memUsingInfo, diskUsing, networkIn, networkOut);
                    option.setInfo(info);
                }
                deviceOptionList.add(option);
            });
        }

        return deviceOptionList;
    }

}