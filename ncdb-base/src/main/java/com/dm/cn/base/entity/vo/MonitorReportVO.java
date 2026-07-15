package com.dm.cn.base.entity.vo;


import lombok.Data;

import java.util.List;

/**
 * 监控运行报告查询实体类
 *
 * @author wys 2024/09/24
 * @date 2024/11/06
 */
@Data
public class MonitorReportVO {
    /**
     * 月份
     */
    private Integer month;

    /**
     * 监控值
     */
    private Float monitorValue;

    /**
     * 最大值
     */
    private Float maxValue;

    /**
     * 每日监控数据
     */
    private List<List<Object>> monitorValueList;

    /**
     * 每日峰值数据
     */
    private List<List<Object>> maxValueList;
}