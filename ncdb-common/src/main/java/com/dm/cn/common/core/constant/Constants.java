package com.dm.cn.common.core.constant;

/**
 * 通用常量信息
 *
 * @author dameng
 */
public class Constants {

    /**
     * http请求
     */
    public static final String HTTP = "http://";

    /**
     * https请求
     */
    public static final String HTTPS = "https://";

    /**
     * ldap
     */
    public static final String LDAP = "ldap://%s:%s";

    /**
     * 参数管理 cache key
     */
    public static final String SYS_CONFIG_KEY = "sys_config:";

    /**
     * 字典管理 cache key
     */
    public static final String SYS_DICT_KEY = "sys_dict:";

    public static final String JPG = "JPG";

    public static final String PNG = "PNG";

    public static final String MARK = "注：";

    /**
     * 外部导入版本
     */
    public static final String OUT_IMPORT_VERSION = "外部导入版本";

    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    public static final String GBK = "GBK";

    /**
     * 停止
     */
    public static final String STOP = "stop";

    public static final String STOP_CH = "停止";

    /**
     * 启动
     */
    public static final String START = "start";

    public static final String START_CH = "启动";

    public static final String BLANK = "";

    public static final String BLANK_SPACE = " ";

    public static final String SH = "sh";

    public static final String BASH_SPACE = "/bin/bash ";

    public static final String CODE = "code";

    public static final String STR_NULL = "null";

    public static final String QUERY_PARAM_BEGIN_TIME = "beginTime";

    public static final String QUERY_PARAM_END_TIME = "endTime";

    public static final String NODE_STATUS = "nodeStatus";

    /**
     * 缓存设备监控信息key名称
     * <p>
     * 占位符：设备ip
     */
    public static final String CACHE_DEVICE_MONITOR = "%s_DEVICE_MONITOR";

    /**
     * 缓存设备信息key名称
     * <p>
     * 占位符：设备ip
     */
    public static final String CACHE_DEVICE_INFO = "%s_DEVICE_INFO_SSH";

    public static final String IP_PORT_FORMAT = "%s:%s";

    public static final String FILE_SEPARATOR = "file.separator";

    public static final String SAVE = "save";

    public static final String CPU_USE = "CPU_USE";

    public static final String CPU_CORE = "CPU_CORE";

    public static final String MEM_TOTAL = "MEM_TOTAL";

    public static final String MEM_USED = "MEM_USED";

    public static final String TRAFFIC_IN = "TRAFFIC_IN";

    public static final String TRAFFIC_OUT = "TRAFFIC_OUT";

    public static final String MEM_FREE = "MEM_FREE";

    public static final String MEM_USE = "MEM_USE";

    public static final String DISK_USED = "DISK_USED";

    public static final String KEY_DISK_IO = "diskName";

    public static final String SAVE_OR_UPDATE_SINGLE = "saveOrUpdateSingle";

    public static final String SAVE_OR_UPDATE_NODE = "saveOrUpdateNode";

    public static final String REMOVE_NODE = "removeNodeById";

    public static final String SAVE_CH = "保存";

    public static final String START_SYMBOL = "#//";

    public static final String START_SYMBOL_URL = "http";

    public static final String UNKNOWN = "unknown";

    public static final String ASCENDING = "ascending";

    public static final String DESCENDING = "descending";

    /**
     * sudo命令
     */
    public static final String SUDO = "sudo ";

    public static final String DELETE_METHOD = "DELETE";

    public static final String USER_NAME = "username";

    /**
     * Long路径
     */
    public static final String PATH_LONG = "java.lang.Long";
    /**
     * HashMap路径
     */
    public static final String PATH_HASH_MAP = "java.util.HashMap";

    /**
     * cpu使用
     */
    public static final String CPU_USING_FIELD = "cpuUsing";

    /**
     * 内存使用
     */
    public static final String MEM_USING_INFO_FIELD = "memUsingInfo";

