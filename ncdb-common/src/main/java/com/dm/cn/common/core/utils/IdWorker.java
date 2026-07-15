package com.dm.cn.common.core.utils;


import com.dm.cn.common.utils.ip.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * <p>名称：IdWorker.java</p>
 * <p>描述：分布式自增长ID</p>
 * <pre>
 *     Twitter的 Snowflake　JAVA实现方案
 * </pre>
 * 核心代码为其IdWorker这个类实现，其原理结构如下，我分别用一个0表示一位，用—分割开部分的作用：
 * 1||0---0000000000 0000000000 0000000000 0000000000 0 --- 00000 ---00000 ---000000000000
 * 在上面的字符串中，第一位为未使用（实际上也可作为long的符号位），接下来的41位为毫秒级时间，
 * 然后5位dataCenter标识位，5位机器ID（并不算标识符，实际是为线程标识），
 * 然后12位该毫秒内的当前毫秒内的计数，加起来刚好64位，为一个Long型。
 * 这样的好处是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞（由dataCenter和机器ID作区分），
 * 并且效率较高，经测试，snowflake每秒能够产生26万ID左右，完全满足需要。
 * <p>
 * 64位ID (42(毫秒)+5(机器ID)+5(业务编码)+12(重复累加))
 *
 * @author Polim
 * @date 2022/08/17
 */
@Slf4j
public class IdWorker {
    /**
     * 时间起始标记点，作为基准，一般取系统的最近时间（一旦确定不能变动）
     */
    private final static long TWE_P_OCH = 1288834974657L;
    /**
     * 机器标识位数
     */
    private final static long WORKER_ID_BITS = 5L;
    /**
     * 数据中心标识位数
     */
    private final static long DATA_CENTER_ID_BITS = 5L;
    /**
     * 机器ID最大值
     */
    private final static long MAX_WORKER_ID = -1L ^ (-1L << WORKER_ID_BITS);
    /**
     * 数据中心ID最大值
     */
    private final static long MAX_DATA_CENTER_ID = -1L ^ (-1L << DATA_CENTER_ID_BITS);
    /**
     * 毫秒内自增位
     */
    private final static long SEQUENCE_BITS = 12L;
    /**
     * 机器ID偏左移12位
     */
    private final static long WORKER_ID_SHIFT = SEQUENCE_BITS;
    /**
     * 数据中心ID左移17位
     */
    private final static long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    /**
     * 时间毫秒左移22位
     */
    private final static long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

    /**
     *
     */
    private final static long SEQUENCE_MASK = -1L ^ (-1L << SEQUENCE_BITS);
    /**
     * 上次生产id时间戳
     */
    private static long LAST_TIME_STAMP = -1L;
    /**
     * 0，并发控制
     */
    private static long SEQUENCE = 0L;

    /**
     *
     */
    private final long WORKER_ID;
    /**
     * 数据标识id部分
     */
    private final long DATA_CENTER_ID;

    /**
     *
     */
    private IdWorker() {
        this.DATA_CENTER_ID = getDataCenterId(MAX_DATA_CENTER_ID);
        this.WORKER_ID = getMaxWorkerId(DATA_CENTER_ID, MAX_WORKER_ID);
    }

    /**
     *
     */
    static final IdWorker ID_WORKER = new IdWorker();

    /**
     * @param workerId     工作机器ID
     * @param dataCenterId 序列号
     */
    public IdWorker(long workerId, long dataCenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", MAX_WORKER_ID));
        }
        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("dataCenterId can't be greater than %d or less than 0", MAX_DATA_CENTER_ID));
        }
        this.WORKER_ID = workerId;
        this.DATA_CENTER_ID = dataCenterId;
    }


    /**
     * @return {@link IdWorker}
     */
    public static IdWorker getId() {
        return ID_WORKER;
    }

    /**
     * 获取下一个ID
     *
     * @return long
     */
    public synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < LAST_TIME_STAMP) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", LAST_TIME_STAMP - timestamp));
        }

        if (LAST_TIME_STAMP == timestamp) {
            // 当前毫秒内，则+1
            SEQUENCE = (SEQUENCE + 1) & SEQUENCE_MASK;
            if (SEQUENCE == 0) {
                // 当前毫秒内计数满了，则等待下一秒
                timestamp = tilNextMillis(LAST_TIME_STAMP);
            }
        } else {
            SEQUENCE = 0L;
        }
        LAST_TIME_STAMP = timestamp;
        // ID偏移组合生成最终的ID，并返回ID
        long nextId = ((timestamp - TWE_P_OCH) << TIMESTAMP_LEFT_SHIFT)
                | (DATA_CENTER_ID << DATA_CENTER_ID_SHIFT)
                | (WORKER_ID << WORKER_ID_SHIFT) | SEQUENCE;

        return nextId;
    }

    /**
     * @param lastTimestamp
     * @return long
     */
    private long tilNextMillis(final long lastTimestamp) {
        long timestamp = this.timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }
        return timestamp;
    }

    /**
     * @return long
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * <p>
     * 获取 maxWorkerId
     * </p>
     *
     * @param dataCenterId
     * @param maxWorkerId
     * @return long
     */
    protected static long getMaxWorkerId(long dataCenterId, long maxWorkerId) {
        StringBuffer mpId = new StringBuffer();
        mpId.append(dataCenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (!name.isEmpty()) {
            /*
             * GET jvmPid
             */
            mpId.append(name.split("@")[0]);
        }
        /*
         * MAC + PID 的 hashcode 获取16个低位
         */
        return (mpId.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }

    /**
     * <p>
     * 数据标识id部分
     * </p>
     *
     * @param maxDataCenterId
     * @return long
     */
    protected static long getDataCenterId(long maxDataCenterId) {
        long id = 0L;
        try {
            InetAddress ip = InetAddress.getByName(IpUtils.getHostIp());
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if (network == null || ObjectUtils.isEmpty(network.getHardwareAddress())) {
                id = 1L;
            } else {
                byte[] mac = network.getHardwareAddress();
                id = ((0x000000FF & (long) mac[mac.length - 1])
                        | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
                id = id % (maxDataCenterId + 1);
            }
        } catch (SocketException | UnknownHostException e) {
            log.error("getDataCenterId is error: {} " + e);
        }
        return id;
    }
}

