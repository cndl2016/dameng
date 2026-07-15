package com.dm.cn.device.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * ip、节点总数vo类
 *
 * @author hzz
 * @date 2024/11/22
 */
@Data
public class DeviceNodeCountVO implements Serializable {

    /**
     * ip地址
     */
    private String ip;

    /**
     * 总数
     */
    private Integer count;

}