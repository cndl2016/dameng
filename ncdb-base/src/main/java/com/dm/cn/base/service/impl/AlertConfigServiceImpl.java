package com.dm.cn.base.service.impl;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dm.cn.base.constant.AlertRuleConstant;
import com.dm.cn.base.converter.AlertConfigConverter;
import com.dm.cn.base.entity.enums.AlertRuleTypeEnum;
import com.dm.cn.base.entity.model.DeviceNormal;
import com.dm.cn.base.entity.param.AlertApplyParam;
import com.dm.cn.base.entity.param.AlertConfigParam;
import com.dm.cn.base.entity.param.AlertRuleParam;
import com.dm.cn.base.entity.server.AlarmMessage;
import com.dm.cn.base.entity.server.AlertConfig;
import com.dm.cn.base.entity.server.AlertNoticeConfig;
import com.dm.cn.base.entity.server.AlertRule;
import com.dm.cn.base.entity.vo.AlertConfigVO;
import com.dm.cn.base.entity.vo.AlertParametersVO;
import com.dm.cn.base.entity.vo.NoticeConfigVO;
import com.dm.cn.base.mapper.AlertConfigMapper;
import com.dm.cn.base.service.AlarmMessageService;
import com.dm.cn.base.service.AlertConfigService;
import com.dm.cn.base.service.AlertNoticeConfigService;
import com.dm.cn.base.service.AlertRuleService;
import com.dm.cn.common.core.constant.Constants;
import com.dm.cn.common.core.constant.NumberConstants;
import com.dm.cn.common.core.constant.SymbolConstants;
import com.dm.cn.common.core.domain.LoginUser;
import com.dm.cn.common.core.enums.SyncTaskEnum;
import com.dm.cn.common.core.exception.ServiceException;
import com.dm.cn.common.core.utils.IdWorker;
import com.dm.cn.common.core.utils.StringUtils;
import com.dm.cn.common.enums.SendMethodEnum;
import com.dm.cn.common.param.EnableParam;
import com.dm.cn.common.security.utils.SecurityUtils;
import com.dm.cn.common.utils.I18nMessageUtil;
import com.dm.cn.system.constant.MessageTipConstant;
import com.dm.cn.system.service.ISysMessageTipService;
import com.dm.framework.common.util.BeanUtil;
import com.dm.framework.common.util.CollectionUtil;
import com.dm.framework.common.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import static com.dm.framework.common.util.CollectionUtil.convertList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * 告警规则配置表逻辑实现
 *
 * @author Auto-Coder
 * @date 2023-03-16
 */
@Service
@Slf4j
public class AlertConfigServiceImpl extends ServiceImpl<AlertConfigMapper, AlertConfig> implements AlertConfigService, CommandLineRunner {

    private static final ReadWriteLock LOCK = new ReentrantReadWriteLock();

    private static final Lock WRITE_LOCK = LOCK.writeLock();

    private TimedCache<String, List<AlertConfigVO>> cacheMap = CacheUtil.newTimedCache(0);

    private AlertRuleService alertRuleService;

    @Autowired
    public void setAlertRuleService(AlertRuleService alertRuleService) {
        this.alertRuleService = alertRuleService;
    }

    @Resource
    private AlertConfigMapper alertConfigMapper;

    @Resource
    private AlertNoticeConfigService alertNoticeConfigService;

    @Resource
    private AlarmMessageService alarmMessageService;

    @Resource
    private ISysMessageTipService sysMessageTipService;

    @Override
    public List<AlertConfigVO> getListByKey(String processor, String manageIp, Integer port) {
        String deviceKey = processor + StringUtil.trim(manageIp, "") + (null != port ? port : "");
        return cacheMap.get(deviceKey);
    }

    @Override
    public AlertConfigVO getAlertByRuleIdOrCode(String key, String ruleIdOrCode) {
        List<AlertConfigVO> list = cacheMap.get(key);
        if (list == null) {
            return null;
        }
        AlertConfigVO res = new AlertConfigVO();
        for (AlertConfigVO alertConfigVo : list) {
            if (ruleIdOrCode.equals(alertConfigVo.getRuleId())) {
                BeanUtils.copyProperties(alertConfigVo, res);
                return res;
            }
            if (ruleIdOrCode.equalsIgnoreCase(alertConfigVo.getRuleCode())) {
                BeanUtils.copyProperties(alertConfigVo, res);
                return res;
            }
        }
        return null;
    }

