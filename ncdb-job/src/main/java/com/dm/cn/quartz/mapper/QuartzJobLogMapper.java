package com.dm.cn.quartz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dm.cn.quartz.entity.param.JobLogParam;
import com.dm.cn.quartz.entity.server.QuartzJobLog;

import java.util.List;

/**
* @author dameng
* @description 针对表【SYS_QUARTZ_JOB_LOG(定时任务日志表)】的数据库操作Mapper
* @createDate 2024-05-14 14:38:33
* @Entity com.dm.cn.quartz.domain.QuartzJobLog
*/
public interface QuartzJobLogMapper extends BaseMapper<QuartzJobLog> {
}




