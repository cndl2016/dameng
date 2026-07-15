package com.dm.cn.system.mapper;

import com.dm.cn.common.core.domain.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色表 数据层
 *
 * @author cn 2024/07/03
 */
public interface SysRoleMapper {
    /**
     * 根据条件分页查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     * @author cn 2024/07/03
     */
    List<SysRole> selectRoleList(SysRole role);

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     * @author cn 2024/07/03
     */
    SysRole selectRoleById(Long roleId);

    /**
     * 查询最大的角色ID
     *
     * @return 角色ID
     * @author cn 2024/07/03
     */
    Long selectMaxRoleId();

    /**
     * 校验角色名称是否唯一
     *
     * @param roleName 角色名称
     * @return 角色信息
     * @author cn 2024/07/03
     */
    SysRole checkRoleNameUnique(String roleName);

    /**
     * 新增角色信息
     *
     * @param role 角色信息
     * @return 结果
     * @author cn 2024/07/03
     */
    int insertRole(SysRole role);

    /**
     * 获取角色数据权限
     *
     * @param roleId 角色id
     * @return 结果
     * @author cn 2024/07/03
     */
    String getRoleDataScope(@Param("roleId") Long roleId);

    /**
     * 修改角色信息
     *
     * @param role 角色信息
     * @return 结果
     * @author cn 2024/07/03
     */
    int updateRole(SysRole role);

    /**
     * 批量删除角色信息
     *
     * @param roleIds 需要删除的角色ID
     * @return 结果
     * @author cn 2024/07/03
     */
    int deleteRoleByIds(Long[] roleIds);

    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 角色列表
     * @author cn 2024/07/03
     */
    List<SysRole> selectRolePermissionByUserId(Long userId);

    /**
     * 根据用户ID查询角色
     *
     * @param userName 用户名
     * @return 角色列表
     * @author cn 2024/07/03
     */
    List<SysRole> selectRolesByUserName(String userName);
}
