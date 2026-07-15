package com.dm.cn.common.core.enums;

/**
 * 慢日志执行类型枚举类
 *
 * @author DAMENG
 * @date 2022/08/04
 */
public enum ExecTypeEnum {
    // 全部类型命令
    ALL_TYPE("0", "全部语句类型"),
    // SET类型命令
    SET("1", "SET"),
    // GET类型命令
    GET("2", "GET"),
    // DEL类型命令
    DEL("3", "DEL"),
    // INCR类型命令
    INCR("4", "INCR"),
    // INCRBY类型命令
    INCR_BY("5", "INCRBY"),
    // INCRBYFLOAT类型命令
    INCR_BY_FLOAT("6", "INCRBYFLOAT"),
    // DECR类型命令
    DECR("7", "DECR"),
    // DECRBY类型命令
    DECR_BY("8", "DECRBY"),
    // GETSET类型命令
    GET_SET("9", "GETSET"),
    // APPEND类型命令
    APPEND("10", "APPEND"),
    // MGET类型命令
    MGET("11", "MGET"),
    // 其他类型命令
    OTHER_TYPE("20", "其他语句类型");

    private String value;
    private String desc;

    /**
     * 私有构造
     *
     * @param value
     * @param desc
     */
    ExecTypeEnum(String value, String desc) {
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

    /**
     * @return {@link String}
     */
    @Override
    public String toString() {
        return "ExecTypeEnum{" +
                "value='" + value + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
