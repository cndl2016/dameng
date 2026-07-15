package com.dm.cn.system.service.impl;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.dm.cn.common.core.constant.Constants;
import com.dm.cn.common.core.constant.NumberConstants;
import com.dm.cn.common.core.constant.SymbolConstants;
import com.dm.cn.common.core.constant.TokenConstants;
import com.dm.cn.common.core.domain.LoginUser;
import com.dm.cn.common.core.domain.SysDept;
import com.dm.cn.common.core.domain.SysRole;
import com.dm.cn.common.core.domain.SysUser;
import com.dm.cn.common.core.enums.UserStatus;
import com.dm.cn.common.core.exception.ServiceException;
import com.dm.cn.common.core.utils.IdWorker;
import com.dm.cn.common.core.utils.StringUtils;
import com.dm.cn.common.core.utils.bean.DbTypeBean;
import com.dm.cn.common.security.utils.SecurityUtils;
import com.dm.cn.common.utils.I18nMessageUtil;
import com.dm.cn.system.config.security.service.CacheService;
import com.dm.cn.system.constant.MessageTipConstant;
import com.dm.cn.system.entity.param.SysUserTreeParam;
import com.dm.cn.system.entity.server.SysUserOnline;
import com.dm.cn.system.entity.server.SysUserRole;
import com.dm.cn.system.entity.vo.UserTreeVO;
import com.dm.cn.system.mapper.SysDeptMapper;
import com.dm.cn.system.mapper.SysRoleMapper;
import com.dm.cn.system.mapper.SysUserMapper;
import com.dm.cn.system.mapper.SysUserRoleMapper;
import com.dm.cn.system.service.ISysConfigService;
import com.dm.cn.system.service.ISysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户 业务层处理
 *
 * @author dameng
 */
@Service
public class SysUserServiceImpl implements ISysUserService {
    private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

    @Resource
    private SysUserMapper userMapper;

    @Resource
    private SysRoleMapper roleMapper;

    @Resource
    private SysUserRoleMapper userRoleMapper;

    @Resource
    private SysDeptMapper deptMapper;

    @Resource
    private ISysConfigService configService;

    @Resource
    private CacheService cacheService;

    @Resource
    private DbTypeBean dbTypeBean;

    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public List<SysUser> selectUserList(SysUser user) {
        // 数据源类型
        user.setH2(dbTypeBean.isH2());
        // 查询数据
        List<SysUser> list = userMapper.selectUserList(user);
        // 处理用户类型信息
        list.forEach(entry -> {
            if (!ObjectUtils.isEmpty(entry.getDomainId()) && entry.getDomainId().equals(NumberConstants.NUMBER_ZERO_LONG)) {
                entry.setSourceType(I18nMessageUtil.getMessage(MessageTipConstant.USER_TYPE_NORMAL));
            } else {
                entry.setSourceType(I18nMessageUtil.getMessage(MessageTipConstant.USER_TYPE_AD));
            }
        });
        return list;
    }

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserByUserName(String userName) {
        return userMapper.selectUserByUserName(userName);
    }

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserById(Long userId) {
        SysUser sysUser = userMapper.selectUserById(userId);
        // 根据用户头像路径，转换为二进制
        if (ObjectUtils.isNotEmpty(sysUser) && StringUtils.isNotEmpty(sysUser.getAvatar())) {
            sysUser.setAvatarBinary(this.avatarToBinary(sysUser.getAvatar()));
        }
        return sysUser;
    }

    /**
     * 通过用户ID列表查询用户
     *
     * @param userIdList 用户ID列表
     * @return {@link List}<{@link SysUser}>
     */
    @Override
    public List<SysUser> selectUserByIds(List<Long> userIdList) {
        return userMapper.selectUserByIds(userIdList);
    }

