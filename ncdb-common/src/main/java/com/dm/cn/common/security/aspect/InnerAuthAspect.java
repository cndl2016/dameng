package com.dm.cn.common.security.aspect;


import com.dm.cn.common.core.constant.TokenConstants;
import com.dm.cn.common.core.utils.ServletUtils;
import com.dm.cn.common.core.utils.StringUtils;
import com.dm.cn.common.exception.InnerAuthException;
import com.dm.cn.common.security.annotation.InnerAuth;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * 内部服务调用验证处理
 *
 * @author dameng
 */
@Aspect
@Component
public class InnerAuthAspect implements Ordered {

    @Around("@annotation(innerAuth)")
    public Object innerAround(ProceedingJoinPoint point, InnerAuth innerAuth) throws Throwable {
        String source = ServletUtils.getRequest().getHeader(TokenConstants.FROM_SOURCE);
        // 内部请求验证
        if (!StringUtils.equals(TokenConstants.INNER, source)) {
            throw new InnerAuthException("没有内部访问权限，不允许访问");
        }

        String userId = ServletUtils.getRequest().getHeader(TokenConstants.DETAILS_USER_ID);
        String userName = ServletUtils.getRequest().getHeader(TokenConstants.DETAILS_USERNAME);
        // 用户信息验证
        boolean isNotSet = innerAuth.isUser() && (StringUtils.isEmpty(userId) || StringUtils.isEmpty(userName));
        if (isNotSet) {
            throw new InnerAuthException("没有设置用户信息，不允许访问 ");
        }
        return point.proceed();
    }

    /**
     * 确保在权限认证aop执行前执行
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
