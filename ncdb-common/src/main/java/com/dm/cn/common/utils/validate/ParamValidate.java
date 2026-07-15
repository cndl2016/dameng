package com.dm.cn.common.utils.validate;


import com.dm.cn.common.core.constant.Constants;
import com.dm.cn.common.core.utils.StringUtils;

/**
 * 参数校验类
 *
 * @author DAMENG
 * @date 2022/06/27
 */
public class ParamValidate {

    /**
     * 校验参数是否为空或字符串null
     *
     * @param data
     * @return boolean
     */
    public static boolean validate(String data) {
        if (!Constants.STR_NULL.equals(data)&& !"".equals(data) && !StringUtils.isEmpty(data)) {
            return true;
        }
        return false;
    }

    /**
     * 校验参数是否为空或字符串null
     *
     * @param data
     * @return boolean
     */
    public static boolean validate(int data) {
        if (data != 0) {
            return true;
        }
        return false;
    }
}
