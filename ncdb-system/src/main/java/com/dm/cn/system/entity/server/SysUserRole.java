package com.dm.cn.system.entity.server;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 用户和角色关联 sys_user_role
 *
 * @author dameng
 */
@Data
public class SysUserRole
{
    /** 用户ID */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /** 角色ID */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long roleId;
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("userId", getUserId())
            .append("roleId", getRoleId())
            .toString();
    }
}
