package com.dm.cn.controller.device;

import com.dm.cn.common.core.web.domain.AjaxResult;
import com.dm.cn.common.log.annotation.Log;
import com.dm.cn.common.log.enums.BusinessType;
import com.dm.cn.common.param.DeviceNodeParam;
import com.dm.cn.common.security.annotation.Logical;
import com.dm.cn.common.security.annotation.RequiresPermissions;
import com.dm.cn.device.entity.param.RegionParam;
import com.dm.cn.device.entity.vo.DeviceNodeVO;
import com.dm.cn.device.service.DeviceNodeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 设备节点控制器
 *
 * @author dameng
 * @date 2024/10/15
 */
@RestController
@RequestMapping("/device/deviceNode")
public class DeviceNodeController {

    @Resource
    private DeviceNodeService deviceNodeService;

    /**
     * 设备节点树查询
     *
     * @param param 设备节点树查询参数（根节点、架构类型）
     * @return {@link AjaxResult}
     */
    @PostMapping("/getDeviceNodeTree")
    @ApiOperation(value = "设备节点树查询")
    public AjaxResult getDeviceNodeTree(@RequestBody DeviceNodeParam param) {
        return AjaxResult.success(deviceNodeService.getDeviceNodeTree(param));
    }

    /**
     * 保存或修改设备节点
     *
     * @param vo 设备节点信息
     * @return {@link AjaxResult}
     */
    @Log(title = "设备维护", businessType = BusinessType.SAVE)
    @RequiresPermissions(value = {"device:node:add", "device:node:edit"}, logical = Logical.OR)
    @PostMapping("/saveOrUpdateDeviceNode")
    @ApiOperation(value = "保存或修改设备节点")
    public AjaxResult saveOrUpdateDeviceNode(@RequestBody DeviceNodeVO vo) {
        return AjaxResult.success(deviceNodeService.saveOrUpdateDeviceNode(vo));
    }

    /**
     * 删除设备节点
     *
     * @param id 设备节点id
     * @return {@link AjaxResult}
     */
    @Log(title = "设备维护", businessType = BusinessType.DELETE)
    @RequiresPermissions("device:node:remove")
    @DeleteMapping("/deleteDeviceNode/{id}")
    @ApiOperation(value = "删除设备节点")
    public AjaxResult deleteDeviceNode(@PathVariable Long id) {
        deviceNodeService.deleteDeviceNode(id);
        return AjaxResult.success();
    }

    /**
     * 获取设备监控节点树
     *
     * @param param 查询参数
     * @return {@link AjaxResult}
     * @author cn 2024/07/11
     */
    @PostMapping("/getDeviceMonitorTree")
    @ApiOperation(value = "设备监控节点树")
    public AjaxResult getDeviceMonitorTree(@RequestBody DeviceNodeParam param) {
        return AjaxResult.success(deviceNodeService.getDeviceMonitorTree(param));
    }

    /**
     * 在线设备节点树查询
     *
     * @param param 查询参数
     * @return {@link List}<{@link DeviceNodeVO}>
     */
    @GetMapping("/getDeviceOnlineTree")
    @ApiOperation(value = "在线设备节点树查询")
    public AjaxResult getDeviceOnlineTree(DeviceNodeParam param) {
        return AjaxResult.success(deviceNodeService.getDeviceOnlineTree(param));
    }

    /**
     * 两地三中心部署用地域节点树查询
     *
     * @param param 查询参数
     * @return {@link List}<{@link DeviceNodeVO}>
     */
    @PostMapping("/getRegionTree")
    @ApiOperation(value = "两地三中心部署用地域节点树查询")
    public AjaxResult getRegionTree(@RequestBody RegionParam param) {
        return AjaxResult.success(deviceNodeService.getRegionTree(param));
    }
}
