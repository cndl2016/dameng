package com.dm.cn.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 设备状态枚举
 *
 * @author Dameng
 * @date 2024/10/15
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum DeviceEnum {
    /**
     * 启用
     */
    ENABLE("0", "启用"),

    /**
     * 停用
     */
    DISABLE("1", "停用"),

    /**
     * 正常
     */
    CONN("0", "正常"),

    /**
     * 异常
     */
    DIS_CONN("1", "异常");

    /**
     * 枚举值
     */
    private String value;

    /**
     * 描述
     */
    private String desc;
}