package com.dm.cn.common.utils;

import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;

/**
 * ini配置文件工具类
 *
 * @author root
 */
public class IniConfUtils {

    /**
     * 修改配置文件中指定 Section 的键值
     *
     * @param confPath 配置文件路径
     * @param section  Section 名
     * @param key      键名
     * @param value    新值
     * @throws IOException 修改失败时抛出异常
     */
    public static void updateConf(String confPath, String section, String key, Object value) throws IOException {
        File confFile = new File(confPath);

        // 1. 加载配置
        Wini conf = new Wini();
        conf.load(confFile);

        // 2. 修改值
        conf.put(section, key, value);

        // 3. 保存回文件
        conf.store(confFile);
    }
}