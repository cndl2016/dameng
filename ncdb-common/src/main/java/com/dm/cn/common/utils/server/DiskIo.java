package com.dm.cn.common.utils.server;


import lombok.Data;

/**
 * CPU相关信息
 *
 * @author dameng
 */
@Data
public class DiskIo {
    /**
     * OS_DISKS_IO 主机磁盘读写
     *             "diskName":"dm-2",//磁盘名
     *             "readIops":"0.00",//读IOPS
     *             "readKb":"0.00",//读带宽
     *             "readWait":"2.28",//读等待
     *             "writeIops":"0.00",//写IOPS
     *             "writeKb":"0.00",//写带宽
     *             "writeWait":"2.28",//写等待
     *             "util":"0.00"//磁盘利用率
     */
    private String diskName;
    private String readIops;
    private String readKb;
    private String readWait;
    private String writeIops;
    private String writeKb;
    private String writeWait;
    private String util;

    /**
     * 文件系统总量
     */
    private Long diskTotal;

    /**
     * 文件系统使用量
     */
    private Long diskUsage;

    /**
     * 文件系统使用率
     */
    private Double diskUsed;

}
