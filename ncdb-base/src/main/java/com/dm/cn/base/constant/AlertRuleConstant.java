package com.dm.cn.base.constant;

/**
 * 告警规则定义表常量类
 *
 * @author Auto-Coder
 * @date 2023-03-03
 */
public interface AlertRuleConstant {

    /**
     * 是否启用
     */
    String STATUS_YES = "yes";

    /**
     * 是否启用
     */
    String STATUS_NO = "no";

    /**
     * 当前监控项值
     */
    String MESSAGE_CUR = "{cur}";

    /**
     * 阈值级别
     */
    String MESSAGE_LEVEL = "{level}";

    /**
     * 阈值
     */
    String MESSAGE_LEVEL_VALUE = "{levelVal}";

    /**
     * 补充信息
     */
    String MESSAGE_DEMO_VALUE = "{demo}";

    /**
     * 补充信息
     */
    String MESSAGE_DEMO_ONE_VALUE = "{demoOne}";

    /**
     * 补充信息
     */
    String MESSAGE_DEMO_TWO_VALUE = "{demoTwo}";

    /**
     * 补充信息
     */
    String MESSAGE_DEMO_THREE_VALUE = "{demoThree}";

}