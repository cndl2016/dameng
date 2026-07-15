package com.dm.cn.system.service.impl;

import cn.hutool.core.codec.Base64;
import com.dm.cn.common.config.InfoConfig;
import com.dm.cn.common.config.manager.AsyncManager;
import com.dm.cn.common.core.constant.Constants;
import com.dm.cn.common.core.constant.NumberConstants;
import com.dm.cn.common.core.constant.SymbolConstants;
import com.dm.cn.common.core.constant.TokenConstants;
import com.dm.cn.common.core.domain.LoginUser;
import com.dm.cn.common.core.domain.SysUser;
import com.dm.cn.common.core.exception.ServiceException;
import com.dm.cn.common.core.utils.StringUtils;
import com.dm.cn.common.core.utils.poi.ExcelUtil;
import com.dm.cn.common.core.web.domain.AjaxResult;
import com.dm.cn.common.utils.I18nMessageUtil;
import com.dm.cn.common.utils.ip.IpUtils;
import com.dm.cn.system.config.security.AsyncFactory;
import com.dm.cn.system.config.security.service.CacheService;
import com.dm.cn.system.config.security.utils.AuthenticationContextHolder;
import com.dm.cn.system.constant.MessageTipConstant;
import com.dm.cn.system.service.ISysConfigService;
import com.dm.cn.system.service.ISysUserService;
import com.google.code.kaptcha.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.FastByteArrayOutputStream;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

/**
 * 登录校验方法
 *
 * @author dameng
 */
@Component
public class SysLoginService {

    private static final Logger log = LoggerFactory.getLogger(ExcelUtil.class);

    @Resource
    private CacheService cacheService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private ISysConfigService configService;

    @Resource
    private ISysUserService userService;

    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    /**
     * 登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @param language 语言
     * @return 结果
     */
    public String getToken(String username, String password, String language) {
        //默认中文
        String newLanguage = Constants.ZH;
        if (Constants.EN_US.equals(language)) {
            newLanguage = Constants.EN;
        }
        LoginUser loginUser = getLoginUser(username, password, newLanguage);
        // 生成token
        return cacheService.createToken(loginUser);
    }

    /**
     * 登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @param code     验证码
     * @param uuid     唯一标识
     * @param language 语言
     * @return 结果
     */
    public String login(String username, String password, String code, String uuid, String language) {
        //默认中文
        String newLanguage = Constants.ZH;
        if (Constants.EN_US.equals(language)) {
            newLanguage = Constants.EN;
        }
        Locale locale = new Locale(newLanguage, "");
        // 验证码校验
        validateCaptcha(username, code, uuid, locale);
        LoginUser loginUser = getLoginUser(username, password, newLanguage);
        loginUser.setLanguage(newLanguage);
        this.updateUser(loginUser.getUserId(), newLanguage);

        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, TokenConstants.LOGIN_SUCCESS, I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_LOGIC_SUCCESS, locale)));
        // 生成token
        return cacheService.createToken(loginUser);
    }

    /**
     * 生成验证码
     */
    public AjaxResult createCaptcha() {
        AjaxResult ajax = AjaxResult.success();
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        ajax.put("captchaEnabled", captchaEnabled);
        if (!captchaEnabled) {
            return ajax;
        }

        // 保存验证码信息
        String uuid = UUID.randomUUID().toString();
        String verifyKey = TokenConstants.CAPTCHA_CODE_KEY + uuid;
        String capStr, code = null;
        BufferedImage image = null;

        // 生成验证码
        String captchaType = InfoConfig.getCaptchaType();
        if (SymbolConstants.MATH.equals(captchaType)) {
            String capText = captchaProducerMath.createText();
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            code = capText.substring(capText.lastIndexOf("@") + 1);
            image = captchaProducerMath.createImage(capStr);
        } else if (SymbolConstants.CHAR.equals(captchaType)) {
            capStr = code = captchaProducer.createText();
            image = captchaProducer.createImage(capStr);
        }
        cacheService.createCaptcha(verifyKey, code);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            log.error("createCaptcha exception: {}", e);
            return AjaxResult.error(e.getMessage());
        }

        ajax.put("uuid", uuid);
        ajax.put("img", Base64.encode(os.toByteArray()));
        return ajax;
    }

    /**
     * 获取登录用户
     *
     * @param username
     * @param password
     * @param newLanguage 语言
     * @return {@link LoginUser}
     */
    private LoginUser getLoginUser(String username, String password, String newLanguage) {
        Locale locale = new Locale(newLanguage, "");
        // 登录前置校验
        loginPreCheck(username, password, locale);
        // 用户验证
        Authentication authentication;
        try {
            // 封装用户名 传递语言过去
            String usernameAndLanguage = newLanguage + " " + username;
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(usernameAndLanguage, password);
            AuthenticationContextHolder.setContext(authenticationToken);
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            log.error("getLoginUser exception: {}", e);
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, TokenConstants.LOGIN_FAIL, I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_PASSWORD_NOT_MATCH, locale)));
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_PASSWORD_NOT_MATCH, locale));
        } finally {
            AuthenticationContextHolder.clearContext();
        }
        return (LoginUser) authentication.getPrincipal();
    }

    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code     验证码
     * @param uuid     唯一标识
     * @param locale   语言
     * @return 结果
     */
    private void validateCaptcha(String username, String code, String uuid, Locale locale) {
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled) {
            String verifyKey = TokenConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
            String captcha = cacheService.validateCaptcha(verifyKey);
            if (captcha == null) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, TokenConstants.LOGIN_FAIL, I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_JCAPTCHA_EXPIRE, locale)));
                throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_JCAPTCHA_EXPIRE, locale));
            }
            if (!code.equalsIgnoreCase(captcha)) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, TokenConstants.LOGIN_FAIL, I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_JCAPTCHA_ERROR, locale)));
                throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_JCAPTCHA_ERROR, locale));
            }
        }
    }

    /**
     * 登录前置校验
     *
     * @param username 用户名
     * @param password 用户密码
     * @param locale   语言
     */
    private void loginPreCheck(String username, String password, Locale locale) {
        // 用户名或密码为空 错误
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, TokenConstants.LOGIN_FAIL, I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_NOT_NULL, locale)));
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_NOT_NULL, locale));
        }
        // 密码如果不在指定范围内 错误
        if (password.length() < NumberConstants.FIVE
                || password.length() > NumberConstants.TWENTY) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, TokenConstants.LOGIN_FAIL, I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_PASSWORD_NOT_MATCH, locale)));
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_PASSWORD_NOT_MATCH, locale));
        }
        // 用户名不在指定范围内 错误
        if (username.length() < NumberConstants.TWO
                || username.length() > NumberConstants.TWENTY) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, TokenConstants.LOGIN_FAIL, I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_PASSWORD_NOT_MATCH, locale)));
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_PASSWORD_NOT_MATCH, locale));
        }
        // IP黑名单校验
        String blackStr = configService.selectConfigByKey("sys.login.blackIPList");
        if (IpUtils.isMatchedIp(blackStr, IpUtils.getIpAddr())) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, TokenConstants.LOGIN_FAIL, I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_LOGIN_BLOCKED, locale)));
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_LOGIN_BLOCKED, locale));
        }
    }

    /**
     * 修改用户语种
     *
     * @param userId
     * @param language
     */
    private void updateUser(Long userId, String language) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setLanguage(language);
        userService.updateLanguage(user);
    }

}
