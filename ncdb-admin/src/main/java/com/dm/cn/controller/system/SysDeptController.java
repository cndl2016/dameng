package com.dm.cn.controller.system;

import com.dm.cn.common.core.constant.NumberConstants;
import com.dm.cn.common.core.domain.SysDept;
import com.dm.cn.common.core.exception.ServiceException;
import com.dm.cn.common.core.utils.StringUtils;
import com.dm.cn.common.core.web.controller.BaseController;
import com.dm.cn.common.core.web.domain.AjaxResult;
import com.dm.cn.common.log.annotation.Log;
import com.dm.cn.common.log.enums.BusinessType;
import com.dm.cn.common.security.annotation.RequiresPermissions;
import com.dm.cn.common.security.utils.SecurityUtils;
import com.dm.cn.common.utils.I18nMessageUtil;
import com.dm.cn.system.constant.MessageTipConstant;
import com.dm.cn.system.service.ISysDeptService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.List;

/**
 * 部门信息
 *
 * @author dameng
 */
@RestController
@RequestMapping("/system/dept")
public class SysDeptController extends BaseController {

    @Resource
    private ISysDeptService deptService;

    /**
     * 获取部门列表
     */
    @RequiresPermissions("system:dept:list")
    @GetMapping("/list")
    @ApiOperation(value = "获取部门列表")
    public AjaxResult list(SysDept dept) {
        List<SysDept> deptList = deptService.selectDeptList(dept);
        return AjaxResult.success(deptList);
    }

    /**
     * 查询部门列表（排除节点）
     */
    @RequiresPermissions("system:dept:list")
    @GetMapping("/list/exclude/{deptId}")
    @ApiOperation(value = "查询部门列表（排除节点）")
    public AjaxResult excludeChild(@PathVariable(value = "deptId", required = false) Long deptId) {
        List<SysDept> deptList = deptService.selectDeptList(new SysDept());
        Iterator<SysDept> it = deptList.iterator();
        while (it.hasNext()) {
            SysDept d = it.next();
            if (d.getDeptId().intValue() == deptId
                    || ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), deptId + "")) {
                it.remove();
            }
        }
        return AjaxResult.success(deptList);
    }

    /**
     * 根据部门编号获取详细信息
     */
    @GetMapping(value = "/{deptId}")
    @ApiOperation(value = "根据部门编号获取详细信息")
    public AjaxResult getInfo(@PathVariable Long deptId) {
        deptService.checkDeptDataScope(deptId);
        return AjaxResult.success(deptService.selectDeptById(deptId));
    }

    /**
     * 获取部门下拉树列表
     */
    @GetMapping("/treeSelect")
    @ApiOperation(value = "获取部门下拉树列表")
    public AjaxResult treeSelect(SysDept dept) {
        List<SysDept> deptList = deptService.selectDeptList(dept);
        return AjaxResult.success(deptService.buildDeptTreeSelect(deptList));
    }

    /**
     * 新增部门
     */
    @RequiresPermissions("system:dept:add")
    @Log(title = "部门管理", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation(value = "新增部门")
    public AjaxResult add(@Validated @RequestBody SysDept dept) {
        if (NumberConstants.ONE_STRING.equals(deptService.checkDeptNameUnique(dept))) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_DEPT_ADD_FAILED, dept.getDeptName()));
        }
        dept.setCreateBy(SecurityUtils.getUsername());
        return toAjax(deptService.insertDept(dept));
    }

    /**
     * 修改部门
     */
    @RequiresPermissions("system:dept:edit")
    @Log(title = "部门管理", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation(value = "修改部门")
    public AjaxResult edit(@Validated @RequestBody SysDept dept) {
        Long deptId = dept.getDeptId();
        deptService.checkDeptDataScope(deptId);
        if (NumberConstants.ONE_STRING.equals(deptService.checkDeptNameUnique(dept))) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_DEPT_UPDATE_FAILED, dept.getDeptName()));
        } else if (dept.getParentId().equals(deptId)) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_DEPT_SUPER_CANNOT_SELF, dept.getDeptName()));
        } else if (StringUtils.equals(NumberConstants.ONE_STRING, dept.getStatus())) {
            if (deptService.selectNormalChildrenDeptById(deptId) > 0) {
                throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_DEPT_EXIST_SON_NOT_ALLOWED_STOP));
            } else if (deptService.checkDeptExistUser(deptId)) {
                throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_DEPT_EXIST_USER_NOT_ALLOWED_STOP));
            }
        }
        dept.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(deptService.updateDept(dept));
    }

    /**
     * 删除部门
     */
    @RequiresPermissions("system:dept:remove")
    @Log(title = "部门管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deptId}")
    @ApiOperation(value = "删除部门")
    public AjaxResult remove(@PathVariable Long deptId) {
        if (deptService.hasChildByDeptId(deptId)) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_DEPT_EXIST_SON_NOT_ALLOWED_DEL));
        }
        if (deptService.checkDeptExistUser(deptId)) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_DEPT_EXIST_USER_NOT_ALLOWED_DEL));
        }
        deptService.checkDeptDataScope(deptId);
        return toAjax(deptService.deleteDeptById(deptId));
    }

}