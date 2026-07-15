package com.dm.cn.common.core.utils.unit;

/**
 * 单位转换
 *
 * @author dameng
 */
public class UnitUtils {
    /**
     * 字节转换
     *
     * @param size 字节大小
     * @return 转换后值
     */
    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else {
            return String.format("%d B", size);
        }
    }

    /**
     * 转换GB、KB等字符串为字节
     *
     * @return 字节
     */
    public static long converFileSizeStrToByte(String str) {
        String tb = "TB";
        String gb = "GB";
        String mb = "MB";
        String kb = "KB";
        String t = "T";
        String g = "G";
        String m = "M";
        String k = "K";
        if (str.contains(tb) || str.contains(t)) {
            return (long) (Double.parseDouble(str.replace("TB", "").replace("T","").trim()) * 1024L * 1024L * 1024L * 1024L);
        } else if (str.contains(gb) || str.contains(g)) {
            return (long) (Double.parseDouble(str.replace("GB", "").replace("G","").trim()) * 1024L * 1024L * 1024L);
        } else if (str.contains(mb) || str.contains(m)) {
            return (long) (Double.parseDouble(str.replace("MB", "").replace("M","").trim()) * 1024L * 1024L);
        } else if (str.contains(kb) || str.contains(k)) {
            return (long) (Double.parseDouble(str.replace("KB", "").replace("K","").trim()) * 1024L);
        } else {
            return (long) Double.parseDouble(str.replace("B", "").trim());
        }
    }
}
