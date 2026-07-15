package com.dm.cn.base.service.impl;


import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.log.StaticLog;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dm.cn.base.constant.SysMessageConstant;
import com.dm.cn.base.entity.param.AlarmCountParam;
import com.dm.cn.base.entity.server.AlarmMessage;
import com.dm.cn.base.entity.server.AlertNoticeConfig;
import com.dm.cn.base.entity.server.AlertNoticeMessage;
import com.dm.cn.base.entity.server.SysMessage;
import com.dm.cn.base.entity.vo.SysMessageVO;
import com.dm.cn.base.mapper.SysMessageMapper;
import com.dm.cn.base.service.*;
import com.dm.cn.common.cache.utils.FastJson;
import com.dm.cn.common.core.constant.Constants;
import com.dm.cn.common.core.constant.NumberConstants;
import com.dm.cn.common.core.utils.DateUtils;
import com.dm.cn.common.core.utils.IdWorker;
import com.dm.cn.common.core.utils.StringUtils;
import com.dm.cn.common.enums.SendMethodEnum;
import com.dm.cn.common.param.SysMessageParam;
import com.dm.cn.common.utils.I18nMessageUtil;
import com.dm.cn.device.service.DeviceService;
import com.dm.cn.system.constant.MessageTipConstant;
import com.dm.cn.system.service.ISysMessageTipService;
import com.dm.cn.system.service.ISysUserService;
import com.dm.framework.common.util.CollectionUtil;
import com.dm.framework.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;


/**
 * 系统消息、监控告警表逻辑实现
 *
 * @author Auto-Coder
 * @date 2025-02-11
 */
@Service
public class SysMessageServiceImpl extends ServiceImpl<SysMessageMapper, SysMessage> implements SysMessageService {

    private static final Logger log = LoggerFactory.getLogger(SysMessageServiceImpl.class);

    @Resource
    private ISysUserService sysUserService;

    @Resource
    private SysMessageMapper sysMessageMapper;

    @Resource
    private DeviceService deviceService;

    @Resource
    private AlertNoticeConfigService alertNoticeConfigService;

    @Resource
    private AlarmMessageService alarmMessageService;

    private AlarmReportService alarmReportWithMailService;
    private AlarmReportService alarmReportWithDingService;
    private AlarmReportService alarmReportWithLarkService;
    private AlarmReportService alarmReportWithSmsService;
    private AlarmReportService alarmReportWithWeComService;
    private AlarmReportService alarmReportWithWeOaService;

    @Autowired
    public void setAlarmReportService(@Qualifier("AlarmReportWithMailService") AlarmReportService alarmReportWithMailService,
                                      @Qualifier("AlarmReportWithDingService") AlarmReportService alarmReportWithDingService,
                                      @Qualifier("AlarmReportWithLarkService") AlarmReportService alarmReportWithLarkService,
                                      @Qualifier("AlarmReportWithSmsService") AlarmReportService alarmReportWithSmsService,
                                      @Qualifier("AlarmReportWithWeComService") AlarmReportService alarmReportWithWeComService,
                                      @Qualifier("AlarmReportWithWeComService") AlarmReportService alarmReportWithWeOaService) {
        this.alarmReportWithMailService = alarmReportWithMailService;
        this.alarmReportWithDingService = alarmReportWithDingService;
        this.alarmReportWithLarkService = alarmReportWithLarkService;
        this.alarmReportWithSmsService = alarmReportWithSmsService;
        this.alarmReportWithWeComService = alarmReportWithWeComService;
        this.alarmReportWithWeOaService = alarmReportWithWeOaService;
    }

    @Resource
    private AlertNoticeMessageService alertNoticeMessageService;

    @Resource
    private ISysMessageTipService sysMessageTipService;

    private static final ReadWriteLock LOCK = new ReentrantReadWriteLock();

    private static final Lock WRITE_LOCK = LOCK.writeLock();

    private final Map<String,SysMessage> msgMap = new ConcurrentHashMap<>(16);

