package com.dm.cn.system.config.security.service;

import com.dm.cn.common.cache.ExpiringCacheMap;
import com.dm.cn.common.core.constant.NumberConstants;
import com.dm.cn.common.core.constant.SymbolConstants;
import com.dm.cn.common.core.constant.TokenConstants;
import com.dm.cn.common.core.domain.LoginUser;
import com.dm.cn.common.core.utils.ServletUtils;
import com.dm.cn.common.core.utils.StringUtils;
import com.dm.cn.common.utils.ip.AddressUtils;
import com.dm.cn.common.utils.ip.IpUtils;
import eu.bitwalker.useragentutils.UserAgent;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.jodah.expiringmap.ExpiringMap;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * token验证处理
 *
 * @author dameng.cn
 */
@Component
public class CacheService {
    private static final Logger log = LoggerFactory.getLogger(CacheService.class);

    /**
     * 令牌自定义标识
     */
    @Value("${token.header}")
    private String header;

    /**
     * 令牌秘钥
     */
    @Value("${token.secret}")
    private String secret;

    /**
     * 令牌有效期（默认30分钟）
     */
    @Value("${token.expireTime}")
    private int expireTime;

    private static final long MILLIS_SECOND = 1000;

    private static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    @Resource
    private ExpiringCacheMap mapCache;

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getLoginUser(HttpServletRequest request) {
        // 获取请求携带的令牌
        String token = getToken(request);
        if (StringUtils.isNotEmpty(token)) {
            try {
                return (LoginUser) mapCache.getExpiringMap().get(getTokenKey((String) parseToken(token).get(TokenConstants.ACCESS_TOKEN)));
            } catch (Exception e) {
                log.error("getLoginUser exception: {}", e);
            }
        }
        return null;
    }

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getLoginUser(String token) {
        try {
            if (StringUtils.isNotEmpty(token)) {
                // 解析对应的权限以及用户信息
                return (LoginUser) mapCache.getExpiringMap().get(getTokenKey((String) parseToken(token).get(TokenConstants.ACCESS_TOKEN)));
            }
        } catch (Exception e) {
            log.error("getLoginUser exception: {}", e);
        }
        return null;
    }

    /**
     * 设置用户身份信息
     */
    public void setLoginUser(LoginUser loginUser) {
        if (StringUtils.isNotNull(loginUser) && StringUtils.isNotEmpty(loginUser.getToken())) {
            refreshToken(loginUser);
        }
    }

    /**
     * 删除用户身份信息
     */
    public void delLoginUser(String token) {
        if (StringUtils.isNotEmpty(token)) {
            String userKey = getTokenKey(token);
            mapCache.getExpiringMap().remove(userKey);
        }
    }

    /**
     * 创建令牌
     *
     * @param loginUser 用户信息
     * @return 令牌
     */
    public String createToken(LoginUser loginUser) {
        String token = UUID.randomUUID().toString();
        loginUser.setToken(token);
        setUserAgent(loginUser);
        refreshToken(loginUser);
        Map<String, Object> claims = new HashMap<>(1);
        claims.put(TokenConstants.ACCESS_TOKEN, token);
        return createToken(claims);
    }

    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     *
     * @param loginUser
     * @return 令牌
     */
    public void verifyToken(LoginUser loginUser) {
        long expireTokenTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (currentTime > expireTokenTime) {
            String userKey = getTokenKey(loginUser.getToken());
            mapCache.getExpiringMap().remove(userKey);
        } else {
            refreshToken(loginUser);
        }
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginUser loginUser) {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE);
        String userKey = getTokenKey(loginUser.getToken());
        mapCache.getExpiringMap().put(userKey, loginUser, expireTime, TimeUnit.MINUTES);
    }

    /**
     * 设置用户代理信息
     *
     * @param loginUser 登录信息
     */
    public void setUserAgent(LoginUser loginUser) {
        UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        String ip = IpUtils.getIpAddr();
        loginUser.setIpaddr(ip);
        loginUser.setLoginLocation(AddressUtils.getRealAddressByIp(ip));
        loginUser.setBrowser(userAgent.getBrowser().getName());
        loginUser.setOs(userAgent.getOperatingSystem().getName());
    }

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims) {
        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret).compact();
        return token;
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 获取请求token
     *
     * @param request
     * @return token
     */
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(header);
        if (StringUtils.isNotEmpty(token) && token.startsWith(TokenConstants.TOKEN_PREFIX)) {
            token = token.replace(TokenConstants.TOKEN_PREFIX, "");
        } else {
            Object authorization = request.getParameterMap().get(TokenConstants.TOKEN);
            if (authorization != null) {
                token = ((String[]) authorization)[0].split(SymbolConstants.QUESTION)[0];
            }
        }
        return token;
    }

    private String getTokenKey(String uuid) {
        return TokenConstants.LOGIN_TOKEN_KEY + uuid;
    }

    public void createCaptcha(String verifyKey, String code) {
        mapCache.getExpiringMap().put(verifyKey, code, NumberConstants.NUMBER_TWO_INTEGER, TimeUnit.MINUTES);
    }

    public String validateCaptcha(String verifyKey) {
        String captcha = (String) mapCache.getExpiringMap().get(verifyKey);
        mapCache.getExpiringMap().remove(verifyKey);
        return captcha;
    }

    public <T> void setCacheObject(String key, T value) {
        mapCache.getExpiringMap().put(key, value);
    }

    public <T> void setCacheObject(final String key, final T value, final long timeout, final TimeUnit timeUnit) {
        mapCache.getExpiringMap().put(key, value, timeout, timeUnit);
    }


    public <T> T getCacheObject(final String key) {
        return (T) mapCache.getExpiringMap().get(key);
    }

    /**
     * 删除多个对象
     */
    public boolean removeDictCache(String prefix) {
        ExpiringMap<Object, Object> map = mapCache.getExpiringMap();
        Iterator<Object> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            if (ObjectUtils.isNotEmpty(key) && key instanceof String && String.valueOf(key).startsWith(prefix)) {
                iterator.remove();
                map.remove(key);
            }
        }
        return true;
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public boolean deleteObject(final String key) {
        mapCache.getExpiringMap().remove(key);
        return true;
    }

    public void clearLoginRecordCache(String loginName) {
        if (mapCache.getExpiringMap().containsKey(getCacheKey(loginName))) {
            deleteObject(getCacheKey(loginName));
        }
    }

    /**
     * 判断 key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public Boolean hasKey(String key) {
        return mapCache.getExpiringMap().containsKey(key);
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Collection<String> keys(final String pattern) {
        return mapCache.getExpiringMap().keySet().stream().map(x -> x.toString())
                .filter(x -> x.startsWith(pattern))
                .collect(Collectors.toList());
    }


    /**
     * 登录账户密码错误次数缓存键名
     *
     * @param username 用户名
     * @return 缓存键key
     */
    private String getCacheKey(String username) {
        return TokenConstants.PWD_ERR_CNT_KEY + username;
    }
}
