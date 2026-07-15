package com.dm.cn.base.constant;

import com.dm.framework.common.constant.EnumInterface;

/**
 * 告警规则定义表常量类
 *
 * @author Auto-Coder
 * @date 2023-03-03
 */
public interface AlertParametersConstant {

    /**
     * 持续时间单位
     */
    enum DurationUnit implements EnumInterface {
        /**
         * 小时
         */
        H("小时", 60 * 60),
        /**
         * 分钟
         */
        M("分钟", 60),
        /**
         * 秒
         */
        S("秒", 1);
        /**
         * 描述，国际化
         */
        String label;

        /**
         * 将时间单位统一转换成秒，需要乘以的数值
         * 小时：60*60
         * 分钟：60
         * 秒：1
         */
        int toSec;

        DurationUnit(String label, int toSec) {
            this.label = label;
            this.toSec = toSec;
        }

        @Override
        public String getLabel() {
            return label;
        }

        public int getToSec() {
            return toSec;
        }
    }

    /**
     * 阈值单位
     */
    enum ValueUnit implements EnumInterface {
        /**
         * %
         */
        PERCENT("%", "%", ValueKind.PERCENT.getValue(), 100),
        /**
         * 个
         */
        NUMBER("个", ValueKind.PCS.getValue(), ValueKind.NUMBER.getValue(), 2000),
        /**
         * 次/秒
         */
        TIMES("次", ValueKind.TIMES.getValue(), ValueKind.NUMBER.getValue(), 2000),
        /**
         * 次
         */
        TIMES_S("次/秒", ValueKind.TPS.getValue(), ValueKind.NUMBER.getValue(), 2000),
        /**
         * 天
         */
        DAY("天", "天", ValueKind.DAY.getValue(), 365),
        /**
         * 小时
         */
        HOUR("小时", ValueKind.H.getValue(), ValueKind.TIME.getValue(), 24 * 7),
        /**
         * 分钟
         */
        MINUTE("分钟", ValueKind.M.getValue(), ValueKind.TIME.getValue(), 60 * 24 * 7),
        /**
         * 秒
         */
        SECOND("秒", ValueKind.S.getValue(), ValueKind.TIME.getValue(), 60 * 60 * 24 * 7),
        /**
         * kbps
         */
        KBPS("kbps", "kbps", ValueKind.BYTE.getValue(), 100 * 1024 * 1024 * 1024),
        /**
         * KB
         */
        KB("KB", "KB", ValueKind.BYTE.getValue(), 100 * 1024 * 1024 * 1024),
        /**
         * KB/S
         */
        KB_S("KB/S", "KB/S", ValueKind.BYTE.getValue(), 100 * 1024 * 1024 * 1024),
        /**
         * MB
         */
        MB("MB", "MB", ValueKind.BYTE.getValue(), 100 * 1024 * 1024),
        /**
         * MB
         */
        MB_S("MB/S", "MB/S", ValueKind.BYTE.getValue(), 100 * 1024 * 1024),
        /**
         * GB
         */
        GB("GB", "GB", ValueKind.BYTE.getValue(), 100 * 1024),
        /**
         * GB/S
         */
        GB_S("GB/S", "GB/S", ValueKind.BYTE.getValue(), 100 * 1024),
        /**
         * TB
         */
        TB("TB", "TB", ValueKind.BYTE.getValue(), 100),
        /**
         * TB/S
         */
        TB_S("TB/S", "TB/S", ValueKind.BYTE.getValue(), 100),
        /**
         * 空
         */
        NONE("", "", ValueKind.NONE.getValue(), 0);
        /**
         * 描述，国际化
         */
        String label;
        String labelEn;
        /**
         * 阈值分类
         * percent:百分比类
         * number：数量类
         * time：时间类
         */
        String kind;

        int def;

        ValueUnit(String label, String labelEn, String kind, int def) {
            this.label = label;
            this.labelEn = labelEn;
            this.kind = kind;
            this.def = def;
        }

        @Override
        public String getLabel() {
            return label;
        }

        public String getLabelEn() {
            return labelEn;
        }

        public String getKind() {
            return kind;
        }

        public int getDef() {
            return def;
        }
    }

    /**
     * 阈值分类
     * percent:百分比类
     * number：数量类
     * time：时间类
     */
    enum ValueKind implements EnumInterface {
        /**
         * 百分比类
         */
        PERCENT("百分比类"),
        /**
         * 数量类
         */
        NUMBER("数量类"),
        /**
         * 天数
         */
        DAY("天数"),
        /**
         * 时间类
         */
        TIME("时间类"),
        /**
         * 次
         */
        TIMES("次"),
        /**
         * 字节类
         */
        BYTE("时间类"),
        /**
         * 小时
         */
        H("小时"),
        /**
         * 分钟
         */
        M("分钟"),
        /**
         * 秒
         */
        S("秒"),
        /**
         * 个
         */
        PCS("个"),
        /**
         * 次/秒
         */
        TPS("tps"),
        /**
         * 空
         */
        NONE("");
        /**
         * 描述，国际化
         */
        String label;

        ValueKind(String label) {
            this.label = label;
        }

        @Override
        public String getLabel() {
            return label;
        }
    }
}