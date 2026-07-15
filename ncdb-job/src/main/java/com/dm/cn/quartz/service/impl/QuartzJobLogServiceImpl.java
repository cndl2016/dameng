package com.dm.cn.quartz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dm.cn.quartz.entity.server.QuartzJobLog;
import com.dm.cn.quartz.mapper.QuartzJobLogMapper;
import com.dm.cn.quartz.service.QuartzJobLogService;
import org.springframework.stereotype.Service;

/**
* @author dameng
* @description 针对表【SYS_QUARTZ_JOB_LOG(定时任务日志表)】的数据库操作Service实现
* @createDate 2024-05-14 14:38:33
*/
@Service
public class QuartzJobLogServiceImpl extends ServiceImpl<QuartzJobLogMapper, QuartzJobLog>
    implements QuartzJobLogService{
}




