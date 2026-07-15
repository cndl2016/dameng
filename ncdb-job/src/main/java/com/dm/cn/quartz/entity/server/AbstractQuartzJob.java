package com.dm.cn.quartz.entity.server;

import com.dm.cn.common.core.constant.NumberConstants;
import com.dm.cn.quartz.constant.ScheduleConstants;
import com.dm.cn.quartz.service.QuartzJobLogService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

/**
 * 抽象quartz调用
 * 这里我采用了设计模式中的模板方法模式
 *
 * @author cn
 */
@Slf4j
public abstract class AbstractQuartzJob implements Job {
    public static final String STATUS_SUCCESS = "1";
    public static final String STATUS_FAIL = "0";

    @Resource
    private QuartzJobLogService logService;

    @Override
    public void execute(JobExecutionContext context) {
        // 开始时间
        long startTimeMillis = System.currentTimeMillis();
        Date startTime = new Date(System.currentTimeMillis() / NumberConstants.ONE_THOUSAND * NumberConstants.ONE_THOUSAND);
        String jobName = context.getJobDetail().getKey().getName();
        String trigName = context.getTrigger().getKey().getName();
        try {
            // 执行业务逻辑
            doExecute(context);
            // 获取执行日志
            String runMsg = context.getJobDetail().getJobDataMap().get(ScheduleConstants.TASK_PARAMS).toString();
            // 结束时间
            long endTimeMillis = System.currentTimeMillis();
            Date endTime = new Date(System.currentTimeMillis() / NumberConstants.ONE_THOUSAND * NumberConstants.ONE_THOUSAND);
            // 计算执行时长
            long duration = (endTimeMillis - startTimeMillis);
            // 记录日志
            addLog(jobName, trigName, startTime, endTime, duration, "任务执行成功!\r\n" + runMsg, STATUS_SUCCESS);
        } catch (Exception ex) {
            // 结束时间
            long endTimeMillis = System.currentTimeMillis();
            Date endTime = new Date(System.currentTimeMillis() / NumberConstants.ONE_THOUSAND * NumberConstants.ONE_THOUSAND);
            // 计算执行时长
            long duration = (endTimeMillis - startTimeMillis);
            // 记录日志
            addLog(jobName, trigName, startTime, endTime, duration, "任务执行失败!\r\n" + ex.getMessage(), STATUS_FAIL);
            log.error("执行任务出错:", ex);
        }
    }

    /**
     * 执行
     *
     * @param jobExecutionContext
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    protected abstract void doExecute(JobExecutionContext jobExecutionContext) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException;


    private void addLog(String jobName, String trigName, Date strStartTime, Date strEndTime, long duration, String content, String status) {
        QuartzJobLog jobLog = new QuartzJobLog();
        jobLog.setJobName(jobName);
        jobLog.setTrigName(trigName);
        jobLog.setStartTime(strStartTime);
        jobLog.setEndTime(strEndTime);
        jobLog.setDuration(duration);
        jobLog.setContent(content);
        jobLog.setStatus(status);
        logService.save(jobLog);
    }
}
