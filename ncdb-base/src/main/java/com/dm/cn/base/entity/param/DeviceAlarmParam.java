package com.dm.cn.base.entity.param;

import com.dm.cn.base.entity.vo.AlertConfigVO;
import com.dm.cn.device.entity.server.Device;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

/**
 * 批量新增设备参数
 * @author root
 */
@Data
public class DeviceAlarmParam {

    /**
     * 设备机房ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long nodeId;

    /**
     * 设备列表集和
     */
    private List<Device> deviceList;

    /**
     * 告警规则集和
     */
    private List<AlertConfigVO> alarmRuleList;
}
