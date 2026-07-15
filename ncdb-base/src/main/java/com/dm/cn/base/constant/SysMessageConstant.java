package com.dm.cn.base.constant;

/**
 * 系统消息、监控告警表常量类
 *
 * @author Auto-Coder
 * @date 2023-03-02
 */
public interface SysMessageConstant {

    /**
     * 消息类别
     */
    String KIND_ALERT = "alert";
    String KIND_OTHER = "other";
    String KIND_SITUATION = "situation";

    /**
     * 消息级别
     * KIND_ALERT 消息级别 alert：fatal、warning
     * KIND_SITUATION 消息级别 situation：max、avg
     */
    String LEVEL_NORMAL = "normal";
    String LEVEL_WARNING = "warning";
    String LEVEL_FATAL = "fatal";
    String LEVEL_MAX = "max";
    String LEVEL_AVG = "avg";

    /**
     * 告警对象表
     */
    String TABLE_DEVICE = "t_device";

    /**
     * 有效状态
     */
    String STATUS_LIVE = "live";
    String STATUS_DEATH = "death";
    String STATUS_KILLED = "killed";
}