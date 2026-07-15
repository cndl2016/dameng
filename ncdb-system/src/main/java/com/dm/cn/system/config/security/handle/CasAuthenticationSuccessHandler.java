package com.dm.cn.system.config.security.handle;

/**
 * @author DAMENG
 */

import com.dm.cn.common.config.properties.CasProperties;
import com.dm.cn.common.core.constant.TokenConstants;
import com.dm.cn.common.core.domain.LoginUser;
import com.dm.cn.system.config.security.service.CacheService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


/**
 * 单点登录
 *
 * @author DAMENG
 * @date 2024/06/21
 */
@Service
public class CasAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();

    @Resource
    private CacheService cacheService;

    @Resource
    private CasProperties casProperties;

    /**
     * 令牌有效期（默认30分钟）
     */
    @Value("${token.expireTime}")
    private int expireTime;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        String targetUrlParameter = getTargetUrlParameter();
        boolean isTrue = isAlwaysUseDefaultTargetUrl()
                || (targetUrlParameter != null && StringUtils.hasText(request.getParameter(targetUrlParameter)));
        if (isTrue) {
            requestCache.removeRequest(request, response);
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }
        clearAuthenticationAttributes(request);
        LoginUser userDetails = (LoginUser) authentication.getPrincipal();
        String token = cacheService.createToken(userDetails);
        //往Cookie中设置token
        Cookie casCookie = new Cookie(TokenConstants.WEB_TOKEN_KEY, token);
        casCookie.setSecure(true);
        casCookie.setHttpOnly(true);
        casCookie.setMaxAge(expireTime * 60);
        response.addCookie(casCookie);
        //设置后端认证成功标识
        HttpSession httpSession = request.getSession();
        httpSession.setAttribute(TokenConstants.CAS_TOKEN, token);
        //登录成功后跳转到前端登录页面
        getRedirectStrategy().sendRedirect(request, response, casProperties.getWebUrl());
    }
}
