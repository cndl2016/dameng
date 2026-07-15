package com.dm.cn.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dm.cn.base.entity.param.AlertApplyParam;
import com.dm.cn.base.entity.param.AlertConfigParam;
import com.dm.cn.base.entity.param.AlertRuleParam;
import com.dm.cn.base.entity.server.AlertConfig;
import com.dm.cn.base.entity.vo.AlertConfigVO;
import com.dm.cn.common.param.EnableParam;

import java.util.List;

/**
 * 告警规则配置表: 针对不同的设备和实例告警规则不同逻辑接口
 *
 * @author Auto-Coder
 * @date 2023-03-16
 */
public interface AlertConfigService extends IService<AlertConfig> {

    /**
     * 更新设备告警配置缓存
     * @param
     */
    void loadDeviceCache();

    /**
     * 通过设备监控数据对象的关键属性，获取设备监控数据对象对象的告警规则
     *
     * @param processor 设备监控数据对象
     * @param manageIp  设备的管理IP
     * @param port      设备的端口
     * @return
     */
    List<AlertConfigVO> getListByKey(String processor, String manageIp, Integer port);

    /**
     *  查询告警条件
     * @param key
     * @param ruleIdOrCode
     * @return
     */
    AlertConfigVO getAlertByRuleIdOrCode(String key, String ruleIdOrCode);

    /**
     * 根据告警对象id，获取指定的告警配置
     *
     * @param param 查询参数对象
     * @return
     */
    List<AlertConfigVO> getListByObjectId(AlertRuleParam param);

    /**
     * 保存告警规则
     *
     * @param param 参数对象
     * @return
     */
    boolean add(AlertConfigParam param);

    /**
     * 切换启用/禁用状态
     *
     * @param param 启用/禁用参数
     * @return boolean
     */
    boolean enableConfig(EnableParam param);

    /**
     * 批量应用告警规则
     *
     * @param param 参数对象
     * @return
     */
    boolean apply(AlertApplyParam param);

    /**
     * 获取要保存的告警规则
     * @param alarmRuleList 规则集和
     * @return 执行结果
     */
    List<AlertConfigVO> getUpdateAlertList(List<AlertConfigVO> alarmRuleList);

    /**
     * 更新告警规则
     * @param config 规则
     * @param objectId 对象id
     *
     */
    void updateAlert(AlertConfigVO config, Long objectId);
}