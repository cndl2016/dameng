package com.dm.cn.base.converter;

import com.dm.cn.base.entity.server.AlertConfig;
import com.dm.cn.base.entity.server.AlertRule;
import com.dm.cn.base.entity.vo.AlertConfigVO;
import com.dm.cn.common.core.utils.IdWorker;

import java.util.ArrayList;
import java.util.List;

/**
 * 告警规则配置表对象转换器
 *
 * @author Auto-Coder
 * @date 2023-03-16
 */
public class AlertConfigConverter {

    /**
     * 实体 to 列表
     *
     * @param alertConfigList 实体集合
     * @return List<AlertConfigVO> 列表集合
     */
    public static List<AlertConfigVO> toAlertConfigVoList(List<AlertConfig> alertConfigList) {
        List<AlertConfigVO> list = new ArrayList<>();
        for (AlertConfig alertConfig : alertConfigList) {
            AlertConfigVO alertConfigVo = toAlertConfigVo(alertConfig);
            list.add(alertConfigVo);
        }
        return list;
    }

    /**
     * 实体 to 列表
     *
     * @param alertConfig 实体
     * @return AlertConfigVO 列表
     */
    public static AlertConfigVO toAlertConfigVo(AlertConfig alertConfig) {
        if (alertConfig == null) {
          return null;
        }
        AlertConfigVO alertConfigVo = new AlertConfigVO();
        alertConfigVo.setId(alertConfig.getId());
        alertConfigVo.setRuleId(alertConfig.getRuleId());
        alertConfigVo.setRuleName(alertConfig.getRuleName());
        alertConfigVo.setRuleCode(alertConfig.getRuleCode());
        alertConfigVo.setRuleDesc(alertConfig.getRuleDesc());
        alertConfigVo.setRuleGroups(alertConfig.getRuleGroups());
        alertConfigVo.setObjectId(alertConfig.getObjectId());
        alertConfigVo.setParameters(alertConfig.getParameters());
        alertConfigVo.setStatus(alertConfig.getStatus());
        alertConfigVo.setTableName(alertConfig.getTableName());
        alertConfigVo.setProcessor(alertConfig.getProcessor());
        alertConfigVo.setRuleType(alertConfigVo.getRuleType());
        alertConfigVo.setMemo(alertConfig.getMemo());
        alertConfigVo.initAlertParametersVo();
        return alertConfigVo;
    }

    /**
     * 告警规则实体 to 告警配置列表
     * @param alertRule
     * @return
     */
    public static AlertConfigVO alertRuleToAlertConfigVo(AlertRule alertRule, Long objectId) {
        AlertConfigVO alertConfigVo = new AlertConfigVO();
        alertConfigVo.setId(alertRule.getId());
        alertConfigVo.setRuleId(alertRule.getId());
        alertConfigVo.setRuleName(alertRule.getName());
        alertConfigVo.setRuleCode(alertRule.getCode());
        alertConfigVo.setRuleDesc(alertRule.getRuleDesc());
        alertConfigVo.setAppendDesc("");
        alertConfigVo.setRuleGroups(alertRule.getGroups());
        alertConfigVo.setObjectId(objectId);
        alertConfigVo.setParameters(alertRule.getParameters());
        alertConfigVo.setStatus(alertRule.getStatus());
        alertConfigVo.setTableName(alertRule.getTableName());
        alertConfigVo.setProcessor(alertRule.getProcessor());
        alertConfigVo.setMemo(alertRule.getMemo());
        alertConfigVo.setRuleType(alertRule.getRuleType());
        alertConfigVo.initAlertParametersVo();
        alertConfigVo.setDefaultParameters(alertConfigVo.getAlertParameters());
        return alertConfigVo;
    }
}