package com.dm.cn.common.core.enums;

/**
 * @author dameng
 * @date 2023/05/16
 */
public enum SyncTaskEnum {
    /**
     * 正常
     */
    ENABLED("1", "开启"),
    /**
     * 失败
     */
    DISABLED("0", "关闭");
    private String value;
    private String desc;

    /**
     * 私有构造
     *
     * @param value
     * @param desc
     */
    SyncTaskEnum(String value, String desc) {
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
