package com.dm.cn.base.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dm.cn.base.entity.server.AlarmMessage;
import com.dm.cn.base.service.AlarmReportService;
import com.dm.cn.common.core.exception.ServiceException;
import com.dm.cn.common.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 微信公众号告警消息通知实现
 *
 * @author danger
 * @date 2023/7/18 下午3:04
 */
@Service("AlarmReportWithWeOaService")
public class AlarmReportWithWeOaServiceImpl implements AlarmReportService {

    private static final Logger log = LoggerFactory.getLogger(AlarmReportWithWeOaServiceImpl.class);

    private SecureRandom random = new SecureRandom();

    /**
     * 拿到设置好参数的AlarmMessageDTO　通过短信发送消息（多个接受对象）
     *
     * @param alarmMessage
     */
    @Override
    @Async
    public void sendAlarmMessageList(AlarmMessage alarmMessage, String recipient) {
        // 校验发送对象
        if (ObjectUtils.isEmpty(recipient)) {
            throw new ServiceException("短信发送对象为空");
        }
        String content = alarmMessage.getContent();
        // 客户测试地址 http://32.113.35.38:8001/ESBService/COMMON/JsonProxyService
        String url = alarmMessage.getHttpUrl();
        String serviceCd = alarmMessage.getServiceCd();
        String clientCd = alarmMessage.getClientCd();
        String json = createJson(recipient, content, serviceCd, clientCd);
        log.info("微信公众号告警通知json:" + json);
        // 调用接口通知
        doPostNew(url, json);
    }

    /**
     * 创建请求json参数
     *
     * @param telNumber 电话号码
     * @param title     标题
     * @param serviceCd 服务编码
     * @param clientCd  请求方系统号
     * @return {@link String}
     */
    private String createJson(String telNumber, String title, String serviceCd, String clientCd) {
        JSONObject json = new JSONObject();
        JSONObject transaction = new JSONObject();
        // 日期时间信息
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss:SSS");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat timeFormat2 = new SimpleDateFormat("HHmmss");
        // 生成消息id
        String msgId = createMsgId(clientCd);
        JSONObject header = new JSONObject();
        // 填充消息头信息
        JSONObject sysHeader = new JSONObject();
        sysHeader.put("msgId", msgId);
        sysHeader.put("msgDate", dateFormat.format(date));
        sysHeader.put("msgTime", timeFormat.format(date));
        sysHeader.put("serviceCd", serviceCd);
        sysHeader.put("operation", "InfoPush");
        sysHeader.put("clientCd", clientCd);
        sysHeader.put("serverCd", "336");
        sysHeader.put("bizId", "");
        sysHeader.put("bizType", "");
        sysHeader.put("orgCode", "");
        sysHeader.put("resCode", "");
        sysHeader.put("resText", "");
        sysHeader.put("bizResCode", "");
        sysHeader.put("bizResText", "");
        sysHeader.put("ver", "");
        sysHeader.put("authId", "");
        sysHeader.put("authPara", "");
        sysHeader.put("authContext", "");
        sysHeader.put("pinIndex", "");
        sysHeader.put("pinValue", "");
        header.put("sysHeader", sysHeader);
        // 初始化消息json
        JSONObject body = new JSONObject();
        JSONObject request = new JSONObject();
        JSONObject bizBody = new JSONObject();
        JSONObject list = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject map = new JSONObject();
        // 装载消息信息
        map.put("funcode", "SMS001");
        map.put("channel", clientCd);
        map.put("type", "106");
        map.put("sdate", dateFormat2.format(new Date()));
        map.put("edate", dateFormat2.format(new Date()));
        map.put("stime", timeFormat2.format(new Date()));
        map.put("etime", timeFormat2.format(new Date()));
        // 多个号码用逗号分割
        map.put("to", telNumber);
        map.put("toWeChat", "True");
        map.put("toSms", "False");
        map.put("body", title);
        jsonArray.add(map);
        list.put("msg", jsonArray);
        bizBody.put("root", list);
        request.put("bizBody", bizBody);
        JSONObject bizHeader = new JSONObject();
        request.put("bizHeader", bizHeader);
        body.put("request", request);
        transaction.put("Header", header);
        transaction.put("Body", body);
        json.put("Transaction", transaction);

        return json.toString();
    }

    /**
     * 创建消息id
     *
     * @param clientCd 请求方系统号
     * @return {@link String}
     */
    private String createMsgId(String clientCd) {
        String msgId = StringUtils.leftPad(clientCd, 4, '0');
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        msgId = msgId + format.format(date) + getNumber();
        return msgId;
    }

    /**
     * 获取随机数字串
     *
     * @return int
     */
    private int getNumber() {
        return (int) (random.nextDouble() * 9000.0D) + 1000;
    }

    /**
     * 调用接口发送消息
     *
     * @param strUrl 通知地址
     * @param params 请求参数
     */
    private void doPostNew(String strUrl, String params) {
        BufferedReader reader = null;
        OutputStreamWriter out = null;
        try {
            // 初始化连接
            URL url = new URL(strUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();
            // 向接口传输消息
            out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            out.append(params);
            out.flush();
            // 读取接口返回
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder res = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                res.append(line);
            }
            log.info("短信告警接口返回信息:" + res);
        } catch (Exception e) {
            log.error("微信公众号告警通知发送失败:{}", e);
            throw new ServiceException(e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error("短信告警通知传输流关闭失败:" + e.getMessage());
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("短信告警通知返回流关闭失败:" + e.getMessage());
                }
            }
        }
    }
}