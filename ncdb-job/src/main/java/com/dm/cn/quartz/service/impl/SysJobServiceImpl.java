package com.dm.cn.quartz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dm.cn.quartz.entity.server.SysJob;
import com.dm.cn.quartz.mapper.SysJobMapper;
import com.dm.cn.quartz.service.SysJobService;
import com.dm.cn.quartz.utils.ScheduleUtils;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * 定时任务调度服务
 *
 * @author cn
 */
@Service
public class SysJobServiceImpl extends ServiceImpl<SysJobMapper, SysJob>
        implements SysJobService {

    @Resource
    private Scheduler scheduler;

    /**
     * 初始化定时任务
     *
     * @throws SchedulerException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostConstruct
    public void init() throws SchedulerException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        scheduler.clear();
        List<SysJob> list = list();
        for (int i = 0; i < list.size(); i++) {
            SysJob job = list.get(i);
            ScheduleUtils.createScheduleJob(scheduler, job);
        }
    }

    @Override
    public int insertJob(SysJob job) throws SchedulerException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ScheduleUtils.createScheduleJob(scheduler, job);
        return 1;
    }

    @Override
    public int updateJob(SysJob job) throws SchedulerException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        // 判断是否存在
        JobKey jobKey = JobKey.jobKey(job.getJobId().toString(), job.getJobGroup());
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }
        ScheduleUtils.createScheduleJob(scheduler, job);
        return 1;
    }

    @Override
    public int deleteJob(SysJob job) throws SchedulerException {
        scheduler.deleteJob(JobKey.jobKey(job.getJobId().toString(), job.getJobGroup()));
        return 1;
    }
}
