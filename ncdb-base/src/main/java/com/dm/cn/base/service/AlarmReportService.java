package com.dm.cn.base.service;

import com.dm.cn.base.entity.server.AlarmMessage;
import org.springframework.stereotype.Service;

/**
 * 告警发送service
 *
 * @author danger
 * @date 2023/03/09
 */
@Service
public interface AlarmReportService {
    /**
     * 通过ｈｔｔｐ请求发送告警消息（多个告警对象）
     *
     * @param alarmMessage 告警消息
     * @param recipient    接收人
     */
    default void sendAlarmMessageList(AlarmMessage alarmMessage, String recipient) {}
}
