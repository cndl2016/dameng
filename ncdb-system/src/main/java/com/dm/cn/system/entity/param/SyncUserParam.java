package com.dm.cn.system.entity.param;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

/**
 * @author dameng
 * @date 2023/05/16
 */
@Data
public class SyncUserParam {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long domainId;
    private List<Long> userIdList;
}
