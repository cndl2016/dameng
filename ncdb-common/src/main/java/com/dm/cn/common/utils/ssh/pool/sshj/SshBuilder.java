package com.dm.cn.common.utils.ssh.pool.sshj;


import cn.hutool.core.io.IoUtil;
import com.dm.cn.common.core.constant.Constants;
import com.dm.cn.common.core.constant.MgConstants;
import com.dm.cn.common.core.constant.NumberConstants;
import com.dm.cn.common.core.constant.SymbolConstants;
import com.dm.cn.common.core.exception.ServiceException;
import com.dm.cn.common.utils.CmdUtil;
import lombok.NoArgsConstructor;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.OpenMode;
import net.schmizz.sshj.sftp.RemoteFile;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.TransportException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * SSH 免密登录 构建者模式
 *
 * @author DAMENG
 * @date 2023/07/07
 */
@NoArgsConstructor
public class SshBuilder {

    private static final Logger log = LoggerFactory.getLogger(SshBuilder.class);

    /**
     * ssh客户端
     */
    private SSHClient sshClient;

    /**
     * 设备ip
     */
    private String host = "";

    /**
     * 连接key标识
     */
    private String key = "";

    /**
     * 客户端池
     */
    private static GenericKeyedObjectPool<String, SSHClient> pool = new SshjCommonPool().getPool();

    /**
     * 获取ssh连接
     *
     * @param host     主机ip
     * @param username 用户名
     * @param port     端口
     * @param pass     密码
     */
    public SshBuilder(String host, String username, int port, String pass) {
        // 通过主机IP、端口和用户名，连接主机，获取Session
        try {
            // 打印池信息
            poolInfo();
            this.host = host;
            this.key = host + SymbolConstants.COLON_DELIMITER + username + SymbolConstants.COLON_DELIMITER + pass + SymbolConstants.COLON_DELIMITER + port;
            // 使用key标识从池中获取客户端
            sshClient = pool.borrowObject(key);
        } catch (Exception e) {
            log.error("SshBuilder获取连接失败", e);
        }
    }

