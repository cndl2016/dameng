package com.dm.cn.device.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 设备监控类型枚
 *
 * @author hzz
 * @date 2024/11/22
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum DeviceMonitorEnum {

    /**
     * 上行
     */
    UPSTREAM("0", "上行"),
    /**
     * 下行
     */
    DOWNSTREAM("1", "下行"),
    /**
     * CPU
     */
    CPU("2", "CPU"),
    /**
     * 内存
     */
    MEMORY("3", "内存"),
    /**
     * 磁盘
     */
    DISK("4", "磁盘");

    /**
     * 枚举值
     */
    private String value;

    /**
     * 描述
     */
    private String desc;

}