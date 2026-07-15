package com.dm.cn.system.config.security.utils;

import com.dm.cn.common.core.constant.Constants;
import com.dm.cn.common.core.domain.SysDictData;
import com.dm.cn.common.core.utils.SpringUtils;
import com.dm.cn.common.core.utils.StringUtils;
import com.dm.cn.system.config.security.service.CacheService;

import java.util.ArrayList;
import java.util.List;

/**
 * 字典工具类
 *
 * @author dameng
 */
public class DictUtils {
    /**
     * 设置字典缓存
     *
     * @param key       参数键
     * @param dictDatas 字典数据列表
     */
    public static void setDictCache(String key, List<SysDictData> dictDatas) {
        SpringUtils.getBean(CacheService.class).setCacheObject(getCacheKey(key), dictDatas);
    }

    /**
     * 获取字典缓存
     *
     * @param key 参数键
     * @return dictDatas 字典数据列表
     */
    public static List<SysDictData> getDictCache(String key) {
        ArrayList arrayCache = SpringUtils.getBean(CacheService.class).getCacheObject(getCacheKey(key));
        if (StringUtils.isNotNull(arrayCache)) {
            return arrayCache;
        }
        return null;
    }

    /**
     * 删除指定字典缓存
     *
     * @param key 字典键
     */
    public static void removeDictCache(String key) {
        SpringUtils.getBean(CacheService.class).deleteObject(getCacheKey(key));
    }

    /**
     * 清空字典缓存
     */
    public static void clearDictCache() {
        SpringUtils.getBean(CacheService.class).removeDictCache(Constants.SYS_DICT_KEY);
    }

    /**
     * 设置cache key
     *
     * @param configKey 参数键
     * @return 缓存键key
     */
    public static String getCacheKey(String configKey) {
        return Constants.SYS_DICT_KEY + configKey;
    }
}
