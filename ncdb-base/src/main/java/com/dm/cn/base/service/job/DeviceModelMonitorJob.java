package com.dm.cn.base.service.job;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dm.cn.base.entity.server.SysJobLog;
import com.dm.cn.base.entity.vo.DeviceGatherInfoVO;
import com.dm.cn.base.service.AlertBaseService;
import com.dm.cn.base.service.SysJobLogService;
import com.dm.cn.common.core.constant.Constants;
import com.dm.cn.common.core.constant.NumberConstants;
import com.dm.cn.common.core.enums.AlarmConditionEnum;
import com.dm.cn.common.core.enums.JobLogStatusEnum;
import com.dm.cn.common.core.utils.IdWorker;
import com.dm.cn.common.core.utils.StringUtils;
import com.dm.cn.common.core.utils.unit.UnitUtils;
import com.dm.cn.common.enums.DeviceEnum;
import com.dm.cn.common.utils.server.Arith;
import com.dm.cn.common.utils.server.DiskIo;
import com.dm.cn.common.utils.server.NetWork;
import com.dm.cn.common.utils.server.Server;
import com.dm.cn.device.entity.server.Device;
import com.dm.cn.device.service.DeviceService;
import com.dm.cn.device.service.SshdService;
import com.dm.framework.common.util.CollectionUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 设备监控模板任务(单片)
 *
 * @author root
 * @date 2023/01/11
 */
@Component
@Data
@NoArgsConstructor
public class DeviceModelMonitorJob {

    private static final Logger log = LoggerFactory.getLogger(DeviceModelMonitorJob.class);

    @Resource
    private DeviceService deviceService;

    @Resource
    private SysJobLogService sysJobLogService;

    @Resource
    private AlertBaseService alertBaseService;

    @Resource
    private SshdService sshdService;

    /**
     * 设备模板监控任务（quartz逻辑调用）
     *
     * @param jobParam 任务参数
     */
    public void deviceModelMonitorJob(String jobParam) {
        // 查询获取所有在线设备
        List<Device> deviceList = deviceService.list(new LambdaQueryWrapper<Device>().eq(Device::getConnStatus, DeviceEnum.CONN.getValue()));
        // 遍历执行监控任务
        deviceList.forEach(device -> {
            execute(device.getId(), device.getDeviceIp());
        });
    }

