package com.dm.cn.device.service;

import com.dm.cn.common.utils.server.Server;
import com.dm.cn.device.entity.server.Device;

import java.util.List;
import java.util.Map;

/**
 * ssh服务
 *
 * @author dameng
 * @date 2024/10/15
 */
public interface SshdService {

    /**
     * 修改配置文件内容
     *
     * @param host     设备ip
     * @param filePath 配置文件地址
     * @param content  修改内容
     */
    void writeOrReplaceFile(String host, String filePath, Map<String, String> content);

    /**
     * 写入配置文件内容
     *
     * @param filePath 配置文件地址
     * @param host     设备ip
     * @param content  修改内容
     */
    void writeFileTxt(String host, String filePath, String content);

    /**
     * 执行命令返回结果
     *
     * @param host 设备ip
     * @param cmd  命令
     * @return {@link String}
     */
    String execCmd(String host, String cmd);

    /**
     * 创建伪终端并执行交互式命令,返回结果
     *
     * @param host 设备ip
     * @param cmd  命令
     * @return {@link String}
     */
    String execPtyCmd(String host, String cmd);

    /**
     * 命令行操作 不读取返回
     *
     * @param cmd  shell命令
     * @param host 设备ip
     */
    void execCmdNoResult(String host, String cmd);

    /**
     * 删除文件
     *
     * @param host     设备ip
     * @param filePath 文件路径
     */
    void deleteFile(String host, String filePath);

    /**
     * 批量删除文件
     *
     * @param host      设备ip
     * @param filePaths 文件路径集合
     */
    void deleteFiles(String host, List<String> filePaths);

    /**
     * 删除开头的文件内容
     *
     * @param host     设备ip
     * @param filePath 文件路径
     * @param content  内容
     */
    void deleteFileText(String host, String filePath, String content);

    /**
     * 新增设备后 将本地config 目录下的文件拷贝到新的设备上 projectPath/config 下
     *
     * @param device 设备
     * @return boolean
     */
    boolean copyConfig(Device device);

    /**
     * 本地文件上传至远程服务器
     *
     * @param device           设备
     * @param sourceLocalPath  本地路径
     * @param targetRemotePath 远程目标路径
     * @return boolean
     */
    boolean putRemoteFiles(Device device, String sourceLocalPath, String targetRemotePath);

    /**
     * 远程服务器文件下载至本地
     *
     * @param device           设备
     * @param sourceRemotePath 本地目标路径
     * @param targetLocalPath  远程路径
     * @return boolean
     */
    boolean getRemoteFiles(Device device, String sourceRemotePath, String targetLocalPath);

    /**
     * 设备监控信息
     *
     * @param host 设备ip
     * @return {@link Server}
     */
    Server deviceMonitor(String host);

    /**
     * 测试设备连接
     *
     * @param host     设备ip
     * @param username 用户名
     * @param port     连接端口
     * @param pass     密码
     */
    void connTest(String host, String username, int port, String... pass);

    /**
     * 测试设备新密码连接
     *
     * @param host     设备ip
     * @param username 用户名
     * @param port     连接端口
     * @param pass     密码
     */
    void connTestNewPwd(String host, String username, int port, String pass);

    /**
     * 获取设备架构信息
     *
     * @param host     设备ip
     * @param username 用户名
     * @param port     连接端口
     * @param pass     密码
     * @return {@link String}
     */
    String getArchInfo(String host, String username, int port, String... pass);

    /**
     * 获取CPU核心数量信息
     *
     * @param host     设备ip
     * @param username 用户名
     * @param port     连接端口
     * @param pass     密码
     * @return int
     */
    int getCpuCoreInfo(String host, String username, int port, String... pass);

    /**
     * 获取操作系统信息
     *
     * @param host     设备ip
     * @param username 用户名
     * @param port     连接端口
     * @param pass     密码
     * @return int
     */
    String getOsInfo(String host, String username, int port, String... pass);

    /**
     * 获取CPU描述信息
     *
     * @param host     设备ip
     * @param username 用户名
     * @param port     连接端口
     * @param pass     密码
     * @return int
     */
    String getCpuDescInfo(String host, String username, int port, String... pass);

