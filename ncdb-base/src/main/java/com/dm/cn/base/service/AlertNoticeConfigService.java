package com.dm.cn.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dm.cn.base.entity.server.AlertConfig;
import com.dm.cn.base.entity.server.AlertNoticeConfig;
import com.dm.cn.base.entity.vo.NoticeConfigVO;

import java.util.List;

/**
 * @author huangzizhong
 * @description 针对表【T_ALERT_NOTICE_CONFIG(告警项通知方式配置表)】的数据库操作Service
 * @createDate 2025-02-11 09:18:35
 */
public interface AlertNoticeConfigService extends IService<AlertNoticeConfig> {

    /**
     * 保存告警和通知方式配置中间表
     *
     * @param alertConfig      告警项配置
     * @param selectNoticeList 通知方式集合
     */
    void save(AlertConfig alertConfig, List<NoticeConfigVO> selectNoticeList);
}