    private void execute(Long deviceId, String deviceIp) {
        try {
            Date date = new Date();
            Thread.sleep(10000);
            log.info("开始执行设备监控任务: " + deviceIp);
            Device device = deviceService.getById(deviceId);
            if (DeviceEnum.DIS_CONN.getValue().equals(device.getConnStatus())) {
                return;
            }
            //　获取系统信息
            Server server = sshdService.deviceMonitor(deviceIp);
            NetWork netWork = server.getNetWork();
            float fileUsed = server.getSysFiles().stream().mapToLong(sys -> UnitUtils.converFileSizeStrToByte(sys.getUsed())).sum();
            float fileTotal = server.getSysFiles().stream().mapToLong(sys -> UnitUtils.converFileSizeStrToByte(sys.getTotal())).sum();
            // 获取设备监控项值
            Map<String, Float> deviceValueMap = new HashMap<>(NumberConstants.FIVE);
            gatherDeviceInfo(server, netWork, fileUsed, fileTotal, deviceValueMap);
            // 获取cpu负载信息
            gatherCpuLoad(deviceIp, deviceValueMap);
            // 采集数据入库
            List<SysJobLog> sysJobLogs = new ArrayList<>();
            for (String value : AlarmConditionEnum.DEVICE_CONDITION_GROUP) {
                SysJobLog sysJobLog = new SysJobLog();
                Float monitorValue = deviceValueMap.get(value);
                if (monitorValue != null && monitorValue > NumberConstants.ZERO) {
                    sysJobLog.setMonitorValues(new BigDecimal(Float.toString(monitorValue)).setScale(2, RoundingMode.HALF_UP).floatValue());
                } else {
                    sysJobLog.setMonitorValues(NumberConstants.ZERO);
                }
                sysJobLog.setJobMessage(deviceIp + " " + AlarmConditionEnum.getDesc(value));
                sysJobLog.setAlarmCondition(value);
                sysJobLogs.add(sysJobLog);
            }
            // 磁盘IO数据入库（单独处理）
            if (CollectionUtil.isNotEmpty(server.getDiskIoList())) {
                for (DiskIo diskIo : server.getDiskIoList()) {
                    gatherDiskInfo(diskIo, deviceValueMap);
                    for (String value : AlarmConditionEnum.DEVICE_DISK_CONDITION_GROUP) {
                        SysJobLog sysJobLog = new SysJobLog();
                        Float monitorValue = deviceValueMap.get(value);
                        if (monitorValue != null && monitorValue > NumberConstants.ZERO) {
                            sysJobLog.setMonitorValues(new BigDecimal(Float.toString(monitorValue)).setScale(2, RoundingMode.HALF_UP).floatValue());
                        } else {
                            sysJobLog.setMonitorValues(NumberConstants.ZERO);
                        }
                        sysJobLog.setObjectItem(diskIo.getDiskName());
                        sysJobLog.setJobMessage(deviceIp + " " + AlarmConditionEnum.getDesc(value));
                        sysJobLog.setAlarmCondition(value);
                        sysJobLogs.add(sysJobLog);
                    }
                }
            }
            // 设置公共信息
            for (SysJobLog sysJobLog : sysJobLogs) {
                sysJobLog.setJobLogId(IdWorker.getId().nextId());
                sysJobLog.setObjectId(deviceId);
                sysJobLog.setStatus(JobLogStatusEnum.SUCCEED.getValue());
                sysJobLog.setCreateTime(date);
            }
            // 入库
            sysJobLogService.saveBatch(sysJobLogs);
            // 计算是否告警
            DeviceGatherInfoVO deviceGatherInfoVo = new DeviceGatherInfoVO();
            deviceGatherInfoVo.setMonitorTime(date);
            deviceGatherInfoVo.setId(deviceId.toString());
            deviceGatherInfoVo.setAgentIp(deviceIp);
            deviceGatherInfoVo.setGatherStatus(true);
            deviceGatherInfoVo.setIsEndRightNow(false);
            deviceGatherInfoVo.setCpuUsage(deviceValueMap.get(AlarmConditionEnum.DEVICE_CPU.getValue()));
            alertBaseService.calcAlert(deviceGatherInfoVo, AlarmConditionEnum.DEVICE_CPU.name().toLowerCase());
            deviceGatherInfoVo.setMemUsage(deviceValueMap.get(AlarmConditionEnum.DEVICE_MEM.getValue()));
            alertBaseService.calcAlert(deviceGatherInfoVo, AlarmConditionEnum.DEVICE_MEM.name().toLowerCase());
            deviceGatherInfoVo.setDiskUsage(deviceValueMap.get(AlarmConditionEnum.DEVICE_DISK.getValue()));
            alertBaseService.calcAlert(deviceGatherInfoVo, AlarmConditionEnum.DEVICE_DISK.name().toLowerCase());
            // 告警项设备网络上下行速率（阈值单位MB/S 监控值单位KB/S）
            deviceGatherInfoVo.setNetworkInput(deviceValueMap.get(AlarmConditionEnum.NETWORK_INPUT.getValue()) / 1024);
            alertBaseService.calcAlert(deviceGatherInfoVo, AlarmConditionEnum.NETWORK_INPUT.name().toLowerCase());
            deviceGatherInfoVo.setNetworkOutput(deviceValueMap.get(AlarmConditionEnum.NETWORK_OUTPUT.getValue()) / 1024);
            alertBaseService.calcAlert(deviceGatherInfoVo, AlarmConditionEnum.NETWORK_OUTPUT.name().toLowerCase());
        } catch (Exception e) {
            log.error("执行设备监控任务: " + deviceIp + "错误: {}", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 设备监控
     */
    private void gatherDeviceInfo(Server server, NetWork netWork, float fileUsed, float fileTotal, Map<String, Float> deviceValueMap) {
        deviceValueMap.put(AlarmConditionEnum.DEVICE_CPU.getValue(), (float) (server.getCpu().getUsed()));
        deviceValueMap.put(AlarmConditionEnum.DEVICE_MEM.getValue(), (float) Arith.round(Arith.mul((server.getMem().getTotal() > NumberConstants.ZERO ? (server.getMem().getUsed() / server.getMem().getTotal()) : NumberConstants.ZERO), NumberConstants.ONE_HUNDRED), NumberConstants.TWO));
        deviceValueMap.put(AlarmConditionEnum.DEVICE_DISK.getValue(), (float) Arith.round(Arith.mul((fileTotal > NumberConstants.ZERO ? (fileUsed / fileTotal) : NumberConstants.ZERO), NumberConstants.ONE_HUNDRED), NumberConstants.TWO));
        deviceValueMap.put(AlarmConditionEnum.NETWORK_INPUT.getValue(), BigDecimal.valueOf(netWork.getBytesRecv()).floatValue());
        deviceValueMap.put(AlarmConditionEnum.NETWORK_OUTPUT.getValue(), BigDecimal.valueOf(netWork.getBytesSent()).floatValue());
    }

    /**
     * 磁盘IO数据
     */
    private void gatherDiskInfo(DiskIo diskIo, Map<String, Float> deviceValueMap) {
        deviceValueMap.put(AlarmConditionEnum.DEVICE_DISK_READ_IOPS.getValue(), Float.valueOf(diskIo.getReadIops()));
        deviceValueMap.put(AlarmConditionEnum.DEVICE_DISK_WRITE_IOPS.getValue(), Float.valueOf(diskIo.getWriteIops()));
        deviceValueMap.put(AlarmConditionEnum.DEVICE_DISK_READ_KB.getValue(), Float.valueOf(diskIo.getReadKb()));
        deviceValueMap.put(AlarmConditionEnum.DEVICE_DISK_WRITE_KB.getValue(), Float.valueOf(diskIo.getWriteKb()));
        deviceValueMap.put(AlarmConditionEnum.DEVICE_DISK_READ_WAIT.getValue(), Float.valueOf(diskIo.getReadWait()));
        deviceValueMap.put(AlarmConditionEnum.DEVICE_DISK_WRITE_WAIT.getValue(), Float.valueOf(diskIo.getWriteWait()));
    }

    /**
     * cpu负载信息
     */
    private void gatherCpuLoad(String deviceIp, Map<String, Float> deviceValueMap) {
        String str = sshdService.execCmd(deviceIp, "uptime");
        if (StringUtils.isNotEmpty(str) && str.contains(Constants.LOAD_AVERAGE)) {
            String cpuLoadStr = StringUtils.substringAfter(str, "load average: ");
            List<String> cpuLoadStrs = StrUtil.split(cpuLoadStr, ",");
            deviceValueMap.put(AlarmConditionEnum.DEVICE_CPU_LOAD_ONE_MINUTE.getValue(), !cpuLoadStrs.isEmpty() ? Float.valueOf(cpuLoadStrs.get(0)) : 0);
            deviceValueMap.put(AlarmConditionEnum.DEVICE_CPU_LOAD_FIVE_MINUTE.getValue(), cpuLoadStrs.size() > 1 ? Float.valueOf(cpuLoadStrs.get(1)) : 0);
            deviceValueMap.put(AlarmConditionEnum.DEVICE_CPU_LOAD_FIFTEEN_MINUTE.getValue(), cpuLoadStrs.size() > 2 ? Float.valueOf(cpuLoadStrs.get(2)) : 0);
        }
    }

}