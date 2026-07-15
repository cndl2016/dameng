package com.dm.cn.common.core.constant;

import java.util.Arrays;
import java.util.List;

/**
 * 命令类
 *
 * @author dameng
 */
public class CommandConstants {

    public static final String GET_VERSION_DIR_COMMAND = "rpm -pql '%s' | head -1";

    public static final String NCDB_OPERATE_FILE = "ps -ef|grep '%s' | grep -v grep | grep -v ncdb-operate-port | grep -v ncdb-start";

    public static final String NCDB_OPERATE_PORT = "netstat -anp| awk '$4 ~ /:%s$/'|grep -v WAIT";

    public static final String NCDB_KILL_PORT = "kill -9 `ps -ef| grep '%s' | grep -v grep | grep -v  ncdb-start  | awk '{print $2} '` ";

    public static final String MKDIR_P = "mkdir -p %s ";

    public static final String RPM_IVH = "rpm --prefix='%s' -ivh '%s/%s' --force";

    /**
     * 解压文件命令
     */
    public static final String TAR_ZXVF = "tar -C '%s' -zxvf '%s'";

    /**
     * 查看解压目录信息
     */
    public static final String TAR_CHECK_DIR = "tar -tzf '%s' | head -1";

    public static final String CHOWN = "chown -R root:root '%s'";

    public static final String RM = "rm %s/%s";

    public static final String ECHO = "echo -e '\n%s' >> %s";

    public static final String ECHO_TEE = "echo \"%s\" | tee %s";

    public static final String SED_I = "sed -i /^%s/d %s";

    public static final String SED_N = "sed -n /^%s/p %s";

    public static final String RM_RF = "rm -rf %s";

    public static final String CHMOD_R_777 = "chmod -R 755 %s";

    public static final String DF_H_TAIL = "df -h | tail -n +2 ";

    public static final String CP_R = "cp -r %s %s";

    public static final String MV = "mv %s %s";

    public static final String UNAME_M = "uname -m";

    public static final String N_PROC = "nproc";

    public static final String GET_OS_INFO = "cat /etc/os-release | grep  -E '^NAME|VERSION'";

    public static final String GET_CPU_DESC_INFO = "cat /proc/cpuinfo | grep 'model name' |uniq";

    public static final String GET_CPU_ARC_INFO = "uname -m";

    public static final String GET_CPU_ARC_INFO_FALLBACK = "lscpu | grep Architecture  | head -n 1 | awk '{print $2 }'";

    public static final String GET_CPU_DESC_INFO_CH_FALLBACK = "lscpu | grep '架构' | head -n 1 | awk '{print $2 }'";

    public static final List<String> GET_CPU_DESC_INFO_LIST = Arrays.asList(
            GET_CPU_DESC_INFO, GET_CPU_ARC_INFO, GET_CPU_ARC_INFO_FALLBACK, GET_CPU_DESC_INFO_CH_FALLBACK);

    public static final String GET_MEM_TOTAL = "free -m | sed -n '2p' | awk '{printf \"%.2f\\n\",($2)/1024}'";

    /**
     * 空闲内存 MEM_FREE= free+available M
     */
    public static final String FREE_M = "free -m | sed -n '2p' | awk '{printf \"MEM_FREE:\"  \"%.2f\\n\",  ($4+$6)/1024}'";

    public static final String FREE_DISK = "df |awk '{a+=$4}END{res = a/1024/1024; printf \"FREE_DISK:\" \"%.2f\\n\", res}'";

    public static final String COMMAND_REDIS_CLI_PASS = "REDISCLI_AUTH";

    /**
     * 查询设备资源命令拼接格式
     */
    public static final String DEVICE_FREE_RESOURCE = "%s;%s;%s";

    /**
     * 获取CPU使用率
     */
    public static final String GET_CPU_USE = "top -bn1 | awk '/^%Cpu/ {match($0, /([0-9.]+) id/, arr); idle = arr[1]; printf \"CPU_USE:%.2f\\n\",100-idle}'";

    /**
     * 获取进程的进程号
     */
    public static final String GET_PROCESS_PID = "ps -ef| grep %s | grep -v grep | awk '{print $2} '";

    /**
     * 使用进程号获取监控信息
     */
    public static final String GET_MONITOR_INFO_BY_PID = "top -bn1 -p %s";

    /**
     * 获取磁盘总量
     */
    public static final String GET_DISK_TOTAL = "df -BG --total | grep total | awk '{printf \"%.2f\\n\", $2}'";

    /**
     * 校验端口号是否可用
     */
    public static final String CHECK_PORT_IN_USE = "netstat -tunlp | grep %s";

    /**
     * 查询目录下文件
     */
    public static final String LS = "ls %s";

    /**
     * 查询目录
     */
    public static final String TEST_D = "test -d '%s' && echo true";

    /**
     * 查询目录下文件
     */
    public static final String CHECK_PYTHON3 = "python3 -V";

    /**
     * 获取用户默认目录
     */
    public static final String GET_DEVICE_USER_HOME = "echo $HOME";

    /**
     * 获取文件大小字节数命令
     */
    public static final String GET_REMOTE_FILE_SIZE = "stat -c %%s %s";

    /**
     * 获取指定路径磁盘剩余可用空间大小字节数命令(KB)
     */
    public static final String GET_PATH_DISK_FREE = "df -P %s | awk 'NR==2 {print $4}'";


}