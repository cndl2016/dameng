package com.dm.cn.common.core.enums;

import java.util.HashMap;

/**
 * @author dameng
 */

public enum VisibleEnum {
    /**
     * 未解除
     */
    VISIBLE("0", "显示"),
    /**
     * 已解除
     */
    INVISIBLE("1", "隐藏");
    private String value;
    private String desc;

    /**
     * 私有构造
     *
     * @param value
     * @param desc
     */
    VisibleEnum(String value, String desc) {
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

    public String getDesc(String value) {
        HashMap<String, String> map = new HashMap(2);
        map.put(VISIBLE.value, VISIBLE.desc);
        map.put(INVISIBLE.value, INVISIBLE.desc);
        return map.get(value);
    }
}
