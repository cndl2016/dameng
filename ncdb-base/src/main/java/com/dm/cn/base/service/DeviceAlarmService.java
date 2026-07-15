package com.dm.cn.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dm.cn.base.entity.param.DeviceAlarmParam;
import com.dm.cn.common.param.DeviceParam;
import com.dm.cn.device.entity.server.Device;

/**
 * 批量新增设备以及告警规则Service
 * @author root
 */
public interface DeviceAlarmService {

    /**
     * 新增设备及其告警规则
     * @param param
     * @return
     */
    boolean addDeviceAndAlarm(DeviceAlarmParam param);

    /**
     * 查询设备列表
     *
     * @param device 设备查询参数
     * @return {@link IPage}<{@link Device}>
     */
    IPage<Device> getListDevice(DeviceParam device);
}
