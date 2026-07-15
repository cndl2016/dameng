package com.dm.cn.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dm.cn.base.entity.enums.AlertRuleTypeEnum;
import com.dm.cn.base.entity.server.AlertRule;
import com.dm.cn.base.mapper.AlertRuleMapper;
import com.dm.cn.base.service.AlertRuleService;
import com.dm.cn.common.core.constant.NumberConstants;
import com.dm.cn.common.core.enums.AlarmConditionEnum;
import com.dm.cn.common.core.utils.StringUtils;
import com.dm.cn.common.utils.validate.ParamValidate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 告警规则定义表逻辑实现
 *
 * @author Auto-Coder
 * @date 2023-03-03
 */
@Service
public class AlertRuleServiceImpl extends ServiceImpl<AlertRuleMapper, AlertRule> implements AlertRuleService {

    @Override
    public List<AlertRule> getListByParam(String tableName, String ruleName, String instanceType, boolean isDevice, String dbName) {
        // 是否是标准模式判断，是返回true，不是返回false。是:不查询主备切换告警项
        boolean flag = NumberConstants.NUMBER_ZERO.equals(instanceType);

        return lambdaQuery().eq(AlertRule::getTableName, tableName)
                .notIn(!isDevice && flag, AlertRule::getId, AlarmConditionEnum.INSTANCE_CLUSTER_STATUS_GROUP)
                .like(StringUtils.isNotEmpty(ruleName), AlertRule::getName, ruleName)
                .eq(ParamValidate.validate(dbName), AlertRule::getRuleType, AlertRuleTypeEnum.DB.getValue())
                .list();
    }

}