    /**
     * 磁盘使用
     */
    public static final String DISK_USING_FIELD = "diskUsing";

    /**
     * 网络入口
     */
    public static final String NETWORK_IN_FIELD = "networkIn";

    /**
     * 网络出口
     */
    public static final String NETWORK_OUT_FIELD = "networkOut";

    /**
     * 空闲内存大小
     */
    public static final String FREE_MEMORY_SIZE_FIELD = "freeMemorySize";

    /**
     * 网络流量单位
     */
    public static final String NETWORK_UNIT = "kb/s";

    /**
     * 磁盘余量
     */
    public static final String MONITOR_FREE_DISK = "FREE_DISK";

    /**
     * 告警常量key-告警列表数据
     */
    public static final String TABLE_MESSAGE = "tableMessage";

    /**
     * 告警常量key-告警总数
     */
    public static final String TOTAL_NUM = "totalNum";

    /**
     * 告警常量key-提醒告警总数
     */
    public static final String WARN_NUM = "warnNum";

    /**
     * 告警常量key-严重告警总数
     */
    public static final String SERIOUS_NUM = "seriousNum";

    /**
     * 全部数据权限
     */
    public static final String DATA_SCOPE_ALL = "1";

    /**
     * 本部门数据权限
     */
    public static final String DATA_SCOPE_DEPT = "3";

    /**
     * 本部门及以下数据权限
     */
    public static final String DATA_SCOPE_DEPT_ALL = "4";

    /**
     * 仅本人数据权限
     */
    public static final String DATA_SCOPE_SELF = "5";

    public static final String CPU = "CPU";

    public static final String MEMORY = "內存";

    public static final String DISK = "磁盘";

    public static final String ZH = "zh";
    public static final String ZH_CN = "zh-CN";

    public static final String EN = "en";
    public static final String EN_US = "en-US";

    /**
     * 缓存运维平台id的可以
     */
    public static final String CACHE_SYS_MANAGER_ID = "SYS_MANAGER_ID";

    /**
     * is前缀
     */
    public static final String IS_PREFIX = "is";

    /**
     * get前缀
     */
    public static final String GET_PREFIX = "get";

    /**
     * 定时任务英文
     */
    public static final String SCHEDULED_TASK = "Scheduled Task";

    /**
     * 返回数据常量
     */
    public static final String OK = "OK";

    /**
     * load average
     */
    public static final String LOAD_AVERAGE = "load average: ";

    /**
     * 磁盘剩余
     */
    public static final String DISK_FREE = "FREE_DISK";

    /**
     * 设备类型告警标志
     */
    public static final String DEVICE_ALERT = "device";

    /**
     * 告警规则对象id字段名称
     */
    public static final String ALERT_TABLE_FOREIGN = "OBJECT_ID";

    /**
     * 实例告警资源名称
     */
    public static final String INSTANCE_ALERT_RESOURCE_NAME = "实例:%s 节点:%s";

    /**
     * 实例进程监控标题
     */
    public static final String INSTANCE_PID_MONITOR_TITLE = "实例%s下进程号为%s的进程";

    /**
     * 实例TopSql监控标题
     */
    public static final String INSTANCE_QUERY_ID_MONITOR_TITLE = "实例%s下执行id为%s的语句";

    /**
     * 节点监控标题
     */
    public static final String NODE_MONITOR_TITLE = "实例%s下%s节点%s:%s";

    /**
     * 实例数据库监控标题
     */
    public static final String INSTANCE_DB_MONITOR_TITLE = "实例%s下%s数据库";

    /**
     * 通用监控缓存key
     */
    public static final String COMMON_MONITOR_KEY = "%s.%s";

    /**
     * 实例数据库监控key
     */
    public static final String INSTANCE_DB_MONITOR_KEY = "%s.%s.%s";

    /**
     * 邮件标题
     */
    public static final String MAIL_TITLE = "告警提示";
}