package com.dm.cn.controller.base;

import com.dm.cn.base.entity.param.AlertApplyParam;
import com.dm.cn.base.entity.param.AlertConfigParam;
import com.dm.cn.base.entity.param.AlertRuleParam;
import com.dm.cn.base.service.AlertConfigService;
import com.dm.cn.common.core.web.domain.AjaxResult;
import com.dm.cn.common.log.annotation.Log;
import com.dm.cn.common.log.enums.BusinessType;
import com.dm.cn.common.param.EnableParam;
import com.dm.cn.common.security.annotation.RequiresPermissions;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 告警规则配置
 *
 * @author DAMENG
 */
@RestController
@RequestMapping("/base/alert/config")
public class AlertConfigController {

    @Resource
    private AlertConfigService alertConfigService;

    @ApiOperation(value = "1.根据告警对象ID，获取指定的告警配置")
    @GetMapping("/list")
    public AjaxResult getListByObjectId(AlertRuleParam param) {
        return AjaxResult.success(alertConfigService.getListByObjectId(param));
    }

    /**
     * 保存告警规则
     *
     * @param param 告警规则信息
     * @return {@link AjaxResult}
     */
    @Log(title = "告警规则", businessType = BusinessType.INSERT)
    @RequiresPermissions("device:alarm:config")
    @PostMapping("/add")
    @ApiOperation(value = "2.保存告警规则")
    public AjaxResult add(@RequestBody AlertConfigParam param) {
        return AjaxResult.success(alertConfigService.add(param));
    }

    /**
     * 修改告警规则启用状态
     *
     * @param enableParam 修改告警规则启用状态
     * @return {@link AjaxResult}
     */
    @Log(title = "告警规则", businessType = BusinessType.UPDATE)
    @RequiresPermissions("device:alarm:config")
    @PutMapping("/enableConfig")
    @ApiOperation(value = "3.修改告警规则启用状态")
    public AjaxResult enableConfig(@RequestBody EnableParam enableParam) {
        return AjaxResult.success(alertConfigService.enableConfig(enableParam));
    }

    /**
     * 批量应用告警规则
     *
     * @param param 告警规则信息
     * @return {@link AjaxResult}
     */
    @Log(title = "告警规则", businessType = BusinessType.INSERT)
    @RequiresPermissions("device:alarm:apply")
    @PostMapping("/apply")
    @ApiOperation(value = "4.批量应用告警规则")
    public AjaxResult apply(@RequestBody AlertApplyParam param) {
        return AjaxResult.success(alertConfigService.apply(param));
    }

    @PostMapping("/loadDeviceCache")
    @ApiOperation(value = "5.刷新设备告警配置缓存数据")
    public AjaxResult loadAllCache() {
        alertConfigService.loadDeviceCache();
        return AjaxResult.success();
    }

}