package com.dm.cn.system.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dm.cn.system.entity.server.SysMessageTip;
import com.dm.cn.system.mapper.SysMessageTipMapper;
import com.dm.cn.system.service.ISysMessageTipService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统信息语言标识表实现
 * @author dyy
 * @date 2025-04-16
 */
@Service
@Order(1)
public class SysMessageTipServiceImpl extends ServiceImpl<SysMessageTipMapper, SysMessageTip> implements IService<SysMessageTip>, ISysMessageTipService, CommandLineRunner {


    private final Map<String,String> cacheMap = new ConcurrentHashMap<>(16);

    private String generateKey(String tableName, String dataId,String code,String lang){
        return String.format("%s:%s:%s:%s", lang, tableName, dataId,code);
    }


    @Override
    public void run(String... args) {
        long count = this.count();
        long current = 1;
        long size = 100;
        Page<SysMessageTip> page = new Page<>(current, size);
        for (; current * size < count + size; current ++){
            page.setCurrent(current);
            IPage<SysMessageTip> pageList = page(page);
            for (SysMessageTip sysMessageTip : pageList.getRecords()) {
                cacheMap.put(generateKey(sysMessageTip.getTableName(),sysMessageTip.getDataId(),sysMessageTip.getCode(),sysMessageTip.getLang()),sysMessageTip.getRemark());
            }
        }
    }

    @Override
    public String getMessageTip(String lang, String tableName, String dataId,String code) {
        return cacheMap.get(generateKey(tableName,dataId,code,lang));
    }
}
