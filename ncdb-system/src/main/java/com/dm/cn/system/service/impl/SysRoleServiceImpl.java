package com.dm.cn.system.service.impl;

import com.dm.cn.common.core.constant.NumberConstants;
import com.dm.cn.common.core.constant.TokenConstants;
import com.dm.cn.common.core.domain.SysRole;
import com.dm.cn.common.core.domain.SysUser;
import com.dm.cn.common.core.exception.ServiceException;
import com.dm.cn.common.core.utils.IdWorker;
import com.dm.cn.common.core.utils.SpringUtils;
import com.dm.cn.common.core.utils.StringUtils;
import com.dm.cn.common.core.utils.bean.DbTypeBean;
import com.dm.cn.common.security.utils.SecurityUtils;
import com.dm.cn.common.utils.I18nMessageUtil;
import com.dm.cn.system.config.security.service.CacheService;
import com.dm.cn.system.constant.MessageTipConstant;
import com.dm.cn.system.entity.server.SysRoleMenu;
import com.dm.cn.system.entity.server.SysUserOnline;
import com.dm.cn.system.mapper.SysRoleMapper;
import com.dm.cn.system.mapper.SysRoleMenuMapper;
import com.dm.cn.system.mapper.SysUserRoleMapper;
import com.dm.cn.system.service.ISysRoleService;
import com.dm.cn.system.service.ISysUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 角色 业务层处理
 *
 * @author cn 2024/07/03
 */
@Service
public class SysRoleServiceImpl implements ISysRoleService {
    @Resource
    private SysRoleMapper roleMapper;
    @Resource
    private SysRoleMenuMapper roleMenuMapper;
    @Resource
    private SysUserRoleMapper userRoleMapper;
    @Resource
    private ISysUserService sysUserService;
    @Resource
    private CacheService cacheService;
    @Resource
    private DbTypeBean dbTypeBean;

    /**
     * 根据条件分页查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     * @author cn 2024/07/03
     */
    @Override
    public List<SysRole> selectRoleList(SysRole role) {
        // 判断数据库类型
        role.setH2(dbTypeBean.isH2());
        return roleMapper.selectRoleList(role);
    }

