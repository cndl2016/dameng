package com.dm.cn.base.entity.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 设备和数据库实体对象
 *
 * @author Auto-Coder
 * @date 2023-03-01
 */
@Data
@ApiModel(value = "设备通用对象")
public class DeviceNormal {

    private static final long serialVersionUID = 1L;

    /**主键*/
    @ApiModelProperty(value = "主键")
    private Long deviceId;

    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    @ApiModelProperty(value = "设备对象")
    private String tableName;

    /**管理IP*/
    @ApiModelProperty(value = "管理IP")
    private String manageIp;

    @ApiModelProperty(value = "数据库端口")
    private Integer port;

}