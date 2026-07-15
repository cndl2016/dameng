package com.dm.cn.common.cache;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 过期map bean
 *
 * @author DAMENG
 */
@Configuration
public class ExpiringCacheMap {

    @Bean
    public ExpiringMap<Object, Object> getExpiringMap() {
        return ExpiringMap.builder()
                .expiration(365, TimeUnit.DAYS)
                .variableExpiration().expirationPolicy(ExpirationPolicy.CREATED).build();
    }
}
