package com.dm.cn.base.entity.server;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dm.cn.common.core.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 告警通知消息记录表
 *
 * @author dameng
 * @TableName T_ALERT_NOTICE_MESSAGE
 * @date 2025/02/13
 */
@TableName(value = "T_ALERT_NOTICE_MESSAGE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertNoticeMessage implements Serializable {

    /**
     * 主键
     */
    @TableId(value = "ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 告警对象ID
     */
    @Excel(name = "告警对象ID")
    @TableField(value = "OBJECT_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long objectId;

    /**
     * 接受人
     */
    @Excel(name = "接收人")
    @TableField(value = "RECIPIENT")
    private String recipient;

    /**
     * 消息标题
     */
    @Excel(name = "消息标题", width = 60)
    @TableField(value = "TITLE")
    private String title;

    /**
     * 消息内容
     */
    @Excel(name = "消息内容", width = 100)
    @TableField(value = "CONTENT")
    private String content;

    /**
     * 告警级别，fatal:严重、warn：警告
     */
    @TableField(value = "ALERT_LEVEL")
    private String alertLevel;

    @Excel(name = "告警级别")
    @TableField(exist = false)
    private String alertLevelDesc;

    /**
     * 通知方式 关联ALERT_NOTICE_METHOD表KEY
     */
    @TableField(value = "NOTICE_METHOD")
    private String noticeMethod;

    @Excel(name = "通知方式")
    @TableField(exist = false)
    private String noticeMethodDesc;

    /**
     * 通知时间
     */
    @Excel(name = "通知时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "NOTICE_TIME")
    private Date noticeTime;

    /**
     * 是否成功 0失败 1成功
     */
    @TableField(value = "IS_SUCCESS")
    private String isSuccess;

    @Excel(name = "是否成功")
    @TableField(exist = false)
    private String isSuccessDesc;

    /**
     * 通知失败返回错误内容
     */
    @TableField(value = "ERROR_MESSAGE")
    private String errorMessage;

    /**
     * 消息标题英文
     */
    @Excel(name = "消息标题英文", width = 60)
    @TableField(value = "TITLE_EN")
    private String titleEn;

    /**
     * 消息内容英文
     */
    @Excel(name = "消息内容英文", width = 100)
    @TableField(value = "CONTENT_EN")
    private String contentEn;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}