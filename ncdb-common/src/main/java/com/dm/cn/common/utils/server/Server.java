package com.dm.cn.common.utils.server;

import com.dm.cn.common.core.text.Convert;
import com.dm.cn.common.core.utils.unit.UnitUtils;
import com.dm.cn.common.utils.ip.IpUtils;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.TickType;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * 服务器相关信息
 *
 * @author dameng
 */
public class Server {

    private static final int OSHI_WAIT_SECOND = 1000;

    /**
     * 网络IO相关信息
     */
    private NetWork netWork = new NetWork();

    /**
     * CPU相关信息
     */
    private Cpu cpu = new Cpu();

    /**
     * 內存相关信息
     */
    private Mem mem = new Mem();

    /**
     * 磁盘IO相关信息
     */
    private List<DiskIo> diskIoList = new ArrayList<>();

    /**
     * JVM相关信息
     */
    private Jvm jvm = new Jvm();

    /**
     * 服务器相关信息
     */
    private Sys sys = new Sys();

    /**
     * 磁盘使用率
     */
    private double diskUsed;

    /**
     * 磁盘相关信息
     */
    private List<SysFileServer> sysFiles = new LinkedList<SysFileServer>();

    public Cpu getCpu() {
        return cpu;
    }

    public void setCpu(Cpu cpu) {
        this.cpu = cpu;
    }

    public List<DiskIo> getDiskIoList() {
        return diskIoList;
    }

    public void setDiskIoList(List<DiskIo> diskIoList) {
        this.diskIoList = diskIoList;
    }

    public Mem getMem() {
        return mem;
    }

    public void setMem(Mem mem) {
        this.mem = mem;
    }

    public Jvm getJvm() {
        return jvm;
    }

    public NetWork getNetWork() {
        return netWork;
    }

    public void setNetWork(NetWork netWork) {
        this.netWork = netWork;
    }

    public void setJvm(Jvm jvm) {
        this.jvm = jvm;
    }

    public Sys getSys() {
        return sys;
    }

    public void setSys(Sys sys) {
        this.sys = sys;
    }

    public List<SysFileServer> getSysFiles() {
        return sysFiles;
    }

    public void setSysFiles(List<SysFileServer> sysFiles) {
        this.sysFiles = sysFiles;
    }

    public void copyTo() throws Exception {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();

        setCpuInfo(hal.getProcessor());

        setMemInfo(hal.getMemory());

        setSysInfo();

        setJvmInfo();

        setSysFiles(si.getOperatingSystem());

        setNetWorkInfo(hal);
    }

    private void setNetWorkInfo(HardwareAbstractionLayer hal) throws InterruptedException {
        List<NetworkIF> networkIfs = hal.getNetworkIFs();
        long bytesRecv = 0;
        long bytesSent = 0;
        long time = 0;
        for (NetworkIF networkif : networkIfs) {
            bytesRecv += networkif.getBytesRecv();
            bytesSent += networkif.getBytesSent();
            time = networkif.getTimeStamp();
        }
        TimeUnit.SECONDS.sleep(1);

        networkIfs = hal.getNetworkIFs();
        long bytesRecv2 = 0;
        long bytesSent2 = 0;
        long time2 = 0;
        for (NetworkIF networkif : networkIfs) {
            bytesRecv2 += networkif.getBytesRecv();
            bytesSent2 += networkif.getBytesSent();
            time2 = networkif.getTimeStamp();
        }
        long recv = 0, sent = 0;
        if(time2 - time != 0){
            recv = (bytesRecv2 - bytesRecv) / (time2 - time) * 1000;
            sent = (bytesSent2 - bytesSent) / (time2 - time) * 1000;
        }
        netWork.setBytesRecv(recv);
        netWork.setBytesSent(sent);

    }

    /**
     * 设置CPU信息
     */
    private void setCpuInfo(CentralProcessor processor) {
        // CPU信息
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        Util.sleep(OSHI_WAIT_SECOND);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[TickType.NICE.getIndex()] - prevTicks[TickType.NICE.getIndex()];
        long irq = ticks[TickType.IRQ.getIndex()] - prevTicks[TickType.IRQ.getIndex()];
        long softirq = ticks[TickType.SOFTIRQ.getIndex()] - prevTicks[TickType.SOFTIRQ.getIndex()];
        long steal = ticks[TickType.STEAL.getIndex()] - prevTicks[TickType.STEAL.getIndex()];
        long cSys = ticks[TickType.SYSTEM.getIndex()] - prevTicks[TickType.SYSTEM.getIndex()];
        long user = ticks[TickType.USER.getIndex()] - prevTicks[TickType.USER.getIndex()];
        long iowait = ticks[TickType.IOWAIT.getIndex()] - prevTicks[TickType.IOWAIT.getIndex()];
        long idle = ticks[TickType.IDLE.getIndex()] - prevTicks[TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
        cpu.setCpuNum(processor.getLogicalProcessorCount());
        cpu.setTotal(totalCpu);
        cpu.setSys(cSys);
        cpu.setUsed(user);
        cpu.setWait(iowait);
        cpu.setFree(idle);
    }

    /**
     * 设置内存信息
     */
    private void setMemInfo(GlobalMemory memory) {
        mem.setTotal(memory.getTotal());
        mem.setUsed(Convert.toDouble(memory.getTotal() - memory.getAvailable()));
        mem.setFree(memory.getAvailable());
    }


    /**
     * 设置服务器信息
     */
    private void setSysInfo() {
        Properties props = System.getProperties();
        sys.setComputerName(IpUtils.getHostName());
        sys.setOsName(props.getProperty("os.name"));
        sys.setOsArch(props.getProperty("os.arch"));
        sys.setUserDir(props.getProperty("user.dir"));
    }

    /**
     * 设置Java虚拟机
     */
    private void setJvmInfo(){
        Properties props = System.getProperties();
        jvm.setTotal(Runtime.getRuntime().totalMemory());
        jvm.setMax(Runtime.getRuntime().maxMemory());
        jvm.setFree(Runtime.getRuntime().freeMemory());
        jvm.setVersion(props.getProperty("java.version"));
        jvm.setHome(props.getProperty("java.home"));
    }

    /**
     * 设置磁盘信息
     */
    private void setSysFiles(OperatingSystem os) {
        FileSystem fileSystem = os.getFileSystem();
        List<OSFileStore> fsArray = fileSystem.getFileStores();
        for (OSFileStore fs : fsArray) {
            long free = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            long used = total - free;
            SysFileServer sysFile = new SysFileServer();
            sysFile.setDirName(fs.getMount());
            sysFile.setSysTypeName(fs.getType());
            sysFile.setTypeName(fs.getName());
            sysFile.setTotal(UnitUtils.convertFileSize(total));
            sysFile.setFree(UnitUtils.convertFileSize(free));
            sysFile.setUsed(UnitUtils.convertFileSize(used));
            sysFile.setUsage(Arith.mul(Arith.div(used, total, 4), 100));
            sysFiles.add(sysFile);
        }
    }

    public double getDiskUsed() {
        return diskUsed;
    }

    public void setDiskUsed(double diskUsed) {
        this.diskUsed = diskUsed;
    }
}