    @Override
    public IPage<SysMessageVO> getSysMessagePageList(SysMessageParam param) {
        IPage<SysMessageVO> page = new Page<>(param.getPageNum(), param.getPageSize());
        // 获取查询时间点
        int hourNo = ObjectUtils.isEmpty(param.getTime()) ? NumberConstants.ZERO : param.getTime();
        LocalDateTime time = LocalDateTime.of(LocalDate.now(), LocalTime.now()).minusHours(hourNo);
        // 获取数据权限过滤后的用户列表
        List<String> userNameList = sysUserService.getUserPermission();
        IPage<SysMessageVO> result = sysMessageMapper.getSysMessagePageList(page, param, time, userNameList);
        return result;
    }

    @Override
    public List<SysMessageVO> exportSysMessageList(SysMessageParam param) {
        return getSysMessagePageList(param).getRecords();
    }

    @Override
    public Long createAlertMessage(String alarmCondition, Long ruleId, String title, String tableName, Long objectId, String resourceName, String kind, String level, Date createTime, String messageContent, String messageContentEn, Boolean isEndRightNow, Long alertId, String objectDbName) {
        SysMessage message;
        Long messageId = IdWorker.getId().nextId();

        if (ruleId == null || objectId == null) {
            return null;
        }

        //防止多后台，及其它高并发写消息重复的情况
        WRITE_LOCK.lock();
        try {
            // 如果是直接结束的告警，则直接创建新的消息
            if (isEndRightNow) {
                message = saveSysMessage(alarmCondition, ruleId, title, tableName, objectId, resourceName, kind, level, createTime, messageContent, messageContentEn, messageId, SysMessageConstant.STATUS_DEATH, objectDbName);
            } else {
                SysMessage sysMessage = getLatelyAlert(ruleId, objectId, kind, SysMessageConstant.KIND_SITUATION.equals(kind)?level:null, objectDbName);
                if (sysMessage != null) {
                    //1...如果存在，且告警级别一致，则更新结束时间
                    //2...如果存在，但告警级别不一致，则将旧的置为过期，创建新的
                    if (StringUtil.isNotEmpty(sysMessage.getMsgLevel()) && sysMessage.getMsgLevel().equalsIgnoreCase(level)) {
                        //告警级别一致
                        //更新数据库,更新结束时间
                        sysMessage.setEndTime(new Date());
                        sysMessage.setContent(messageContent);
                        updateById(sysMessage);
                        //没产生新的数据，直接返回
                        return null;
                    } else {
                        //告警级别不一致
                        //更新数据库:将数据库数据状态置为过期
                        deleteAlertMessage(ruleId, objectId, kind, null, objectDbName);
                        //创建新的
                        message = saveSysMessage(alarmCondition, ruleId, title, tableName, objectId, resourceName, kind, level, createTime, messageContent, messageContentEn, messageId, isEndRightNow? SysMessageConstant.STATUS_DEATH : SysMessageConstant.STATUS_LIVE, objectDbName);

                    }
                } else {
                    //3...如果不存在，则创建新的
                    message = saveSysMessage(alarmCondition, ruleId, title, tableName, objectId, resourceName, kind, level, createTime, messageContent, messageContentEn, messageId, isEndRightNow? SysMessageConstant.STATUS_DEATH : SysMessageConstant.STATUS_LIVE, objectDbName);
                }
            }
            message.setAlertConfigId(alertId);
        }finally {
            WRITE_LOCK.unlock();
        }
        if (message != null || !StringUtil.isEmpty(message.getObjectId())) {
            ExecutorService executor = ThreadUtil.newSingleExecutor();
            try {
                executor.execute(() -> {
                    StaticLog.info("自动发送告警消息，业务ID：{}", message.getId());
                    noticeMessage(message, true);
                });
            } finally {
                executor.shutdown();
            }
        }
        return messageId;
    }

