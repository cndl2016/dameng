package com.dm.cn.common.cache.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;

import java.util.Date;


/**
 * @author Fe
 * @JSONField(serialize=false) 在属性get方法上加serialize=false可设置属性不输出
 * name="" 可设置序列化后的属性名称
 * 试用场景  实体对象往协议层数据转换
 */
public class FastJson {

    public static SerializeConfig mapping = new SerializeConfig();

    public static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    static {
        mapping.put(Date.class, new SimpleDateFormatSerializer(DEFAULT_DATE_FORMAT));
    }

    public static <T> String toJson(T t) {
        return JSON.toJSONString(t, mapping);
    }

    public static JSONObject fromJson(String json) {
        return JSON.parseObject(json);
    }

}

