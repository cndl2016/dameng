package com.dm.cn.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 监控状态枚举
 *
 * @author Dameng
 * @date 2022/11/28
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum MonitorStatusEnum {
    /**
     * 未启用
     */
    DISABLE("0", "未启用"),
    /**
     * 运行中
     */
    ENABLE("1", "运行中"),
    /**
     * 异常
     */
    ERROR("2", "异常");

    /**
     * 枚举值
     */
    private String value;

    /**
     * 描述
     */
    private String desc;
}
