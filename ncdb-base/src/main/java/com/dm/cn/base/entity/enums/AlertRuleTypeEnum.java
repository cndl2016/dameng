package com.dm.cn.base.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author root
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum AlertRuleTypeEnum {
    /**
     * 安全
     */
    DEVICE("0", "设备"),

    /**
     * 低风险
     */
    INSTANCE("1", "实例"),

    /**
     * 高风险
     */
    DB("2", "数据库");


    private String value;
    private String desc;
}
