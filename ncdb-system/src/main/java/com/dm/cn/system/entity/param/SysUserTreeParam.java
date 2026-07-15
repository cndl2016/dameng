package com.dm.cn.system.entity.param;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 实例参数查询实体
 *
 * @author DAMENG
 * @date 2022/06/17
 */

@Data
public class SysUserTreeParam  {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long deptId;
}
