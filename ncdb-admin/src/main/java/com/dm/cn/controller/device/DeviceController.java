package com.dm.cn.controller.device;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dm.cn.base.entity.param.DeviceAlarmParam;
import com.dm.cn.base.service.DeviceAlarmService;
import com.dm.cn.base.service.SysJobLogService;
import com.dm.cn.common.core.exception.ServiceException;
import com.dm.cn.common.core.web.domain.AjaxResult;
import com.dm.cn.common.log.annotation.Log;
import com.dm.cn.common.log.enums.BusinessType;
import com.dm.cn.common.param.DeviceMonitorParam;
import com.dm.cn.common.param.DeviceParam;
import com.dm.cn.common.param.EnableParam;
import com.dm.cn.common.security.annotation.Logical;
import com.dm.cn.common.security.annotation.RequiresPermissions;
import com.dm.cn.common.utils.I18nMessageUtil;
import com.dm.cn.device.entity.server.Device;
import com.dm.cn.device.service.DeviceService;
import com.dm.cn.system.constant.MessageTipConstant;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 设备控制器
 *
 * @author dameng
 * @date 2024/10/15
 */
@RestController
@RequestMapping("/device")
public class DeviceController {

    @Resource
    private DeviceService deviceService;

    @Resource
    private SysJobLogService sysJobLogService;

    @Resource
    private DeviceAlarmService deviceAlarmService;

    /**
     * 查询设备列表
     *
     * @param device 设备查询参数
     * @return {@link AjaxResult}
     */
    @GetMapping("/getListDevice")
    @ApiOperation(value = "查询设备列表")
    public AjaxResult getListDevice(DeviceParam device) {
        IPage<Device> devices = deviceAlarmService.getListDevice(device);
        return AjaxResult.success(devices);
    }

    /**
     * 获取在线设备IP列表
     *
     * @return {@link AjaxResult}
     */
    @GetMapping("/getDeviceIpList")
    @ApiOperation(value = "获取在线设备IP列表")
    public AjaxResult getDeviceIpList() {
        return AjaxResult.success(deviceService.getNodeDeviceIpList());
    }

    /**
     * 设备启用禁用
     *
     * @param enableParam 启停用参数
     * @return {@link AjaxResult}
     * @author cn 2024/07/10
     */
    @Log(title = "设备维护", businessType = BusinessType.ENABLE)
    @RequiresPermissions("device:edit")
    @PutMapping("/enableDevice")
    @ApiOperation(value = "设备启用禁用")
    public AjaxResult enableDevice(@RequestBody EnableParam enableParam) {
        return AjaxResult.success(deviceService.enableDevice(enableParam));
    }

    /**
     * 删除设备
     *
     * @param id 设备id
     * @return {@link AjaxResult}
     * @author cn 2024/07/10
     */
    @Log(title = "设备维护", businessType = BusinessType.DELETE)
    @RequiresPermissions("device:remove")
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除设备")
    public AjaxResult removeDeviceById(@PathVariable Long id) {
        boolean flag = deviceService.deleteDeviceById(id);
        if (flag) {
            return AjaxResult.success(true);
        } else {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.COMMON_NOT_DELETE));
        }
    }

    /**
     * 保存或更新设备
     *
     * @param device 设备信息
     * @return {@link AjaxResult}
     * @author cn 2024/07/10
     */
    @Log(title = "设备维护", businessType = BusinessType.SAVE)
    @RequiresPermissions(value = {"device:add", "device:edit"}, logical = Logical.OR)
    @PostMapping("/saveOrUpdateDevice")
    @ApiOperation(value = "保存或更新设备")
    public AjaxResult saveOrUpdateDevice(@RequestBody Device device) {
        return AjaxResult.success(deviceService.saveOrUpdateDevice(device));
    }

    /**
     * 获取设备监控列表
     *
     * @param param 查询参数
     * @return {@link AjaxResult}
     * @author cn 2024/07/11
     */
    @PostMapping("/getMonitorList")
    @ApiOperation(value = "获取设备监控列表")
    public AjaxResult getMonitorList(@RequestBody DeviceMonitorParam param) {
        return AjaxResult.success(deviceService.getMonitorList(param));
    }

    /**
     * 获取设备监控Echart数据
     *
     * @param param 查询参数
     * @return {@link AjaxResult}m
     * @author cn 2024/07/11
     */
    @PostMapping("/getMonitorDataForEchart")
    @ApiOperation(value = "获取设备监控Echart数据")
    public AjaxResult getMonitorDataForEchart(@RequestBody DeviceMonitorParam param) {
        return AjaxResult.success(sysJobLogService.getMonitorDataForEchart(param));
    }

    @PostMapping("/testPing")
    @Operation(summary = "测试连接")
    public AjaxResult testPing(@RequestBody List<Device> deviceList) {
        return AjaxResult.success(deviceService.testPing(deviceList));
    }

    @PostMapping("/addDevices")
    @Log(title = "设备维护", businessType = BusinessType.SAVE)
    @Operation(summary = "设备批量新增")
    public AjaxResult addDevices(@RequestBody DeviceAlarmParam param) {
        return AjaxResult.success(deviceAlarmService.addDeviceAndAlarm(param));
    }
}