    /**
     * 校验角色是否有数据权限
     *
     * @param roleId 角色id
     * @author cn 2024/07/03
     */
    @Override
    public void checkRoleDataScope(Long roleId) {
        // 非管理员用户，校验是否有权限访问角色数据
        if (!SysUser.isAdmin(SecurityUtils.getUserId())) {
            SysRole role = new SysRole();
            role.setRoleId(roleId);
            List<SysRole> roles = SpringUtils.getAopProxy(this).selectRoleList(role);
            if (StringUtils.isEmpty(roles)) {
                throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_ROLE_DATA_NO_PERMISSION));
            }
        }
    }

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     * @author cn 2024/07/03
     */
    @Override
    public SysRole selectRoleById(Long roleId) {
        return roleMapper.selectRoleById(roleId);
    }

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色信息
     * @return 结果
     * @author cn 2024/07/03
     */
    @Override
    public String checkRoleNameUnique(SysRole role) {
        // roleId如果为空赋值-1
        Long roleId = StringUtils.isNull(role.getRoleId()) ? -1L : role.getRoleId();
        // 查询是否有重复角色名
        SysRole info = roleMapper.checkRoleNameUnique(role.getRoleName());
        // 存在重名角色，返回1
        if (StringUtils.isNotNull(info) && info.getRoleId().longValue() != roleId.longValue()) {
            return NumberConstants.ONE_STRING;
        }
        // 不存在重名，返回0
        return NumberConstants.ZERO_STRING;
    }

    /**
     * 新增保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     * @author cn 2024/07/03
     */
    @Override
    public int insertRole(SysRole role) {
        // 新增角色信息
        role.setRoleId(IdWorker.getId().nextId());
        // 新增的部门排序固定，用创建时间排序
        role.setRoleSort(NumberConstants.FOUR);
        roleMapper.insertRole(role);
        return insertRoleMenu(role);
    }

    /**
     * 校验角色是否允许操作
     *
     * @param role 角色信息
     * @author cn 2024/07/03
     */
    @Override
    public void checkRoleAllowed(SysRole role) {
        if (StringUtils.isNotNull(role.getRoleId()) && role.isAdmin()) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_ROLE_NOT_OPERATE_MANAGER));
        }
    }

    /**
     * 修改保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     * @author cn 2024/07/03
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRole(SysRole role) {
        if (NumberConstants.ONE_STRING.equals(role.getStatus()) && countUserRoleByRoleId(role.getRoleId()) > 0) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_ROLE_NOT_DISABLE));
        }
        String oldDataScope = roleMapper.getRoleDataScope(role.getRoleId());
        List<Long> oldMenuList = roleMenuMapper.getRoleMenuIds(role.getRoleId());
        List<Long> newMenuList = Arrays.asList(role.getMenuIds());
        // 修改角色信息
        roleMapper.updateRole(role);
        // 删除角色与菜单关联
        roleMenuMapper.deleteRoleMenuByRoleId(role.getRoleId());
        // 创建新的角色菜单关系
        int num = insertRoleMenu(role);
        // 若角色权限被修改，则进行强制登出
        Collections.sort(oldMenuList);
        Collections.sort(newMenuList);
        if (!oldDataScope.equals(role.getDataScope()) || !oldMenuList.equals(newMenuList)) {
            logOutRole(role.getRoleId());
        }
        return num;
    }

    /**
     * 修改角色状态
     *
     * @param role 角色信息
     * @return 结果
     * @author cn 2024/07/03
     */
    @Override
    public int updateRoleStatus(SysRole role) {
        if (NumberConstants.ONE_STRING.equals(role.getStatus()) && countUserRoleByRoleId(role.getRoleId()) > 0) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_ROLE_NOT_DISABLE));
        }
        return roleMapper.updateRole(role);
    }

    /**
     * 批量删除角色信息
     *
     * @param roleIds 需要删除的角色ID
     * @return 结果
     * @author cn 2024/07/03
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRoleByIds(Long[] roleIds) {
        for (Long roleId : roleIds) {
            // 管理员不许删除
            checkRoleAllowed(new SysRole(roleId));
            // 没有操作权限不许删除
            checkRoleDataScope(roleId);
            // 已使用角色不许删除
            SysRole role = selectRoleById(roleId);
            if (countUserRoleByRoleId(roleId) > 0) {
                throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_ROLE_NOT_DELETE, role.getRoleName()));
            }
        }
        // 删除角色与菜单关联
        roleMenuMapper.deleteRoleMenu(roleIds);
        return roleMapper.deleteRoleByIds(roleIds);
    }

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     * @author cn 2024/07/03
     */
    @Override
    public Set<String> selectRolePermissionByUserId(Long userId) {
        // 根据用户ID查询该用户所有的角色权限
        List<SysRole> perms = roleMapper.selectRolePermissionByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (SysRole perm : perms) {
            if (StringUtils.isNotNull(perm)) {
                permsSet.addAll(Arrays.asList(perm.getRoleName().trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 查询所有角色
     *
     * @return 角色列表
     * @author cn 2024/07/03
     */
    @Override
    public List<SysRole> selectRoleAll() {
        return SpringUtils.getAopProxy(this).selectRoleList(new SysRole());
    }

    /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId 角色ID
     * @return 结果
     * @author cn 2024/07/03
     */
    private int countUserRoleByRoleId(Long roleId) {
        return userRoleMapper.countUserRoleByRoleId(roleId);
    }

    /**
     * 新增角色菜单信息
     *
     * @param role 角色对象
     * @author cn 2024/07/03
     */
    private int insertRoleMenu(SysRole role) {
        int rows = 1;
        // 新增角色菜单关系
        List<SysRoleMenu> list = new ArrayList<SysRoleMenu>();
        for (Long menuId : role.getMenuIds()) {
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(role.getRoleId());
            rm.setMenuId(menuId);
            list.add(rm);
        }
        if (list.size() > 0) {
            rows = roleMenuMapper.batchRoleMenu(list);
        }
        return rows;
    }

    /**
     * 角色权限被修改，则进行强制登出
     *
     * @param roleId
     * @author cn 2024/07/03
     */
    private void logOutRole(Long roleId) {
        // 根据角色查询所有该角色相关用户
        List<String> userNameList = userRoleMapper.getUserNameByRoleId(roleId);
        // 查询所有在线用户
        List<SysUserOnline> allOnlineUser = sysUserService.getAllOnlineUser();
        for (SysUserOnline userOnline : allOnlineUser) {
            if (userNameList.contains(userOnline.getUserName())) {
                // 删除用户登录token缓存，使用户强制登出
                cacheService.deleteObject(TokenConstants.LOGIN_TOKEN_KEY + userOnline.getTokenId());
            }
        }
    }
}