    @Override
    public SysMessage saveSysMessage(String alarmCondition, Long ruleId, String title, String tableName, Long objectId, String resourceName, String kind, String level, Date createTime, String messageContent, String messageContentEn, Long messageId, String activeState, String objectDbName) {
        SysMessage message = new SysMessage();
        message.setId(messageId);
        message.setRuleId(ruleId.toString());
        message.setObjectId(objectId.toString());
        message.setResourceName(resourceName);
        message.setTitle(title);
        message.setAlarmCondition(alarmCondition);
        //获取英文
        String titleEn = sysMessageTipService.getMessageTip(Constants.EN, "T_ALERT_RULE", ruleId.toString(), "name");
        message.setTitleEn(titleEn);
        message.setContent(messageContent);
        message.setContentEn(messageContentEn);
        message.setMsgLevel(level);
        message.setMsgKind(kind);
        message.setActiveState(activeState);
        message.setCreateTime(createTime);
        message.setEndTime(createTime);
        message.setTableName(tableName);
        save(message);
        updateMsg(ruleId, objectId, kind, SysMessageConstant.KIND_SITUATION.equals(kind)?level:null,message, objectDbName);
        return message;
    }

    @Override
    public SysMessage getLatelyAlert(Long ruleId, Long objectId, String kind, String level, String objectDbName) {
        String key = generateKey(objectId, ruleId, kind, level, objectDbName);
        return msgMap.get(key);
    }

    @Override
    public void deleteAlertMessage(Long ruleId, Long objectId, String kind, String level, String objectDbName) {
        SysMessage message = getLatelyAlert(ruleId, objectId, kind, SysMessageConstant.KIND_SITUATION.equals(kind)?level:null, objectDbName);
        if (message != null && !StringUtil.isEmpty(message.getObjectId())) {
            ExecutorService executor = ThreadUtil.newSingleExecutor();
            try {
                executor.execute(() -> {
                    StaticLog.info("自动发送告警解除消息，业务ID：{}", message.getId());
                    noticeMessage(message, false);
                });
            } finally {
                executor.shutdown();
            }
            LambdaUpdateWrapper<SysMessage> wrapper = new LambdaUpdateWrapper<>();
            wrapper.set(SysMessage::getActiveState, SysMessageConstant.STATUS_DEATH);
            wrapper.set(SysMessage::getEndTime, new Date());
            wrapper.eq(SysMessage::getRuleId, ruleId);
            wrapper.eq(SysMessage::getObjectId, objectId);
            if (StringUtil.isNotEmpty(kind)) {
                wrapper.eq(SysMessage::getMsgKind, kind);
            }
            if (StringUtil.isNotEmpty(level)) {
                wrapper.eq(SysMessage::getMsgLevel, level);
            }
            wrapper.eq(SysMessage::getActiveState, SysMessageConstant.STATUS_LIVE);
            update(wrapper);
        }
        deleteMsg(ruleId, objectId, kind, SysMessageConstant.KIND_SITUATION.equals(kind)?level:null, objectDbName);
    }

