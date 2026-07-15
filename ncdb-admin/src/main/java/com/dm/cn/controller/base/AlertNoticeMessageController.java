package com.dm.cn.controller.base;

import com.dm.cn.base.entity.param.AlertNoticeMessageParam;
import com.dm.cn.base.entity.server.AlertNoticeMessage;
import com.dm.cn.base.service.AlertNoticeMessageService;
import com.dm.cn.common.core.utils.poi.ExcelUtil;
import com.dm.cn.common.core.web.domain.AjaxResult;
import com.dm.cn.common.security.annotation.RequiresPermissions;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 告警通知消息记录配置
 *
 * @author DAMENG
 */
@RestController
@RequestMapping("/base/alert/notice/message")
public class AlertNoticeMessageController {

    @Resource
    private AlertNoticeMessageService alertNoticeMessageService;

    @ApiOperation(value = "1.告警通知消息记录分页查询")
    @GetMapping("/list")
    public AjaxResult getList(AlertNoticeMessageParam param) {
        return AjaxResult.success(alertNoticeMessageService.getPage(param));
    }

    /**
     * 告警通知消息记录导出
     */
    @RequiresPermissions("log:alarm:list:export")
    @PostMapping("/export")
    @ApiOperation(value = "告警通知消息记录导出")
    public void export(HttpServletResponse response, AlertNoticeMessageParam param) {
        List<AlertNoticeMessage> dataList = alertNoticeMessageService.export(param);
        ExcelUtil<AlertNoticeMessage> util = new ExcelUtil<>(AlertNoticeMessage.class);
        util.exportExcel(response, dataList, "告警通知消息记录");
    }

}