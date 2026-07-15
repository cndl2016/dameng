package com.dm.cn.base.utils;

import com.dm.cn.common.cache.utils.FastJson;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * websocket 发送消息
 *
 * @author dameng
 * @date 2022/12/13
 */
public class WebSocketSendMsgUtil {

    private static final Logger log = LoggerFactory.getLogger(WebSocketSendMsgUtil.class);

    private static Gson gson;

    static {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    /**
     * 发送消息
     *
     * @param result
     * @param session
     */
    public static void sendMessage(Map<String, Object> result, Session session) {
        try {
            session.getBasicRemote().sendText(toJson(result));
        } catch (IOException e) {
            log.error("sendMessage websocket error：{}", e);
        }
    }

    /**
     * 发送消息
     *
     * @param code
     * @param msg
     * @param data
     * @param session
     */
    public static void sendMessage(int code, String msg, Object data, Session session) {
        try {
            Map<String, Object> resultMap = new HashMap<>(3);
            resultMap.put("code", code);
            resultMap.put("msg", msg);
            resultMap.put("data", data);
            // bean中日期类型使用@JSONFormat, 需要使用fastJson进行转换
            session.getBasicRemote().sendText(FastJson.toJson(resultMap));
        } catch (IOException e) {
            log.error("sendMessage error：{}", e);
        }
    }

    /**
     * Object 转成 json
     *
     * @param src
     * @return String
     */
    public static String toJson(Object src) {
        return gson.toJson(src);
    }
}