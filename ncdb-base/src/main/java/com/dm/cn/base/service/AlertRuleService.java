package com.dm.cn.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dm.cn.base.entity.server.AlertRule;

import java.util.List;

/**
 * 告警规则定义表: 逻辑接口
 *
 * @author Auto-Coder
 * @date 2023-03-03
 */
public interface AlertRuleService extends IService<AlertRule> {

    /**
     * 或者指定设备对象的告警规则
     *
     * @param tableName    表名
     * @param ruleName     告警项
     * @param instanceType 实例类型
     * @param isDevice     是否是设备
     * @param dbName     数据库名称
     * @return
     */
    List<AlertRule> getListByParam(String tableName, String ruleName, String instanceType, boolean isDevice, String dbName);

}