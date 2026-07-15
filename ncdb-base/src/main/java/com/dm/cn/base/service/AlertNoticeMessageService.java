package com.dm.cn.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dm.cn.base.entity.param.AlertNoticeMessageParam;
import com.dm.cn.base.entity.server.AlertNoticeMessage;

import java.util.List;

/**
 * @author huangzizhong
 * @description 针对表【T_ALERT_NOTICE_MESSAGE(告警通知消息记录表)】的数据库操作Service
 * @createDate 2025-02-13 09:23:07
 */
public interface AlertNoticeMessageService extends IService<AlertNoticeMessage> {
    /**
     * 告警通知消息记录分页查询
     *
     * @param param 查询参数对象
     * @return
     */
    IPage<AlertNoticeMessage> getPage(AlertNoticeMessageParam param);


    /**
     * 导出告警通知消息记录
     *
     * @param param 查询参数对象
     * @return
     */
    List<AlertNoticeMessage> export(AlertNoticeMessageParam param);

}