package com.dm.cn.device.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 设备监控信息vo类
 *
 * @author hzz
 * @date 2024/11/22
 */
@Data
public class DeviceOptionVO implements Serializable {

    /**
     * 设备信息
     */
    String info;

    /**
     * 值
     */
    String value;

    /**
     * 标签·
     */
    String label;

}