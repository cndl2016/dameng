package com.dm.cn.common.param;

import com.dm.cn.common.core.web.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 告警历史记录查询参数
 *
 * @author root
 * @date 2022/12/16
 */
@Data
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class SysMessageParam extends BaseEntity {

    /**
     * 关键词
     */
    private String searchValue;

    /**
     * 查询时间点（小时数）
     */
    private Integer time;

    /**
     * 告警等级
     */
    private String alarmLevel;

    /**
     * 实例id
     */
    private Long instanceId;
}