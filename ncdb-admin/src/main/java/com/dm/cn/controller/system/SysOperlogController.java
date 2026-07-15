package com.dm.cn.controller.system;

import com.dm.cn.common.core.utils.poi.ExcelUtil;
import com.dm.cn.common.core.web.controller.BaseController;
import com.dm.cn.common.core.web.domain.AjaxResult;
import com.dm.cn.common.core.web.page.TableDataInfo;
import com.dm.cn.common.log.annotation.Log;
import com.dm.cn.common.log.enums.BusinessType;
import com.dm.cn.common.security.annotation.RequiresPermissions;
import com.dm.cn.system.entity.server.SysOperLog;
import com.dm.cn.system.service.ISysOperLogService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 操作日志记录
 *
 * @author dameng
 */
@RestController
@RequestMapping("/system/opLog")
public class SysOperlogController extends BaseController
{
    @Resource
    private ISysOperLogService operLogService;

    @RequiresPermissions("log:opLog:list")
    @GetMapping("/list")
    @ApiOperation(value = "查询系统操作日志")
    public TableDataInfo list(SysOperLog operLog)
    {
        startPage();
        List<SysOperLog> list = operLogService.selectOperLogList(operLog);
        return getDataTable(list);
    }

    @Log(title = "操作日志", businessType = BusinessType.EXPORT)
    @RequiresPermissions("log:opLog:export")
    @PostMapping("/export")
    @ApiOperation(value = "导出系统操作日志")
    public void export(HttpServletResponse response, SysOperLog operLog)
    {
        List<SysOperLog> list = operLogService.selectOperLogList(operLog);
        ExcelUtil<SysOperLog> util = new ExcelUtil<SysOperLog>(SysOperLog.class);
        util.exportExcel(response, list, "操作日志");
    }

    @Log(title = "操作日志", businessType = BusinessType.DELETE)
    @RequiresPermissions("log:opLog:remove")
    @DeleteMapping("/{operIds}")
    @ApiOperation(value = "批量删除系统操作日志")
    public AjaxResult remove(@PathVariable Long[] operIds)
    {
        return toAjax(operLogService.deleteOperLogByIds(operIds));
    }

    @RequiresPermissions("log:opLog:remove")
    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    @ApiOperation(value = "清空操作日志")
    public AjaxResult clean()
    {
        operLogService.cleanOperLog();
        return AjaxResult.success();
    }

}
