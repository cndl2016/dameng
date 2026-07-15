package com.dm.cn.controller.base;

import com.dm.cn.base.entity.vo.SysMessageVO;
import com.dm.cn.base.service.SysMessageService;
import com.dm.cn.common.core.utils.poi.ExcelUtil;
import com.dm.cn.common.core.web.domain.AjaxResult;
import com.dm.cn.common.param.SysMessageParam;
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
 * 告警历史
 *
 * @author root 2025/2/13
 */
@RestController
@RequestMapping("/base/sysMessage")
public class SysMessageController {

    @Resource
    private SysMessageService sysMessageService;

    /**
     * 查询告警历史记录
     */
    @GetMapping("/getPageList")
    @ApiOperation(value = "查询告警历史记录")
    public AjaxResult getSysMessagePageList(SysMessageParam param) {
        return AjaxResult.success(sysMessageService.getSysMessagePageList(param));
    }

    /**
     * 告警历史记录导出
     */
    @RequiresPermissions("log:alarm:list:export")
    @PostMapping("/exportSysMessage")
    @ApiOperation(value = "告警历史记录导出")
    public void exportSysMessage(HttpServletResponse response, SysMessageParam param) {
        List<SysMessageVO> list = sysMessageService.exportSysMessageList(param);
        ExcelUtil<SysMessageVO> util = new ExcelUtil<>(SysMessageVO.class);
        util.exportExcel(response, list, "告警历史数据");
    }
}