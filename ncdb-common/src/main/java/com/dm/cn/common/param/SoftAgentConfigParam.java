package com.dm.cn.common.param;

import com.dm.cn.common.core.web.domain.BaseEntity;
import lombok.Data;

/**
 * 采集频率分页查询参数
 *
 * @author DAMENG
 * @date 2025/02/06
 */
@Data
public class SoftAgentConfigParam extends BaseEntity {

    /**
     * 关键字检索，模糊查询参数名称
     */
    private String keywords;

}