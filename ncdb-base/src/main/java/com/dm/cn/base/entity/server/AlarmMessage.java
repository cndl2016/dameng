package com.dm.cn.base.entity.server;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 告警消息表
 *
 * @author dameng
 * @TableName T_ALARM_MESSAGE
 * @date 2024/10/24
 */
@TableName(value = "T_ALARM_MESSAGE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlarmMessage implements Serializable {

    /**
     * 主键
     */
    @TableId(value = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 通知行为，0:邮箱，1:飞书，2:钉钉，3:企业微信，4:短信通知，5:公众号
     */
    @TableField(value = "SEND_METHOD")
    private String sendMethod;

    /**
     * 通知地址
     */
    @TableField(value = "HTTP_URL")
    private String httpUrl;

    /**
     * 机器人签名
     */
    @TableField(value = "HTTP_SIGN")
    private String httpSign;

    /**
     * HTTPS代理地址
     */
    @TableField(value = "HTTPS_PROXY")
    private String httpsProxy;

    /**
     * 消息发送对象
     */
    @TableField(value = "TO_USER")
    private String toUser;

    /**
     * 服务编码
     */
    @TableField(value = "SERVICE_CD")
    private String serviceCd;

    /**
     * 请求方系统号
     */
    @TableField(value = "CLIENT_CD")
    private String clientCd;

    /**
     * 短信类型代码
     */
    @TableField(value = "SMS_TYPE")
    private String smsType;

    /**
     * 发送对象手机号
     */
    @TableField(value = "TO_PHONE")
    private String toPhone;

    /**
     * 是否使用通知方式（0：未使用，1：使用）
     */
    @TableField(value = "IS_USE")
    private String isUse;

    /**
     * 告警消息内容
     */
    @NotEmpty(message = "告警消息内容不可为空")
    @TableField(exist = false)
    private String content;

    /**
     * 告警消息内容英文
     */
    @TableField(exist = false)
    private String contentEn;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}