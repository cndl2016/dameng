package com.dm.cn.base.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 告警配置的通知模版vo
 *
 * @author root
 * @date 2025/02/11
 */
@Data
public class NoticeConfigVO implements Serializable {

    /**
     * 告警通知id
     */
    private Long alarmMessageId;

    /**
     * 告警通知接收人
     */
    private String recipient;

}