package com.dm.cn.common.enums;

/**
 * 通用枚举类
 *
 * @author DAMENG
 * @date 2022/10/23
 */
public enum CommonEnum {
    // 枚举定义的常量对象必须在最前面
    SUCCESS("0", "成功"),

    ERROR("1", "失败");

    private String value;

    private String desc;

    /**
     * 私有构造
     *
     * @param value
     * @param desc
     */
    CommonEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return "NodeTypeEnum{" +
                "value='" + value + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }

}