package com.dm.cn.common.core.enums;

import com.dm.cn.common.core.constant.NumberConstants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AlarmConditionEnum 告警类型枚举类
 *
 * @author root
 * @date 2022/11/17
 */
public enum AlarmConditionEnum {
    /**
     * 设备 CPU使用率
     */
    DEVICE_CPU("3", "设备 cpu使用率"),
    /**
     * 设备 cpu使用率
     */
    DEVICE_MEM("4", "设备 内存使用率"),
    /**
     * 设备 磁盘使用率
     */
    DEVICE_DISK("5", "设备 磁盘使用率"),
    /**
     * 网络 下行速率
     */
    NETWORK_INPUT("6", "设备 网络下行速率"),
    /**
     * 网络 上行速率
     */
    NETWORK_OUTPUT("7", "设备 网络上行速率"),

    /**
     * 设备离线
     */
    DEVICE_OFFLINE("13", "设备离线"),

    /**
     * 被阻塞客户端数量
     */
    BLOCKED_CLIENTS("19", "被阻塞客户端数量"),

    /**
     * 内存总量
     */
    USED_MEMORY("20", "内存总量"),

    /**
     * 数据集内存量
     */
    USED_MEMORY_DATASET("21", "数据集内存量"),

    /**
     * 数据集内存占比
     */
    USED_MEMORY_DATASET_PERC("22", "数据集内存占比"),

    /**
     * Lua引擎内存量
     */
    USED_MEMORY_LUA("23", "Lua引擎内存量"),

    /**
     * 脚本引擎内存量
     */
    USED_MEMORY_SCRIPTS("24", "脚本引擎内存量"),

    /**
     * 分配器碎片率
     */
    ALLOCATOR_FRAG_RATIO("25", "分配器碎片率"),

    /**
     * 分配器驻留内存占比
     */
    ALLOCATOR_RSS_RATIO("26", "分配器驻留内存占比"),

    /**
     * 内存驻留开销占比
     */
    RSS_OVERHEAD_RATIO("27", "内存驻留开销占比"),

    /**
     * 内存碎片占比
     */
    MEM_FRAGMENTATION_RATIO("28", "内存碎片占比"),

    /**
     * 复制备份使用内存量
     */
    MEM_REPLICATION_BACKLOG("29", "复制备份使用内存量"),

    /**
     * 客户端使用内存量
     */
    MEM_CLIENTS_NORMAL("30", "客户端使用内存量"),

    /**
     * aof缓冲区内存量
     */
    MEM_AOF_BUFFER("31", "aof缓冲区内存量"),

    /**
     * 被拒绝同步请求次数
     */
    SYNC_PARTIAL_ERR("32", "被拒绝同步请求次数"),

    /**
     * 过期key占比
     */
    EXPIRED_STALE_PERC("33", "过期key占比"),

    /**
     * 主备切换
     */
    STANDBY_CHANGE("35", "主备切换"),

    /**
     * key过期告警
     */
    KEY_MONITOR("36", "key过期告警"),

    /**
     * 设备 CPU 1分钟负载
     */
    DEVICE_CPU_LOAD_ONE_MINUTE("39", "设备 cpu 1分钟负载"),

    /**
     * 设备 CPU 5分钟负载
     */
    DEVICE_CPU_LOAD_FIVE_MINUTE("40", "设备 cpu 5分钟负载"),

    /**
     * 设备 CPU 15分钟负载
     */
    DEVICE_CPU_LOAD_FIFTEEN_MINUTE("41", "设备 cpu 15分钟负载"),

    /**
     * 设备 磁盘 读IOPS
     */
    DEVICE_DISK_READ_IOPS("42", "设备 磁盘 读IOPS"),

    /**
     * 设备 磁盘 写IOPS
     */
    DEVICE_DISK_WRITE_IOPS("43", "设备 磁盘 写IOPS"),

    /**
     * 设备 磁盘 读宽度
     */
    DEVICE_DISK_READ_KB("44", "设备 磁盘 读宽度"),

    /**
     * 设备 磁盘 写宽度
     */
    DEVICE_DISK_WRITE_KB("45", "设备 磁盘 写宽度"),

    /**
     * 设备 磁盘 读等待
     */
    DEVICE_DISK_READ_WAIT("46", "设备 磁盘 读等待"),

    /**
     * 设备 磁盘 写等待
     */
    DEVICE_DISK_WRITE_WAIT("47", "设备 磁盘 写等待");

    private String value;
    private String desc;

    public final static String[] DEVICE_CONDITION_GROUP = {"3", "4", "5", "6", "7", "39", "40", "41"};

    public final static String[] DEVICE_DISK_CONDITION_GROUP = {"42", "43", "44", "45", "46", "47"};

