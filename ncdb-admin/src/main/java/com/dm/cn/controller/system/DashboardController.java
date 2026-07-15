package com.dm.cn.controller.system;

import com.dm.cn.base.entity.param.AlarmCountParam;
import com.dm.cn.base.service.SysMessageService;
import com.dm.cn.common.core.web.domain.AjaxResult;
import com.dm.cn.device.service.DeviceService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 首页面板信息控制器
 *
 * @author hzz
 * @date 2024/11/14
 */
@RestController
@RequestMapping("/system/dashboard")
public class DashboardController {

    @Resource
    private DeviceService deviceService;

    @Resource
    private SysMessageService sysMessageService;

    @PostMapping("/alarmLog")
    @ApiOperation(value = "首页告警信息统计数据")
    public AjaxResult getAlarmList(@RequestBody AlarmCountParam param) {
        return AjaxResult.success(sysMessageService.getSysMessageData(param));
    }

    @GetMapping("/getDeviceTopChart")
    @ApiOperation(value = "首页获取设备top信息展示")
    public AjaxResult getDeviceTopChart() {
        return AjaxResult.success(deviceService.getDeviceTopChart());
    }

}