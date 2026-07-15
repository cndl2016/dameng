package com.dm.cn.device.entity.vo;

import com.dm.cn.device.entity.server.Device;
import lombok.Data;

import java.util.List;

/**
 * 设备资源节点树视图对象
 * @author dameng
 */
@Data
public class DeviceResourceVo {

    /**
     * 设备资源树
     */
   private List<DeviceNodeVO> deviceNodeTree;

    /**
     * 最小内存限制
     */
   private int minMemLimit;

    /**
     * 最小磁盘限制
     */
   private int minDiskLimit;

    /**
     * 设备列表
     */
    private List<Device> deviceList;
}
