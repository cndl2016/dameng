package com.dm.cn.common.param;


import com.dm.cn.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备监控列表查询参数
 *
 * @author DAMENG
 * @date 2023/04/11
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DeviceMonitorParam extends BaseEntity {

    /**
     * 设备ID
     */
    private Long deviceId;

    /**
     * 设备IP
     */
    private String deviceIp;

    /**
     * 所属区域ID
     */
    private Long nodeId;

    /**
     * 监控内容
     */
    private String monitorType;
}