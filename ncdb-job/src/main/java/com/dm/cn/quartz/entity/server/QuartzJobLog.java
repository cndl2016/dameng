package com.dm.cn.quartz.entity.server;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dm.cn.common.core.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 定时任务日志表
 *
 * @author cn
 * @TableName SYS_QUARTZ_JOB_LOG
 * @date 2024/05/15
 */
@TableName(value ="SYS_QUARTZ_JOB_LOG")
@Data
public class QuartzJobLog implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "LOG_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @Excel(name = "LOG_ID", cellType = Excel.ColumnType.NUMERIC)
    private Long logId;

    /**
     * 任务名称
     */
    @Excel(name = "任务名称", width = 20)
    private String jobName;

    /**
     * 触发器名称
     */
    private String trigName;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 执行情况
     */
    @Excel(name = "执行情况", width = 120)
    private String content;

    /**
     * 状态
     */
    @Excel(name = "状态", readConverterExp = "0=失败,1=成功")
    private String status;

    /**
     * 持续时间
     */
    @Excel(name = "持续时间", width = 30)
    private Long duration;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}