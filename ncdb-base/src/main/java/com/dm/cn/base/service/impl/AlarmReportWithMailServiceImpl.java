package com.dm.cn.base.service.impl;

import com.dm.cn.base.entity.server.AlarmMessage;
import com.dm.cn.base.service.AlarmReportService;
import com.dm.cn.common.core.constant.Constants;
import com.dm.cn.common.core.utils.SpringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 邮件告警消息通知实现
 *
 * @author danger
 * @date 2023/3/9 下午6:57
 */
@Service("AlarmReportWithMailService")
public class AlarmReportWithMailServiceImpl implements AlarmReportService {

    @Value("${spring.mail.username}")
    private String sendUserMail;

    /**
     * 拿到设置好参数的AlarmMessageDTO　通过邮箱发送消息（多个接受对象）
     *
     * @param alarmMessage
     */
    @Override
    @Async
    public void sendAlarmMessageList(AlarmMessage alarmMessage, String recipient) {
        // 简单的邮件消息对象
        SimpleMailMessage message = new SimpleMailMessage();
        // 和配置username相同,发送方
        message.setFrom(sendUserMail);
        // 接收方
        String[] toUser = recipient.split(",");
        message.setTo(toUser);
        // 标题
        message.setSubject(Constants.MAIL_TITLE);
        // 正文
        message.setText(alarmMessage.getContent());
        // 发送
        MailSender mailSender = SpringUtils.getBean(MailSender.class);

        mailSender.send(message);
    }
}