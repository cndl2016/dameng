package com.dm.cn.system.config.security.utils;

import org.springframework.security.core.Authentication;

/**
 * 身份验证信息
 *
 * @author dameng.cn
 */
public class AuthenticationContextHolder
{
    private static final ThreadLocal<Authentication> CONTEXT_HOLDER = new ThreadLocal<>();

    public static Authentication getContext()
    {
        return CONTEXT_HOLDER.get();
    }

    public static void setContext(Authentication context)
    {
        CONTEXT_HOLDER.set(context);
    }

    public static void clearContext()
    {
        CONTEXT_HOLDER.remove();
    }
}
