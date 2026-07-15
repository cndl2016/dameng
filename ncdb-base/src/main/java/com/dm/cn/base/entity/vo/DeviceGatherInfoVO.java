package com.dm.cn.base.entity.vo;


import com.dm.cn.base.entity.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author fwh
 * @date 2025/2/6
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "设备通用列表模型对象（告警使用）")
public class DeviceGatherInfoVO extends BaseModel {

    @ApiModelProperty(value = "设备连接状态")
    private String status;

    @ApiModelProperty(value = "CPU利用率")
    private Float cpuUsage;

    @ApiModelProperty(value = "内存利用率")
    private Float memUsage;

    @ApiModelProperty(value = "磁盘空间利用率")
    private Float diskUsage;

    @ApiModelProperty(value = "网络下行速率")
    private Float networkInput;

    @ApiModelProperty(value = "网络上行速率")
    private Float networkOutput;
}
