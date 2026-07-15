package com.dm.cn.device.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 获取设备实例个数vo类
 *
 * @author hzz
 * @date 2024/11/21
 */
@Data
public class DeviceInstanceNumVO implements Serializable {

    /**
     * 类型
     */
    private Integer type;

    /**
     * 总数
     */
    private Integer count;

}