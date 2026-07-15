package com.dm.cn.system.service;


/**
 * 系统信息语言标识表
 * @author dyy
 * @date 2025/04/16
 */
public interface ISysMessageTipService {

    /**
     * 根据语言标识获取信息
     * @param lang 语言标识
     * @param tableName 表名
     * @param dataId 关联id
     * @param code 编码
     * @return String
     */
    String getMessageTip(String lang,String tableName,String dataId,String code);
}
