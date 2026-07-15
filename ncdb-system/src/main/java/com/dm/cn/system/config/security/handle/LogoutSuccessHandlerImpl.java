package com.dm.cn.system.config.security.handle;

import com.alibaba.fastjson.JSON;
import com.dm.cn.common.core.constant.NumberConstants;
import com.dm.cn.common.core.domain.LoginUser;
import com.dm.cn.common.core.domain.SysUser;
import com.dm.cn.common.core.utils.ServletUtils;
import com.dm.cn.common.core.utils.SpringUtils;
import com.dm.cn.common.core.utils.StringUtils;
import com.dm.cn.common.core.web.domain.AjaxResult;
import com.dm.cn.common.utils.I18nMessageUtil;
import com.dm.cn.system.config.security.service.CacheService;
import com.dm.cn.system.constant.MessageTipConstant;
import com.dm.cn.system.entity.server.SysLogininfor;
import com.dm.cn.system.service.ISysLoginInforService;
import com.dm.cn.system.service.ISysUserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

import static com.dm.cn.common.core.constant.Constants.USER_NAME;

/**
 * 自定义退出处理类 返回成功
 *
 * @author dameng.cn
 */
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {
    @Resource
    private CacheService cacheService;

    @Resource
    private ISysUserService userService;

    /**
     * 退出处理
     *
     * @return
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Cookie[] cookies = request.getCookies();
        String username = null;
        for (Cookie val : cookies) {
            if (StringUtils.equals(val.getName(), USER_NAME)) {
                username = val.getValue();
            }
        }
        if (StringUtils.isNotEmpty(username)) {
            SysLogininfor lastLoginUser = SpringUtils.getBean(ISysLoginInforService.class).getLastLoginUser(username);
            SysUser user = userService.selectUserByUserName(username);
            if(!ObjectUtils.isEmpty(lastLoginUser)){
                if (StringUtils.isNotEmpty(lastLoginUser.getUserName())) {
                    SysLogininfor loginInfo = new SysLogininfor();
                    loginInfo.setUserName(lastLoginUser.getUserName());
                    loginInfo.setIpaddr(lastLoginUser.getIpaddr());
                    loginInfo.setLoginLocation(lastLoginUser.getLoginLocation());
                    loginInfo.setBrowser(lastLoginUser.getBrowser());
                    loginInfo.setOs(lastLoginUser.getOs());
                    Locale locale = new Locale(user.getLanguage(), "");
                    loginInfo.setMsg(I18nMessageUtil.getMessage(MessageTipConstant.LOGOUT_SUCCESS, locale));
                    loginInfo.setStatus(NumberConstants.ZERO_STRING);
                    SpringUtils.getBean(ISysLoginInforService.class).insertLoginInfor(loginInfo);
                }
            }
        }
        LoginUser loginUser = cacheService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser)) {
            // 删除用户缓存记录
            cacheService.delLoginUser(loginUser.getToken());
        }
        ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.success("退出成功")));
    }
}
