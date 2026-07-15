package com.dm.cn.common.enums;


import com.dm.cn.common.core.constant.NumberConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 告警方式枚举
 *
 * @author danger
 * @date 2023/3/10 上午9:58
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SendMethodEnum {

    //邮箱
    MAIL("0", "邮箱"),

    //飞书
    LARK("1", "飞书"),

    //钉钉
    DING("2", "钉钉"),

    //企业微信
    WECOM("3", "企业微信"),

    //短信通知
    SMS("4", "短信通知"),

    //微信公众号
    WEOA("5", "微信公众号");

    private String value;

    private String desc;

    /**
     * 根据value值获取描述信息
     *
     * @param value
     * @return
     */
    public static String getDesc(String value) {
        Map<String, String> sendMethodMap = new HashMap(NumberConstants.SIX);
        sendMethodMap.put(MAIL.getValue(), MAIL.getDesc());
        sendMethodMap.put(LARK.getValue(), LARK.getDesc());
        sendMethodMap.put(DING.getValue(), DING.getDesc());
        sendMethodMap.put(WECOM.getValue(), WECOM.getDesc());
        sendMethodMap.put(SMS.getValue(), SMS.getDesc());
        sendMethodMap.put(WEOA.getValue(), WEOA.getDesc());

        return sendMethodMap.get(value);
    }

}