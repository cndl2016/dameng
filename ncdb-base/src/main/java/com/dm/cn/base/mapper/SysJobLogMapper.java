package com.dm.cn.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dm.cn.base.entity.server.SysJobLog;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 针对【SYS_JOB_LOG（定时任务调度日志表）】的数据库操作Mapper
 *
 * @author dameng
 * @Entity com.dm.cn.base.entity.server.SysJobLog
 * @date 2024/10/18
 */
public interface SysJobLogMapper extends BaseMapper<SysJobLog> {

    /**
     * 清除7天之前的日志记录
     *
     * @param time     时间点
     */
    void clearExpiredLog(@Param("time") LocalDateTime time);

}