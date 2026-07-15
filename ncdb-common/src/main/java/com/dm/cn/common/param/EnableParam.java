package com.dm.cn.common.param;

import com.dm.cn.common.core.web.domain.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 启停用参数
 *
 * @author DAMENG
 * @date 2022/06/17
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class EnableParam extends BaseEntity {

    /**
     * 设备id
     */
    private long id;

    /**
     * 设备状态
     */
    private String status;

    /**
     * 监控对象类型 instance/device
     */
    private String alertType;

    /**
     * 已选择告警对象id(设备或节点id)
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long selectObjectId;

}