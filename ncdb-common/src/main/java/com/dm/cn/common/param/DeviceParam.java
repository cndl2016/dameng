package com.dm.cn.common.param;

import com.dm.cn.common.annotation.EncryptIp;
import com.dm.cn.common.core.web.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 设备节点树查询参数
 *
 * @author dameng
 * @date 2024/10/15
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceParam extends BaseEntity {

    /**
     * 设备机房
     */
    private String deviceHome;

    /**
     * 设备节点id
     */
    private Long nodeId;

    /**
     * 设备ip
     */
    @EncryptIp
    private String deviceIp;

    /**
     * 连接状态
     */
    private String connStatus;

    /**
     * 设备id列表
     */
    private List<String> idList;
}
