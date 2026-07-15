package com.dm.cn.base.entity.server;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * 告警规则配置表实体对象
 *
 * @author Auto-Coder
 * @date 2023-03-16
 */
@Data
@TableName("T_ALERT_CONFIG")
public class AlertConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**主键*/
    @TableId(value = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;


    /**告警规则定义ID*/
    @TableField(value = "RULE_ID")
    private Long ruleId;


    /**告警名称*/
    @TableField(value = "RULE_NAME")
    private String ruleName;


    /**规则编码*/
    @TableField(value = "RULE_CODE")
    private String ruleCode;


    /**规则描述*/
    @TableField(value = "RULE_DESC")
    private String ruleDesc;

    /**分组*/
    @TableField(value = "RULE_GROUPS")
    private String ruleGroups;


    /**告警对象id*/
    @TableField(value = "OBJECT_ID")
    private Long objectId;


    /**参数JSON*/
    @TableField(value = "PARAMETERS")
    private String parameters;


    /**启用状态*/
    @TableField(value = "STATUS")
    private String status;


    /**告警对象表*/
    @TableField(value = "TABLE_NAME")
    private String tableName;


    /**告警处理过程类*/
    @TableField(value = "PROCESSOR")
    private String processor;


    /**备注*/
    @TableField(value = "MEMO")
    private String memo;

    /**
     * 是否为默认告警规则
     */
    @TableField(exist = false)
    private Boolean isDefaultRule;

    /**数据库名称*/
    @TableField(value = "DB_NAME")
    private String dbName;

    /**规则类型*/
    @TableField(value = "RULE_TYPE")
    private String ruleType;
}

