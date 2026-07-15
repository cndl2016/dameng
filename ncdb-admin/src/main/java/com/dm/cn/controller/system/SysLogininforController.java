package com.dm.cn.controller.system;

import com.dm.cn.common.core.utils.poi.ExcelUtil;
import com.dm.cn.common.core.web.controller.BaseController;
import com.dm.cn.common.core.web.domain.AjaxResult;
import com.dm.cn.common.core.web.page.TableDataInfo;
import com.dm.cn.common.log.annotation.Log;
import com.dm.cn.common.log.enums.BusinessType;
import com.dm.cn.common.security.annotation.RequiresPermissions;
import com.dm.cn.system.entity.server.SysLogininfor;
import com.dm.cn.system.service.ISysLoginInforService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 系统访问记录
 *
 * @author dameng
 */
@RestController
@RequestMapping("/system/loginInfo")
public class SysLogininforController extends BaseController {
    @Resource
    private ISysLoginInforService logininforService;

    @RequiresPermissions("log:loginInfo:list")
    @GetMapping("/list")
    @ApiOperation(value = "查询系统登录日志集合")
    public TableDataInfo list(SysLogininfor loginInfo) {
        startPage();
        List<SysLogininfor> list = logininforService.selectLoginInforList(loginInfo);
        return getDataTable(list);
    }

    @Log(title = "登录日志", businessType = BusinessType.EXPORT)
    @RequiresPermissions("log:loginInfo:export")
    @PostMapping("/export")
    @ApiOperation(value = "导出系统登录日志集合")
    public void export(HttpServletResponse response, SysLogininfor loginInfo) {
        List<SysLogininfor> list = logininforService.selectLoginInforList(loginInfo);
        ExcelUtil<SysLogininfor> util = new ExcelUtil<SysLogininfor>(SysLogininfor.class);
        util.exportExcel(response, list, "登录日志");
    }

    @RequiresPermissions("log:loginInfo:remove")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    @ApiOperation(value = "批量删除系统登录日志")
    public AjaxResult remove(@PathVariable Long[] infoIds) {
        return toAjax(logininforService.deleteLoginInforByIds(infoIds));
    }

    @RequiresPermissions("log:loginInfo:remove")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/clean")
    @ApiOperation(value = "清空系统登录日志")
    public AjaxResult clean() {
        logininforService.cleanLoginInfor();
        return AjaxResult.success();
    }

}
