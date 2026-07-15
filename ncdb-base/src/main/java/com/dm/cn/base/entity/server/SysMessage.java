package com.dm.cn.base.entity.server;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dm.framework.common.log.Log;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统消息、监控告警表实体对象
 *
 * @author Auto-Coder
 * @date 2023-03-02
 */
@Data
@ApiModel(value = "系统消息、监控告警表")
@TableName("SYS_MESSAGE")
public class SysMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**主键ID*/
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**资源ID*/
    @Log(title = "资源ID")
    private String objectId;


    /**资源名称*/
    @Log(title = "资源名称")
    private String resourceName;


    /**规则ID*/
    @Log(title = "规则ID")
    private String ruleId;


    /**消息标题*/
    @Log(title = "消息标题")
    private String title;


    /**消息内容*/
    @Log(title = "消息内容")
    private String content;

    /**
     * 消息标题英文
     */
    @Log(title = "消息标题英文")
    private String titleEn;

    /**
     * 消息内容英文
     */
    @Log(title = "消息内容英文")
    private String contentEn;

    /**创建时间*/
    @Log(title = "创建时间")
    private Date createTime;


    /**结束时间*/
    @Log(title = "结束时间")
    private Date endTime;


    /**消息级别*/
    @Log(title = "消息级别")
    private String msgLevel;


    /**类型*/
    @Log(title = "类型")
    private String msgKind;


    /**设备对象表*/
    @Log(title = "设备对象表")
    private String tableName;


    /**有效状态：有效live,过期无效death,关闭失效killed*/
    @Log(title = "有效状态")
    private String activeState;


    /**发送邮件状态 todo yyj test*/
    @Log(title = "发送邮件状态")
    private String sendMailStatus;


    /**发送时间*/
    @Log(title = "发送时间")
    private Date sendTime;

    /**
     * 告警项
     */
    private String alarmCondition;

    /**
     * 告警配置id
     */
    @TableField(exist = false)
    private Long alertConfigId;

}

