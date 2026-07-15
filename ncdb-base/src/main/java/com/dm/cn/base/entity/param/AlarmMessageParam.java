package com.dm.cn.base.entity.param;

import com.dm.cn.base.entity.server.AlarmMessage;
import lombok.Data;

/**
 * 告警通知方式保存入参
 *
 * @author dameng
 */
@Data
public class AlarmMessageParam {

    /**
     * 告警规则主表对象
     */
    private AlarmMessage alarmMessage;

}