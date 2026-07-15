package com.dm.cn.device.service;

import com.dm.cn.common.config.InfoConfig;
import com.dm.cn.common.config.NcdbApiConfig;
import com.dm.cn.common.core.constant.CommandConstants;
import com.dm.cn.common.core.constant.SymbolConstants;
import com.dm.cn.common.security.utils.SecurityUtils;
import com.dm.cn.common.utils.CmdUtil;
import com.dm.cn.common.utils.ThreadPoolManager;
import com.dm.cn.device.entity.server.Device;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

/**
 * 设备服务初始化逻辑(项目启动完成时执行)
 *
 * @author root
 * @date 2024/03/05
 */
@Configuration
public class DeviceInitService implements ApplicationListener<ApplicationReadyEvent> {

    @Resource
    private InfoConfig infoConfig;

    @Resource
    private NcdbApiConfig config;

    @Resource
    private DeviceService deviceService;

    @Resource
    private SshdService sshdService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

    }
}
