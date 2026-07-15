package com.dm.cn.common.enums;

import com.dm.cn.common.core.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author root
 * @date 2024/10/17
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ModelTypeEnum {

    /**
     * 设备类型
     */
    DEVICE("0", "设备类型");

    private String value;
    private String desc;

    /**
     * 通过value判断值是否在枚举中存在
     *
     * @param value
     * @return
     */
    public static boolean isExist(String value) {
        if (StringUtils.isEmpty(value)) {
            return false;
        }

        for (ModelTypeEnum modelTypeEnum : ModelTypeEnum.values()) {
            if (modelTypeEnum.value.equals(value)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 通过name判断值是否在枚举中存在
     *
     * @param name
     * @return
     */
    public static boolean isExistByName(String name) {
        if (StringUtils.isEmpty(name)) {
            return false;
        }

        for (ModelTypeEnum modelTypeEnum : ModelTypeEnum.values()) {
            if (modelTypeEnum.name().equals(name)) {
                return true;
            }
        }

        return false;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 特别注意：将类型name设置为key
     *
     * @param value
     * @return
     */
    public static String getDesc(String value) {
        Map<String, String> modelTypeMap = new HashMap<>();
        modelTypeMap.put(DEVICE.name(), DEVICE.getDesc());

        return modelTypeMap.get(value);
    }

}