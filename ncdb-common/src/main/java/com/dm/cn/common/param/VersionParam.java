package com.dm.cn.common.param;

import com.dm.cn.common.core.web.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author DAMENG
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VersionParam extends BaseEntity {

    /**
     * 版本名称、版本号模糊查询
     */
    private String name;

    /**
     * 启用状态
     */
    private String enableStatus;

    /**
     * 查询开始时间
     */
    private String beginTime;

    /**
     * 查询结束时间
     */
    private String endTime;

}