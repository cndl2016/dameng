package com.dm.cn.base.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dm.cn.base.entity.server.AlarmMessage;
import com.dm.cn.base.service.AlarmReportService;
import com.dm.cn.common.core.constant.MgConstants;
import com.dm.cn.common.core.exception.ServiceException;
import com.dm.cn.common.utils.DecodeUtil;
import com.dm.cn.common.utils.RestTemplateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 飞书告警消息通知实现
 *
 * @author danger
 * @date 2023/3/9 下午6:57
 */
@Service("AlarmReportWithLarkService")
public class AlarmReportWithLarkServiceImpl implements AlarmReportService {

    private static final Logger log = LoggerFactory.getLogger(AlarmReportWithLarkServiceImpl.class);

    /**
     * 拿到设置好参数的AlarmMessageDTO　通过http请求发送消息（多个接受对象）
     *
     * @param alarmMessage
     */
    @Override
    @Async
    public void sendAlarmMessageList(AlarmMessage alarmMessage, String recipient) {
        JSONObject encryptSignObj;
        try {
            // 获取带加密签名的json内容
            encryptSignObj = getEncryptSign(alarmMessage.getHttpSign(), alarmMessage.getContent());
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
        // send post request with http request
        String resp = RestTemplateUtil.sendPost(encryptSignObj, alarmMessage.getHttpUrl());
        // http发送失败，尝试脚本发送
        if (!MgConstants.MG_SUCCESS.equals(resp)) {
            throw new ServiceException(resp);
        }
    }

    /**
     * 获取http验证签名
     *
     * @param sign    　dto签名
     * @param content 　需要发送的告警消息内容
     * @return　JSONObject
     */
    private JSONObject getEncryptSign(String sign, String content) throws NoSuchAlgorithmException, InvalidKeyException {
        int millTime = Integer.parseInt(System.currentTimeMillis() / 1000 + "");
        String encryptionSign = DecodeUtil.encryptSign(sign, millTime);
        String jsonSignStr = signToJsonStr(encryptionSign, content, millTime);

        return JSONObject.parseObject(jsonSignStr);
    }

    /**
     * 将内容和加密后的签名组装为飞书指定的json格式字符串
     *
     * @param sign    dto签名
     * @param content 需要发送的告警消息内容
     * @return
     */
    private String signToJsonStr(String sign, String content, int time) {
        String formatStr = "{\"timestamp\":\"1599360473\",\"sign\":\"abcdefghijkmnopq\",\"msg_type\":\"text\",\"content\":{\"text\":\"request example\"}}";
        JSONObject jsonObject = JSONObject.parseObject(formatStr);
        jsonObject.replace("timestamp", time);
        jsonObject.replace("sign", sign);
        // 将前面格式化的String内容转换为json格式
        JSONObject contentJson = JSONObject.parseObject(jsonObject.getOrDefault("content", "").toString());
        contentJson.replace("text", content);
        jsonObject.replace("content", contentJson);
        // 测试输出结果
        log.info(jsonObject.toString());

        return jsonObject.toString();
    }

}