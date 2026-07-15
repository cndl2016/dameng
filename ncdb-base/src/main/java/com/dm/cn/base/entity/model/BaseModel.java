package com.dm.cn.base.entity.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据传输抽象类（未引入代理暂时存放这里）
 *
 * @author Auto-Coder
 * @date 2023-03-21
 */
@Data
public abstract class BaseModel implements Serializable {

    private static final long serialVersionUID = 3713214534641425216L;

    /**
     * 设备ID
     */
    private String id;

    /**
     * 采集配置项ID
     */
    private String gatherId;

    /**
     * 采集数据批次号
     */
    private Long gatherNo;

    /**
     * 监控时间
     */
    private Date monitorTime;

    /**
     * 代理IP
     */
    private String agentIp;

    /**
     * 代理端口
     */
    private Integer port;

    /**
     * 数据采集状态 success/fail
     */
    private Boolean gatherStatus;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 补充说明信息
     */
    private String demo;

    /**
     * 补充说明信息1
     */
    private String demoOne;

    /**
     * 补充说明信息2
     */
    private String demoTwo;

    /**
     * 补充说明信息2
     */
    private String demoThree;


    /**
     * 是否立即结束告警（默认false）
     */
    private Boolean isEndRightNow;

    /**
     * 实例ID
     */
    private Long instanceId;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 实例名称
     */
    private String instanceName;

    /**
     * 告警项
     */
    private String alarmCondition;

    /**
     * 数据库名称
     */
    private String dbName;

    /**
     * 进程号
     */
    private String pid;

    /**
     * 执行id
     */
    private String queryId;
}
