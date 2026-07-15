package com.dm.cn.common.core.enums;

/**
 * JobLogStatusEnum job执行状态
 *
 * @author root
 * @date 2022/11/24
 */
public enum JobLogStatusEnum {
    /**
     * 正常
     */
    SUCCEED("0", "正常"),
    /**
     * 失败
     */
    FAILED("1", "失败");
    private String value;
    private String desc;

    /**
     * 私有构造
     *
     * @param value
     * @param desc
     */
    JobLogStatusEnum(String value, String desc) {
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
