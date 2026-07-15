package com.dm.cn.system.entity.server;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色表实体对象
 * @author dyy
 * @date 2025-04-16
 */
@Data
@TableName("SYS_MESSAGE_TIP")
@NoArgsConstructor
@AllArgsConstructor
public class SysMessageTip {

    /**主键*/
    @TableId(type = IdType.INPUT)
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;

    /**业务id*/
    @TableField(value = "DATA_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private String dataId;

    /**关联表*/
    @TableField(value = "TABLE_NAME")
    private String tableName;

    /**语言标识*/
    @TableField(value = "LANG")
    private String lang;

    /**模版内容*/
    @TableField(value = "REMARK")
    private String remark;

    /**code编码*/
    @TableField(value = "CODE")
    private String code;

}
