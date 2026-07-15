package com.dm.cn.quartz.entity.server;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 定时任务调度表 sys_job
 *
 * @author cn
 */
@TableName(value = "SYS_JOB")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysJob implements Serializable {

    /**
     * 定时任务ID
     */
    @TableId(value = "JOB_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long jobId;


    /**
     * 定时任务名称
     */
    @TableField(value = "JOB_NAME")
    private String jobName;

    /**
     * 定时任务组
     */
    @TableField(value = "JOB_GROUP")
    private String jobGroup;

    /**
     * 任务参数
     */
    @TableField(value = "JOB_PARAM")
    private String jobParam;

    /**
     * 目标bean名
     */
    @TableField(value = "BEAN_TARGET")
    private String beanTarget;

    /**
     * 目标bean的方法名
     */
    @TableField(value = "BEAN_METHOD_TARGET")
    private String beanMethodTarget;

    /**
     * 执行表达式
     */
    @TableField(value = "CRON_EXPRESSION")
    private String cronExpression;

    /**
     * 是否并发
     * 0代表允许并发执行
     * 1代表不允许并发执行
     */
    @TableField(value = "CONCURRENT")
    private String concurrent;

    /**
     * 计划策略
     */
    @TableField(value = "MISFIRE_POLICY")
    private String misfirePolicy;

    @TableField(value = "CREATE_USER")
    private String createUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "CREATE_TIME")
    private Date createTime;

    @TableField(value = "UPDATE_USER")
    private String updateUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "UPDATE_TIME")
    private Date updateTime;

    @TableField(value = "REMARK")
    private String remark;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private String jobStatus;
}
