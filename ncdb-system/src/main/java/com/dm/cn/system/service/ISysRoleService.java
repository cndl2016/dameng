package com.dm.cn.system.service;

import com.dm.cn.common.core.domain.SysRole;

import java.util.List;
import java.util.Set;

/**
 * 角色业务层
 *
 * @author cn 2024/07/03
 */
public interface ISysRoleService {
    /**
     * 根据条件分页查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     * @author cn 2024/07/03
     */
    List<SysRole> selectRoleList(SysRole role);

    /**
     * 校验角色是否有数据权限
     *
     * @param roleId 角色id
     * @author cn 2024/07/03
     */
    void checkRoleDataScope(Long roleId);

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     * @author cn 2024/07/03
     */
    SysRole selectRoleById(Long roleId);

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色信息
     * @return 结果
     * @author cn 2024/07/03
     */
    String checkRoleNameUnique(SysRole role);

    /**
     * 新增保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     * @author cn 2024/07/03
     */
    int insertRole(SysRole role);

    /**
     * 校验角色是否允许操作
     *
     * @param role 角色信息
     * @author cn 2024/07/03
     */
    void checkRoleAllowed(SysRole role);

    /**
     * 修改保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     * @author cn 2024/07/03
     */
    int updateRole(SysRole role);

    /**
     * 修改角色状态
     *
     * @param role 角色信息
     * @return 结果
     * @author cn 2024/07/03
     */
    int updateRoleStatus(SysRole role);

    /**
     * 批量删除角色信息
     *
     * @param roleIds 需要删除的角色ID
     * @return 结果
     * @author cn 2024/07/03
     */
    int deleteRoleByIds(Long[] roleIds);

    /**
     * 根据用户ID查询角色权限
     *
     * @param userId 用户ID
     * @return 权限列表
     * @author cn 2024/07/03
     */
    Set<String> selectRolePermissionByUserId(Long userId);

    /**
     * 查询所有角色
     *
     * @return 角色列表
     * @author cn 2024/07/03
     */
    List<SysRole> selectRoleAll();
}
