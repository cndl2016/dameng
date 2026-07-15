package com.dm.cn.common.security.utils;

import com.dm.cn.common.core.constant.TokenConstants;
import com.dm.cn.common.core.domain.LoginUser;
import com.dm.cn.common.core.domain.SysRole;
import com.dm.cn.common.core.domain.SysUser;
import com.dm.cn.common.core.utils.ServletUtils;
import com.dm.cn.common.core.utils.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 安全服务工具类
 *
 * @author dameng
 */
public class SecurityUtils {

    private static final String OS_NAME = "os.name";

    private static final String WINDOWS = "windows";

    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        return SecurityContextHolder.getUserId();
    }

    /**
     * 获取用户名称
     */
    public static String getUsername() {
        return SecurityContextHolder.getUserName();
    }

    /**
     * 获取登录用户信息
     */
    public static LoginUser getLoginUser() {
        return SecurityContextHolder.get(TokenConstants.LOGIN_USER, LoginUser.class);
    }

    /**
     * 获取请求token
     */
    public static String getToken() {
        return getToken(ServletUtils.getRequest());
    }

    /**
     * 根据request获取请求token
     */
    public static String getToken(HttpServletRequest request) {
        // 从header获取token标识
        String token = request.getHeader(TokenConstants.AUTHENTICATION);
        return replaceTokenPrefix(token);
    }

    /**
     * 裁剪token前缀
     */
    public static String replaceTokenPrefix(String token) {
        // 如果前端设置了令牌前缀，则裁剪掉前缀
        if (StringUtils.isNotEmpty(token) && token.startsWith(TokenConstants.TOKEN_PREFIX)) {
            token = token.replaceFirst(TokenConstants.TOKEN_PREFIX, "");
        }
        return token;
    }

    /**
     * 获取用户最大权限
     *
     * @return 结果
     */
    public static String getUserPermission() {
        SysUser user = getLoginUser().getSysUser();
        List<String> permissionList = new ArrayList<>();
        // 获取用户所拥有的角色的最大数据权限
        for (SysRole role : user.getRoles()) {
            permissionList.add(role.getDataScope());
        }
        Collections.sort(permissionList);
        String maxPermission = permissionList.get(0);
        return maxPermission;
    }

    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 判断密码是否相同
     *
     * @param rawPassword     真实密码
     * @param encodedPassword 加密后字符
     * @return 结果
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public static boolean isWin() {
        if (System.getProperty(OS_NAME).toLowerCase().startsWith(WINDOWS)) {
            return true;
        }
        return false;
    }
}