    /**
     * 查询用户所属角色组
     *
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserRoleGroup(String userName) {
        List<SysRole> list = roleMapper.selectRolesByUserName(userName);
        if (CollectionUtils.isEmpty(list)) {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SysRole::getRoleName).collect(Collectors.joining(","));
    }

    @Override
    public String selectUserRoleGroupEn(String userName) {
        List<SysRole> list = roleMapper.selectRolesByUserName(userName);
        if (CollectionUtils.isEmpty(list)) {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SysRole::getRoleNameEn).collect(Collectors.joining(","));
    }

    /**
     * 根据uid查询域导入的用户
     *
     * @param uid ad域objectGUID
     * @return 结果
     */
    @Override
    public SysUser selectDomainUserByUid(String uid) {
        return userMapper.selectDomainUserByUid(uid);
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param userName 用户名称
     * @return 结果
     */
    @Override
    public boolean checkUserNameUnique(String userName) {
        int count = userMapper.checkUserNameUnique(userName);
        return count <= 0;
    }

    /**
     * 校验域用户名称是否唯一
     *
     * @param userName 用户名称
     * @return 结果
     */
    @Override
    public String checkDomainUserNameUnique(String userName, String uid) {
        int count = userMapper.checkDomainUserNameUnique(userName, uid);
        if (count > 0) {
            return NumberConstants.ONE_STRING;
        }
        return NumberConstants.ZERO_STRING;
    }

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     * @return boolean
     */
    @Override
    public boolean checkPhoneUnique(SysUser user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.checkPhoneUnique(user.getPhoneNumber());
        return !StringUtils.isNotNull(info) || info.getUserId().longValue() == userId.longValue();
    }

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return boolean
     */
    @Override
    public boolean checkEmailUnique(SysUser user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.checkEmailUnique(user.getEmail());
        return !StringUtils.isNotNull(info) || info.getUserId().longValue() == userId.longValue();
    }

    /**
     * 校验用户操作对象是否为管理员
     *
     * @param user 用户信息
     */
    @Override
    public void checkUserOperateAdmin(SysUser user) {
        if (StringUtils.isNotNull(user.getUserId()) && user.isAdmin()) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_NOT_OPERATE_MANAGER));
        }
    }

    /**
     * 校验用户是否有权限，目前仅支持admin进行操作
     *
     */
    @Override
    public void checkUserPermission() {
        if (!SysUser.isAdmin(SecurityUtils.getUserId())) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_DATA_NO_PERMISSION));
        }
    }

    /**
     * 新增保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertUser(SysUser user) {
        user.setUserId(IdWorker.getId().nextId());
        // 新增用户信息
        int rows = userMapper.insertUser(user);
        // 新增用户与角色管理
        insertUserRole(user);
        return rows;
    }

    /**
     * 导入域用户
     *
     * @param userList 用户列表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int importDomainUser(List<SysUser> userList) {
        List<String> notUniqueUserNameList = new ArrayList<>();
        // 检查导入列表中是否存在与现有用户名重复的用户
        userList.forEach(user -> {
            if (!checkUserNameUnique(user.getUserName())) {
                notUniqueUserNameList.add(user.getUserName());
                return;
            }
            user.setUserId(IdWorker.getId().nextId());
            user.setCreateBy(SecurityUtils.getUsername());
            user.setCreateTime(new Date(System.currentTimeMillis() / NumberConstants.ONE_THOUSAND * NumberConstants.ONE_THOUSAND));
            // 默认部门
            if (ObjectUtils.isEmpty(user.getDeptId())) {
                user.setDeptId(user.getDefaultDeptId());
            }
            // 默认角色
            if (ObjectUtils.isEmpty(user.getRoleId())) {
                user.setRoleIds(new Long[]{user.getDefaultRoleId()});
            } else {
                user.setRoleIds(new Long[]{user.getRoleId()});
            }
            // 用户角色信息入库
            insertUserRole(user);
            // 导入的用户设置默认密码
            user.setPassword(SecurityUtils.encryptPassword(configService.selectConfigByKey("sys.user.initPassword")));
        });
        // 存在与现有用户名重复的用户则进行报错
        if (!notUniqueUserNameList.isEmpty()) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.IMPORT_SYSTEM_USER_NAME_EXIST, StringUtils.join(notUniqueUserNameList, SymbolConstants.COMMA)));
        }
        // 用户批量入库
        return userMapper.insertBatchUser(userList);
    }

    /**
     * 通过用户域ID查询用户
     *
     * @param domainId 域ID
     * @return {@link List}<{@link String}>
     */
    @Override
    public List<String> selectFilterUid(Long domainId) {
        return userMapper.selectFilterObjectGuid(domainId);
    }

    /**
     * 修改保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateUser(SysUser user) {
        Long userId = user.getUserId();
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 新增用户与角色管理
        insertUserRole(user);
        return userMapper.updateUser(user);
    }

    @Override
    public int updateLanguage(SysUser user) {
        return userMapper.updateUser(user);
    }

    /**
     * 修改用户状态
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserStatus(SysUser user) {
        if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            List<SysUserOnline> allOnlineUser = getAllOnlineUser();
            for (SysUserOnline userOnline : allOnlineUser) {
                // 用户禁用时删除用户登录token缓存，使用户强制登出
                if (userOnline.getUserName().equals(user.getUserName())) {
                    cacheService.deleteObject(TokenConstants.LOGIN_TOKEN_KEY + userOnline.getTokenId());
                }
            }

        }
        return userMapper.updateUser(user);
    }

    /**
     * 修改用户基本信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserProfile(SysUser user) {
        return userMapper.updateUser(user);
    }

    /**
     * 修改用户头像
     *
     * @param userName 用户名
     * @param avatar   头像地址
     * @return 结果
     */
    @Override
    public boolean updateUserAvatar(String userName, String avatar) {
        return userMapper.updateUserAvatar(userName, avatar) > 0;
    }

    /**
     * 重置用户密码
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int resetPwd(SysUser user) {
        return userMapper.updateUser(user);
    }

    /**
     * 重置用户密码
     *
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    @Override
    public int resetUserPwd(String userName, String password) {
        return userMapper.resetUserPwd(userName, password);
    }

    /**
     * 新增用户角色信息
     *
     * @param user 用户对象
     */
    private void insertUserRole(SysUser user) {
        Long[] roles = user.getRoleIds();
        if (StringUtils.isNotNull(roles)) {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<>();
            for (Long roleId : roles) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(user.getUserId());
                ur.setRoleId(roleId);
                list.add(ur);
            }
            if (!list.isEmpty()) {
                userRoleMapper.batchUserRole(list);
            }
        }
    }

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteUserByIds(Long[] userIds) {
        for (Long userId : userIds) {
            checkUserOperateAdmin(new SysUser(userId));
            checkUserPermission();
        }
        // 删除用户与角色关联
        userRoleMapper.deleteUserRole(userIds);
        return userMapper.deleteUserByIds(userIds);
    }

    /**
     * 获取所有在线用户
     *
     * @return {@link List}<{@link SysUserOnline}>
     */
    @Override
    public List<SysUserOnline> getAllOnlineUser() {
        Collection<String> keys = cacheService.keys(TokenConstants.LOGIN_TOKEN_KEY);
        List<SysUserOnline> userOnlineList = new ArrayList<>();
        for (String key : keys) {
            LoginUser user = cacheService.getCacheObject(key);
            userOnlineList.add(loginUserToUserOnline(user));
        }
        return userOnlineList;
    }

    /**
     * 构建在线用户对象
     *
     * @param user 在线用户对象
     * @return {@link SysUserOnline}
     */
    private SysUserOnline loginUserToUserOnline(LoginUser user) {
        if (StringUtils.isNull(user)) {
            return null;
        }
        SysUserOnline sysUserOnline = new SysUserOnline();
        sysUserOnline.setTokenId(user.getToken());
        sysUserOnline.setUserName(user.getUsername());
        sysUserOnline.setIpaddr(user.getIpaddr());
        sysUserOnline.setLoginTime(user.getLoginTime());
        return sysUserOnline;
    }

    /**
     * 获取同步用户列表
     *
     * @param domainId 域用户
     * @return {@link List}<{@link SysUser}>
     */
    @Override
    public List<SysUser> getSyncUserList(Long domainId) {
        return userMapper.getSyncUserList(domainId);
    }

    /**
     * 批量更新域用户
     *
     * @param updateList 域用户列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatchUserByUid(List<SysUser> updateList) {
        // 待删除用户列表
        List<Long> deleteList = new ArrayList<>();
        // 如果域用户改名，与系统用户重名，则需要删除，因为username不可以重复，系统登录会报错
        updateList.forEach(entry -> {
            if (NumberConstants.ONE_STRING.equals(checkDomainUserNameUnique(entry.getUserName(), entry.getObjectGuid()))) {
                deleteList.add(selectDomainUserByUid(entry.getObjectGuid()).getUserId());
            }
        });
        if (!ObjectUtils.isEmpty(deleteList)) {
            deleteUserByIds(deleteList.toArray(new Long[0]));
        }
        // 更新用户列表
        userMapper.updateBatchUserByUid(updateList);
    }

    /**
     * 获取用户列表树
     *
     * @param param 列表树参数信息
     * @return {@link List}<{@link UserTreeVO}>
     */
    @Override
    public List<UserTreeVO> getUserTree(SysUserTreeParam param) {
        // 部门列表
        List<SysDept> deptList = deptMapper.getDeptList();
        // 用户列表
        List<SysUser> userList = userMapper.getUserList();
        // 树构建
        UserTreeVO root = new UserTreeVO(param.getDeptId(), Constants.BLANK, Constants.BLANK, Constants.BLANK, 0);
        createUserTree(root, deptList, userList);
        return root.getChildren();
    }

    /**
     * 构建用户树
     *
     * @param parent   父级节点
     * @param deptList 部门列表
     * @param userList 用户列表
     */
    private void createUserTree(UserTreeVO parent, List<SysDept> deptList, List<SysUser> userList) {
        List<UserTreeVO> childList = new ArrayList<>();
        // 部门下的用户
        List<SysUser> childUserList = userList.stream()
                .filter(x -> parent.getId().equals(x.getDeptId()))
                .collect(Collectors.toList());
        if (!childUserList.isEmpty()) {
            childUserList.forEach(user -> {
                UserTreeVO child = new UserTreeVO(user.getUserId(), user.getNickName() + "(" + user.getUserName() + ")", user.getPhoneNumber(), "user", parent.getLevel() + 1);
                childList.add(child);
            });
        }
        // 部门下的子部门
        List<SysDept> childDeptList = deptList.stream()
                .filter(x -> parent.getId().equals(x.getParentId()))
                .collect(Collectors.toList());
        if (!childDeptList.isEmpty()) {
            childDeptList.forEach(dept -> {
                UserTreeVO child = new UserTreeVO(dept.getDeptId(), dept.getDeptName(), "", "dept", parent.getLevel() + 1);
                createUserTree(child, deptList, userList);
                childList.add(child);
            });
        }
        parent.setChildren(childList);
    }

    /**
     * 获取用戶权限
     *
     * @return {@link List}<{@link String}>
     */
    @Override
    public List<String> getUserPermission() {
        List<String> userNameList = new ArrayList<>();
        SysUser user = SecurityUtils.getLoginUser().getSysUser();
        String maxPermission = SecurityUtils.getUserPermission();
        // 1.全部数据权限：返回空集合
        if (Constants.DATA_SCOPE_ALL.equals(maxPermission)) {
            return userNameList;
        } else if (Constants.DATA_SCOPE_DEPT.equals(maxPermission)) {
            // 3.本部门数据权限：返回本部门所有用户名集合
            userNameList = userMapper.getUserList().stream()
                    .filter(x -> x.getDeptId().equals(user.getDeptId()))
                    .map(SysUser::getUserName).collect(Collectors.toList());
        } else if (Constants.DATA_SCOPE_DEPT_ALL.equals(maxPermission)) {
            // 4.本部门及以下数据权限：返回本部门及以下部门所有用户名集合
            userNameList = userMapper.getUserByDeptId(user.getDeptId());
        } else if (Constants.DATA_SCOPE_SELF.equals(maxPermission)) {
            // 5.仅本人数据权限：返回本人用户名
            userNameList.add(user.getUserName());
        }
        return userNameList;
    }

    /**
     * 根据用户头像路径，将图片转换为二进制
     *
     * @param avatar 头像路径
     * @return {@link String}
     */
    @Override
    public String avatarToBinary(String avatar) {
        String avatarBinary = "";
        try {
            // 头像路径
            Path imageFile = Paths.get(avatar);
            // 转为比特流
            byte[] imagesBytes = Files.readAllBytes(imageFile);
            // 转为字符串
            avatarBinary = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(imagesBytes);
        } catch (Exception e) {
            log.error("avatarToBinary: {}", e);
        }

        return avatarBinary;
    }
}
