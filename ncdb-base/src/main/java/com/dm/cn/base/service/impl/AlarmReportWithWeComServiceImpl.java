package com.dm.cn.base.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dm.cn.base.entity.server.AlarmMessage;
import com.dm.cn.base.service.AlarmReportService;
import com.dm.cn.common.core.constant.MgConstants;
import com.dm.cn.common.core.exception.ServiceException;
import com.dm.cn.common.utils.RestTemplateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * 企业微信告警消息通知实现
 *
 * @author danger
 * @date 2023/3/9 下午6:57
 */
@Service("AlarmReportWithWeComService")
public class AlarmReportWithWeComServiceImpl implements AlarmReportService {

    private static final Logger log = LoggerFactory.getLogger(AlarmReportWithWeComServiceImpl.class);

    /**
     * 拿到设置好参数的AlarmMessageDTO　通过http请求发送消息（多个接受对象）
     *
     * @param alarmMessage
     */
    @Override
    @Async
    public void sendAlarmMessageList(AlarmMessage alarmMessage, String recipient) {
        // 获取带加密签名的ｊｓｏｎ内容
        JSONObject formatContent = getFormatContent(alarmMessage.getContent());
        // send post request with http request
        String resp = RestTemplateUtil.sendPost(formatContent, alarmMessage.getHttpUrl());
        // http发送失败，尝试脚本发送
        if (!MgConstants.MG_SUCCESS.equals(resp)) {
            throw new ServiceException(resp);
        }
    }

    /**
     * 将内容组装为企业微信指定的json格式字符串
     *
     * @param content
     * @return
     */
    private JSONObject getFormatContent(String content) {
        String formatStr = "{\"at\":{\"isAtAll\":true,\"atMobiles\":[]},\"msgtype\":\"text\",\"text\":{\"content\":\"request example\"}}";
        JSONObject jsonObject = JSONObject.parseObject(formatStr);
        // 将前面格式化的String内容转换为json格式
        JSONObject textJson = JSONObject.parseObject(jsonObject.getOrDefault("text", "").toString());
        textJson.replace("content", content);
        jsonObject.replace("text", textJson);

        // at phoneNumber
        ArrayList<String> phoneNumberList = new ArrayList<>();
        String jsonStr = JSONObject.toJSONString(phoneNumberList);
        JSONObject jsonPhoneNumber = JSONObject.parseObject(jsonObject.getOrDefault("at", "isAtAll:true,atMobiles:[]").toString());
        jsonPhoneNumber.replace("atMobiles", jsonStr);
        jsonObject.replace("at", jsonPhoneNumber);

        // 测试输出结果
        log.info(jsonObject.toString());

        return jsonObject;
    }

}