package com.dm.cn.base.entity.server;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 定时任务调度日志表
 *
 * @author dameng
 * @TableName SYS_JOB_LOG
 * @date 2024/10/18
 */
@TableName(value = "SYS_JOB_LOG")
@Data
public class SysJobLog implements Serializable {
    /**
     * 任务日志ID
     */
    @TableId
    private Long jobLogId;

    /**
     * 日志信息
     */
    private String jobMessage;

    /**
     * 执行状态（0正常 1失败）
     */
    private String status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 监控实际值(百分比、KB)
     */
    private float monitorValues;

    /**
     * 告警项（3 设备 内存使用率， 4 设备 CPU使用率， 5 设备 磁盘使用率）
     */
    private String alarmCondition;

    /**
     * 告警值
     */
    private float alarmValues;

    /**
     * 对象id（设备或节点）
     */
    private Long objectId;

    /**
     * 对象细分指标（如磁盘告警，存在多种磁盘，借助此字段分类）
     */
    private String objectItem;

    /**
     * 告警级别
     */
    @TableField(exist = false)
    private String alarmLevel;


    /**
     * 节点名称
     */
    @TableField(exist = false)
    private String nodeName;

    /**
     * 节点类型
     */
    @TableField(exist = false)
    private String nodeType;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 对象名称
     */
    private String objectName;
}