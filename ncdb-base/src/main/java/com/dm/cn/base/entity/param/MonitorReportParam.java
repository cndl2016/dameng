package com.dm.cn.base.entity.param;

import com.dm.cn.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 运行报告参数
 *
 * @author wys 2024/09/24
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MonitorReportParam extends BaseEntity {
    /**
     * id
     */
    private Long monitorId;

    /**
     * 开始年份
     */
    private int startYear;

    /**
     * 开始月份
     */
    private int startMonth;

    /**
     * 结束年份
     */
    private int endYear;

    /**
     * 结束月份
     */
    private int endMonth;
}
