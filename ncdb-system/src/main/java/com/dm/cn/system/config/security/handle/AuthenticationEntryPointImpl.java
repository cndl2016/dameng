package com.dm.cn.system.config.security.handle;

import com.alibaba.fastjson.JSON;
import com.dm.cn.common.core.constant.NumberConstants;
import com.dm.cn.common.core.utils.ServletUtils;
import com.dm.cn.common.core.web.domain.AjaxResult;
import com.dm.cn.common.utils.I18nMessageUtil;
import com.dm.cn.system.constant.MessageTipConstant;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * 认证失败处理类 返回未授权
 *
 * @author dameng.cn
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint, Serializable {
    private static final long serialVersionUID = -8970718410437077606L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) {
        int code = NumberConstants.UNAUTHORIZED;
        String msg = I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_HTTP_REQUEST_FAIL, request.getRequestURI());
        ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.error(code, msg)));
    }
}
