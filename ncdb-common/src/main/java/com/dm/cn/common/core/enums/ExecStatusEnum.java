package com.dm.cn.common.core.enums;

/**
 * ExecStatusEnum 命令行执行结果枚举类
 *
 * @author root
 */

public enum ExecStatusEnum {
    /**
     * 成功
     */
    SUCCEED("0", "成功"),
    /**
     * 错误
     */
    ERROR("1", "错误"),
    /**
     * 失败
     */
    FAILED("2", "失败");
    private String value;
    private String desc;

    /**
     * 私有构造
     *
     * @param value
     * @param desc
     */
    ExecStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * @return {@link String}
     */
    public String getValue() {
        return value;
    }

    /**
     * @return {@link String}
     */
    public String getDesc() {
        return desc;
    }
}
