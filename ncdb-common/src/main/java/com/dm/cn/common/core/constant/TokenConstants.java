package com.dm.cn.common.core.constant;

/**
 * Token的Key常量
 *
 * @author dameng
 */
public class TokenConstants {
    /**
     * 令牌自定义标识
     */
    public static final String AUTHENTICATION = "Authorization";

    public static final String TOKEN = "token";

    /**
     * 令牌
     */
    public static final String ACCESS_TOKEN = "access_token";

    /**
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 权限缓存前缀
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * CAS登录成功后的后台标识
     */
    public static final String CAS_TOKEN = "cas_token";

    /**
     * CAS登录成功后的前台Cookie的Key
     */
    public static final String WEB_TOKEN_KEY = "Admin-Token";

     /**
     * 用户ID字段
     */
    public static final String DETAILS_USER_ID = "user_id";

    /**
     * 用户名字段
     */
    public static final String DETAILS_USERNAME = "username";

    /**
     * 用户标识
     */
    public static final String USER_KEY = "user_key";

    /**
     * 登录用户
     */
    public static final String LOGIN_USER = "login_user";

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "Success";

    /**
     * 注销
     */
    public static final String LOGOUT = "Logout";

    /**
     * 注册
     */
    public static final String REGISTER = "Register";

    /**
     * 登录失败
     */
    public static final String LOGIN_FAIL = "Error";

    /**
     * 资源映射路径 前缀
     */
    public static final String RESOURCE_PREFIX = "/profile";

    /**
     * 验证码 key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 防重提交 key
     */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    /**
     * 登录账户密码错误次数 key
     */
    public static final String PWD_ERR_CNT_KEY = "pwd_err_cnt:";

    /**
     * 请求来源
     */
    public static final String FROM_SOURCE = "from-source";

    /**
     * 内部请求
     */
    public static final String INNER = "inner";

    /**
     * 设备列表缓存
     */
    public static final String DEVICE_LIST_KEY = "device-list";
}
