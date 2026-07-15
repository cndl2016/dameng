package com.dm.cn.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dm.cn.base.entity.param.AlarmMessageParam;
import com.dm.cn.base.entity.server.AlarmMessage;

import java.util.List;

/**
 * @author dameng
 * @description 针对表【T_ALARM_MESSAGE(告警消息表)】的数据库操作Service
 * @createDate 2024-10-15 11:46:34
 * @date 2024/10/19
 */
public interface AlarmMessageService extends IService<AlarmMessage> {

    /**
     * 保存告警通知方式
     *
     * @param param 参数对象
     * @return
     */
    boolean add(AlarmMessageParam param);

    /**
     * 获取所有的告警通知方式信息
     *
     * @return
     */
    List<AlarmMessage> getAll();

}