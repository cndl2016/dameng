package com.dm.cn.system.entity.param;

import com.dm.cn.common.core.web.domain.BaseEntity;
import lombok.Data;

/**
 * 助手列表查询参数
 *
 * @author cn
 * @date 2023/07/09
 */
@Data
public class AssistantParam extends BaseEntity {
    /**
     * 助手名称
     */
    private String assistantName;
    /**
     * 文件路径
     */
    private String filePath;
}
