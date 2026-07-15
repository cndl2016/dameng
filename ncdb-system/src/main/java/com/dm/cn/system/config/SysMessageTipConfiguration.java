package com.dm.cn.system.config;


import com.dm.cn.system.service.ISysMessageTipService;
import com.dm.cn.system.utils.SysMessageTipUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;


/**
 * 国际化配置
 * @author dyy
 */
@Configuration
public class SysMessageTipConfiguration {

    private ISysMessageTipService sysMessageTipService;

    @Autowired(required = false)
    public void setSysMessageTipService(ISysMessageTipService sysMessageTipService) {
        this.sysMessageTipService = sysMessageTipService;
    }

    @PostConstruct
    public void init() {
        if(sysMessageTipService!=null){
            SysMessageTipUtil.init(sysMessageTipService);
        }
    }
}
