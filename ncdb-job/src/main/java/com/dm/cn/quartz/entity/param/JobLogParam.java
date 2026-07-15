package com.dm.cn.quartz.entity.param;

import com.dm.cn.common.core.web.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询设备参数
 *
 * @author dameng
 * @date 2023/01/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobLogParam extends BaseEntity {
    /**
     * 任务名称
     */
    private String jobName;
    /**
     * 执行状态
     */
    private String status;
}
