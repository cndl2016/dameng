package com.dm.cn.quartz.utils;

import com.dm.cn.common.core.utils.SpringUtils;
import com.dm.cn.quartz.entity.server.SysJob;
import org.quartz.JobExecutionContext;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.dm.cn.quartz.constant.ScheduleConstants.TASK_PARAMS;

/**
 * 执行定时任务的方法
 *
 * @author cn
 */
public class JobExecuteUtils {

    /**
     * 获取bean并执行对应的方法
     *
     * @param jobExecutionContext
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static void executeMethod(JobExecutionContext jobExecutionContext) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        Object param = jobExecutionContext.getMergedJobDataMap().get(TASK_PARAMS);
        SysJob job = new SysJob();
        BeanUtils.copyProperties(param, job);
        Object bean = SpringUtils.getBean(Class.forName(job.getBeanTarget()));
        Method method = bean.getClass().getMethod(job.getBeanMethodTarget(), String.class);
        method.invoke(bean, job.getJobParam());
    }
}