    /**
     * 获取内存总量信息
     *
     * @param host     设备ip
     * @param username 用户名
     * @param port     连接端口
     * @param pass     密码
     * @return {@link String}
     */
    String getMemTotalInfo(String host, String username, int port, String... pass);

    /**
     * 获取磁盘总量信息
     *
     * @param host     设备ip
     * @param username 用户名
     * @param port     连接端口
     * @param pass     密码
     * @return {@link String}
     */
    String getDiskTotalInfo(String host, String username, int port, String... pass);

    /**
     * 使用 exec 调用shell脚本
     *
     * @param shellPath 脚本路径
     * @param params    参数
     * @param host      设备ip
     * @return boolean 返回值
     */
    boolean callShellByExec(String host, String shellPath, String[] params);

    /**
     * 钉钉告警发送
     *
     * @param shellPath 脚本路径
     * @param params    脚本参数
     * @return boolean
     */
    boolean callShellByExecWithDingTalk(String shellPath, String[] params);

    /**
     * 指定directive 执行
     *
     * @param shellPath 脚本路径
     * @param params    参数
     * @param directive 命令
     * @return {@link String}
     */
    String callLocalShellByExec(String shellPath, List<String> params, String directive);

    /**
     * 使用 exec 调用shell脚本
     *
     * @param shellPath 脚本路径
     * @param params    参数
     * @return boolean 返回值
     */
    boolean callLocalShellByExec(String shellPath, String[] params);

    /**
     * 执行ping命令判断ip地址是否能联通
     *
     * @param ipAddress ip地址
     * @return boolean
     */
    boolean pingExec(String ipAddress);

    /**
     * 创建目录
     *
     * @param host 设备ip
     * @param path 目录
     */
    void mkdirs(String host, String path);

    /**
     * 创建目录并执行后续命令
     *
     * @param host 设备ip
     * @param path 目录
     * @param cmd  后续命令
     */
    void mkdirsAndOthers(String host, String path, String cmd);

    /**
     * 设备空余磁盘
     *
     * @param host 设备ip
     * @return {@link Double}
     */
    Double deviceFreeDisk(String host);

    /**
     * 查找文件的内容
     *
     * @param host     服务器地址
     * @param filePath 文件路径
     * @param content  查找内容
     * @return {@link List}<{@link String}>
     */
    List<String> findFileText(String host, String filePath, String content);

    /**
     * 获取设备空余端口
     *
     * @param host      设备ip
     * @param startPort 起始端口
     * @param endPort   终止端口
     * @return int
     */
    int getDevicePort(String host, int startPort, int endPort);

    /**
     * 获取设备空余内存
     *
     * @param host 设备ip
     * @return {@link Double}
     */
    Double deviceFreeMem(String host);

    /**
     * 根据架构标识区分系统的架构
     *
     * @return
     */
    String getDeviceArchType();

    /**
     * 获取设备资源信息(内存、磁盘剩余空间和CPU使用率)
     * @param host 设备ip
     * @return 设备资源信息
     */
    Map<String, Double> deviceFreeResource(String host);

    /**
     * 获取设备资源信息(内存、磁盘剩余空间和CPU使用率)
     *
     * @param host 设备ip
     * @return 设备资源信息
     */
    String getDeviceHomeDir(String host);

    /**
     * 获取路径下文件绝对路径列表
     *
     * @param host 设备ip
     * @param path 路径
     * @return {@link List}<{@link String}>
     */
    List<String> getAbsoluteFilePathList(String host, String path);

    /**
     * 检查远程文件是否存在
     *
     * @param host 设备ip
     * @param path 路径
     * @return {@link List}<{@link String}>
     */
    boolean checkRemoteFileExist(String host, String path);

    /**
     * 检查远程目录是否存在
     *
     * @param host 设备ip
     * @param path 路径
     * @return {@link List}<{@link String}>
     */
    boolean checkRemoteDirExist(String host, String path);

    /**
     * 获取文件大小字节数
     *
     * @param host 设备ip
     * @param path 路径
     * @return long
     */
    long getRemoteFileSize(String host, String path);

    /**
     * 远程服务器间文件传输
     *
     * @param source         源设备
     * @param target         目标设备
     * @param sourceFilePath 源文件路径
     * @param targetFilePath 目标文件路径
     * @return {@link String}
     */
    String remoteTransferFile(Device source, Device target, String sourceFilePath, String targetFilePath);
}