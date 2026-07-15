package com.dm.cn.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 配置文件 接口、脚本等
 *
 * @author DAMENG
 * @date 2022/08/27
 */
@Data
@Component
public class NcdbApiConfig {
    /**
     * 安装解压根目录
     */
    @Value("${ncdb.destination}")
    public String destination;

    /**
     * 默认远程脚本存放目录
     */
    @Value("${ncdb.scriptPath}")
    public String scriptPath;

    /**
     * 数据文件存放目录
     */
    @Value("${ncdb.dataPath}")
    public String dataPath;

    /**
     * 告警脚本路径
     */
    @Value("${ncdb.alarmMessage}")
    public String alarmMessage;

    /**
     * 设备监控脚本路径
     */
    @Value("${ncdb.deviceMonitor}")
    public String deviceMonitor;

    /**
     * 获取服务端口脚本路径
     */
    @Value("${ncdb.portRange}")
    public String portRange;

    /**
     * 文件内容新增或修改脚本路径
     */
    @Value("${ncdb.writeFile}")
    public String writeFile;

    /**
     * toml文件内容新增或修改脚本路径
     */
    @Value("${ncdb.writeTomlFile}")
    public String writeTomlFile;

    /**
     * 命令行默认查询最大返回结果数量
     */
    @Value("${ncdb.queryMaxLimit}")
    public int queryMaxLimit;

    /**
     * x86系统架构标识
     */
    @Value("${architecture.x86Flags}")
    public List<String> x86FlagList;

    /**
     * arm系统架构标识
     */
    @Value("${architecture.armFlags}")
    public List<String> armFlagList;

    /**
     * 一个实例服务需要的最小内存
     */
    @Value("${serviceSize.memResource}")
    public int memResource;

    /**
     * 一个实例服务需要的最小磁盘
     */
    @Value("${serviceSize.diskResource}")
    public int diskResource;

    /**
     * 获取manager项目路径
     *
     * @return {@link String}
     */
    public String getAppPath() {
        return new ApplicationHome().getDir().getPath();
    }

    /**
     * 获取manager项目下脚本工具路径
     *
     * @return {@link String}
     */
    public String getAppPathConfig() {
        return getAppPath() + "/config";
    }
}