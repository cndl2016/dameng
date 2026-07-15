package com.dm.cn.quartz.entity.server;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;

import java.lang.reflect.InvocationTargetException;

import static com.dm.cn.quartz.utils.JobExecuteUtils.executeMethod;


/**
 * 定时任务处理（禁止并发执行）
 * @author cn
 */
@DisallowConcurrentExecution
public class QuartzDisallowConcurrentExecution extends AbstractQuartzJob
{
    @Override
    protected void doExecute(JobExecutionContext jobExecutionContext) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        executeMethod(jobExecutionContext);
    }
}
