package com.dm.cn.base.entity.server;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * 告警规则定义表实体对象
 *
 * @author Auto-Coder
 * @date 2023-03-03
 */
@Data
@TableName("T_ALERT_RULE")
public class AlertRule implements Serializable {

    private static final long serialVersionUID = 1L;

    /**主键*/
    @TableId(value = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;


    /**告警名称*/
    @TableField(value = "NAME")
    private String name;


    /**规则编码*/
    @TableField(value = "CODE")
    private String code;


    /**规则描述*/
    @TableField(value = "RULE_DESC")
    private String ruleDesc;


    /**备注*/
    @TableField(value = "MEMO")
    private String memo;


    /**告警处理过程类*/
    @TableField(value = "PROCESSOR")
    private String processor;


    /**告警参数JSON*/
    @TableField(value = "PARAMETERS")
    private String parameters;


    /**分组*/
    @TableField(value = "GROUPS")
    private String groups;


    /**所属对象表*/
    @TableField(value = "TABLE_NAME")
    private String tableName;

    /**默认启用状态*/
    @TableField(value = "STATUS")
    private String status;

    /**规则类型*/
    @TableField(value = "RULE_TYPE")
    private String ruleType;

}

