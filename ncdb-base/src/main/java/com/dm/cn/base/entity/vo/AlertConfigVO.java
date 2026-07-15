package com.dm.cn.base.entity.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dm.cn.base.constant.AlertConfigConstant;
import com.dm.framework.common.util.BeanUtil;
import com.dm.framework.common.util.StringUtil;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 告警规则配置表列表模型对象
 *
 * @author Auto-Coder
 * @date 2023-03-16
 */
@Data
@ApiModel(value = "告警规则配置表列表模型对象")
public class AlertConfigVO {

    /**主键*/
    @ApiModelProperty(value = "主键")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**告警规则定义ID*/
    @ApiModelProperty(value = "告警规则定义ID",notes="关联ALERT_RULE.ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long ruleId;

    /**告警名称*/
    @ApiModelProperty(value = "告警名称")
    private String ruleName;

    /**告警名称英文*/
    @ApiModelProperty(value = "告警名称英文")
    private String ruleNameEn;

    /**规则编码*/
    @ApiModelProperty(value = "规则编码")
    private String ruleCode;

    /**规则描述*/
    @ApiModelProperty(value = "规则描述")
    private String ruleDesc;

    /**追加描述*/
    @ApiModelProperty(value = "追加描述")
    private String appendDesc;

    /**分组*/
    @ApiModelProperty(value = "分组")
    private String ruleGroups;

    /**告警对象id*/
    @ApiModelProperty(value = "告警对象id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long objectId;

    @ApiModelProperty(value = "告警对象名称")
    private String objectName;

    /**
     * 设备key = 管理IP+端口，如果无端口，为空字符串
     * 实例key = 实例id
     */
    @ApiModelProperty(value = "告警key")
    private String objectKey;

    /**参数JSON*/
    @ApiModelProperty(value = "参数JSON")
    private String parameters;

    /**启用状态*/
    @ApiModelProperty(value = "启用状态",notes="yes\no")
    private String status;

    /**告警对象表*/
    @ApiModelProperty(value = "告警对象表")
    private String tableName;

    /**告警处理过程类*/
    @ApiModelProperty(value = "告警对象实体类")
    private String processor;

    /**备注*/
    @ApiModelProperty(value = "备注")
    private String memo;

    /**备注英文*/
    @ApiModelProperty(value = "备注英文")
    private String memoEn;

    @ApiModelProperty(value = "参数对象")
    private List<AlertParametersVO> alertParameters;

    @ApiModelProperty(value = "规则默认参数对象")
    private List<AlertParametersVO> defaultParameters;

    @ApiModelProperty(value = "是否保存")
    private String isSave;

    @ApiModelProperty(value = "模板已选通知方式集合(通知方式类型)")
    private List<String> sendMethodCheckedList;

    @ApiModelProperty(value = "飞书通知方式标识")
    private String noticeLark;

    @ApiModelProperty(value = "钉钉通知方式标识")
    private String noticeDing;

    @ApiModelProperty(value = "企业微信通知方式标识")
    private String noticeWecom;

    @ApiModelProperty(value = "勾选的邮件接收人")
    private String emailRecipient;

    @ApiModelProperty(value = "勾选的短信接收人")
    private String smsRecipient;

    @ApiModelProperty(value = "勾选的微信公众号接收人")
    private String weoaRecipient;

    @ApiModelProperty(value = "所选的邮箱地址集和")
    private List<String> selectNoticeList;

    @ApiModelProperty(value = "通知方式id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long noticeId;

    @ApiModelProperty(value = "是否为默认告警规则")
    private Boolean isDefaultRule;

    @ApiModelProperty(value = "告警项")
    private String alarmCondition;

    @ApiModelProperty(value = "规则类型")
    private String ruleType;

    @ApiModelProperty(value = "告警对象数据库名称")
    private String objectDbName;

    public void initAlertParametersVo(){
        if (StringUtil.isEmpty(this.parameters)) {
            return;
        }
        if (!this.parameters.startsWith(AlertConfigConstant.OPEN_BRACE) && !this.parameters.startsWith(AlertConfigConstant.OPEN_BRACKET)) {
            return;
        }
        if (this.parameters.startsWith(AlertConfigConstant.OPEN_BRACE)) {
            List<AlertParametersVO> alertParameters = new ArrayList<>();
            AlertParametersVO alertParametersVo = BeanUtil.wrapBean(AlertParametersVO.class, this.parameters);
            alertParameters.add(alertParametersVo);
            alertParametersVo.initParamDatas();
            setAlertParameters(alertParameters);
        } else {
            List<AlertParametersVO> alertParameters = BeanUtil.wrapBeanList(AlertParametersVO.class, this.parameters);
            for (AlertParametersVO parametersVo : alertParameters) {
                parametersVo.initParamDatas();
            }
            setAlertParameters(alertParameters);
        }
    }
}


