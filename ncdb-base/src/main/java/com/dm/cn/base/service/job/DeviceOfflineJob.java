package com.dm.cn.base.service.job;


import com.dm.cn.base.entity.vo.DeviceGatherInfoVO;
import com.dm.cn.base.service.AlertBaseService;
import com.dm.cn.common.core.constant.TokenConstants;
import com.dm.cn.common.core.enums.AlarmConditionEnum;
import com.dm.cn.common.enums.DeviceEnum;
import com.dm.cn.common.enums.EncodeModeEnum;
import com.dm.cn.common.utils.CmdUtil;
import com.dm.cn.common.utils.ssh.pool.sshj.SshBuilder;
import com.dm.cn.device.entity.server.Device;
import com.dm.cn.device.service.DeviceService;
import com.dm.cn.system.config.security.service.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 设备离线告警任务
 *
 * @author root
 * @date 2023/04/13
 */
@Component
public class DeviceOfflineJob {

    private static final Logger log = LoggerFactory.getLogger(DeviceOfflineJob.class);

    @Resource
    private DeviceService deviceService;

    @Resource
    private CacheService cacheService;

    @Resource
    private AlertBaseService alertBaseService;

    /**
     * 设备离线监控任务（quartz逻辑调用）
     *
     * @param jobParam 任务参数
     */
    public void deviceOfflineJob(String jobParam) {
        log.info("执行设备离线任务: ");
        List<Device> deviceList;
        // 获取设备信息
        if (cacheService.hasKey(TokenConstants.DEVICE_LIST_KEY)) {
            deviceList = cacheService.getCacheObject(TokenConstants.DEVICE_LIST_KEY);
        } else {
            deviceList = deviceService.lambdaQuery().list();
            cacheService.setCacheObject(TokenConstants.DEVICE_LIST_KEY, deviceList);
        }
        deviceList.forEach(device -> {
            String pwd = CmdUtil.handleEncodePass(device.getDeviceSshPwd(), EncodeModeEnum.DECODE.name());
            DeviceGatherInfoVO deviceGatherInfoVo = new DeviceGatherInfoVO();
            deviceGatherInfoVo.setMonitorTime(new Date());
            deviceGatherInfoVo.setId(device.getId().toString());
            deviceGatherInfoVo.setAgentIp(device.getDeviceIp());
            deviceGatherInfoVo.setGatherStatus(true);
            deviceGatherInfoVo.setIsEndRightNow(false);
            // 检查设备是否在线
            boolean login = new SshBuilder().connTest(device.getDeviceIp(), device.getDeviceSshUsr(), device.getPort(), pwd);
            if (!login) {
                // 更新设备启用状态和连接状态
                deviceService.lambdaUpdate()
                        .set(Device::getConnStatus, DeviceEnum.DIS_CONN.getValue())
                        .eq(Device::getId, device.getId())
                        .update();
                deviceGatherInfoVo.setStatus(DeviceEnum.DIS_CONN.getValue());
            } else {
                // 更新设备启用状态和连接状态
                deviceService.lambdaUpdate()
                        .set(Device::getConnStatus, DeviceEnum.CONN.getValue())
                        .eq(Device::getId, device.getId())
                        .update();
                deviceGatherInfoVo.setStatus(DeviceEnum.CONN.getValue());
            }
            // 计算是否告警
            alertBaseService.calcAlert(deviceGatherInfoVo, AlarmConditionEnum.DEVICE_OFFLINE.name().toLowerCase());
        });
    }
}
