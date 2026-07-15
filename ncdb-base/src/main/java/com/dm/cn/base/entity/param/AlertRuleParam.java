package com.dm.cn.base.entity.param;

import com.dm.cn.common.core.web.domain.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 告警规则项查询参数
 *
 * @author root
 * @date 2025/02/10
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AlertRuleParam extends BaseEntity {

    /**
     * 告警对象id(设备或节点id)
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long objectId;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 告警项名称
     */
    private String ruleName;

    /**
     * 实例类型
     */
    private String instanceType;

    /**
     * 数据库名称
     */
    private String dbName;

}