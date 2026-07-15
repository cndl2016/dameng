package com.dm.cn.controller.system;

import com.dm.cn.common.core.constant.NumberConstants;
import com.dm.cn.common.core.domain.SysDictType;
import com.dm.cn.common.core.utils.poi.ExcelUtil;
import com.dm.cn.common.core.web.controller.BaseController;
import com.dm.cn.common.core.web.domain.AjaxResult;
import com.dm.cn.common.core.web.page.TableDataInfo;
import com.dm.cn.common.log.annotation.Log;
import com.dm.cn.common.log.enums.BusinessType;
import com.dm.cn.common.security.annotation.RequiresPermissions;
import com.dm.cn.common.security.utils.SecurityUtils;
import com.dm.cn.system.service.ISysDictTypeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 数据字典信息
 *
 * @author dameng
 */
@RestController
@RequestMapping("/system/dict/type")
public class SysDictTypeController extends BaseController
{
    @Resource
    private ISysDictTypeService dictTypeService;

    @RequiresPermissions("system:dict:list")
    @GetMapping("/list")
    @ApiOperation(value = "根据条件分页查询字典类型")
    public TableDataInfo list(SysDictType dictType)
    {
        startPage();
        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
        return getDataTable(list);
    }

    @Log(title = "字典类型", businessType = BusinessType.EXPORT)
    @RequiresPermissions("system:dict:export")
    @PostMapping("/export")
    @ApiOperation(value = "导出字典类型")
    public void export(HttpServletResponse response, SysDictType dictType)
    {
        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
        ExcelUtil<SysDictType> util = new ExcelUtil<SysDictType>(SysDictType.class);
        util.exportExcel(response, list, "字典类型");
    }

    /**
     * 查询字典类型详细
     */
    @RequiresPermissions("system:dict:list")
    @GetMapping(value = "/{dictId}")
    @ApiOperation(value = "根据字典类型ID查询信息")
    public AjaxResult getInfo(@PathVariable Long dictId)
    {
        return AjaxResult.success(dictTypeService.selectDictTypeById(dictId));
    }

    /**
     * 新增保存字典类型信息
     */
    @RequiresPermissions("system:dict:add")
    @Log(title = "字典类型", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation(value = "新增保存字典类型信息")
    public AjaxResult add(@Validated @RequestBody SysDictType dict)
    {
        if (NumberConstants.ONE_STRING.equals(dictTypeService.checkDictTypeUnique(dict)))
        {
            return AjaxResult.error("新增字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        dict.setCreateBy(SecurityUtils.getUsername());
        return toAjax(dictTypeService.insertDictType(dict));
    }

    /**
     * 修改保存字典类型信息
     */
    @RequiresPermissions("system:dict:edit")
    @Log(title = "字典类型", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation(value = "修改保存字典类型信息")
    public AjaxResult edit(@Validated @RequestBody SysDictType dict)
    {
        if (NumberConstants.ONE_STRING.equals(dictTypeService.checkDictTypeUnique(dict)))
        {
            return AjaxResult.error("修改字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        dict.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(dictTypeService.updateDictType(dict));
    }

    /**
     * 批量删除字典信息
     */
    @RequiresPermissions("system:dict:remove")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictIds}")
    @ApiOperation(value = "批量删除字典信息")
    public AjaxResult remove(@PathVariable Long[] dictIds)
    {
        dictTypeService.deleteDictTypeByIds(dictIds);
        return success();
    }

    /**
     * 刷新字典缓存
     */
    @RequiresPermissions("system:dict:refresh")
    @Log(title = "字典类型", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    @ApiOperation(value = "刷新字典缓存")
    public AjaxResult refreshCache()
    {
        dictTypeService.resetDictCache();
        return AjaxResult.success();
    }

    /**
     * 获取字典选择框列表
     */
    @GetMapping("/optionselect")
    @ApiOperation(value = "获取字典选择框列表")
    public AjaxResult optionselect()
    {
        List<SysDictType> dictTypes = dictTypeService.selectDictTypeAll();
        return AjaxResult.success(dictTypes);
    }
}
