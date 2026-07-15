package com.dm.cn.device.entity.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 设备节点树视图对象
 * @author dameng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceNodeVO implements Serializable {

    /**
     * 设备树节点id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 设备树父节点id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long upperId;

    /**
     * 设备树节点名称
     */
    private String nodeName;

    /**
     * 设备节点类型
     */
    private String nodeType;

    /**
     * 设备数量
     */
    private Integer deviceNum;

    /**
     * 层级
     */
    private Integer level;

    /**
     * 全id路径
     */
    private String fullIdPath;

    /**
     * 全名称路径
     */
    private String fullNamePath;

    /**
     * 是否为设备
     */
    private Boolean isDevice;

    /**
     * 设备树子节点
     */
    private List<DeviceNodeVO> children;

    /**
     * 设备连接状态
     */
    private String connStatus;

    /**
     * 设备架构类型
     */
    private String archType;

    /**
     * CPU核数
     */
    private Integer cpuCore;

    /**
     * 内存剩余
     */
    private Double memFree;

    /**
     * 磁盘剩余
     */
    private Double diskFree;

    /**
     * CPU使用率
     */
    private String cpuUsing;
}

