package com.dm.cn.common.enums;

/**
 * rest http 相应状态 枚举类
 *
 * @author DAMENG
 * @date 2022/09/07
 */
public enum RestStatusEnum {
    // 枚举定义的常量对象必须在最前面
    SUCCESS(200, "Success");

    private int value;
    private String desc;

    /**
     * 私有构造
     *
     * @param value
     * @param desc
     */
    private RestStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * @return {@link String}
     */
    public int getValue() {
        return value;
    }

    /**
     * @return {@link String}
     */
    public String getDesc() {
        return desc;
    }

}