package com.dm.cn.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dm.cn.base.entity.server.SysJobLog;
import com.dm.cn.base.mapper.SysJobLogMapper;
import com.dm.cn.base.service.SysJobLogService;
import com.dm.cn.common.core.constant.Constants;
import com.dm.cn.common.core.constant.NumberConstants;
import com.dm.cn.common.core.constant.SymbolConstants;
import com.dm.cn.common.core.enums.AlarmConditionEnum;
import com.dm.cn.common.core.utils.StringUtils;
import com.dm.cn.common.core.web.domain.AjaxResult;
import com.dm.cn.common.param.DeviceMonitorParam;
import com.dm.cn.common.utils.validate.ParamValidate;
import com.dm.cn.device.entity.server.Device;
import com.dm.cn.device.entity.vo.DeviceMonitorInfoVO;
import com.dm.cn.device.service.DeviceService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 针对【SYS_JOB_LOG（定时任务调度日志表）】的数据库操作Service实现类
 *
 * @author dameng
 * @date 2024/10/18
 */
@Service
public class SysJobLogServiceImpl extends ServiceImpl<SysJobLogMapper, SysJobLog>
        implements SysJobLogService {

    @Resource
    private DeviceService deviceService;

    @Resource
    private SysJobLogMapper sysJobLogMapper;

    /**
     * 获取设备监控Echart数据
     *
     * @param param
     * @return {@link AjaxResult}
     * @author cn 2024/07/11
     */
    @Override
    public DeviceMonitorInfoVO getMonitorDataForEchart(DeviceMonitorParam param) {
        DeviceMonitorInfoVO vo = new DeviceMonitorInfoVO();
        // 根据设备IP找到所属设备ID
        if (StringUtils.isNotEmpty(param.getDeviceIp())) {
            Device device = deviceService.lambdaQuery().eq(Device::getDeviceIp, param.getDeviceIp()).one();
            param.setDeviceId(device.getId());
        }
        String beginTime = String.valueOf(param.getParams().get(Constants.QUERY_PARAM_BEGIN_TIME));
        String endTime = String.valueOf(param.getParams().get(Constants.QUERY_PARAM_END_TIME));

        List<List<Object>> cpuData = new ArrayList<>();
        List<List<Object>> memData = new ArrayList<>();
        List<List<Object>> inputData = new ArrayList<>();
        List<List<Object>> outputData = new ArrayList<>();
        List<List<Object>> cpuLoadOneMinuteData = new ArrayList<>();
        List<List<Object>> cpuLoadFiveMinuteData = new ArrayList<>();
        List<List<Object>> cpuLoadFifteenMinuteData = new ArrayList<>();

        // 从定时任务日志表获取最新的数据
        List<SysJobLog> logList = lambdaQuery()
                .select(SysJobLog::getObjectId, SysJobLog::getAlarmCondition, SysJobLog::getCreateTime, SysJobLog::getMonitorValues)
                .in(SysJobLog::getAlarmCondition, List.of(
                        AlarmConditionEnum.DEVICE_CPU.getValue(),
                        AlarmConditionEnum.DEVICE_MEM.getValue(),
                        AlarmConditionEnum.NETWORK_INPUT.getValue(),
                        AlarmConditionEnum.NETWORK_OUTPUT.getValue(),
                        AlarmConditionEnum.DEVICE_CPU_LOAD_ONE_MINUTE.getValue(),
                        AlarmConditionEnum.DEVICE_CPU_LOAD_FIVE_MINUTE.getValue(),
                        AlarmConditionEnum.DEVICE_CPU_LOAD_FIFTEEN_MINUTE.getValue()))
                .eq(param.getDeviceId() != 0L, SysJobLog::getObjectId, param.getDeviceId())
                .ge(ParamValidate.validate(beginTime), SysJobLog::getCreateTime, beginTime)
                .le(ParamValidate.validate(endTime), SysJobLog::getCreateTime, endTime).orderByAsc(SysJobLog::getCreateTime)
                .list();

        logList.forEach(log -> {
            if (AlarmConditionEnum.DEVICE_CPU.getValue().equals(log.getAlarmCondition())) {
                // CPU数据
                cpuData.add(List.of(DateFormatUtils.format(log.getCreateTime(), "yyyy-MM-dd HH:mm:ss"),
                        new DecimalFormat("#.##").format(log.getMonitorValues())));
            } else if (AlarmConditionEnum.DEVICE_MEM.getValue().equals(log.getAlarmCondition())) {
                // 内存数据
                memData.add(List.of(DateFormatUtils.format(log.getCreateTime(), "yyyy-MM-dd HH:mm:ss"),
                        new DecimalFormat("#.##").format(log.getMonitorValues())));
            } else if (AlarmConditionEnum.NETWORK_INPUT.getValue().equals(log.getAlarmCondition())) {
                // 下行数据
                inputData.add(List.of(DateFormatUtils.format(log.getCreateTime(), "yyyy-MM-dd HH:mm:ss"),
                        new DecimalFormat("#.##").format(log.getMonitorValues())));
            } else if (AlarmConditionEnum.NETWORK_OUTPUT.getValue().equals(log.getAlarmCondition())) {
                // 上行数据
                outputData.add(List.of(DateFormatUtils.format(log.getCreateTime(), "yyyy-MM-dd HH:mm:ss"),
                        new DecimalFormat("#.##").format(log.getMonitorValues())));
            } else if (AlarmConditionEnum.DEVICE_CPU_LOAD_ONE_MINUTE.getValue().equals(log.getAlarmCondition())) {
                // 1分钟负载
                cpuLoadOneMinuteData.add(List.of(DateFormatUtils.format(log.getCreateTime(), "yyyy-MM-dd HH:mm:ss"),
                        new DecimalFormat("#.##").format(log.getMonitorValues())));
            } else if (AlarmConditionEnum.DEVICE_CPU_LOAD_FIVE_MINUTE.getValue().equals(log.getAlarmCondition())) {
                // 5分钟负载
                cpuLoadFiveMinuteData.add(List.of(DateFormatUtils.format(log.getCreateTime(), "yyyy-MM-dd HH:mm:ss"),
                        new DecimalFormat("#.##").format(log.getMonitorValues())));
            } else if (AlarmConditionEnum.DEVICE_CPU_LOAD_FIFTEEN_MINUTE.getValue().equals(log.getAlarmCondition())) {
                // 15分钟负载
                cpuLoadFifteenMinuteData.add(List.of(DateFormatUtils.format(log.getCreateTime(), "yyyy-MM-dd HH:mm:ss"),
                        new DecimalFormat("#.##").format(log.getMonitorValues())));
            }
        });
        vo.setCpuData(cpuData);
        vo.setMemData(memData);
        vo.setInputData(inputData);
        vo.setOutputData(outputData);
        vo.setCpuLoadOneMinuteData(cpuLoadOneMinuteData);
        vo.setCpuLoadFiveMinuteData(cpuLoadFiveMinuteData);
        vo.setCpuLoadFifteenMinuteData(cpuLoadFifteenMinuteData);

        setDiskData(param, vo, beginTime, endTime);
        return vo;
    }

    /**
     * 设置磁盘相关的数据
     * @param param
     * @param vo
     * @param beginTime
     * @param endTime
     */
    private void setDiskData(DeviceMonitorParam param, DeviceMonitorInfoVO vo, String beginTime, String endTime) {
        // 从定时任务日志表获取最新的磁盘数据
        List<SysJobLog> diskLogList = lambdaQuery()
                .select(SysJobLog::getObjectId, SysJobLog::getAlarmCondition, SysJobLog::getCreateTime, SysJobLog::getMonitorValues, SysJobLog::getObjectItem)
                .in(SysJobLog::getAlarmCondition, List.of(
                        AlarmConditionEnum.DEVICE_DISK_READ_IOPS.getValue(),
                        AlarmConditionEnum.DEVICE_DISK_WRITE_IOPS.getValue(),
                        AlarmConditionEnum.DEVICE_DISK_READ_KB.getValue(),
                        AlarmConditionEnum.DEVICE_DISK_WRITE_KB.getValue(),
                        AlarmConditionEnum.DEVICE_DISK_READ_WAIT.getValue(),
                        AlarmConditionEnum.DEVICE_DISK_WRITE_WAIT.getValue()))
                .eq(param.getDeviceId() != 0L, SysJobLog::getObjectId, param.getDeviceId())
                .ge(ParamValidate.validate(beginTime), SysJobLog::getCreateTime, beginTime)
                .le(ParamValidate.validate(endTime), SysJobLog::getCreateTime, endTime).orderByAsc(SysJobLog::getCreateTime)
                .list();

        // 根据磁盘名称分组
        Map<String, List<SysJobLog>> map = diskLogList.stream().collect(Collectors.groupingBy(SysJobLog::getObjectItem));
        Map<String, List<List<Object>>> diskReadIopsData = new HashMap<>(4);
        Map<String, List<List<Object>>> diskWriteIopsData = new HashMap<>(4);
        Map<String, List<List<Object>>> diskReadKbData = new HashMap<>(4);
        Map<String, List<List<Object>>> diskWriteKbData = new HashMap<>(4);
        Map<String, List<List<Object>>> diskReadWaitData = new HashMap<>(4);
        Map<String, List<List<Object>>> diskWriteWaitData = new HashMap<>(4);

        for (Map.Entry<String, List<SysJobLog>> entry : map.entrySet()) {
            List<List<Object>> diskReadIopsList = new ArrayList<>();
            List<List<Object>> diskWriteIopsList = new ArrayList<>();
            List<List<Object>> diskReadKbList = new ArrayList<>();
            List<List<Object>> diskWriteKbList = new ArrayList<>();
            List<List<Object>> diskReadWaitList = new ArrayList<>();
            List<List<Object>> diskWriteWaitList = new ArrayList<>();
            for (SysJobLog log : entry.getValue()) {
                if (AlarmConditionEnum.DEVICE_DISK_READ_IOPS.getValue().equals(log.getAlarmCondition())) {
                    // CPU数据
                    diskReadIopsList.add(List.of(DateFormatUtils.format(log.getCreateTime(), "yyyy-MM-dd HH:mm:ss"),
                            new DecimalFormat(SymbolConstants.DECIMAL_FORMAT_PATTERN).format(log.getMonitorValues())));
                } else if (AlarmConditionEnum.DEVICE_DISK_WRITE_IOPS.getValue().equals(log.getAlarmCondition())) {
                    // 内存数据
                    diskWriteIopsList.add(List.of(DateFormatUtils.format(log.getCreateTime(), "yyyy-MM-dd HH:mm:ss"),
                            new DecimalFormat(SymbolConstants.DECIMAL_FORMAT_PATTERN).format(log.getMonitorValues())));
                } else if (AlarmConditionEnum.DEVICE_DISK_READ_KB.getValue().equals(log.getAlarmCondition())) {
                    // 下行数据
                    diskReadKbList.add(List.of(DateFormatUtils.format(log.getCreateTime(), "yyyy-MM-dd HH:mm:ss"),
                            new DecimalFormat(SymbolConstants.DECIMAL_FORMAT_PATTERN).format(log.getMonitorValues())));
                } else if (AlarmConditionEnum.DEVICE_DISK_WRITE_KB.getValue().equals(log.getAlarmCondition())) {
                    // 上行数据
                    diskWriteKbList.add(List.of(DateFormatUtils.format(log.getCreateTime(), "yyyy-MM-dd HH:mm:ss"),
                            new DecimalFormat(SymbolConstants.DECIMAL_FORMAT_PATTERN).format(log.getMonitorValues())));
                } else if (AlarmConditionEnum.DEVICE_DISK_READ_WAIT.getValue().equals(log.getAlarmCondition())) {
                    // 1分钟负载
                    diskReadWaitList.add(List.of(DateFormatUtils.format(log.getCreateTime(), "yyyy-MM-dd HH:mm:ss"),
                            new DecimalFormat(SymbolConstants.DECIMAL_FORMAT_PATTERN).format(log.getMonitorValues())));
                } else if (AlarmConditionEnum.DEVICE_DISK_WRITE_WAIT.getValue().equals(log.getAlarmCondition())) {
                    // 5分钟负载
                    diskWriteWaitList.add(List.of(DateFormatUtils.format(log.getCreateTime(), "yyyy-MM-dd HH:mm:ss"),
                            new DecimalFormat(SymbolConstants.DECIMAL_FORMAT_PATTERN).format(log.getMonitorValues())));
                }
            }
            diskReadIopsData.put(entry.getKey(), diskReadIopsList);
            diskWriteIopsData.put(entry.getKey(), diskWriteIopsList);
            diskReadKbData.put(entry.getKey(), diskReadKbList);
            diskWriteKbData.put(entry.getKey(), diskWriteKbList);
            diskReadWaitData.put(entry.getKey(), diskReadWaitList);
            diskWriteWaitData.put(entry.getKey(), diskWriteWaitList);
        }

        vo.setDiskReadIopsData(diskReadIopsData);
        vo.setDiskWriteIopsData(diskWriteIopsData);
        vo.setDiskReadKbData(diskReadKbData);
        vo.setDiskWriteKbData(diskWriteKbData);
        vo.setDiskReadWaitData(diskReadWaitData);
        vo.setDiskWriteWaitData(diskWriteWaitData);
    }

    @Override
    public void clearExpiredLog() {
        // 7天之前的记录
        LocalDateTime sevenDayAgo = LocalDateTime.of(LocalDate.now(), LocalTime.now()).minusDays(NumberConstants.SEVEN);
        // 清除日志记录
        sysJobLogMapper.clearExpiredLog(sevenDayAgo);
    }

}