    @Override
    public void run(String... args) {
        loadAllCache();
    }

    private void loadAllCache() {
        log.info(" *** loadAllCache *** ");
        List<AlertConfigVO> all = getDeviceAll();
        loadCache(all);
    }

    @Override
    public void loadDeviceCache() {
        log.info(" *** loadDeviceCache *** ");
        List<AlertConfigVO> all = getDeviceAll();
        loadCache(all);
    }

    private void loadCache(List<AlertConfigVO> all) {
        Map<String, List<AlertConfigVO>> map = all.stream().collect(Collectors.groupingBy(AlertConfigVO::getObjectKey));
        WRITE_LOCK.lock();
        try {
            for (String key : map.keySet()) {
                this.cacheMap.put(key, map.get(key));
            }
        } finally {
            WRITE_LOCK.unlock();
        }
    }

    /**
     * 获取所有设备告警规则配置
     *
     * @return
     */
    private List<AlertConfigVO> getDeviceAll() {
        List<AlertConfig> all = list();
        List<DeviceNormal> allDevices = alertConfigMapper.getAllDevices();
        List<AlertRule> ruleList = alertRuleService.list();
        List<AlertConfigVO> allConfigs = new ArrayList<>();
        // 设置设备告警配置规则信息
        for (DeviceNormal deviceNormal : allDevices) {
            List<AlertConfigVO> configVoList = new ArrayList<>();
            List<AlertConfig> configList = all.stream().filter(item -> item.getObjectId().equals(deviceNormal.getDeviceId())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(configList)) {
                configVoList = AlertConfigConverter.toAlertConfigVoList(configList);
            }
            List<Long> roleIds = CollectionUtil.toList(configList, AlertConfig::getRuleId, true);
            List<AlertRule> filterList = ruleList.stream().filter(t -> t.getTableName().equals(deviceNormal.getTableName())
                    && (CollectionUtil.isEmpty(roleIds) || !roleIds.contains(t.getId()))).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(filterList)) {
                for (AlertRule alertRule : filterList) {
                    AlertConfigVO configVo = AlertConfigConverter.alertRuleToAlertConfigVo(alertRule, deviceNormal.getDeviceId());
                    configVoList.add(configVo);
                }
            }
            for (AlertConfigVO configVo : configVoList) {
                configVo.setObjectName(deviceNormal.getDeviceName());
                configVo.setObjectKey(configVo.getProcessor() + StringUtil.trim(deviceNormal.getManageIp(), "") + ((null != deviceNormal.getPort() && 0 != deviceNormal.getPort()) ? deviceNormal.getPort() : ""));
            }
            allConfigs.addAll(configVoList);
        }
        return allConfigs;
    }

    /**
     * 更新缓存
     *
     * @param configList 设备列表
     */
    private void updateDeviceCache(List<AlertConfig> configList) {
        List<DeviceNormal> allDevices = alertConfigMapper.getAllDevices();
        for (AlertConfig alertConfig : configList) {
            DeviceNormal deviceNormal = CollectionUtil.findOne(allDevices, t -> t.getDeviceId().equals(alertConfig.getObjectId()));
            if (deviceNormal == null) {
                continue;
            }
            String key = alertConfig.getProcessor() + StringUtil.trim(deviceNormal.getManageIp(), "") + ((null != deviceNormal.getPort() && 0 != deviceNormal.getPort()) ? deviceNormal.getPort() : "");
            AlertConfigVO alertConfigVo = AlertConfigConverter.toAlertConfigVo(alertConfig);
            alertConfigVo.setObjectName(deviceNormal.getDeviceName());
            alertConfigVo.setObjectKey(key);
            handleUpdateCacheRule(key, alertConfigVo);
        }
    }

    /**
     * 更新缓存中的告警规则
     *
     * @param key           key
     * @param alertConfigVo 规则配置
     */
    private void handleUpdateCacheRule(String key, AlertConfigVO alertConfigVo) {
        WRITE_LOCK.lock();
        try {
            if (cacheMap.containsKey(key)) {
                //获取之前的规则，然后进行替换
                List<AlertConfigVO> list = cacheMap.get(key);
                Optional<AlertConfigVO> first = list.stream().filter(item -> item.getRuleId().equals(alertConfigVo.getRuleId())).findFirst();
                if (first.isPresent()) {
                    AlertConfigVO oldAlertConfigVo = first.get();
                    list.removeIf(item -> item.getRuleId().equals(oldAlertConfigVo.getRuleId()));
                }
                list.add(alertConfigVo);
                cacheMap.put(key, list);
            } else {
                // 不存在key 则初始化
                List<AlertConfigVO> list = new ArrayList<>();
                list.add(alertConfigVo);
                cacheMap.put(key, list);
            }
        } finally {
            WRITE_LOCK.unlock();
        }
    }

    @Override
    public List<AlertConfigVO> getListByObjectId(AlertRuleParam param) {
        Long objectId = param.getObjectId();
        String tableName = param.getTableName();
        // 为空则是查询设备告警,有值则是查询实例告警
        String instanceType = param.getInstanceType();
        boolean isDevice = true;
        if (StringUtils.isNotEmpty(instanceType)) {
            isDevice = false;
        }
        if (ObjectUtils.isEmpty(objectId) || StringUtils.isEmpty(tableName)) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.BASE_ALERT_CONFIG_PARAM_NOT_NULL));
        }
        // 1.查询配置的数据
        List<AlertConfig> configList = lambdaQuery().eq(AlertConfig::getObjectId, objectId)
                .like(StringUtils.isNotEmpty(param.getRuleName()), AlertConfig::getRuleName, param.getRuleName())
                .list();
        // 获取实例和数据库告警对象
        Map<Boolean, List<AlertConfig>> configMap = configList.stream().collect(Collectors.partitioningBy(config -> StringUtils.isNotEmpty(config.getDbName())));
        List<AlertConfig> instanceConfigList = configMap.get(false);
        List<AlertConfig> dbConfigList = configMap.get(true);
        Map<String, List<AlertConfig>> dbAlertConfigMap = dbConfigList.stream().collect(groupingBy(AlertConfig::getDbName));
        // 获取指定对象告警配置
        boolean isDb = StringUtils.isNotEmpty(param.getDbName());
        List<AlertConfig> targetConfigList = (isDb && !ObjectUtils.isEmpty(dbAlertConfigMap.get(param.getDbName()))) ?
                dbAlertConfigMap.get(param.getDbName()) : instanceConfigList;
        // 如果为数据库配置查询 校验实例中是否有其缺失的配置项
        if (isDb) {
            targetConfigList = targetConfigList.stream().filter(config -> AlertRuleTypeEnum.DB.getValue().equals(config.getRuleType())).collect(toList());
            List<Long> dbRuleIds = CollectionUtil.toList(targetConfigList, AlertConfig::getRuleId, true);
            List<AlertConfig> distinctList = instanceConfigList.stream()
                    .filter(config -> !dbRuleIds.contains(config.getRuleId()) && AlertRuleTypeEnum.DB.getValue().equals(config.getRuleType())).collect(toList());
            targetConfigList.addAll(distinctList);
        }
        List<AlertConfigVO> configVoList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(targetConfigList)) {
            configVoList = convertList(targetConfigList, AlertConfigConverter::toAlertConfigVo);
            for (AlertConfigVO config : configVoList) {
                // 设置属性为已保存
                config.setIsSave(NumberConstants.NUMBER_ONE);
            }
        }
        List<Long> roleIds = CollectionUtil.toList(targetConfigList, AlertConfig::getRuleId, true);
        // 验证设备规则配置中是否有规则缺失
        List<AlertRule> ruleList = alertRuleService.getListByParam(tableName, param.getRuleName(), instanceType, isDevice, param.getDbName());
        List<AlertRule> filterList = ruleList.stream().filter(item -> CollectionUtil.isEmpty(roleIds) || !roleIds.contains(item.getId())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(filterList)) {
            for (AlertRule alertRule : filterList) {
                AlertConfigVO configVo = AlertConfigConverter.alertRuleToAlertConfigVo(alertRule, objectId);
                configVo.setIsDefaultRule(true);
                configVoList.add(configVo);
            }
        }
        // 设置告警信息
        if (CollectionUtil.isNotEmpty(configVoList)) {
            // 告警配置id集合
            List<Long> alertConfigIdLists = configVoList.stream().map(AlertConfigVO::getId).collect(toList());
            // 获取所有的通知方式信息
            List<AlarmMessage> alarmMessageList = alarmMessageService.getAll();
            // 获取配置的告警通知方式中间表数据
            List<AlertNoticeConfig> noticeConfigList = alertNoticeConfigService.lambdaQuery().in(AlertNoticeConfig::getAlertConfigId, alertConfigIdLists).list();
            for (AlertConfigVO configVO : configVoList) {
                initNoticeConfig(alarmMessageList, noticeConfigList, configVO);
            }
        }

        // 重新排序
        List<AlertConfigVO> result = configVoList.stream()
                .sorted(Comparator.comparing(AlertConfigVO::getRuleId))
                .collect(toList());
        return convertToEn(result);
    }

    /**
     * 英文国际化
     *
     * @return
     */
    private List<AlertConfigVO> convertToEn(List<AlertConfigVO> configVoList) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (Constants.EN.equals(loginUser.getLanguage())) {
            for (AlertConfigVO alertConfigVo : configVoList) {
                String nameEn = sysMessageTipService.getMessageTip(Constants.EN, "T_ALERT_RULE", alertConfigVo.getRuleId().toString(), "name");
                alertConfigVo.setRuleNameEn(nameEn);
                String memoEn = sysMessageTipService.getMessageTip(Constants.EN, "T_ALERT_RULE", alertConfigVo.getRuleId().toString(), "memo");
                alertConfigVo.setMemoEn(memoEn);
            }
        }
        return configVoList;
    }

    /**
     * 初始化配置的告警通知方式等数据
     *
     * @param alarmMessageList 所有的通知方式
     * @param noticeConfigList 告警项配置的通知方式
     * @param config           告警项配置对象
     */
    private void initNoticeConfig(List<AlarmMessage> alarmMessageList, List<AlertNoticeConfig> noticeConfigList, AlertConfigVO config) {
        if (CollectionUtil.isNotEmpty(noticeConfigList)) {
            List<AlertNoticeConfig> currConfigList = noticeConfigList.stream().filter(item -> config.getId().equals(item.getAlertConfigId())).collect(Collectors.toList());
            List<Long> alarmMessageIds = CollectionUtil.toList(currConfigList, AlertNoticeConfig::getAlarmMessageId, true);
            List<AlarmMessage> filterMessageList = alarmMessageList.stream().filter(item -> !CollectionUtil.isEmpty(alarmMessageIds) && alarmMessageIds.contains(item.getId())).collect(Collectors.toList());
            for (AlarmMessage alarmMessage : filterMessageList) {
                AlertNoticeConfig noticeConfig = currConfigList.stream().filter(item -> alarmMessage.getId().equals(item.getAlarmMessageId())).findFirst().orElse(null);
                // 设置需要的属性值
                if (SendMethodEnum.MAIL.getValue().equals(alarmMessage.getSendMethod())) {
                    if (!ObjectUtils.isEmpty(noticeConfig)) {
                        config.setEmailRecipient(noticeConfig.getRecipient());
                    }
                }
                if (SendMethodEnum.SMS.getValue().equals(alarmMessage.getSendMethod())) {
                    if (!ObjectUtils.isEmpty(noticeConfig)) {
                        config.setSmsRecipient(noticeConfig.getRecipient());
                    }
                }
                if (SendMethodEnum.WEOA.getValue().equals(alarmMessage.getSendMethod())) {
                    if (!ObjectUtils.isEmpty(noticeConfig)) {
                        config.setWeoaRecipient(noticeConfig.getRecipient());
                    }
                }
                if (SendMethodEnum.LARK.getValue().equals(alarmMessage.getSendMethod())) {
                    config.setNoticeLark(SyncTaskEnum.ENABLED.getValue());
                }
                if (SendMethodEnum.DING.getValue().equals(alarmMessage.getSendMethod())) {
                    config.setNoticeDing(SyncTaskEnum.ENABLED.getValue());
                }
                if (SendMethodEnum.WECOM.getValue().equals(alarmMessage.getSendMethod())) {
                    config.setNoticeWecom(SyncTaskEnum.ENABLED.getValue());
                }
            }
            config.setSendMethodCheckedList(CollectionUtil.toList(filterMessageList, AlarmMessage::getSendMethod, true));
        }
    }

    @Override
    public boolean add(AlertConfigParam param) {
        AlertConfig alertConfig = param.getAlertConfig();
        List<AlertParametersVO> alertParameters = param.getAlertParameters();
        // 参数转换
        String status = AlertRuleConstant.STATUS_NO;
        for (AlertParametersVO parametersVo : alertParameters) {
            parametersVo.initParamDatas();
            status = Boolean.TRUE.equals(parametersVo.getWarningStatus()) || Boolean.TRUE.equals(parametersVo.getFatalStatus()) ?
                    AlertRuleConstant.STATUS_YES : AlertRuleConstant.STATUS_NO;
        }
        alertConfig.setParameters(BeanUtil.toJsonString(alertParameters));
        alertConfig.setStatus(status);
        if (alertConfig.getObjectId() == -1) {
            AlertRule rule = new AlertRule();
            BeanUtils.copyProperties(alertConfig, rule);
            rule.setName(alertConfig.getRuleName());
            rule.setCode(alertConfig.getRuleCode());
            rule.setGroups(alertConfig.getRuleGroups());
            alertRuleService.saveOrUpdate(rule);
            this.loadAllCache();
        } else {
            // 默认告警规则使用
            if (!ObjectUtils.isEmpty(alertConfig.getIsDefaultRule()) && alertConfig.getIsDefaultRule()) {
                alertConfig.setId(IdWorker.getId().nextId());
            }
            alertConfig.setDbName(param.getDbName());
            saveOrUpdate(alertConfig);
            // 重新刷新缓存
            this.updateDeviceCache(Collections.singletonList(alertConfig));

        }
        // 保存告警和通知方式配置信息中间表
        alertNoticeConfigService.save(alertConfig, param.getSelectNoticeList());
        return true;
    }

    @Override
    public boolean enableConfig(EnableParam param) {
        boolean flag;
        if (param.getSelectObjectId() == NumberConstants.MINUS_ONE) {
            // 默认告警规则
            AlertRule alertRule = alertRuleService.getById(param.getId());
            if (ObjectUtils.isEmpty(alertRule)) {
                throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.COMMON_RECORD_NOT_EXIST, param.getId()));
            }
            // 设置新的状态
            alertRule.setStatus(param.getStatus());
            List<AlertParametersVO> alertParameters = AlertConfigConverter.alertRuleToAlertConfigVo(alertRule, param.getSelectObjectId()).getAlertParameters();
            if (!ObjectUtils.isEmpty(alertParameters)) {
                boolean status = AlertRuleConstant.STATUS_YES.equals(param.getStatus());
                AlertParametersVO alertParametersVO = alertParameters.get(NumberConstants.ZERO);
                alertParametersVO.setFatalStatus(status);
                if (!ObjectUtils.isEmpty(alertParametersVO.getWarningStatus())) {
                    alertParametersVO.setWarningStatus(status);
                }
                alertRule.setParameters(BeanUtil.toJsonString(alertParametersVO));
            }
            // 更新数据
            flag = alertRuleService.updateById(alertRule);
            // 重新刷新缓存
            this.loadDeviceCache();
        } else {
            // 实例或设备具体告警规则
            AlertConfig alertConfig = getById(param.getId());
            if (ObjectUtils.isEmpty(alertConfig)) {
                throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.COMMON_RECORD_NOT_EXIST, param.getId()));
            }
            // 设置新的状态
            alertConfig.setStatus(param.getStatus());
            List<AlertParametersVO> alertParameters = AlertConfigConverter.toAlertConfigVo(alertConfig).getAlertParameters();
            if (!ObjectUtils.isEmpty(alertParameters)) {
                boolean status = AlertRuleConstant.STATUS_YES.equals(param.getStatus());
                AlertParametersVO alertParametersVO = alertParameters.get(NumberConstants.ZERO);
                alertParametersVO.setFatalStatus(status);
                if (!ObjectUtils.isEmpty(alertParametersVO.getWarningStatus())) {
                    alertParametersVO.setWarningStatus(status);
                }
                alertConfig.setParameters(BeanUtil.toJsonString(alertParametersVO));
            }
            // 更新数据
            flag = updateById(alertConfig);
            // 重新刷新缓存
            this.updateDeviceCache(Collections.singletonList(alertConfig));
        }
        return flag;
    }

    @Override
    public boolean apply(AlertApplyParam param) {
        Long selectObjectId = param.getSelectObjectId();
        String tableName = param.getTableName();

        if (ObjectUtils.isEmpty(selectObjectId) || StringUtils.isEmpty(tableName)) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.BASE_ALERT_CONFIG_PARAM_NOT_NULL));
        }

        List<Long> objectIdList = param.getObjectIdList();
        if (CollectionUtil.isNotEmpty(objectIdList)) {
            // 如果勾选了自己，排除掉
            List<Long> newObjectIdlist = new ArrayList<>();
            for (Long objectId : objectIdList) {
                if (!selectObjectId.equals(objectId)) {
                    newObjectIdlist.add(objectId);
                }
            }
            // 根据选择的设备或节点id查询已经设置的告警数据
            List<AlertConfig> configList = lambdaQuery().eq(AlertConfig::getObjectId, selectObjectId).isNull(AlertConfig::getDbName).list();
            // 根据选择的设备或节点id查询已经设置的通知方式数据
            List<AlertNoticeConfig> noticeConfigList = alertNoticeConfigService.lambdaQuery().eq(AlertNoticeConfig::getObjectId, selectObjectId).list();

            List<AlertConfig> dataList = new ArrayList<>();
            List<AlertNoticeConfig> noticeDataList = new ArrayList<>();
            for (Long objectId : newObjectIdlist) {
                for (AlertConfig config : configList) {
                    Long newId = IdWorker.getId().nextId();
                    AlertConfig newConfig = new AlertConfig();
                    BeanUtils.copyProperties(config, newConfig);
                    newConfig.setId(newId);
                    // 重新设置告警对象id
                    newConfig.setObjectId(objectId);
                    dataList.add(newConfig);

                    // 在通知方式集合中，找到告警项设置的对应的通知方式
                    List<AlertNoticeConfig> curNoticeConfigList = noticeConfigList.stream().filter(item -> item.getAlertConfigId().equals(config.getId())).collect(Collectors.toList());
                    for (AlertNoticeConfig noticeConfig : curNoticeConfigList) {
                        AlertNoticeConfig newNoticeConfig = new AlertNoticeConfig();
                        BeanUtils.copyProperties(noticeConfig, newNoticeConfig);
                        newNoticeConfig.setAlertConfigId(newId);
                        newNoticeConfig.setId(IdWorker.getId().nextId());
                        // 重新设置告警对象id
                        newNoticeConfig.setObjectId(objectId);
                        noticeDataList.add(newNoticeConfig);
                    }
                }
            }
            if (CollectionUtil.isNotEmpty(dataList)) {
                // 删除已经保存的配置数据
                remove(new LambdaQueryWrapper<AlertConfig>().in(AlertConfig::getObjectId, newObjectIdlist).isNull(AlertConfig::getDbName));
                // 批量保存配置数据
                saveBatch(dataList);

                // 删除已经保存的通知方式数据
                alertNoticeConfigService.remove(new LambdaQueryWrapper<AlertNoticeConfig>().in(AlertNoticeConfig::getObjectId, newObjectIdlist));
                if (CollectionUtil.isNotEmpty(noticeDataList)) {
                    // 批量保存通知方式中间表数据
                    alertNoticeConfigService.saveBatch(noticeDataList);
                }

                // 重新刷新缓存
                this.updateDeviceCache(dataList);
            }
        }

        return true;
    }

    @Override
    public List<AlertConfigVO> getUpdateAlertList(List<AlertConfigVO> alarmRuleList) {
        // 查询默认告警规则配置的通知方式信息
        List<Long> ruleIds = alarmRuleList.stream().map(AlertConfigVO::getRuleId).collect(toList());
        List<AlertNoticeConfig> noticeConfigList = alertNoticeConfigService.lambdaQuery().in(AlertNoticeConfig::getAlertConfigId, ruleIds).list();
        Map<Long, List<AlertNoticeConfig>> noticeList = noticeConfigList.stream()
                .collect(groupingBy(AlertNoticeConfig::getAlertRuleId));
        // 更新的告警规则集和
        List<AlertConfigVO> updateConfigList = new ArrayList<>();
        for (AlertConfigVO alertConfigVO : alarmRuleList) {
            // 新告警规则
            List<AlertParametersVO> alertParameters = alertConfigVO.getAlertParameters();
            // 原告警规则
            List<AlertParametersVO> defaultParameters = alertConfigVO.getDefaultParameters();
            if (!ObjectUtils.isEmpty(alertParameters) && !ObjectUtils.isEmpty(defaultParameters)) {
                AlertParametersVO latest = alertParameters.get(0);
                AlertParametersVO original = defaultParameters.get(0);
                boolean res = ObjectUtils.nullSafeEquals(latest, original);
                if (!res) {
                    updateConfigList.add(alertConfigVO);
                } else {
                    // 阈值未修改 判断通知方式是否更改
                    List<AlertNoticeConfig> originalNotice = noticeList.get(alertConfigVO.getRuleId());
                    List<String> selectNoticeList = alertConfigVO.getSelectNoticeList();
                    if (ObjectUtils.isEmpty(originalNotice)) {
                        if (!ObjectUtils.isEmpty(selectNoticeList)) {
                            // 原告警规则通知方式未空 新配置不为空 更新告警规则
                            updateConfigList.add(alertConfigVO);
                        }
                    } else {
                        if (ObjectUtils.isEmpty(selectNoticeList)) {
                            // 原告警规则通知方式不未空 新配置未空 更新告警规则
                            updateConfigList.add(alertConfigVO);
                        } else {
                            // 新旧配置均不未空 判断是否相等
                            String recipient = originalNotice.get(NumberConstants.ZERO).getRecipient();
                            List<String> recipientList = Arrays.stream(recipient.split(SymbolConstants.COMMA_DELIMITER))
                                    .map(String::trim).filter(s -> !s.isEmpty()).collect(toList());
                            if (!new HashSet<>(recipientList).equals(new HashSet<>(selectNoticeList))) {
                                // 不相等 更新告警规则
                                updateConfigList.add(alertConfigVO);
                            }
                        }
                    }
                }
            }
        }
        return updateConfigList;
    }

    @Override
    public void updateAlert(AlertConfigVO config, Long objectId) {
        // 要保存的告警规则集和
        List<AlertConfig> saveConfigList = new ArrayList<>();
        AlertParametersVO latest = config.getAlertParameters().get(0);
        AlertConfig save = new AlertConfig();
        BeanUtils.copyProperties(config, save);
        latest.initParamDatas();
        save.setParameters(BeanUtil.toJsonString(latest));
        String status = (!ObjectUtils.isEmpty(latest.getWarningStatus()) && latest.getWarningStatus()) ||
                (!ObjectUtils.isEmpty(latest.getFatalStatus()) && latest.getFatalStatus()) ?
                AlertRuleConstant.STATUS_YES : AlertRuleConstant.STATUS_NO;
        save.setStatus(status);
        save.setId(IdWorker.getId().nextId());
        save.setObjectId(objectId);
        saveConfigList.add(save);
        if (!ObjectUtils.isEmpty(config.getSelectNoticeList())) {
            // 保存通知方式
            NoticeConfigVO vo = new NoticeConfigVO();
            vo.setAlarmMessageId(config.getNoticeId());
            String notice = config.getSelectNoticeList().stream().filter(s -> !s.isEmpty()).collect(Collectors.joining(SymbolConstants.COMMA_DELIMITER));
            vo.setRecipient(notice);
            alertNoticeConfigService.save(save, List.of(vo));
        }
        // 保存告警规则
        saveBatch(saveConfigList);
    }
}