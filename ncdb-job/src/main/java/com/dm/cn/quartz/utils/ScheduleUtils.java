package com.dm.cn.quartz.utils;

import com.dm.cn.quartz.constant.ScheduleConstants;
import com.dm.cn.quartz.entity.server.QuartzDisallowConcurrentExecution;
import com.dm.cn.quartz.entity.server.QuartzJobExecution;
import com.dm.cn.quartz.entity.server.SysJob;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

import static com.dm.cn.quartz.constant.ScheduleConstants.TASK_PARAMS;

/**
 * 定时任务工具类
 * @author cn
 */
@Component
public class ScheduleUtils {

    /**
     * 得到quartz任务类
     *
     * @param sysJob 执行计划
     * @return 具体执行任务类
     */
    private static Class<? extends Job> getQuartzJobClass(SysJob sysJob)
    {
        boolean isConcurrent = ScheduleConstants.CONCURRENT_ENABLED.equals(sysJob.getConcurrent());
        return isConcurrent ? QuartzJobExecution.class : QuartzDisallowConcurrentExecution.class;
    }

    /**
     * 创建定时任务
     */
    public static void createScheduleJob(Scheduler scheduler, SysJob job) throws SchedulerException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        //获取任务类型
        Class<? extends Job> jobClass = getQuartzJobClass(job);

        // 构建job信息
        String cornExpression = job.getCronExpression();
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(job.getJobId().toString(),job.getJobGroup()).build();

        // 表达式调度构建器
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cornExpression);

        //配置执行策略
        cronScheduleBuilder = handleCronScheduleMisfirePolicy(job,cronScheduleBuilder);

        // 按新的cronExpression表达式构建一个新的trigger
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(job.getJobId().toString(),job.getJobGroup())
                .withSchedule(cronScheduleBuilder).build();

        // 放入参数，运行时的方法可以获取
        jobDetail.getJobDataMap().put(TASK_PARAMS, job);

        // 执行调度任务
        scheduler.scheduleJob(jobDetail, trigger);

    }

    /**
     * 设置定时任务策略
     */
    public static CronScheduleBuilder handleCronScheduleMisfirePolicy(SysJob job, CronScheduleBuilder cb) {

        switch (job.getMisfirePolicy())
        {
            case ScheduleConstants.MISFIRE_DEFAULT:
                return cb;
            case ScheduleConstants.MISFIRE_IGNORE_MISFIRES:
                return cb.withMisfireHandlingInstructionIgnoreMisfires();
            case ScheduleConstants.MISFIRE_FIRE_AND_PROCEED:
                return cb.withMisfireHandlingInstructionFireAndProceed();
            case ScheduleConstants.MISFIRE_DO_NOTHING:
                return cb.withMisfireHandlingInstructionDoNothing();
            default:
                throw new RuntimeException("策略异常");
        }
    }

}
