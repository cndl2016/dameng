package com.dm.cn.common.core.enums;

import java.util.HashMap;

/**
 * UserPropEnum 用户属性
 *
 * @author root
 * @date 2023/05/12
 */
public enum UserPropEnum {
    /**
     * 账号
     */
    USER_NAME_PROP("0", "账号"),
    /**
     * 昵称
     */
    NICK_NAME_PROP("1", "昵称"),
    /**
     * 手机
     */
    PHONE_PROP("2", "手机"),
    /**
     * 邮箱
     */
    MAIL_PROP("3", "邮箱");

    private String value;
    private String desc;


    /**
     * 私有构造
     *
     * @param value
     * @param desc
     */
    UserPropEnum(String value, String desc) {
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

    public static String getDefault(String value) {
        HashMap<String, String> userPropMap = new HashMap(4);
        userPropMap.put(USER_NAME_PROP.value, "userPrincipalName");
        userPropMap.put(NICK_NAME_PROP.value, "cn");
        userPropMap.put(PHONE_PROP.value, null);
        userPropMap.put(MAIL_PROP.value, null);
        return userPropMap.get(value);
    }
}
