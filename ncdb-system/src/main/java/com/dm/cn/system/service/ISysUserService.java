package com.dm.cn.system.service;

import com.dm.cn.common.core.domain.SysUser;
import com.dm.cn.system.entity.param.SysUserTreeParam;
import com.dm.cn.system.entity.server.SysUserOnline;
import com.dm.cn.system.entity.vo.UserTreeVO;

import java.util.List;

/**
 * 用户 业务层
 *
 * @author dameng
 */
public interface ISysUserService {
    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    List<SysUser> selectUserList(SysUser user);

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
     * 通过ID列表查询用户
     *
     * @param userIdList 用户ID
     * @return 用户对象信息
     */
    List<SysUser> selectUserByIds(List<Long> userIdList);

    /**
     * 根据用户ID查询用户所属角色组
     *
     * @param userName 用户名
     * @return 结果
     */
    String selectUserRoleGroup(String userName);

    /**
     * 根据用户ID查询用户所属角色组（英文）
     *
     * @param userName 用户名
     * @return 结果
     */
    String selectUserRoleGroupEn(String userName);

    /**
     * 根据uid查询用户
     *
     * @param uid ad域objectGUID
     * @return 结果
     */
    SysUser selectDomainUserByUid(String uid);

    /**
     * 校验用户名称是否唯一
     *
     * @param userName 用户名称
     * @return 结果
     */
    boolean checkUserNameUnique(String userName);

    /**
     * 校验域用户名称是否唯一
     *
     * @param userName 用户名称
     * @param uid      ad域objectGUID
     * @return 结果
     */
    String checkDomainUserNameUnique(String userName, String uid);

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    boolean checkPhoneUnique(SysUser user);

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    boolean checkEmailUnique(SysUser user);

    /**
     * 校验用户操作对象是否为管理员
     *
     * @param user 用户信息
     */
    void checkUserOperateAdmin(SysUser user);

    /**
     * 校验用户是否有数据权限
     *
     */
    void checkUserPermission();

    /**
     * 新增用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    int insertUser(SysUser user);

    /**
     * 修改用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    int updateUser(SysUser user);

    /**
     * 修改用户语种信息
     *
     * @param user 用户信息
     * @return 结果
     */
    int updateLanguage(SysUser user);

    /**
     * 修改用户状态
     *
     * @param user 用户信息
     * @return 结果
     */
    int updateUserStatus(SysUser user);

    /**
     * 修改用户基本信息
     *
     * @param user 用户信息
     * @return 结果
     */
    int updateUserProfile(SysUser user);

    /**
     * 修改用户头像
     *
     * @param userName 用户名
     * @param avatar   头像地址
     * @return 结果
     */
    boolean updateUserAvatar(String userName, String avatar);

    /**
     * 重置用户密码
     *
     * @param user 用户信息
     * @return 结果
     */
    int resetPwd(SysUser user);

    /**
     * 重置用户密码
     *
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    int resetUserPwd(String userName, String password);

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    int deleteUserByIds(Long[] userIds);

    /**
     * 获取在线用户
     *
     * @return {@link List}<{@link SysUserOnline}>
     */
    List<SysUserOnline> getAllOnlineUser();

    /**
     * 导入域用户
     *
     * @param userList 域用户列表
     * @return int
     */
    int importDomainUser(List<SysUser> userList);

    /**
     * 根据域id筛选已导入用户
     *
     * @param domainId 域id
     * @return {@link List}<{@link String}>
     */
    List<String> selectFilterUid(Long domainId);

    /**
     * 获取域同步用户列表
     *
     * @param domainId 域id
     * @return {@link List}<{@link SysUser}>
     */
    List<SysUser> getSyncUserList(Long domainId);

    /**
     * 批量更新域用户
     *
     * @param updateList 域用户列表
     */
    void updateBatchUserByUid(List<SysUser> updateList);

    /**
     * 获取用户列表树
     *
     * @param param 列表树参数信息
     * @return {@link List}<{@link UserTreeVO}>
     */
    List<UserTreeVO> getUserTree(SysUserTreeParam param);

    /**
     * 获取用戶权限
     *
     * @return {@link List}<{@link String}>
     * @author yth 2024/06/12
     */
    List<String> getUserPermission();

    /**
     * 根据用户头像路径，将图片转换为二进制
     *
     * @param avatar 用户头像路径
     * @return {@link String}
     */
    String avatarToBinary(String avatar);

}
