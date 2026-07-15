package com.dm.cn.base.service;

import com.dm.cn.base.entity.model.BaseModel;

/**
 * 告警基础类接口
 *
 * @author Auto-Coder
 * @date 2023-03-20
 */
public interface AlertBaseService {

    /**
     * 计算告警信息
     *
     * @param baseModel 数据模型
     */
    void calcAlert(BaseModel baseModel);

    /**
     * 计算告警信息
     *
     * @param baseModel 数据模型
     * @param ruleIdOrCode 规则ID或CODE
     */
    void calcAlert(BaseModel baseModel, String ruleIdOrCode);

    /**
     *
     * 计算节点告警信息
     * @param baseModel 数据模型
     * @param ruleIdOrCode 规则ID或CODE
     */
    void calcInstanceAlert(BaseModel baseModel, String ruleIdOrCode);
}
