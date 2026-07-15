package com.dm.cn.base.constant;

import com.dm.cn.base.filter.ColumnCondition;

import java.util.List;
import java.util.Set;

/**
 * 系统列定义表常量类
 *
 * @author Auto-Coder
 * @date 2023-03-06
 */
public interface SysColumnUserConstant {

    /**
     * 列数据获取方法指定参数类型,待查列、id参数的数据类型
     */
    Class[] DEFAULT_FEILDS = new Class[]{List.class,Set.class};
    /**
     * 列数据获取方法指定参数类型,主表专用,待查列、id参数、主表过滤条件的数据类型
     */
    Class[] MAIN_FEILDS = new Class[]{List.class, ColumnCondition.class};

    /**
     * 主键参数名固定为id
     */
    String DEFAULT_NAME = "id";

    /**
     * 主表过滤条件类别,eq等于，ne不等，gt大于，ge大于等于，lt小于，le小于等于，and取交集，or取并集
     */
    String CONDITION_EQ = "eq";
    String CONDITION_NE = "ne";
    String CONDITION_GT = "gt";
    String CONDITION_GE = "ge";
    String CONDITION_LT = "lt";
    String CONDITION_LE = "le";
    String CONDITION_AND = "and";
    String CONDITION_OR = "or";

    /**
     * 列分组类别,普通，主键,隐藏列,其它
     */
    String GROUP_NORMAL = "normal";
    String GROUP_ID = "id";
    String GROUP_HIDE = "hide";
    String GROUP_OTHER = "other";

    /**
     * 列类别,普通，主键，过滤条件
     */
    String TYPE_MASTER = "master";
    String TYPE_DB = "db";
}