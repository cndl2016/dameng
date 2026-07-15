package com.dm.cn.base.entity.param;

import com.dm.cn.common.core.web.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 告警通知消息记录查询参数
 *
 * @author root
 * @date 2025/02/13
 */
@Data
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class AlertNoticeMessageParam extends BaseEntity {

    /**
     * 消息标题
     */
    private String title;

    /**
     * 告警等级
     */
    private String alertLevel;

}