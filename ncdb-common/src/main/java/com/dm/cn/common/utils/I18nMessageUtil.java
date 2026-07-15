package com.dm.cn.common.utils;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dm.cn.common.core.constant.Constants;
import com.dm.cn.common.core.domain.LoginUser;
import com.dm.cn.common.security.utils.SecurityUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * 国际化消息工具类
 * @author zrh
 * @date 2022-09-06
 */
public class I18nMessageUtil {

    /**
     * 根据消息键和参数 获取消息 委托给spring messageSource
     *
     * @param key  消息键
     * @param args 参数
     * @return 获取国际化翻译值
     */
    public static String getMessage(String key, Object... args) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String language = Constants.ZH;
        if (loginUser != null && StringUtils.isNotEmpty(loginUser.getLanguage())) {
            language = loginUser.getLanguage();
        }
        Locale locale = new Locale(language, "");
        MessageSource messageSource = SpringUtil.getBean(MessageSource.class);
        return messageSource.getMessage(key, args, locale);
    }

    /**
     * 根据消息键和参数 获取中文消息 委托给spring messageSource
     * @param key 消息键
     * @param args 参数
     * @return 获取国际化翻译值
     */
    public static String getMessageZh(String key, Object... args) {
        String language = Constants.ZH;
        Locale locale = new Locale(language, "");
        MessageSource messageSource = SpringUtil.getBean(MessageSource.class);
        return messageSource.getMessage(key, args, locale);
    }

    /**
     * 根据消息键和参数 获取英文消息 委托给spring messageSource
     * @param key 消息键
     * @param args 参数
     * @return 获取国际化翻译值
     */
    public static String getMessageEn(String key, Object... args) {
        String language = Constants.EN;
        Locale locale = new Locale(language, "");
        MessageSource messageSource = SpringUtil.getBean(MessageSource.class);
        return messageSource.getMessage(key, args, locale);
    }

    /**
     * 根据消息键和参数 获取消息 委托给spring messageSource
     * @param key 消息键
     * @param locale 指定国家语言Locale
     * @param args 参数
     * @return 获取国际化翻译值
     */
    public static String getMessage(String key, Locale locale, Object... args) {
        MessageSource messageSource = SpringUtil.getBean(MessageSource.class);
        if(locale == null){
            return getMessage(key, args);
        }else {
            return messageSource.getMessage(key, args, locale);
        }
    }

    /**
     * 根据消息键和参数 获取消息 委托给spring messageSource
     * @param key 消息键
     * @param defaultMessage 未配置国际化词条时，返回指定的默认值
     * @param args 参数
     * @return 获取国际化翻译值
     */
    public static String getMessageNullDefault(String key, String defaultMessage, Object... args) {
        String message = null;
        try{
            message = getMessage(key, args);
        }catch (Exception e){
            //ignore
        }
        if(StringUtils.isBlank(message)){
            MessageSource messageSource = SpringUtil.getBean(MessageSource.class);
            message = messageSource.getMessage(key, args, defaultMessage, LocaleContextHolder.getLocale());;
        }
        return message;
    }

    /**
     * 根据消息键和参数 获取消息 委托给spring messageSource
     * @param key 消息键
     * @param defaultMessage 未配置国际化词条时，返回指定的默认值
     * @param locale 指定国家语言Locale
     * @param args 参数
     * @return 获取国际化翻译值
     */
    public static String getMessageNullDefault(String key, String defaultMessage, Locale locale, Object... args) {
        String message = null;
        try{
            message = getMessage(key, locale, args);
        }catch (Exception e){
            //ignore
        }
        if(StringUtils.isBlank(message)){
            MessageSource messageSource = SpringUtil.getBean(MessageSource.class);
            message = messageSource.getMessage(key, args, defaultMessage, locale);
        }
        return message;
    }

}
