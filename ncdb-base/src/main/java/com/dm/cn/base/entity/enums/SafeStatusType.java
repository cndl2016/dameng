package com.dm.cn.base.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 安全状态类型枚举
 * @author root
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum SafeStatusType {

    /**
     * 安全
     */
    SAFE("0", "安全"),

    /**
     * 低风险
     */
    LOW_RISK("1", "低风险"),

    /**
     * 高风险
     */
    HIGH_RISK("2", "高风险");


    private String value;
    private String desc;
}