    @Override
    public Map<String, Object> getSysMessageData(AlarmCountParam param) {
        // 数据权限过滤
        List<String> userNameList = sysUserService.getUserPermission();
        // 通过当前日期获取1天之前的时间节点进行查询
        String queryTime = DateUtils.parseDateToStr(FastJson.DEFAULT_DATE_FORMAT, DateUtils.addDays(DateUtils.getNowDate(), NumberConstants.MINUS_ONE));
        List<SysMessageVO> sysMessageVoList = sysMessageMapper.getSysMessageVoList(param, userNameList, queryTime);
        // 2.如果设备不在线,将设备的状态位置为1
        //　在线且启用中的设备列表
        List<String> ipOnlineList = deviceService.getNodeDeviceIpList();
        for (SysMessageVO sysMessageVo : sysMessageVoList) {
            // 默认设置在线
            sysMessageVo.setDeviceStatus(String.valueOf(NumberConstants.ZERO));
            if (SysMessageConstant.TABLE_DEVICE.equals(sysMessageVo.getTableName())) {
                if (!StringUtils.isEmpty(sysMessageVo.getNodeIp()) && !ipOnlineList.contains(sysMessageVo.getNodeIp())) {
                    // 告警的设备是否在线  0:在 1:不在
                    sysMessageVo.setDeviceStatus(String.valueOf(NumberConstants.ONE));
                }
            }
        }
        // 返回结果集
        Map<String, Object> resultMap = new HashMap<>(NumberConstants.HASH_MAP_DEFAULT_INITIAL_CAPACITY);
        // 3.近24小时内的告警数量统计
        Integer totalNum = sysMessageVoList.size();
        Integer warnNum = (int) sysMessageVoList.stream().filter(x -> SysMessageConstant.LEVEL_WARNING.equals(x.getMsgLevel())).count();
        Integer fatalNum = (int) sysMessageVoList.stream().filter(x -> SysMessageConstant.LEVEL_FATAL.equals(x.getMsgLevel())).count();
        // 告警列表数据
        resultMap.put(Constants.TABLE_MESSAGE, sysMessageVoList);
        // 告警总数
        resultMap.put(Constants.TOTAL_NUM, totalNum);
        // 提醒告警总数
        resultMap.put(Constants.WARN_NUM, warnNum);
        // 严重告警总数
        resultMap.put(Constants.SERIOUS_NUM, fatalNum);
        return resultMap;
    }

    private String generateKey(Long objectId,Long ruleId,String kind,String level, String objectDbName){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(objectId).append("_").append(ruleId).append("_").append(kind);
        if(StringUtil.isNotEmpty(level)){
            stringBuilder.append(level);
        }
        if (StringUtils.isNotEmpty(objectDbName)) {
            stringBuilder.append(objectDbName);
        }
        return stringBuilder.toString();
    }

    private void updateMsg(Long ruleId, Long deviceId, String kind, String level,SysMessage sysMessage, String objectDbName){
        String key = generateKey(deviceId, ruleId, kind, level, objectDbName);
        msgMap.put(key,sysMessage);
    }

    private void deleteMsg(Long ruleId, Long deviceId, String kind, String level, String objectDbName){
        String key = generateKey(deviceId, ruleId, kind, level, objectDbName);
        msgMap.remove(key);
    }

    /**
     * 告警通知
     *
     * @param sysMessage
     * @param alertFlag 告警解除标志
     * @return
     */
    @Override
    public void noticeMessage(SysMessage sysMessage, boolean alertFlag) {
        LambdaQueryWrapper<AlertNoticeConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlertNoticeConfig::getAlertConfigId, sysMessage.getAlertConfigId())
                .eq(AlertNoticeConfig::getAlertRuleId, sysMessage.getRuleId());
        List<AlertNoticeConfig> alertNoticeConfigList = alertNoticeConfigService.list(wrapper);
        if (CollectionUtil.isEmpty(alertNoticeConfigList)) {
            return;
        }
        String msgLevel = SysMessageConstant.LEVEL_FATAL.equals(sysMessage.getMsgLevel()) ? "高风险" : "低风险";
        String content;
        String contentEn;
        //组装消息
        if (alertFlag) {
            content = I18nMessageUtil.getMessageZh(MessageTipConstant.BASE_MAIL_NOTICE_CONTENT_DEVICE, sysMessage.getResourceName(), msgLevel, sysMessage.getContent());
            contentEn = I18nMessageUtil.getMessageEn(MessageTipConstant.BASE_MAIL_NOTICE_CONTENT_DEVICE, sysMessage.getResourceName(), sysMessage.getMsgLevel(), sysMessage.getContentEn());
        } else {
            content = I18nMessageUtil.getMessageZh(MessageTipConstant.BASE_MAIL_NOTICE_RELEASE_CONTENT_DEVICE, sysMessage.getResourceName(), sysMessage.getTitle());
            contentEn = I18nMessageUtil.getMessageEn(MessageTipConstant.BASE_MAIL_NOTICE_RELEASE_CONTENT_DEVICE, sysMessage.getResourceName(), sysMessage.getTitleEn());
        }
        //获取通知配置并转换为map
        List<Long> alarmMessageIds = alertNoticeConfigList.stream().map(AlertNoticeConfig::getAlarmMessageId).collect(Collectors.toList());
        List<AlarmMessage> alarmMessageList = alarmMessageService.list(new LambdaQueryWrapper<AlarmMessage>().in(AlarmMessage::getId, alarmMessageIds));
        Map<Long, AlarmMessage> map = CollectionUtil.toMap(alarmMessageList, AlarmMessage::getId);

