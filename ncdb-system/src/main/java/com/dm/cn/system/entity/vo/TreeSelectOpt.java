package com.dm.cn.system.entity.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dm.cn.common.core.constant.Constants;
import com.dm.cn.common.core.domain.SysDept;
import com.dm.cn.common.core.domain.SysMenu;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import dm.jdbc.util.StringUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Treeselect树结构实体类
 *
 * @author dameng
 */
@Data
public class TreeSelectOpt implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 节点ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 节点名称
     */
    private String label;

    /**
     * 子节点
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TreeSelectOpt> children;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;

    /**
     * 是否禁用
     */
    @TableField(exist = false)
    private String status;

    public TreeSelectOpt() {

    }

    public TreeSelectOpt(SysDept dept) {
        this.id = dept.getDeptId();
        if (Constants.EN.equals(dept.getLanguage()) && StringUtil.isNotEmpty(dept.getDeptNameEn())) {
            this.label = dept.getDeptNameEn();
        } else {
            this.label = dept.getDeptName();
        }
        this.children = dept.getChildren().stream().map(TreeSelectOpt::new).collect(Collectors.toList());
        this.parentId = dept.getParentId();
        this.status = dept.getStatus();
    }

    public TreeSelectOpt(SysMenu menu) {
        this.id = menu.getMenuId();
        this.label = menu.getMenuName();
        this.children = menu.getChildren().stream().map(TreeSelectOpt::new).collect(Collectors.toList());
        this.parentId = menu.getParentId();
    }


}
