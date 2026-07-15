package com.dm.cn.device.entity.server;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 设备节点表实体类
 *
 * @author dameng
 * @date 2024/10/15
 */
@TableName(value = "T_DEVICE_NODE")
@Data
public class DeviceNode implements Serializable {
    /**
     * ID
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 上级ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long upperId;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 节点类型：0：地域节点，1：机房节点
     */
    private String nodeType;

    /**
     * 创建者
     */
    private String createUser;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新者
     */
    private String updateUser;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 节点层级
     */
    private Integer level;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}