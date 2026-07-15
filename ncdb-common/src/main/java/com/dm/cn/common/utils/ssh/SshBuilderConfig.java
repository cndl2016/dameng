package com.dm.cn.common.utils.ssh;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author DAMENG
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ssh")
public class SshBuilderConfig {
    private String idRsaPathLinux;
    private String idRsaPathWin;
    private Integer maxTotal;
    private Integer numTestsPerEvictionRun;
    private Integer maxTotalPerKey;
    private Integer maxIdlePerKey;
    private Integer minIdlePerKey;
    private Boolean isBlockWhenExhausted;
    private Boolean isTestOnCreate;
    private Boolean isTestOnBorrow;
    private Integer minEvictableIdleTime;
    private Integer timeBetweenEvictionRuns;
}
