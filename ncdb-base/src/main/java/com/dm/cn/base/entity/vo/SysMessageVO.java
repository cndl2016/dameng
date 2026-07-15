package com.dm.cn.base.entity.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dm.cn.common.core.annotation.Excel;
import com.dm.framework.common.log.Log;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 告警历史vo(首页统计查询用)
 *
 * @author root
 * @date 2024/11/14
 */
@Data
public class SysMessageVO implements Serializable {

    /**
     * 主键
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Log(title = "主键")
    private Long id;

    /**
     * 对象id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Log(title = "对象id")
    private Long objectId;

    /**
     * 实例id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @Log(title = "实例id")
    private Long instanceId;

    /**
     * 实例名称
     */
    @TableField(exist = false)
    private String instanceName;

    /**
     * 节点ip
     */
    @Log(title = "节点ip")
    private String nodeIp;

    /**
     * 节点端口
     */
    @Log(title = "节点端口")
    private Integer nodePort;

    /**
     * 实例类型
     */
    @Log(title = "实例类型")
    private String instanceType;

    /**
     * 告警的设备是否在线
     */
    @Log(title = "告警的设备是否在线")
    private String deviceStatus;

    /**
     * 数据库类型
     */
    @Log(title = "数据库类型")
    private String dbType;


    /**资源名称*/
    @Log(title = "资源名称")
    @Excel(name = "告警对象", width = 40, sort = 1)
    private String resourceName;


    /**规则ID*/
    @Log(title = "规则ID")
    private String ruleId;


    /**消息标题*/
    @Log(title = "消息标题")
    @Excel(name = "告警项", width = 30, sort = 0)
    private String title;

    /**消息标题英文*/
    @Log(title = "消息标题英文")
    private String titleEn;

    /**消息内容*/
    @Log(title = "消息内容")
    @Excel(name = "告警内容", width = 100, sort = 2)
    private String content;

    /**消息内容英文*/
    @Log(title = "消息内容英文")
    private String contentEn;

    /**创建时间*/
    @Log(title = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "告警开始时间", width = 30, sort = 3, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


    /**结束时间*/
    @Log(title = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "告警最近一次更新时间", width = 30, sort = 4, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;


    /**消息级别*/
    @Log(title = "消息级别")
    @Excel(name = "告警级别", sort = 5, readConverterExp = "warning=低风险,fatal=高风险")
    private String msgLevel;


    /**类型*/
    @Log(title = "类型")
    private String msgKind;


    /**设备对象表*/
    @Log(title = "设备对象表")
    private String tableName;


    /**有效状态：有效live,过期无效death,关闭失效killed*/
    @Log(title = "有效状态")
    @Excel(name = "告警状态", sort = 6, readConverterExp = "live=未结束,death=已结束")
    private String activeState;


    /**发送邮件状态*/
    @Log(title = "发送邮件状态")
    private String sendMailStatus;


    /**发送时间*/
    @Log(title = "发送时间")
    private Date sendTime;
}