    /**
     * 测试设备间连接状况
     *
     * @param host     源主机IP
     * @param port     端口
     * @param username 用户名
     * @param pass     密码
     * @param ipList   目标主机IP集合
     * @return {@link List}<{@link String}>
     */
    public List<String> connTestOfList(String host, Integer port, String username, String pass, List<String> ipList) {
        List<String> errorMsg = new ArrayList<>();
        try {
            poolInfo();
            this.key = host + SymbolConstants.COLON_DELIMITER + username + SymbolConstants.COLON_DELIMITER + pass + SymbolConstants.COLON_DELIMITER + port;
            sshClient = pool.borrowObject(key);
            Session session = sshClient.startSession();
            if (!session.isOpen()) {
                errorMsg.add(String.format("%s:%s 无法连接", host, port));
                return errorMsg;
            }
            if (ipList.size() > 0) {
                ipList.forEach(ipAddr -> {
                    Session sessionPing = null;
                    try {
                        sessionPing = sshClient.startSession();
                        Session.Command cmd = sessionPing.exec(String.format("ping -c 3 -w 1 %s", ipAddr));
                        BufferedReader reader = new BufferedReader(new InputStreamReader(cmd.getInputStream()));
                        String line;
                        boolean isReachable = true;
                        while ((line = reader.readLine()) != null) {
                            if (line.contains("100% packet loss")) { // 判断 ping 命令返回的结果
                                isReachable = false;
                            }
                        }
                        cmd.join(500, TimeUnit.MILLISECONDS);
                        if (!isReachable) {
                            errorMsg.add(String.format("%s:%s 与 %s 间通信异常", host, port, ipAddr));
                        }
                    } catch (Exception e) {
                        errorMsg.add(String.format("%s:%s 与 %s 间通信异常：%s", host, port, ipAddr, e.getMessage()));
                    } finally {
                        if (sessionPing != null) {
                            try {
                                sessionPing.close();
                            } catch (Exception e) {
                                log.error("sessionPing流关闭异常：{}", e);
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            errorMsg.add(String.format("%s:%s 无法连接：%s", host, port, e.getMessage()));
            return errorMsg;
        } finally {
            close();
        }
        return errorMsg;
    }

    /**
     * 连接测试
     *
     * @param host     主机ip
     * @param username 用户名
     * @param port     端口
     * @param pass     密码
     * @return boolean
     */
    public boolean connTest(String host, String username, int port, String pass) {
        Session session = null;
        try {
            // 打印池信息
            poolInfo();
            this.key = host + SymbolConstants.COLON_DELIMITER + username + SymbolConstants.COLON_DELIMITER + pass + SymbolConstants.COLON_DELIMITER + port;
            // 获取客户端
            sshClient = pool.borrowObject(key);
            // 打开session连接
            session = sshClient.startSession();
            // 检查是否连通
            if (!session.isOpen()) {
                throw new ServerException("can not create session");
            }
        } catch (Exception e) {
            log.error("SshBuilder connTest连接测试失败", e);
            return false;
        } finally {
            if (session != null) {
                try {
                    // session关闭
                    session.close();
                } catch (TransportException e) {
                    log.error("SshBuilder connTest TransportException{}连接关闭错误", castUserPassword(e));
                } catch (ConnectionException e) {
                    log.error("SshBuilder connTest ConnectionException{}连接关闭错误", castUserPassword(e));
                }
            }
            close();
        }
        return true;
    }

    /**
     * 命令行操作
     *
     * @param cmd shell命令
     * @return ssh对象
     * @throws ServiceException ssh操作异常
     */
    public String getExec(String cmd) throws IOException {
        log.info("getExec:" + cmd);
        Session session = null;
        try {
            // 打印池信息
            poolInfo();
            session = sshClient.startSession();
            Session.Command res = session.exec(cmd);
            return IOUtils.toString(res.getInputStream(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("SshBuilder getExec{}", castUserPassword(e));
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return SymbolConstants.BLANK;
    }

    /**
     * 创建伪终端并执行交互式命令
     *
     * @param cmd shell命令
     * @return {@link String}
     * @throws IOException
     */
    public String getPtyExec(String cmd) throws IOException {
        log.info("getPtyExec:" + cmd);
        Session session = null;
        try {
            // 打印池信息
            poolInfo();
            session = sshClient.startSession();
            session.allocateDefaultPTY();
            Session.Command res = session.exec(cmd);
            return IOUtils.toString(res.getInputStream(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("SshBuilder getPtyExec{}", castUserPassword(e));
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return SymbolConstants.BLANK;
    }

    /**
     * 命令行操作
     *
     * @param cmd    shell命令
     * @param params 入参
     * @return ssh对象
     * @throws ServiceException ssh操作异常
     */
    public String getExec(String cmd, String[] params) throws Exception {
        Session session = null;
        try {
            poolInfo();
            session = sshClient.startSession();
            Session.Command res = session.exec(cmd + Constants.BLANK_SPACE + String.join(Constants.BLANK_SPACE, params));
            return IOUtils.toString(res.getInputStream(), Constants.UTF8);
        } catch (NullPointerException | IOException e) {
            log.error("SshjBuilder getExec{}", castUserPassword(e));
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return "";
    }

    /**
     * 命令行操作 不读取返回
     *
     * @param cmd shell命令
     * @return ssh对象
     * @throws ServiceException ssh操作异常
     */
    public void execCmdNoResult(String cmd) throws IOException {
        Session session = null;
        try {
            session = sshClient.startSession();
            // 执行命令
            session.exec(cmd);
        } catch (Exception e) {
            log.error("SshBuilder getExec{}", castUserPassword(e));
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /**
     * 命令行操作
     *
     * @param cmd      shell命令
     * @param consumer 提供执行结果行集合文本（若为空则表示不需要返回）
     * @return ssh对象
     * @throws ServiceException ssh操作异常
     */
    public SshBuilder exec(String cmd, Consumer<List<String>> consumer) throws ConnectionException, TransportException {
        // 打印池信息
        poolInfo();
        Session session = null;
        try {
            // 打印池信息
            poolInfo();
            session = sshClient.startSession();
            // 执行命令
            Session.Command res = session.exec(cmd);
            // 获取返回
            String result = IOUtils.toString(res.getInputStream(), Constants.UTF8);
            if (consumer != null) {
                List<String> lines = IoUtil.readLines(new ByteArrayInputStream(result.getBytes()), Charset.forName(CmdUtil.getSystemLanguage()), new ArrayList<>());
                //读取错误信息
                if (lines.isEmpty()) {
                    lines = IoUtil.readUtf8Lines(new ByteArrayInputStream(result.getBytes()), new ArrayList<>());
                }
                consumer.accept(lines);
            }
        } catch (NullPointerException | IOException e) {
            log.error("SshBuilder getExec执行命令失败{}", castUserPassword(e));
            throw new ServiceException(String.format(MgConstants.SSH_EXEC_FAILED, this.host));
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return this;
    }

    /**
     * 无返回结果的命令行操作
     *
     * @param cmd shell命令
     * @return ssh对象
     * @throws ServiceException ssh操作异常
     */
    public SshBuilder exec(String cmd) throws Exception {
        poolInfo();
        return exec(cmd, null);
    }

    /**
     * 获取文件传输服务
     *
     * @param consumer 文件传输客户端函数式接口
     * @return {@link SshBuilder}
     */
    public SshBuilder sftpServer(Consumer<SFTPClient> consumer) {
        SFTPClient sftpClient;
        try {
            // 获取文件传输客户端
            sftpClient = sshClient.newSFTPClient();
            consumer.accept(sftpClient);
        } catch (IOException e) {
            throw new ServiceException(MgConstants.SFTP_FAILED);
        }
        return this;
    }

    /**
     * 远程服务器间文件传输
     *
     * @param sourceKey      源服务器连接信息串
     * @param targetKey      目标服务器连接信息串
     * @param sourceFilePath 源文件路径
     * @param targetFilePath 目标文件路径
     * @return {@link String}
     */
    public String doRemoteTransferFile(String sourceKey, String targetKey, String sourceFilePath, String targetFilePath) {
        SSHClient source = null;
        SSHClient target = null;
        SFTPClient sourceClient = null;
        SFTPClient targetClient = null;
        RemoteFile remoteSourceFile = null;
        RemoteFile remoteTargetFile = null;
        try {
            // 获取源与目标服务器连接
            source = pool.borrowObject(sourceKey);
            target = pool.borrowObject(targetKey);
            // 获取远程文件传输客户端
            sourceClient = source.newSFTPClient();
            targetClient = target.newSFTPClient();
            // 构建源与目标服务器远程文件对象
            remoteSourceFile = sourceClient.open(sourceFilePath);
            remoteTargetFile = targetClient.open(targetFilePath, EnumSet.of(OpenMode.WRITE, OpenMode.CREAT));
            // 读源文件+写目标文件
            byte[] buffer = new byte[NumberConstants.FILE_TRANSFER_BUFFER_SIZE];
            long fileOffset = NumberConstants.ZERO;
            int byteRead;
            while ((byteRead = remoteSourceFile.read(fileOffset, buffer, NumberConstants.ZERO, buffer.length)) > NumberConstants.ZERO) {
                remoteTargetFile.write(fileOffset, buffer, NumberConstants.ZERO, byteRead);
                fileOffset += byteRead;
            }
            return Constants.OK;
        } catch (Exception e) {
            log.error("传输远程服务器间文件出错", e);
            return e.getMessage();
        } finally {
            // 关闭远程服务器间文件传输的资源
            closeRemoteFileTransfer(sourceKey, targetKey, source, target, sourceClient, targetClient, remoteSourceFile, remoteTargetFile);
        }
    }

    /**
     * 关闭远程服务器间文件传输的资源
     *
     * @param sourceKey        源服务器连接信息串
     * @param targetKey        目标服务器连接信息串
     * @param source           源服务器连接
     * @param target           目标服务器连接
     * @param sourceClient     远程源文件传输客户端
     * @param targetClient     远程目标文件传输客户端
     * @param remoteSourceFile 源服务器远程文件对象
     * @param remoteTargetFile 目标服务器远程文件对象
     */
    private void closeRemoteFileTransfer(String sourceKey, String targetKey, SSHClient source, SSHClient target, SFTPClient sourceClient, SFTPClient targetClient, RemoteFile remoteSourceFile, RemoteFile remoteTargetFile) {
        // 关闭远程文件对象
        if (remoteSourceFile != null) {
            try {
                remoteSourceFile.close();
            } catch (IOException e) {
                log.error("源远程文件对象关闭失败：{}", e);
            }
        }
        // 关闭远程文件对象
        if (remoteTargetFile != null) {
            try {
                remoteTargetFile.close();
            } catch (IOException e) {
                log.error("目标远程文件对象关闭失败：{}", e);
            }
        }
        // 关闭远程文件传输客户端
        if (sourceClient != null) {
            try {
                sourceClient.close();
            } catch (IOException e) {
                log.error("源远程文件对象关闭失败：{}", e);
            }
        }
        // 关闭远程文件传输客户端
        if (targetClient != null) {
            try {
                targetClient.close();
            } catch (IOException e) {
                log.error("源远程文件对象关闭失败：{}", e);
            }
        }
        // 归还池资源
        if (source != null) {
            pool.returnObject(sourceKey, source);
        }
        // 归还池资源
        if (target != null) {
            pool.returnObject(targetKey, target);
        }
    }

    /**
     * 关闭客户端连接
     */
    public void close() {
        if (sshClient != null) {
            // 归还池资源
            pool.returnObject(key, sshClient);
        }
    }

    /**
     * 处理打印信息中的密码敏感信息
     *
     * @param e 错误信息
     * @return {@link String}
     */
    private String castUserPassword(Exception e) {
        // 若信息中存在密码认证
        if (e.getMessage().contains(Constants.SUDO)) {
            // 截取认证信息后的命令
            return StringUtils.substringAfter(e.getMessage(), "|");
        }
        return e.getMessage();
    }

    /**
     * 打印连接池使用情况
     */
    private void poolInfo() {
        log.info("pool.getNumActive活跃对象=================" + pool.getNumActive());
        log.info("pool.getNumIdle空闲对象=================" + pool.getNumIdle());
        log.info("pool.getMaxTotal最大容量=================" + pool.getMaxTotal());
    }
}
