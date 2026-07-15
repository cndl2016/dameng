package com.dm.cn.controller.system;

import com.dm.cn.common.core.constant.Constants;
import com.dm.cn.common.core.domain.LoginUser;
import com.dm.cn.common.core.domain.SysRole;
import com.dm.cn.common.core.domain.SysUser;
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
import com.dm.cn.system.entity.param.SysUserTreeParam;
import com.dm.cn.system.service.ISysDeptService;
import com.dm.cn.system.service.ISysPermissionService;
import com.dm.cn.system.service.ISysRoleService;
import com.dm.cn.system.service.ISysUserService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户信息
 *
 * @author cn
 * @date 2024/08/20
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController {
    @Resource
    private ISysUserService userService;

    @Resource
    private ISysRoleService roleService;

    @Resource
    private ISysDeptService deptService;

    @Resource
    private ISysPermissionService permissionService;

    /**
     * 获取用户列表
     *
     * @author yyj 2024-07-01
     */
    @RequiresPermissions("system:user:list")
    @GetMapping("/list")
    @ApiOperation(value = "获取用户列表")
    public TableDataInfo list(SysUser user) {
        // 开始分页
        startPage();
        // 查询用户数据
        List<SysUser> userList = userService.selectUserList(user);
        // 组装返回信息
        return getDataTable(userList);
    }

    /**
     * 登陆时获取用户信息
     *
     * @return 用户信息
     * @author yyj 2024-07-01
     */
    @GetMapping("getInfo")
    @ApiOperation(value = "获取用户信息")
    public AjaxResult getInfo() {
        Long userId = SecurityUtils.getUserId();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(userId);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(userId);
        // 组装返回信息
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", userService.selectUserById(userId));
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }

    /**
     * 根据用户编号获取详细信息(用户新增时返回所有角色信息，修改用户时额外返回用户信息以及它的角色信息)
     *
     * @author yyj 2024-07-01
     */
    @RequiresPermissions("system:user:list")
    @GetMapping(value = {"/", "/{userId}"})
    @ApiOperation(value = "根据用户编号获取详细信息")
    public AjaxResult getUserInfo(@PathVariable(value = "userId", required = false) Long userId) {
        // 检查用户是否有权限 普通用户已屏蔽系统管理菜单，判断是否是管理员
        userService.checkUserPermission();
        AjaxResult ajax = AjaxResult.success();
        // 查询所有角色，普通用户过滤管理员角色
        List<SysRole> roles = roleService.selectRoleAll();
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        // 修改用户时额外返回用户信息以及它的角色信息
        if (StringUtils.isNotNull(userId)) {
            SysUser sysUser = userService.selectUserById(userId);
            ajax.put(AjaxResult.DATA_TAG, sysUser);
            ajax.put("roleIds", sysUser.getRoles().stream().filter(Objects::nonNull).map(role -> String.valueOf(role.getRoleId())).collect(Collectors.toList()));
        }
        return ajax;
    }

    /**
     * 新增用户
     *
     * @author yyj 2024-07-01
     */
    @RequiresPermissions("system:user:add")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation(value = "新增用户")
    public AjaxResult add(@Validated @RequestBody SysUser user) {
        if (!userService.checkUserNameUnique(user.getUserName())) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_ADD_ACCOUNT_EXIST, user.getUserName()));
        } else if (StringUtils.isNotEmpty(user.getPhoneNumber())
                && !userService.checkPhoneUnique(user)) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_ADD_PHONE_EXIST, user.getUserName()));
        } else if (StringUtils.isNotEmpty(user.getEmail())
                && !userService.checkEmailUnique(user)) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_ADD_EMAIL_EXIST, user.getUserName()));
        } else if (!deptService.checkDeptValid(user.getDeptId())) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_ADD_DEPT_EXIST, user.getUserName()));
        }
        user.setCreateBy(SecurityUtils.getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     *
     * @author yyj 2024-07-01
     */
    @RequiresPermissions("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation(value = "修改用户")
    public AjaxResult edit(@Validated @RequestBody SysUser user) {
        // 判断如果修改的是管理员，直接拦截
        userService.checkUserOperateAdmin(user);
        // 检查用户是否有权限 普通用户已屏蔽系统管理菜单，判断是否是管理员
        userService.checkUserPermission();
        if (StringUtils.isNotEmpty(user.getPhoneNumber())
                && !userService.checkPhoneUnique(user)) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_UPDATE_PHONE_EXIST, user.getUserName()));
        } else if (StringUtils.isNotEmpty(user.getEmail())
                && !userService.checkEmailUnique(user)) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_UPDATE_EMAIL_EXIST, user.getUserName()));
        } else if (!deptService.checkDeptValid(user.getDeptId())) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_UPDATE_DEPT_EXIST, user.getUserName()));
        }
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.updateUser(user));
    }

    /**
     * 删除用户
     *
     * @author yyj 2024-07-01
     */
    @RequiresPermissions("system:user:remove")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    @ApiOperation(value = "删除用户")
    public AjaxResult remove(@PathVariable Long[] userIds) {
        // 拦截删除自己的操作
        if (ArrayUtils.contains(userIds, SecurityUtils.getUserId())) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_NOT_ALLOWED_DEL_SELF));
        }
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     *
     * @author yyj 2024-07-01
     */
    @RequiresPermissions("system:user:resetPwd")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    @ApiOperation(value = "重置密码")
    public AjaxResult resetPwd(@RequestBody SysUser user) {
        // 判断如果重置的是管理员的密码，直接拦截
        userService.checkUserOperateAdmin(user);
        // 检查用户是否有权限 普通用户已屏蔽系统管理菜单，判断是否是管理员
        userService.checkUserPermission();
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.resetPwd(user));
    }

    /**
     * 状态修改
     *
     * @author yyj 2024-07-01
     */
    @RequiresPermissions("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    @ApiOperation(value = "状态修改")
    public AjaxResult changeStatus(@RequestBody SysUser user) {
        // 判断如果修改的是管理员的状态，直接拦截
        userService.checkUserOperateAdmin(user);
        // 检查用户是否有权限 普通用户已屏蔽系统管理菜单，判断是否是管理员
        userService.checkUserPermission();
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.updateUserStatus(user));
    }

    /**
     * 获取用户列表树
     *
     * @author yyj 2024-07-01
     */
    @PostMapping("/getUserTree")
    @ApiOperation(value = "获取用户列表树")
    public AjaxResult getUserTree(@RequestBody SysUserTreeParam param) {
        return AjaxResult.success(userService.getUserTree(param));
    }


    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/updateLanguage/{language}")
    @ApiOperation(value = "语种切换")
    public AjaxResult updateLanguage(@PathVariable String language) {
        if (Constants.ZH_CN.equals(language)) {
            language = Constants.ZH;
        } else if (Constants.EN_US.equals(language)) {
            language = Constants.EN;
        }
        // 修改用户表数据
        Long userId = SecurityUtils.getUserId();
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setLanguage(language);
        user.setUpdateTime(new Date());
        user.setUpdateBy(SecurityUtils.getUsername());

        // 更新缓存数据
        LoginUser loginUser = SecurityUtils.getLoginUser();
        loginUser.setLanguage(language);

        return toAjax(userService.updateLanguage(user));
    }

}
