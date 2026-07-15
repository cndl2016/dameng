package com.dm.cn.base.filter;

/**
 * 过滤条件接口
 *
 * @author Auto-Coder
 * @date 2023-03-01
 */
public interface Condition {

    /**
     * 过滤条件接口
     *
     * @param condition 查询条件
     */
    void accept(ColumnCondition condition);
}
