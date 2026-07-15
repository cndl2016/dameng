package com.dm.cn.common.utils.ssh.pool.sshj;

import com.dm.cn.common.core.constant.NumberConstants;
import com.dm.cn.common.core.constant.SymbolConstants;
import com.dm.cn.common.core.utils.SpringUtils;
import com.dm.cn.common.security.utils.SecurityUtils;
import com.dm.cn.common.utils.ssh.SshBuilderConfig;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.rmi.ServerException;

/**
 * @author DAMENG
 */
public class PoolSshjFactory implements KeyedPooledObjectFactory<String, SSHClient> {

    private static final Logger log = LoggerFactory.getLogger(PoolSshjFactory.class);


    /**
     * 激活对象，借用时调用
     *
     * @param myKey
     * @param pooledObject
     */
    @Override
    public void activateObject(String myKey, PooledObject<SSHClient> pooledObject) {
    }

    /**
     * 销毁对象
     *
     * @param myKey
     * @param pooledObject
     * @throws IOException
     */
    @Override
    public void destroyObject(String myKey, PooledObject<SSHClient> pooledObject) throws IOException {
        pooledObject.getObject().close();
    }

    /**
     * 创建对象
     *
     * @param myKey
     * @return {@link PooledObject}<{@link Session}>
     * @throws IOException
     */
    @Override
    public PooledObject<SSHClient> makeObject(String myKey) throws IOException {
		// sonar bug! 连接不能关闭，关闭会导致拿到的连接直接关闭
        SSHClient sshClient = new SSHClient();
        Session session;
        try {
            String[] split = myKey.split(SymbolConstants.COLON_DELIMITER);
            // host + ":" + username + ":" + pass + ":" + port
            String host = split[NumberConstants.ZERO];
            String username = split[NumberConstants.ONE];
            String pass = split[NumberConstants.TWO];
            int port = Integer.parseInt(split[NumberConstants.NUMBER_THREE_INTEGER]);
            if (StringUtils.isNotBlank(pass) && !SymbolConstants.NULL.equals(pass)) {
                // 给Session添加密码
                sshClient.addHostKeyVerifier(new PromiscuousVerifier());
                sshClient.connect(host, port);
                sshClient.authPassword(username, pass);
            } else {
                String idRsaPath;
                // publicKey方式认证
                if (SecurityUtils.isWin()) {
                    idRsaPath = SpringUtils.getBean(SshBuilderConfig.class).getIdRsaPathWin();
                } else {
                    idRsaPath = SpringUtils.getBean(SshBuilderConfig.class).getIdRsaPathLinux();
                }
                sshClient.authPublickey(username, idRsaPath);
                sshClient.connect(host, port);
            }
            sshClient.useCompression();
            sshClient.setConnectTimeout(3000);
            session = sshClient.startSession();
            if (!session.isOpen()) {
                throw new ServerException("不能获得session连接");
            }
            return new DefaultPooledObject(sshClient);
        } catch (IOException e) {
            log.error(" ClientSession createClient" + myKey + e);
            throw new ServerException(e.getMessage());
        }
    }

    /**
     * 钝化对象，归还时调用
     *
     * @param myKey
     * @param pooledObject
     */
    @Override
    public void passivateObject(String myKey, PooledObject<SSHClient> pooledObject) {
    }

    /**
     * 判断对象是否可用
     *
     * @param myKey
     * @param pooledObject
     * @return boolean
     */
    @Override
    public boolean validateObject(String myKey, PooledObject<SSHClient> pooledObject) {
        return pooledObject.getObject().isConnected();
    }

}

