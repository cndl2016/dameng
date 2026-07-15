package com.dm.cn.common.utils.ssh.pool.sshj;

import com.dm.cn.common.core.utils.SpringUtils;
import com.dm.cn.common.utils.ssh.SshBuilderConfig;
import lombok.Getter;
import net.schmizz.sshj.SSHClient;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * @author DAMENG
 */
@Configuration
public class SshjCommonPool {

    @Getter
    private final GenericKeyedObjectPool<String, SSHClient> pool;

    public SshjCommonPool() {
        GenericKeyedObjectPoolConfig<SSHClient> config = new GenericKeyedObjectPoolConfig<>();
        // 最大资源数
        config.setMaxTotal(SpringUtils.getBean(SshBuilderConfig.class).getMaxTotal());
        // 在空闲连接回收器运行时检查的连接数
        config.setNumTestsPerEvictionRun(SpringUtils.getBean(SshBuilderConfig.class).getNumTestsPerEvictionRun());
        // 每个 key 对应的最大资源数
        config.setMaxTotalPerKey(SpringUtils.getBean(SshBuilderConfig.class).getMaxTotalPerKey());
        // 最大 key 数量
        config.setMaxIdlePerKey(SpringUtils.getBean(SshBuilderConfig.class).getMaxIdlePerKey());
        // 空闲最小key数量
        config.setMinIdlePerKey(SpringUtils.getBean(SshBuilderConfig.class).getMinIdlePerKey());
        // 没有可用资源时是否要等待
        config.setBlockWhenExhausted(SpringUtils.getBean(SshBuilderConfig.class).getIsBlockWhenExhausted());
        // 创建新资源对象后是否立即校验是否可用
        config.setTestOnCreate(SpringUtils.getBean(SshBuilderConfig.class).getIsTestOnCreate());
        // 借用资源对象前是否校验是否可用
        config.setTestOnBorrow(SpringUtils.getBean(SshBuilderConfig.class).getIsTestOnBorrow());
        // 空闲最小剔除间隔
        config.setMinEvictableIdleTime(Duration.ofMinutes(SpringUtils.getBean(SshBuilderConfig.class).getMinEvictableIdleTime()));
        // 两次过期操作时间间隔
        config.setTimeBetweenEvictionRuns(Duration.ofSeconds(SpringUtils.getBean(SshBuilderConfig.class).getTimeBetweenEvictionRuns()));
        // 软空闲剔除时间间隔
        config.setSoftMinEvictableIdleTime(Duration.ofMinutes(SpringUtils.getBean(SshBuilderConfig.class).getMinEvictableIdleTime()));

        KeyedPooledObjectFactory<String, SSHClient> factory = new PoolSshjFactory();
        this.pool = new GenericKeyedObjectPool<>(factory, config);
    }
}