    /**
     * 实例主备切换告警分组id
     */
    public final static List<String> INSTANCE_CLUSTER_STATUS_GROUP = Arrays.asList("35");

    /**
     * 私有构造
     *
     * @param value
     * @param desc
     */
    AlarmConditionEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * @return {@link String}
     */
    public String getValue() {
        return value;
    }

    /**
     * @return {@link String}
     */
    public String getDesc() {
        return desc;
    }

    public static String getDesc(String value) {
        Map<String, String> nodeTypeMap = new HashMap(64);
        nodeTypeMap.put(DEVICE_CPU.value, DEVICE_CPU.getDesc());
        nodeTypeMap.put(DEVICE_CPU_LOAD_ONE_MINUTE.value, DEVICE_CPU_LOAD_ONE_MINUTE.getDesc());
        nodeTypeMap.put(DEVICE_CPU_LOAD_FIVE_MINUTE.value, DEVICE_CPU_LOAD_FIVE_MINUTE.getDesc());
        nodeTypeMap.put(DEVICE_CPU_LOAD_FIFTEEN_MINUTE.value, DEVICE_CPU_LOAD_FIFTEEN_MINUTE.getDesc());
        nodeTypeMap.put(DEVICE_DISK_READ_IOPS.value, DEVICE_DISK_READ_IOPS.getDesc());
        nodeTypeMap.put(DEVICE_DISK_WRITE_IOPS.value, DEVICE_DISK_WRITE_IOPS.getDesc());
        nodeTypeMap.put(DEVICE_DISK_READ_KB.value, DEVICE_DISK_READ_KB.getDesc());
        nodeTypeMap.put(DEVICE_DISK_WRITE_KB.value, DEVICE_DISK_WRITE_KB.getDesc());
        nodeTypeMap.put(DEVICE_DISK_READ_WAIT.value, DEVICE_DISK_READ_WAIT.getDesc());
        nodeTypeMap.put(DEVICE_DISK_WRITE_WAIT.value, DEVICE_DISK_WRITE_WAIT.getDesc());
        nodeTypeMap.put(DEVICE_MEM.value, DEVICE_MEM.getDesc());
        nodeTypeMap.put(DEVICE_DISK.value, DEVICE_DISK.getDesc());
        nodeTypeMap.put(NETWORK_INPUT.value, NETWORK_INPUT.getDesc());
        nodeTypeMap.put(NETWORK_OUTPUT.value, NETWORK_OUTPUT.getDesc());
        nodeTypeMap.put(DEVICE_OFFLINE.value, DEVICE_OFFLINE.getDesc());
        nodeTypeMap.put(BLOCKED_CLIENTS.value, BLOCKED_CLIENTS.getDesc());
        nodeTypeMap.put(USED_MEMORY.value, USED_MEMORY.getDesc());
        nodeTypeMap.put(USED_MEMORY_DATASET.value, USED_MEMORY_DATASET.getDesc());
        nodeTypeMap.put(USED_MEMORY_DATASET_PERC.value, USED_MEMORY_DATASET_PERC.getDesc());
        nodeTypeMap.put(USED_MEMORY_LUA.value, USED_MEMORY_LUA.getDesc());
        nodeTypeMap.put(USED_MEMORY_SCRIPTS.value, USED_MEMORY_SCRIPTS.getDesc());
        nodeTypeMap.put(ALLOCATOR_FRAG_RATIO.value, ALLOCATOR_FRAG_RATIO.getDesc());
        nodeTypeMap.put(ALLOCATOR_RSS_RATIO.value, ALLOCATOR_RSS_RATIO.getDesc());
        nodeTypeMap.put(RSS_OVERHEAD_RATIO.value, RSS_OVERHEAD_RATIO.getDesc());
        nodeTypeMap.put(MEM_FRAGMENTATION_RATIO.value, MEM_FRAGMENTATION_RATIO.getDesc());
        nodeTypeMap.put(MEM_REPLICATION_BACKLOG.value, MEM_REPLICATION_BACKLOG.getDesc());
        nodeTypeMap.put(MEM_CLIENTS_NORMAL.value, MEM_CLIENTS_NORMAL.getDesc());
        nodeTypeMap.put(MEM_AOF_BUFFER.value, MEM_AOF_BUFFER.getDesc());
        nodeTypeMap.put(SYNC_PARTIAL_ERR.value, SYNC_PARTIAL_ERR.getDesc());
        nodeTypeMap.put(EXPIRED_STALE_PERC.value, EXPIRED_STALE_PERC.getDesc());
        nodeTypeMap.put(KEY_MONITOR.value, KEY_MONITOR.getDesc());
        nodeTypeMap.put(STANDBY_CHANGE.value, STANDBY_CHANGE.getDesc());
        return nodeTypeMap.get(value);
    }
}
