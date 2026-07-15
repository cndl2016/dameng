package com.dm.cn.device.entity.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 地域节点树查询参数
 *
 * @author fwh
 * @date 2024/10/31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionParam {
    /**
     * 节点ID
     */
    private Long rootId;

    /**
     * 排除节点ID
     */
    private Long excludeId;

    /**
     * 要查询的设备架构
     */
    private String queryArch;
}
