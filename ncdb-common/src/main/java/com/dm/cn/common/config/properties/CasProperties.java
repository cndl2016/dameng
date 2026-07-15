package com.dm.cn.common.config.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * CAS的配置参数
 *
 * @author DAMENG
 */
@Data
@Component
public class CasProperties {
    @Value("${cas.server.host.url}")
    private String casServerUrl;

    @Value("${cas.server.host.login_url}")
    private String casServerLoginUrl;

    @Value("${cas.server.host.logout_url}")
    private String casServerLogoutUrl;

    @Value("${app.casEnable}")
    private boolean casEnable;

    @Value("${app.server.host.url}")
    private String appServerUrl;

    @Value("${app.login_url}")
    private String appLoginUrl;

    @Value("${app.logout_url}")
    private String appLogoutUrl;

    @Value("${app.web_url}")
    private String webUrl;

}
