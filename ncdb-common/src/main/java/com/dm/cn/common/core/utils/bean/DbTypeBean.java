package com.dm.cn.common.core.utils.bean;

import com.dm.cn.common.core.constant.MgConstants;
import com.dm.cn.common.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author DAMENG
 */
@Configuration
public class DbTypeBean {

    private static final String H2_DRIVER_CLASSNAME = "org.h2.Driver";

    private static final String DM8_DRIVER_CLASSNAME = "dm.jdbc.driver.DmDriver";

    @Value("${spring.datasource.dynamic.datasource.master.driver-class-name}")
    private String driverClassName;

    /**
     * @return boolean
     */
    public boolean isH2() {
        if (H2_DRIVER_CLASSNAME.equals(driverClassName)) {
            return true;
        } else if (DM8_DRIVER_CLASSNAME.equals(driverClassName)) {
            return false;
        }
        throw new ServiceException(String.format(MgConstants.DB_PARAM_ERROR, driverClassName));
    }
}

