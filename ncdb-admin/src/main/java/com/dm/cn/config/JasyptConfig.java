package com.dm.cn.config;

import com.dm.cn.common.security.utils.SecurityUtils;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 设置加密密码，需要妥善保管
 *
 * @author DAMENG
 * @date 2024/06/28
 */
@Component
public class JasyptConfig {

    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(SecurityUtils.encryptPassword("manager"));
        return encryptor;
    }
}
