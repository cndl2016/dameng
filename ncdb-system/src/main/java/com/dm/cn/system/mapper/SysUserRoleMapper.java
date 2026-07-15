package com.dm.cn.system.mapper;

import com.dm.cn.system.entity.server.SysUserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户与角色关联表 数据层
 *
 * @author dameng
 */
public interface SysUserRoleMapper {
    /**
     * 通过用户ID删除用户和角色关联
     *
     * @param userId 用户ID
     * @return 结果
     */
    int deleteUserRoleByUserId(Long userId);

    /**
     * 批量删除用户和角色关联
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteUserRole(Long[] ids);

    /**
     * 批量新增用户角色信息
     *
     * @param userRoleList 用户角色列表
     * @return 结果
     */
    int batchUserRole(List<SysUserRole> userRoleList);

    /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId 角色ID
     * @return 结果
     * @author cn 2024/07/03
     */
    int countUserRoleByRoleId(Long roleId);

    /**
     * 根据角色id获取用户名
     *
     * @param roleId 角色ID
     * @return 结果
     * @author cn 2024/07/03
     */
    List<String> getUserNameByRoleId(@Param("roleId") Long roleId);
}
