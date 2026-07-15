package com.dm.cn.device.entity.vo;

import com.dm.cn.device.entity.server.Device;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * 设备监控信息视图对象
 *
 * @author dameng
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DeviceMonitorInfoVO extends Device {
    /**
     * CPU数据
     */
    List<List<Object>> cpuData;

    /**
     * 内存数据
     */
    List<List<Object>> memData;

    /**
     * 网络输入数据
     */
    List<List<Object>> inputData;

    /**
     * 网络输出数据
     */
    List<List<Object>> outputData;

    /**
     * CPU数据
     */
    List<List<Object>> cpuLoadOneMinuteData;

    /**
     * CPU数据
     */
    List<List<Object>> cpuLoadFiveMinuteData;

    /**
     * CPU数据
     */
    List<List<Object>> cpuLoadFifteenMinuteData;

    /**
     * 磁盘读IOPS
     */
    Map<String, List<List<Object>>> diskReadIopsData;

    /**
     * 磁盘写IOPS
     */
    Map<String, List<List<Object>>> diskWriteIopsData;

    /**
     * 磁盘读宽带
     */
    Map<String, List<List<Object>>> diskReadKbData;

    /**
     * 磁盘写宽带
     */
    Map<String, List<List<Object>>> diskWriteKbData;

    /**
     * 磁盘读等待
     */
    Map<String, List<List<Object>>> diskReadWaitData;

    /**
     * 磁盘写等待
     */
    Map<String, List<List<Object>>> diskWriteWaitData;
}
