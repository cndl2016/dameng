package com.dm.cn.system.entity.server;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 角色和菜单关联 sys_role_menu
 *
 * @author dameng
 */
@Data
public class SysRoleMenu
{
    /** 角色ID */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long roleId;

    /** 菜单ID */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long menuId;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("roleId", getRoleId())
            .append("menuId", getMenuId())
            .toString();
    }
}
