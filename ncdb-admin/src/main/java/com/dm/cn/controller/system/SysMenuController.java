package com.dm.cn.controller.system;

import com.dm.cn.common.core.constant.NumberConstants;
import com.dm.cn.common.core.domain.SysMenu;
import com.dm.cn.common.core.enums.VisibleEnum;
import com.dm.cn.common.core.utils.StringUtils;
import com.dm.cn.common.core.web.controller.BaseController;
import com.dm.cn.common.core.web.domain.AjaxResult;
import com.dm.cn.common.log.annotation.Log;
import com.dm.cn.common.log.enums.BusinessType;
import com.dm.cn.common.security.annotation.RequiresPermissions;
import com.dm.cn.common.security.utils.SecurityUtils;
import com.dm.cn.system.entity.vo.RouterVO;
import com.dm.cn.system.service.ISysMenuService;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单信息
 *
 * @author dameng
 */
@RestController
@RequestMapping("/system/menu")
public class SysMenuController extends BaseController {

    @Resource
    private ISysMenuService menuService;

    /**
     * 获取菜单列表
     */
    @RequiresPermissions("system:menu:list")
    @GetMapping("/list")
    @ApiOperation(value = "获取菜单列表")
    public AjaxResult list(SysMenu menu) {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuList(menu, userId);
        return AjaxResult.success(menus);
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @RequiresPermissions("system:menu:list")
    @GetMapping(value = "/{menuId}")
    @ApiOperation(value = "根据菜单编号获取详细信息")
    public AjaxResult getInfo(@PathVariable Long menuId) {
        return AjaxResult.success(menuService.selectMenuById(menuId));
    }

    /**
     * 获取菜单下拉树列表
     *
     * @param menu
     * @return {@link AjaxResult}
     * @author cn 2024/07/03
     */
    @GetMapping("/treeSelect")
    @ApiOperation(value = "获取菜单下拉树列表")
    public AjaxResult treeSelect(SysMenu menu) {
        // 获取当前登录用户ID
        Long userId = SecurityUtils.getUserId();
        // 获取用户菜单权限，并排除不显示的菜单
        List<SysMenu> menus = sysMenuFilter(menuService.selectMenuList(menu, userId));
        // 构建菜单树
        return AjaxResult.success(menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 加载对应角色菜单列表树
     *
     * @param roleId
     * @return {@link AjaxResult}
     * @author cn 2024/07/03
     */
    @GetMapping(value = "/roleMenuTreeSelect/{roleId}")
    @ApiOperation(value = "加载对应角色菜单列表树")
    public AjaxResult roleMenuTreeSelect(@PathVariable("roleId") Long roleId) {
        // 获取当前登录用户ID
        Long userId = SecurityUtils.getUserId();
        // 获取用户菜单权限，并排除不显示的菜单
        List<SysMenu> menus = sysMenuFilter(menuService.selectMenuList(userId));
        AjaxResult ajax = AjaxResult.success();
        // 根据角色ID查询该角色相关的菜单ID集合
        ajax.put("checkedKeys", menuService.selectMenuListByRoleId(roleId));
        // 构建菜单树
        ajax.put("menus", menuService.buildMenuTreeSelect(menus));
        return ajax;
    }

    /**
     * 新增菜单
     */
    @RequiresPermissions("system:menu:add")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation(value = "新增菜单")
    public AjaxResult add(@Validated @RequestBody SysMenu menu) {
        if (NumberConstants.ONE_STRING.equals(menuService.checkMenuNameUnique(menu))) {
            return AjaxResult.error("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        } else if (NumberConstants.ZERO_STRING.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath())) {
            return AjaxResult.error("新增菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        menu.setCreateBy(SecurityUtils.getUsername());
        return toAjax(menuService.insertMenu(menu));
    }

    /**
     * 修改菜单
     */
    @RequiresPermissions("system:menu:edit")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation(value = "修改菜单")
    public AjaxResult edit(@Validated @RequestBody SysMenu menu) {
        if (NumberConstants.ONE_STRING.equals(menuService.checkMenuNameUnique(menu))) {
            return AjaxResult.error("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        } else if (NumberConstants.ZERO_STRING.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath())) {
            return AjaxResult.error("修改菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        } else if (menu.getMenuId().equals(menu.getParentId())) {
            return AjaxResult.error("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        menu.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(menuService.updateMenu(menu));
    }

    /**
     * 删除菜单
     */
    @RequiresPermissions("system:menu:remove")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{menuId}")
    @ApiOperation(value = "删除菜单")
    public AjaxResult remove(@PathVariable("menuId") Long menuId) {
        if (menuService.hasChildByMenuId(menuId)) {
            return AjaxResult.error("存在子菜单,不允许删除");
        }
        if (menuService.checkMenuExistRole(menuId)) {
            return AjaxResult.error("菜单已分配,不允许删除");
        }
        return toAjax(menuService.deleteMenuById(menuId));
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("getRouters")
    @ApiOperation(value = "获取路由信息")
    public AjaxResult getRouters() {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        List<RouterVO> routerVoList = menuService.buildMenus(menus);
        return AjaxResult.success(routerVoList);
    }

    /**
     * 角色对应菜单权限 过滤用户、角色、部门相关菜单
     */
    private List<SysMenu> sysMenuFilter(List<SysMenu> list) {
        return list.stream()
                .filter(entry -> VisibleEnum.VISIBLE.getValue().equals(entry.getVisible()))
                .collect(Collectors.toList());
    }
}
