package com.dm.cn.quartz.constant;


/**
 * 常量
 *
 * @author cn
 */
public class ScheduleConstants {
    /**
     * 参数
     */
    public static final String TASK_PARAMS = "PARAMS";

    /**
     * 默认
     */
    public static final String MISFIRE_DEFAULT = "0";

    /**
     * 忽略错过的执行,按新 Cron 继续运行。
     */
    public static final String MISFIRE_IGNORE_MISFIRES = "1";

    /**
     * 补偿错过的执行,然后继续运行。
     */
    public static final String MISFIRE_FIRE_AND_PROCEED = "2";

    /**
     * 错过的执行不做任何处理,等待下一次 Cron 触发。
     */
    public static final String MISFIRE_DO_NOTHING = "3";

    /**
     * 允许并发执行
     */
    public static final String CONCURRENT_ENABLED = "0";

    /**
     * 不允许并发执行
     */
    public static final String CONCURRENT_DISABLED = "1";
}
