package com.dm.cn.device.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 获取系统设备个数树结构实体类
 *
 * @author hzz
 * @date 2024/11/21
 */
@Data
public class DeviceInstanceNum implements Serializable {

    /**
     * 设备数量
     */
    private int deviceCount;

    /**
     * 单机模式实例数量
     */
    private int singleInstanceCount;

    /**
     * 主备模式实例数量
     */
    private int sentinelInstanceCount;

    /**
     * 分布式实例数量
     */
    private int clusterInstanceCount;

}