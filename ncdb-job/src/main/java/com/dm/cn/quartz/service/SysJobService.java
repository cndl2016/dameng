package com.dm.cn.quartz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dm.cn.quartz.entity.server.SysJob;
import org.quartz.SchedulerException;

import java.lang.reflect.InvocationTargetException;

/**
 * 定时任务调度信息
 * @author cn
 */
public interface SysJobService extends IService<SysJob> {

    /**
     * 新增任务
     *
     * @param job 调度信息
     * @return 结果
     * @throws SchedulerException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     */
    int insertJob(SysJob job) throws SchedulerException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;

    /**
     * 更新任务
     *
     * @param job 调度信息
     * @return 结果
     * @throws SchedulerException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     */
    int updateJob(SysJob job) throws SchedulerException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;

    /**
     * 删除任务后，所对应的trigger也将被删除
     *
     * @param job 调度信息
     * @return 结果
     * @throws SchedulerException
     */
    int deleteJob(SysJob job) throws SchedulerException;

}
