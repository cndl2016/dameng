package com.dm.cn.common.core.utils;

import com.dm.cn.common.core.text.Convert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 客户端工具类
 *
 * @author dameng.cn
 */
public class ServletUtils {

    private static final Logger log = LoggerFactory.getLogger(ServletUtils.class);

    /**
     * 获取String参数
     */
    public static String getParameter(String name) {
        HttpServletRequest request = getRequest();
        if(request != null){
            return request.getParameter(name);
        }
        return "";
    }

    /**
     * 获取Boolean参数
     */
    public static Boolean getParameterToBool(String name) {
        HttpServletRequest request = getRequest();
        if(request != null){
            return Convert.toBool(request.getParameter(name));
        }
        return null;
    }

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest() {
        try {
            ServletRequestAttributes attributes = getRequestAttributes();
            if(attributes != null) {
                return attributes.getRequest();
            }
        } catch (Exception e) {
            log.error("HttpServletRequest exception: {}", e);
            return null;
        }
        return null;
    }

    /**
     * 获取response
     */
    public static HttpServletResponse getResponse() {
        try {
            ServletRequestAttributes attributes = getRequestAttributes();
            if(attributes != null) {
                return attributes.getResponse();
            }
        } catch (Exception e) {
            log.error("HttpServletResponse exception: {}", e);
            return null;
        }
        return null;
    }


    public static ServletRequestAttributes getRequestAttributes() {
        try {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            return (ServletRequestAttributes) attributes;
        } catch (Exception e) {
            log.error("ServletRequestAttributes exception: {}", e);
            return null;
        }
    }

    /**
     * 将字符串渲染到客户端
     *
     * @param response 渲染对象
     * @param string   待渲染的字符串
     */
    public static void renderString(HttpServletResponse response, String string) {
        try {
            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(string);
        } catch (IOException e) {
            log.error("renderString exception: {}", e);
        }
    }

    /**
     * 获得所有请求参数
     *
     * @param request 请求对象{@link ServletRequest}
     * @return Map
     */
    public static Map<String, String> getParamMap(ServletRequest request) {
        Set<Map.Entry<String, String[]>> entries = getParams(request).entrySet();
        Map<String, String> params = new HashMap<>(entries.size());

        for (Map.Entry<String, String[]> entry : entries) {
            params.put(entry.getKey(), StringUtils.join(entry.getValue(), ","));
        }
        return params;
    }

    /**
     * 获得所有请求参数
     *
     * @param request 请求对象{@link ServletRequest}
     * @return Map
     */
    public static Map<String, String[]> getParams(ServletRequest request) {
        final Map<String, String[]> map = request.getParameterMap();
        return Collections.unmodifiableMap(map);
    }

}
