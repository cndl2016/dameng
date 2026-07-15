package com.dm.cn.base.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dm.cn.base.constant.SysMessageConstant;
import com.dm.cn.base.entity.enums.SafeStatusType;
import com.dm.cn.base.entity.param.DeviceAlarmParam;
import com.dm.cn.base.entity.server.SysMessage;
import com.dm.cn.base.entity.vo.AlertConfigVO;
import com.dm.cn.base.service.AlertConfigService;
import com.dm.cn.base.service.DeviceAlarmService;
import com.dm.cn.base.service.SysMessageService;
import com.dm.cn.common.param.DeviceParam;
import com.dm.cn.device.entity.server.Device;
import com.dm.cn.device.service.DeviceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author root
 */
@Service
public class DeviceAlarmServiceImpl implements DeviceAlarmService {

    @Resource
    private DeviceService deviceService;
    @Resource
    private AlertConfigService alertConfigService;
    @Resource
    private SysMessageService sysMessageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addDeviceAndAlarm(DeviceAlarmParam param) {
        // 判断要保存的告警规则
        List<AlertConfigVO> updateAlertList = alertConfigService.getUpdateAlertList(param.getAlarmRuleList());
        // 保存设备
        List<Device> deviceList = param.getDeviceList();

        for (Device device : deviceList) {
            device.setNodeId(param.getNodeId());
            // 保存设备
            deviceService.saveOrUpdateDevice(device);
            for (AlertConfigVO config : updateAlertList) {
                alertConfigService.updateAlert(config, device.getId());
            }
        }

        return true;
    }

    @Override
    public IPage<Device> getListDevice(DeviceParam param) {
        // 查询设备列表
        IPage<Device> devicePage = deviceService.getListDevice(param);
        // 获取设备告警记录
        List<Device> deviceList = devicePage.getRecords();
        List<Long> ids = deviceList.stream().map(Device::getId).collect(Collectors.toList());
        List<SysMessage> sysMessageList = sysMessageService.lambdaQuery()
                .in(!ObjectUtils.isEmpty(ids),SysMessage::getObjectId, ids)
                .eq(SysMessage::getActiveState, SysMessageConstant.STATUS_LIVE)
                .list();
        Map<String, List<SysMessage>> deviceMessageMap = sysMessageList.stream().collect(Collectors.groupingBy(SysMessage::getObjectId));
        for (Device device : deviceList) {
            List<SysMessage> messageList = deviceMessageMap.get(device.getId().toString());
            if (ObjectUtils.isEmpty(messageList)) {
                // 设备安全状态为安全
                device.setSafeStatus(SafeStatusType.SAFE.getValue());
            } else {
                // 设置告警信息
                List<String> contentList = messageList.stream().map(SysMessage::getContent).collect(Collectors.toList());
                device.setMessageList(contentList);
                Map<String, List<SysMessage>> msgLevelMap = messageList.stream().collect(Collectors.groupingBy(SysMessage::getMsgLevel));
                String safeStatus = ObjectUtils.isEmpty(msgLevelMap.get(SysMessageConstant.LEVEL_FATAL)) ?
                        SafeStatusType.LOW_RISK.getValue() : SafeStatusType.HIGH_RISK.getValue();
                device.setSafeStatus(safeStatus);
            }
        }
        return devicePage;
    }
}