        //根据通知方式的不同来发送消息
        for (AlertNoticeConfig alertNoticeConfig : alertNoticeConfigList) {
            if (!map.containsKey(alertNoticeConfig.getAlarmMessageId())) {
                continue;
            }
            AlarmMessage alarmMessage = map.get(alertNoticeConfig.getAlarmMessageId());
            alarmMessage.setContent(content);
            alarmMessage.setContentEn(contentEn);
            //发送并记录日志
            AlertNoticeMessage alertNoticeMessage = new AlertNoticeMessage();
            try {
                alertNoticeMessage.setId(IdWorker.getId().nextId());
                alertNoticeMessage.setObjectId(Long.valueOf(sysMessage.getObjectId()));
                alertNoticeMessage.setRecipient(alertNoticeConfig.getRecipient());
                alertNoticeMessage.setTitle(sysMessage.getTitle());
                alertNoticeMessage.setTitleEn(sysMessage.getTitleEn());
                alertNoticeMessage.setContent(content);
                alertNoticeMessage.setContentEn(contentEn);
                alertNoticeMessage.setAlertLevel(sysMessage.getMsgLevel());
                alertNoticeMessage.setNoticeMethod(alarmMessage.getSendMethod());
                alertNoticeMessage.setNoticeTime(new Date());
                noticeBySendMethod(alertNoticeConfig, alarmMessage);
                alertNoticeMessage.setIsSuccess(NumberConstants.NUMBER_ONE);
            } catch (Exception e) {
                log.error("告警通知发送消息失败：{}", e);
                alertNoticeMessage.setIsSuccess(NumberConstants.NUMBER_ZERO);
                alertNoticeMessage.setErrorMessage(e.getMessage());
            }
            alertNoticeMessageService.save(alertNoticeMessage);
        }
    }

    @Override
    public void deleteByObjectId(Long instanceId, List<Long> nodeIds) {
        this.baseMapper.delete(new LambdaQueryWrapper<SysMessage>().eq(SysMessage::getObjectId, instanceId).in(SysMessage::getObjectId, nodeIds));
    }

    /**
     * 根据不同的方式通知
     * @param alertNoticeConfig
     * @param alarmMessage
     */
    private void noticeBySendMethod(AlertNoticeConfig alertNoticeConfig, AlarmMessage alarmMessage) {
        if (SendMethodEnum.MAIL.getValue().equals(alarmMessage.getSendMethod())) {
            alarmReportWithMailService.sendAlarmMessageList(alarmMessage, alertNoticeConfig.getRecipient());
        } else if (SendMethodEnum.LARK.getValue().equals(alarmMessage.getSendMethod())) {
            alarmReportWithLarkService.sendAlarmMessageList(alarmMessage, null);
        } else if (SendMethodEnum.DING.getValue().equals(alarmMessage.getSendMethod())) {
            alarmReportWithDingService.sendAlarmMessageList(alarmMessage, null);
        } else if (SendMethodEnum.WECOM.getValue().equals(alarmMessage.getSendMethod())) {
            alarmReportWithWeComService.sendAlarmMessageList(alarmMessage, null);
        } else if (SendMethodEnum.SMS.getValue().equals(alarmMessage.getSendMethod())) {
            alarmReportWithSmsService.sendAlarmMessageList(alarmMessage, alertNoticeConfig.getRecipient());
        } else if (SendMethodEnum.WEOA.getValue().equals(alarmMessage.getSendMethod())) {
            alarmReportWithWeOaService.sendAlarmMessageList(alarmMessage, alertNoticeConfig.getRecipient());
        }
    }
}
