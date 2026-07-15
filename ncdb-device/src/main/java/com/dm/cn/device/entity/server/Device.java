package com.dm.cn.device.entity.server;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dm.cn.common.core.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 设备表实体类
 *
 * @author dameng
 * @date 2024/10/15
 */
@TableName(value = "T_DEVICE")
@Data
public class Device implements Serializable {
    /**
     * 唯一标识
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 设备IP
     */
    private String deviceIp;

    /**
     * SSH登录名
     */
    private String deviceSshUsr;

    /**
     * SSH登录密码
     */
    private String deviceSshPwd;

    /**
     * 说明
     */
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 节点个数
     */
    @TableField(exist = false)
    private Integer nodeNum;

    /**
     * 启用状态
     */
    private String enableStatus;

    /**
     * 连接状态
     */
    private String connStatus;

    /**
     * 内存使用率
     */
    @TableField(exist = false)
    private String memUsing;

    /**
     * 磁盘使用率
     */
    @TableField(exist = false)
    private String diskUsing;

    /**
     * CPU使用率
     */
    @TableField(exist = false)
    private String cpuUsing;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 设备内存使用情况（百分比值）
     */
    @TableField(exist = false)
    private Double memUsingInfo;

    /**
     * 更新用户
     */
    private String updateUser;

    /**
     * 设备下行
     */
    @TableField(exist = false)
    private String networkOut;

    /**
     * 设备上行
     */
    @TableField(exist = false)
    private String networkIn;

    /**
     * 设备机房ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long nodeId;

    /**
     * 设备端口
     */
    private Integer port;

    /**
     * 脚本版本
     */
    private String scriptVersion;

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
    @TableField(exist = false)
    private double memFree;

    /**
     * 磁盘剩余
     */
    @TableField(exist = false)
    private double diskFree;

    /**
     * 内存总量
     */
    private String memTotal;

    /**
     * 操作系统名称
     */
    private String osName;

    /**
     * 操作系统版本
     */
    private String osVersion;

    /**
     * cpu描述
     */
    private String cpuDesc;

    /**
     * 设备地区
     */
    @TableField(exist = false)
    private String deviceArea;

    /**
     * 设备机房
     */
    @TableField(exist = false)
    @Excel(name = "设备机房")
    private String deviceHome;

    /**
     * 设备机房id
     */
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long deviceHomeId;

    /**
     * 安全状态
     */
    @TableField(exist = false)
    private String safeStatus;

    /**
     * 告警信息列表
     */
    @TableField(exist = false)
    private List<String> messageList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 磁盘总量
     */
    private String diskTotal;
}