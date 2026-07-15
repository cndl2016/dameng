package com.dm.cn.base.service.job;

import com.dm.cn.base.service.SysJobLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 监控日志清除任务
 *
 * @author root
 * @date 2024/11/28
 */
@Component
public class ClearLogJob {

    private static final Logger logger = LoggerFactory.getLogger(ClearLogJob.class);

    @Resource
    private SysJobLogService sysJobLogService;

    /**
     * 监控日志清除任务入口(定时任务调用，方法名称为灰并不是未使用)
     *
     * @param jobParam 任务参数(参数未使用，JobExecuteUtils执行定时任务统一传入了参数)
     */
    public void clearLogJob(String jobParam) {
        logger.info("清除设备+监控记录");
        sysJobLogService.clearExpiredLog();
    }

}