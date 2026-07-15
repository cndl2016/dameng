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
 * 数据采集频率配置表
 *
 * @author dameng
 * @TableName SYS_SOFT_AGENT_CONFIG
 * @date 2025/01/24
 */
@TableName(value = "SYS_SOFT_AGENT_CONFIG")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoftAgentConfig implements Serializable {

    /**
     * 主键
     */
    @TableId(value = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 参数名称
     */
    @TableField(value = "NAME")
    private String name;

    /**
     * 参数编码
     */
    @TableField(value = "CODE")
    private String code;

    /**
     * cron表达式
     */
    @TableField(value = "CRON_EXPRESSION")
    private String cronExpression;

    /**
     * 分组 device
     */
    @TableField(value = "GROUPS")
    private String groups;

    /**
     * 分组中文
     */
    @TableField(exist = false)
    private String groupsName;

    /**
     * 备注
     */
    @TableField(value = "REMARK")
    private String remark;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}