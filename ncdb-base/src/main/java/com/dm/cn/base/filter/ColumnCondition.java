package com.dm.cn.base.filter;


import com.dm.cn.base.constant.SysColumnUserConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 列表主表过滤条件
 *
 * @author Auto-Coder
 * @date 2023-03-02
 */
@Data
@ApiModel(value = "列表主表过滤条件")
public class ColumnCondition {

    /**过滤规则*/
    @ApiModelProperty(value = "过滤规则")
    private String type;

    /**排序列*/
    @ApiModelProperty(value = "过滤列")
    private String columnCode;

    /**条件*/
    @ApiModelProperty(value = "条件")
    private List<Object> value;

    {
        type = null;
        columnCode = null;
        value = new ArrayList<>();
    }

    /**
     * 组合条件方法
     * @param condition and拼接条件
     * @param require 条件类别
     */
    private void consumer(Condition condition, String require){
        ColumnCondition item = new ColumnCondition();
        item.type = require;
        value.add(item);
        condition.accept(item);
    }

    /**
     * eq拼接方法
     * @param code 条件列code
     * @param val 条件值
     * @param  require 条件类别
     */
    private void logic(String code,Object val,String require){
        ColumnCondition item = new ColumnCondition();
        item.type = require;
        item.columnCode = code;
        item.value.add(val);
        value.add(item);
    }

    /**
     * and拼接方法
     * @param condition and拼接条件
     * @return ColumnCondition
     */
    public ColumnCondition and(Condition condition){
        consumer(condition, SysColumnUserConstant.CONDITION_AND);
        return this;
    }

    /**
     * or拼接方法
     * @param condition 拼接条件
     * @return ColumnCondition
     */
    public ColumnCondition or(Condition condition){
        consumer(condition,SysColumnUserConstant.CONDITION_OR);
        return this;
    }

    /**
     * eq拼接方法
     * @param code 条件列code
     * @param val 条件值
     * @return ColumnCondition
     */
    public ColumnCondition eq(String code,Object val){
        logic(code,val,SysColumnUserConstant.CONDITION_EQ);
        return this;
    }

    /**
     * ne拼接方法
     * @param code 条件列code
     * @param val 条件值
     * @return ColumnCondition
     */
    public ColumnCondition ne(String code,Object val){
        logic(code,val,SysColumnUserConstant.CONDITION_NE);
        return this;
    }

    /**
     * gt拼接方法
     * @param code 条件列code
     * @param val 条件值
     * @return ColumnCondition
     */
    public ColumnCondition gt(String code,Object val){
        logic(code,val,SysColumnUserConstant.CONDITION_GT);
        return this;
    }

    /**
     * ge拼接方法
     * @param code 条件列code
     * @param val 条件值
     * @return ColumnCondition
     */
    public ColumnCondition ge(String code,Object val){
        logic(code,val,SysColumnUserConstant.CONDITION_GE);
        return this;
    }

    /**
     * lt拼接方法
     * @param code 条件列code
     * @param val 条件值
     * @return ColumnCondition
     */
    public ColumnCondition lt(String code,Object val){
        logic(code,val,SysColumnUserConstant.CONDITION_LT);
        return this;
    }

    /**
     * le拼接方法
     * @param code 条件列code
     * @param val 条件值
     * @return ColumnCondition
     */
    public ColumnCondition le(String code,Object val){
        logic(code,val,SysColumnUserConstant.CONDITION_LE);
        return this;
    }
}
