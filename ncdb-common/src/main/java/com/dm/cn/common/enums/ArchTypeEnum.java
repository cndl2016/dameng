package com.dm.cn.common.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 系统架构类型
 *
 * @author root
 * @date 2022/11/17
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ArchTypeEnum {


    /**
     * X86
     */
    X86("0", "X86架构"),


    /**
     * ARM
     */
    ARM("1", "ARM架构");

    private String value;
    private String desc;
}
