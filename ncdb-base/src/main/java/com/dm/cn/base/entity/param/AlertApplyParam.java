package com.dm.cn.base.entity.param;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

/**
 * 告警规则应用入参
 *
 * @author dameng
 */
@Data
public class AlertApplyParam {

    /**
     * 已选择告警对象id(设备或节点id)
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long selectObjectId;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 应用的设备或节点id集合
     */
    private List<Long> objectIdList;

    /**
     * 监控对象类型 instance/device
     */
    private String alertType;

}