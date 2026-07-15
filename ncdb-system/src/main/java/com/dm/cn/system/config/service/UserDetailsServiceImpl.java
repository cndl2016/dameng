package com.dm.cn.system.config.service;

import com.dm.cn.common.core.domain.LoginUser;
import com.dm.cn.common.core.domain.SysUser;
import com.dm.cn.common.core.enums.UserStatus;
import com.dm.cn.common.core.exception.ServiceException;
import com.dm.cn.common.core.utils.StringUtils;
import com.dm.cn.common.utils.I18nMessageUtil;
import com.dm.cn.system.constant.MessageTipConstant;
import com.dm.cn.system.service.ISysPermissionService;
import com.dm.cn.system.service.ISysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Locale;

/**
 * 用户验证处理
 *
 * @author dameng.cn
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Resource
    private ISysUserService userService;

    @Resource
    private SysPasswordService passwordService;

    @Resource
    private ISysPermissionService permissionService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String[] array = username.split(" ", 2);
        String lang = array[0];
        username = array[1];
        SysUser user = userService.selectUserByUserName(username);
        Locale locale = new Locale(lang, "");
        if (StringUtils.isNull(user)) {
            log.info("登录用户：{} 不存在.", username);
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_ACCOUNT_NOT_EXISTS, locale, username));
        } else if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) {
            log.info("登录用户：{} 已被删除.", username);
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_ACCOUNT_STATUS_DELETE, locale, username));
        } else if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用.", username);
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_ACCOUNT_STATUS_DISABLE, locale, username));
        }
        passwordService.validate(user, locale);
        return new LoginUser(user.getUserId(), user.getDeptId(), user, permissionService.getMenuPermission(user.getUserId()));
    }

}