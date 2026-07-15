package com.dm.cn.base.entity.param;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 告警记录查询统计参数
 * @author root
 */
@Data
public class AlarmCountParam {

    /**
     * 实例id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long instanceId;

    /**
     * 告警状态
     */
    private String activeState;
}
