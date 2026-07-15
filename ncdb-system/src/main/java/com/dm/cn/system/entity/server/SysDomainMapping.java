package com.dm.cn.system.entity.server;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * 域用户属性映射表
 *
 * @author dameng
 * @TableName SYS_DOMAIN_MAPPING
 */
@TableName(value = "SYS_DOMAIN_MAPPING")
@Data
public class SysDomainMapping implements Serializable {
    /**
     * 主键id
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 域id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long domainId;

    /**
     * manager用户属性
     */
    private String managerUserProp;

    /**
     * ad域用户属性
     */
    private String domainUserProp;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
