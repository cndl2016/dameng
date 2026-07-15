package com.dm.cn.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dm.cn.base.entity.param.AlarmMessageParam;
import com.dm.cn.base.entity.server.AlarmMessage;
import com.dm.cn.base.entity.server.AlertNoticeConfig;
import com.dm.cn.base.mapper.AlarmMessageMapper;
import com.dm.cn.base.service.AlarmMessageService;
import com.dm.cn.base.service.AlertNoticeConfigService;
import com.dm.cn.common.core.utils.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author dameng
 * @description 针对表【T_ALARM_MESSAGE(告警消息表)】的数据库操作Service实现
 * @createDate 2024-10-15 11:46:34
 */
@Service
public class AlarmMessageServiceImpl extends ServiceImpl<AlarmMessageMapper, AlarmMessage>
        implements AlarmMessageService {

    @Resource
    private AlertNoticeConfigService alertNoticeConfigService;

    @Override
    public boolean add(AlarmMessageParam param) {
        boolean flag = false;
        AlarmMessage alarmMessage = param.getAlarmMessage();
        // 如果都为空就不保存，认为它是脏数据
        if (StringUtils.isNotEmpty(alarmMessage.getHttpUrl()) || StringUtils.isNotEmpty(alarmMessage.getHttpSign())
                || StringUtils.isNotEmpty(alarmMessage.getHttpsProxy()) || StringUtils.isNotEmpty(alarmMessage.getToUser())
                || StringUtils.isNotEmpty(alarmMessage.getToPhone()) || StringUtils.isNotEmpty(alarmMessage.getClientCd())
                || StringUtils.isNotEmpty(alarmMessage.getServiceCd())) {
            saveOrUpdate(alarmMessage);
            flag = true;
        }

        if (!flag && !ObjectUtils.isEmpty(alarmMessage.getId())) {
            AlarmMessage save = getById(alarmMessage.getId());
            if (null != save) {
                // 删除主表
                removeById(alarmMessage.getId());

                // 删除中间表
                alertNoticeConfigService.remove(new LambdaQueryWrapper<AlertNoticeConfig>().eq(AlertNoticeConfig::getAlarmMessageId, alarmMessage.getId()));
            }
        }

        return true;
    }

    @Override
    public List<AlarmMessage> getAll() {
        return lambdaQuery().orderByAsc(AlarmMessage::getSendMethod).list();
    }

}