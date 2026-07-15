package com.dm.cn.system.entity.param;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * @author dameng
 * @date 2023/05/16
 */
@Data
public class SyncTaskParam {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long domainId;

    private String syncEnable;

    private String syncCron;
}
