package com.dm.cn.controller.system;

import com.dm.cn.common.core.constant.NumberConstants;
import com.dm.cn.common.core.domain.SysRole;
import com.dm.cn.common.core.exception.ServiceException;
import com.dm.cn.common.core.utils.StringUtils;
import com.dm.cn.common.core.web.controller.BaseController;
import com.dm.cn.common.core.web.domain.AjaxResult;
import com.dm.cn.common.core.web.page.TableDataInfo;
import com.dm.cn.common.log.annotation.Log;
import com.dm.cn.common.log.enums.BusinessType;
import com.dm.cn.common.security.annotation.RequiresPermissions;
import com.dm.cn.common.security.utils.SecurityUtils;
import com.dm.cn.common.utils.I18nMessageUtil;
import com.dm.cn.system.constant.MessageTipConstant;
import com.dm.cn.system.service.ISysRoleService;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 角色信息
 *
 * @author cn 2024/07/03
 */
@RestController
@RequestMapping("/system/role")
public class SysRoleController extends BaseController {
    @Resource
    private ISysRoleService roleService;

    /**
     * 根据条件分页查询角色数据
     *
     * @param role
     * @return {@link TableDataInfo}
     * @author cn 2024/07/03
     */
    @RequiresPermissions("system:role:list")
    @GetMapping("/list")
    @ApiOperation(value = "根据条件分页查询角色数据")
    public TableDataInfo list(SysRole role) {
        startPage();
        List<SysRole> list = roleService.selectRoleList(role);
        return getDataTable(list);
    }

    /**
     * 根据角色编号获取详细信息
     *
     * @param roleId
     * @return {@link AjaxResult}
     * @author cn 2024/07/03
     */
    @GetMapping(value = "/{roleId}")
    @ApiOperation(value = "根据角色编号获取详细信息")
    public AjaxResult getRoleInfo(@PathVariable Long roleId) {
        roleService.checkRoleDataScope(roleId);
        return AjaxResult.success(roleService.selectRoleById(roleId));
    }

    /**
     * 新增角色
     *
     * @param role
     * @return {@link AjaxResult}
     * @author cn 2024/07/03
     */
    @RequiresPermissions("system:role:add")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation(value = "新增角色")
    public AjaxResult add(@Validated @RequestBody SysRole role) {
        if (NumberConstants.ONE_STRING.equals(roleService.checkRoleNameUnique(role))) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_ROLE_ADD_FAILED, role.getRoleName()));
        }
        if (StringUtils.isEmpty((role.getDataScope()))) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_ROLE_ADD_PERMISSIONS_NOT_CONFIG, role.getRoleName()));
        }
        role.setCreateBy(SecurityUtils.getUsername());
        return toAjax(roleService.insertRole(role));

    }

    /**
     * 修改保存角色
     *
     * @param role
     * @return {@link AjaxResult}
     * @author cn 2024/07/03
     */
    @RequiresPermissions("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation(value = "修改保存角色")
    public AjaxResult edit(@Validated @RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        if (NumberConstants.ONE_STRING.equals(roleService.checkRoleNameUnique(role))) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_ROLE_UPDATE_FAILED, role.getRoleName()));
        }
        role.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(roleService.updateRole(role));
    }

    /**
     * 状态修改
     *
     * @param role
     * @return {@link AjaxResult}
     * @author cn 2024/07/03
     */
    @RequiresPermissions("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    @ApiOperation(value = "状态修改")
    public AjaxResult changeStatus(@RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        role.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(roleService.updateRoleStatus(role));
    }

    /**
     * 删除角色
     *
     * @param roleIds
     * @return {@link AjaxResult}
     * @author cn 2024/07/03
     */
    @RequiresPermissions("system:role:remove")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roleIds}")
    @ApiOperation(value = "删除角色")
    public AjaxResult remove(@PathVariable Long[] roleIds) {
        return toAjax(roleService.deleteRoleByIds(roleIds));
    }
}