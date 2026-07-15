package com.dm.cn.system.mapper;

import com.dm.cn.system.entity.server.SysRoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色与菜单关联表 数据层
 *
 * @author dameng
 */
public interface SysRoleMenuMapper {
    /**
     * 查询菜单使用数量
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    int checkMenuExistRole(Long menuId);

    /**
     * 获取角色菜单信息
     *
     * @param roleId 角色id
     * @return 结果
     * @author cn 2024/07/03
     */
    List<Long> getRoleMenuIds(@Param("roleId") Long roleId);

    /**
     * 通过角色ID删除角色和菜单关联
     *
     * @param roleId 角色ID
     * @return 结果
     * @author cn 2024/07/03
     */
    int deleteRoleMenuByRoleId(Long roleId);

    /**
     * 批量删除角色菜单关联信息
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     * @author cn 2024/07/03
     */
    int deleteRoleMenu(Long[] ids);

    /**
     * 批量新增角色菜单信息
     *
     * @param roleMenuList 角色菜单列表
     * @return 结果
     * @author cn 2024/07/03
     */
    int batchRoleMenu(List<SysRoleMenu> roleMenuList);
}
