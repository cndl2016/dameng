package com.dm.cn.system.config.security.utils;

import com.alibaba.fastjson.JSON;
import com.dm.cn.common.annotation.RepeatSubmit;
import com.dm.cn.common.core.constant.TokenConstants;
import com.dm.cn.common.core.domain.LoginUser;
import com.dm.cn.common.core.utils.ServletUtils;
import com.dm.cn.common.core.utils.StringUtils;
import com.dm.cn.common.core.web.domain.AjaxResult;
import com.dm.cn.common.security.utils.SecurityContextHolder;
import com.dm.cn.common.security.utils.SecurityUtils;
import com.dm.cn.system.config.security.auth.AuthUtil;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 自定义请求头拦截器，将Header数据封装到线程变量中方便获取
 * 注意：此拦截器会同时验证当前用户有效期自动刷新有效期
 *
 * @author dameng
 */
public abstract class AbstractHeaderInterceptor implements AsyncHandlerInterceptor, HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        RepeatSubmit annotation = method.getAnnotation(RepeatSubmit.class);
        if (annotation != null) {
            if (this.isRepeatSubmit(request, annotation)) {
                AjaxResult ajaxResult = AjaxResult.error(annotation.message());
                ServletUtils.renderString(response, JSON.toJSONString(ajaxResult));
                return false;
            }
        }
        String token = SecurityUtils.getToken();
        if (StringUtils.isNotEmpty(token)) {
            LoginUser loginUser = AuthUtil.getLoginUser(token);
            if (StringUtils.isNotNull(loginUser)) {
                AuthUtil.verifyLoginUserExpire(loginUser);
                SecurityContextHolder.set(TokenConstants.LOGIN_USER, loginUser);
                SecurityContextHolder.setUserId(String.valueOf(loginUser.getUserId()));
                SecurityContextHolder.setUserName(loginUser.getUsername());
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        SecurityContextHolder.remove();
    }

    /**
     * 验证是否重复提交由子类实现具体的防重复提交的规则
     *
     * @param request
     * @param annotation
     * @return boolean
     */
    public abstract boolean isRepeatSubmit(HttpServletRequest request, RepeatSubmit annotation);
}
