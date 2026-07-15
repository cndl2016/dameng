package com.dm.cn.controller.base;

import com.dm.cn.base.entity.server.SoftAgentConfig;
import com.dm.cn.base.service.SoftAgentConfigService;
import com.dm.cn.common.core.web.controller.BaseController;
import com.dm.cn.common.core.web.domain.AjaxResult;
import com.dm.cn.common.log.annotation.Log;
import com.dm.cn.common.log.enums.BusinessType;
import com.dm.cn.common.param.SoftAgentConfigParam;
import com.dm.cn.common.security.annotation.RequiresPermissions;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 采集频率控制器
 *
 * @author DAMENG
 */
@RestController
@RequestMapping("/base/agent")
public class SoftAgentConfigController extends BaseController {

    @Resource
    private SoftAgentConfigService softAgentConfigService;

    @GetMapping("/getAgentConfigPage")
    @ApiOperation(value = "采集频率列表查询")
    public AjaxResult getPage(SoftAgentConfigParam param) {
        return AjaxResult.success(softAgentConfigService.getPage(param));
    }

    /**
     * 修改采集频率
     *
     * @param softAgentConfig
     * @return
     */
    @Log(title = "采集频率", businessType = BusinessType.UPDATE)
    @RequiresPermissions(value = {"base:agent:edit"})
    @PostMapping("/update")
    @ApiOperation(value = "修改采集频率")
    public AjaxResult update(@RequestBody SoftAgentConfig softAgentConfig) {
        return AjaxResult.success(softAgentConfigService.update(softAgentConfig));
    }

}