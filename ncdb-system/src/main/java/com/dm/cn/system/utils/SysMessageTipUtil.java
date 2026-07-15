package com.dm.cn.system.utils;


import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.dm.cn.common.core.constant.Constants;
import com.dm.cn.common.core.exception.ServiceException;
import com.dm.cn.system.service.ISysMessageTipService;
import org.springframework.context.i18n.LocaleContextHolder;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

/**
 * 系统信息语言标识工具类
 * @author dyy
 * @date 2025-04-16
 */
public class SysMessageTipUtil {

    private static ISysMessageTipService sysMessageTipService;

    public static void init(ISysMessageTipService sysMessageTipService) {
        SysMessageTipUtil.sysMessageTipService = sysMessageTipService;
    }

    public static ISysMessageTipService getSysMessageTipService() {
        if(sysMessageTipService == null){
            throw new ServiceException("国际化功能未初始化");
        }
        return sysMessageTipService;
    }

    public static String getMessageTip(String tableName,String dataId,String code,String defaultValue){
        String messageTip = getSysMessageTipService().getMessageTip(LocaleContextHolder.getLocale().getLanguage(),tableName,dataId,code);
        if(messageTip == null){
            return defaultValue;
        }
        return messageTip;
    }

    public static <T> String getMessageTip(String tableName,String dataId,SFunction<T,?> function,String defaultValue){
        try {
            String code = getName(function);
            return getMessageTip(tableName, dataId, code, defaultValue);
        } catch (Exception e) {
            throw new ServiceException("获取信息失败");
        }
    }

    /**
     * 获取属性名
     * @param function 函数
     * @return 属性名
     */
    public static <T> String getName(SFunction<T,?> function) throws Exception{
        Method method = function.getClass().getDeclaredMethod("writeReplace");
        method.setAccessible(true);
        SerializedLambda serializedLambda = (SerializedLambda) method.invoke(function);
        String methodName = serializedLambda.getImplMethodName();
        return extract(methodName);
    }

    /**
     * 提取属性名
     * @param str 方法名
     * @return 属性名
     */
    private static String extract(String str){
        if(str.startsWith(Constants.IS_PREFIX)){
            return str.substring(2,3).toLowerCase()+str.substring(3);
        }else if(str.startsWith(Constants.GET_PREFIX)){
            return str.substring(3,4).toLowerCase()+str.substring(4);
        }
        throw new ServiceException("方法名格式错误");
    }
}
