package com.dm.cn.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author DAMENG
 * @date 2022/09/11
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum PassStatusEnum {
    /**
     * 密码状态，0：未设置，1：已设置
     */
    UN_SET("0", "未设置"),
    HAS_SET("1", "已设置");
    private String value;
    private String desc;
}
