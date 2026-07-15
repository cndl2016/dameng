package com.dm.cn.common.param;

import lombok.Data;

/**
 * 设备节点树查询参数
 *
 * @author dameng
 * @date 2024/10/15
 */
@Data
public class DeviceNodeParam {
    /**
     * 根节点ID
     */
    private Long rootId;

    /**
     * 要查询的设备架构
     */
    private String queryArch;

    /**
     * 启用状态筛选
     */
    private String enableStatus;
}