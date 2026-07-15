package com.dm.cn.base.entity.server;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 告警项通知方式配置表
 *
 * @author dameng
 * @TableName T_ALERT_NOTICE_CONFIG
 * @date 2025/02/11
 */
@TableName(value = "T_ALERT_NOTICE_CONFIG")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertNoticeConfig implements Serializable {

    /**
     * 主键
     */
    @TableId(value = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 对象ID(设备或节点ID)
     */
    @TableField(value = "OBJECT_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long objectId;

    /**
     * 告警规则ID
     */
    @TableField(value = "ALERT_RULE_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long alertRuleId;

    /**
     * 告警配置ID
     */
    @TableField(value = "ALERT_CONFIG_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long alertConfigId;

    /**
     * 告警通知方式ID
     */
    @TableField(value = "ALARM_MESSAGE_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long alarmMessageId;

    /**
     * 接受人(多个用逗号隔开)
     */
    @TableField(value = "RECIPIENT")
    private String recipient;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}