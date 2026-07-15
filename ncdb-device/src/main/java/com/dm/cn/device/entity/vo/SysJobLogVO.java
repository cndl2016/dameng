package com.dm.cn.device.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 首页统计查询sys_job_log数据vo类(device无法依赖base)
 *
 * @author dameng
 * @date 2024/11/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysJobLogVO implements Serializable {

    /**
     * 任务日志ID
     */
    private Long jobLogId;

    /**
     * 日志信息
     */
    private String jobMessage;

    /**
     * 监控实际值(百分比、KB)
     */
    private Float monitorValues;

    /**
     * 告警项（3 设备 内存使用率， 4 设备 CPU使用率， 5 设备 磁盘使用率）
     */
    private String alarmCondition;

    /**
     * 创建时间
     */
    private Date createTime;

}