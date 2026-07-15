package com.dm.cn.common.utils.ssh;

import cn.hutool.log.StaticLog;
import com.dm.cn.common.enums.EncodeModeEnum;
import com.dm.cn.common.task.constant.TaskConstant;
import com.dm.cn.common.task.handle.TaskResultHandle;
import com.dm.cn.common.utils.CmdUtil;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.future.AuthFuture;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.client.session.ClientSession;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * ssh执行工具
 *
 * @author dyy
 * @date 2025/3/7
 */
public class SshExecUtil {

    /**
     * 检查是否可连接到ssh主机
     */
    public static Boolean checkSshConnect(String agentIp, String username, String password) {
        SshClient sshClient = SshClient.setUpDefaultClient();
        ClientSession channelSession = null;
        try {
            sshClient.start();
            ConnectFuture connectFuture = sshClient.connect(username, agentIp, 22).verify(3000L);
            if (!connectFuture.isConnected()) {
                return false;
            }
            channelSession = connectFuture.getClientSession();
            channelSession.addPasswordIdentity(CmdUtil.handleEncodePass(password, EncodeModeEnum.DECODE.name()));
            AuthFuture authFuture = channelSession.auth().verify(3000L);
            if (!authFuture.isSuccess()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            StaticLog.error(e);
            return false;
        } finally {
            if (channelSession != null) {
                try {
                    channelSession.close();
                    if (channelSession.isClosed()) {
                        StaticLog.info("channelSession closed ...");
                    }
                } catch (Exception e) {
                    StaticLog.error("channelSession closed failed");
                }
            }
            sshClient.stop();
        }
    }

    /**
     * ssh远程执行shell
     */
    public static void execShBySsh(String command, String ip, int port, String user, String password, String workDir, boolean viewErr, TaskResultHandle taskResultHandle) {
        if (workDir != null) {
            command = "cd " + workDir + " && " + command;
        }
        StaticLog.info(" command = " + command);
        SshClient sshClient = SshClient.setUpDefaultClient();
        ClientSession channelSession = null;
        ChannelExec channelExec = null;
        ByteArrayOutputStream err = null;
        try {
            sshClient.start();
            ConnectFuture connectFuture = sshClient.connect(user, ip, port).verify(3000L);
            if (!connectFuture.isConnected()) {
                taskResultHandle.sendResult("网络通信异常，请检测网络或账号密码是否正常！");
                taskResultHandle.sendStatus(TaskConstant.STATUS_FAILURE);
                return;
            }
            channelSession = connectFuture.getClientSession();
            channelSession.addPasswordIdentity(CmdUtil.handleEncodePass(password, EncodeModeEnum.DECODE.name()));
            AuthFuture authFuture = channelSession.auth().verify(3000L);
            if (!authFuture.isSuccess()) {
                taskResultHandle.sendResult("网络通信异常，请检测网络或账号密码是否正常！");
                taskResultHandle.sendStatus(TaskConstant.STATUS_FAILURE);
                return;
            }
            channelExec = channelSession.createExecChannel(command);
            if (viewErr) {
                channelExec.setRedirectErrorStream(true);
            } else {
                err = new ByteArrayOutputStream();
                channelExec.setErr(err);
            }
            channelExec.open();
            channelExec.waitFor(Collections.singleton(ClientChannelEvent.OPENED), 0);
            BufferedReader br = new BufferedReader(new InputStreamReader(channelExec.getInvertedOut(), StandardCharsets.UTF_8));
            String lineMes;
            while ((lineMes = br.readLine()) != null) {
                StaticLog.info(" lineMes = " + lineMes);
                taskResultHandle.sendResult(lineMes);
                taskResultHandle.sendResult("\n");
            }
            channelExec.waitFor(Collections.singleton(ClientChannelEvent.CLOSED), 0);
            taskResultHandle.sendStatus(channelExec.getExitStatus());
        } catch (Exception e) {
            StaticLog.error(e);
            taskResultHandle.sendStatus(TaskConstant.STATUS_FAILURE);
            taskResultHandle.sendResult("执行脚本失败：" + e.toString());
        } finally {
            if (err != null) {
                try {
                    err.close();
                } catch (Exception e) {
                    StaticLog.error("byteStream closed failed");
                }
            }
            if (channelExec != null) {
                try {
                    channelExec.close();
                } catch (Exception e) {
                    StaticLog.error("channelExec closed failed");
                }
            }
            if (channelSession != null) {
                try {
                    channelSession.close();
                    if (channelSession.isClosed()) {
                        StaticLog.info("channelSession closed ...");
                    }
                } catch (Exception e) {
                    StaticLog.error("channelSession closed failed");
                }
            }
            sshClient.stop();
        }
    }

    /**
     * ssh远程执行shell文件内容
     */
    public static void execShellFileBySsh(String fileContext, String ip, int port, String user, String password, String workDir, boolean viewErr, TaskResultHandle taskResultHandle) {
        String command = String.format("bash -s <<'EOF'\n%s\nEOF", fileContext);
        execShBySsh(command, ip, port, user, password, workDir, viewErr, taskResultHandle);
    }

}