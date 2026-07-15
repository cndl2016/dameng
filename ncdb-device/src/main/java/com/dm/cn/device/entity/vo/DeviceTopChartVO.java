package com.dm.cn.device.entity.vo;

import com.dm.cn.device.entity.server.Device;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 首页获取设备前十的信息vo类
 *
 * @author hzz
 * @date 2024/11/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceTopChartVO {

    /**
     * cpu数据集合
     */
    private List<SysJobLogVO> cpuDataList;

    /**
     * 内存数据集合
     */
    private List<SysJobLogVO> memoryDataList;

    /**
     * 磁盘数据集合
     */
    private List<SysJobLogVO> diskDataList;

    /**
     * 设备节点数据集合
     */
    private List<Device> nodeNumberDataList;

}