package com.dm.cn.system.mapper;

import com.dm.cn.common.core.domain.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户表 数据层
 *
 * @author dameng
 */
public interface SysUserMapper {
    /**
     * 根据条件分页查询用户列表
     *
     * @param sysUser 用户信息
     * @return 用户信息集合信息
     */
    List<SysUser> selectUserList(SysUser sysUser);

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    SysUser selectUserByUserName(String userName);

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    SysUser selectUserById(Long userId);

    /**
     * 新增用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    int insertUser(SysUser user);


    /**
     * 批量新增用户信息
     *
     * @param userList 用户信息列表
     * @return boolean
     */
    int insertBatchUser(List<SysUser> userList);

    /**
     * 修改用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    int updateUser(SysUser user);

    /**
     * 修改用户头像
     *
     * @param userName 用户名
     * @param avatar   头像地址
     * @return 结果
     */
    int updateUserAvatar(@Param("userName") String userName, @Param("avatar") String avatar);

    /**
     * 重置用户密码
     *
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    int resetUserPwd(@Param("userName") String userName, @Param("password") String password);

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    int deleteUserById(Long userId);

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    int deleteUserByIds(Long[] userIds);

    /**
     * 校验用户名称是否唯一
     *
     * @param userName 用户名称
     * @return 结果
     */
    int checkUserNameUnique(String userName);

    /**
     * 校验ad域用户名称是否唯一
     *
     * @param userName 用户名称
     * @param uid      ad域objectGUID
     * @return 结果
     */
    int checkDomainUserNameUnique(@Param("userName") String userName, @Param("uid") String uid);

    /**
     * 校验手机号码是否唯一
     *
     * @param phoneNumber 手机号码
     * @return 结果
     */
    SysUser checkPhoneUnique(String phoneNumber);

    /**
     * 校验email是否唯一
     *
     * @param email 用户邮箱
     * @return 结果
     */
    SysUser checkEmailUnique(String email);

    /**
     * 根据条件分页查询用户列表
     *
     * @return 用户信息集合信息
     */
    List<SysUser> selectAlarmUser();

    /**
     * 筛选已导入用户
     *
     * @param domainId 域id
     * @return {@link List}<{@link String}>
     */
    List<String> selectFilterObjectGuid(@Param("domainId") Long domainId);

    /**
     * 获取域同步用户列表
     *
     * @param domainId 域id
     * @return {@link List}<{@link SysUser}>
     */
    List<SysUser> getSyncUserList(@Param("domainId") Long domainId);

    /**
     * 通过ID列表查询用户
     *
     * @param userIdList 用户ID
     * @return 用户对象信息
     */
    List<SysUser> selectUserByIds(List<Long> userIdList);

    /**
     * 批量更新域用户
     *
     * @param updateList 域id
     * @return {@link List}<{@link SysUser}>
     */
    boolean updateBatchUserByUid(List<SysUser> updateList);

    /**
     * 根据uid查询域导入的用户
     *
     * @param uid ad域objectGUID
     * @return 结果
     */
    SysUser selectDomainUserByUid(@Param("uid") String uid);

    /**
     * 查询所有用户
     *
     * @return 用户对象信息
     */
    List<SysUser> getUserList();

    /**
     * 根据部门id查询用户
     *
     * @param deptId 部门id
     * @return 结果
     */
    List<String> getUserByDeptId(@Param("deptId") Long deptId);
}
