package com.dm.cn.base.service.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.dm.cn.base.entity.server.AlarmMessage;
import com.dm.cn.base.service.AlarmReportService;
import com.dm.cn.common.core.constant.MgConstants;
import com.dm.cn.common.core.exception.ServiceException;
import com.dm.cn.common.utils.DecodeUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 钉钉告警消息通知实现
 *
 * @author danger
 * @date 2023/3/9 下午6:57
 */
@Service("AlarmReportWithDingService")
public class AlarmReportWithDingServiceImpl implements AlarmReportService {

    /**
     * 拿到设置好参数的AlarmMessageDTO　通过http请求发送消息（多个接受对象）
     *
     * @param alarmMessage
     */
    @Override
    @Async
    public void sendAlarmMessageList(AlarmMessage alarmMessage, String recipient) {
        // 是否通知所有人
        boolean isAtAll = true;

        // 通知具体人的手机号码列表
        List<String> mobileList = new ArrayList<>();

        // 钉钉请求地址
        String dingUrl = getDingUrl(alarmMessage.getHttpSign(), alarmMessage.getHttpUrl());
        // 钉钉机器人消息内容
        String content = alarmMessage.getContent();
        // 组装请求内容
        String bodyContent = buildReqBody(content, isAtAll, mobileList);

        String resp = HttpRequest.post(dingUrl)
                .body(bodyContent, "application/json;charset=UTF-8").execute().body();

        if (!resp.contains(MgConstants.DING_TALK_SUCCESS_MSG)) {
            throw new ServiceException(resp);
        }
    }

    /**
     * 组装Ding talk http 地址
     *
     * @param sign    　　　加密后的签名
     * @param httpUrl 　根据机器人生成的http地址解析出携带的token
     * @return　JSONObject
     */
    private String getDingUrl(String sign, String httpUrl) {
        // 解析出url携带的token
        String httpToken = httpUrl.substring(httpUrl.indexOf("access_token=") + 13, httpUrl.length());
        // 如果解析出的token为空，则说明该钉钉地址无效，返回ｅｒｒｏｒ
        if ("".equals(httpToken)) {
            throw new ServiceException(MgConstants.INVALID_HTTP_TOKEN);
        }
        String timestamp = String.valueOf(System.currentTimeMillis());
        String encryptionSign = DecodeUtil.dingHmacSha256(sign, timestamp);
        // 钉钉机器人地址（配置机器人的webhook） https://oapi.dingtalk.com/robot/send?access_token=XXXXXX&timestamp=XXX&sign=XXX
        String dingUrl = "https://oapi.dingtalk.com/robot/send?access_token=" + httpToken + "&timestamp=" + timestamp + "&sign=" + encryptionSign;
        return dingUrl;
    }

    /**
     * 将内容和加密后的签名组装为飞书指定的json格式字符串
     *
     * @param content    内容
     * @param isAtAll    是否通知所有人
     * @param mobileList 通知具体人的手机号码列表
     * @return
     */
    private String buildReqBody(String content, boolean isAtAll, List<String> mobileList) {
        // 消息内容
        Map<String, String> contentMap = new HashMap<>(2);
        contentMap.put("content", content);

        // 通知人
        Map<String, Object> atMap = new HashMap<>(3);
        // 1.是否通知所有人
        atMap.put("isAtAll", isAtAll);
        // 2.通知具体人的手机号码列表
        atMap.put("atMobiles", mobileList);

        Map<String, Object> reqMap = new HashMap<>(4);
        reqMap.put("msgtype", "text");
        reqMap.put("text", contentMap);
        reqMap.put("at", atMap);

        return JSON.toJSONString(reqMap);
    }

}