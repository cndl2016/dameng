package com.dm.cn.controller.base;

import com.dm.cn.base.entity.param.AlarmMessageParam;
import com.dm.cn.base.service.AlarmMessageService;
import com.dm.cn.common.core.web.domain.AjaxResult;
import com.dm.cn.common.log.annotation.Log;
import com.dm.cn.common.log.enums.BusinessType;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 告警通知方式表控制器
 *
 * @author root
 * @date 2025/02/10
 */
@RestController
@RequestMapping("/base/alarmMessage")
public class AlarmMessageController {

    @Resource
    private AlarmMessageService alarmMessageService;

    @ApiOperation(value = "获取所有的告警通知方式信息")
    @PostMapping("/getAll")
    public AjaxResult getAll() {
        return AjaxResult.success(alarmMessageService.getAll());
    }

    /**
     * 保存告警通知方式
     *
     * @param param 告警通知方式信息
     * @return {@link AjaxResult}
     */
    @Log(title = "告警规则", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ApiOperation(value = "1.保存告警通知方式")
    public AjaxResult add(@RequestBody AlarmMessageParam param) {
        return AjaxResult.success(alarmMessageService.add(param));
    }

}