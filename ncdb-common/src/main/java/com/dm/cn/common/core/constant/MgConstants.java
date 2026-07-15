package com.dm.cn.common.core.constant;

/**
 * 消息提示类
 *
 * @author DAMENG
 * @date 2022/08/05
 */
public class MgConstants {
    /**
     * 无需国际化的常量
      */

    public static final String MG_SUCCESS = "success";

    public static final String DING_TALK_SUCCESS_MSG = "\"errmsg\":\"ok\"";

    public static final String SSH_EXEC_FAILED = "%s设备SSH执行命令失败";

    public static final String SFTP_FAILED = "文件传输失败";

    public static final String INVALID_HTTP_TOKEN = "请求地址的token无效!";

    public static final String DB_PARAM_ERROR = "不支持的数据库驱动类：%s";

    public static final String SLOT_DECODE_CHECK_ERROR = "slot char format error";

    public static final String DEVICE_INFO = "CPU:%s,內存:%s,磁盘:%s,上行:%s,下行:%s";

    public static final String UNKNOWN_FILE_TYPE = "不支持解压的文件类型！";

    public static final String DECOMPRESS_DIR_ALREADY_EXISTS = "解压目录冲突！%s目录已存在，请确认目录情况或修改解压目录";

    public static final String DEVICE_IP_EXIST = "设备%s已存在!";

    public static final String DEVICE_NOT_EXIST = "设备%s不存在!";

    public static final String TASK_NAME_EXIST = "任务名称已存在：%s";

    public static final String INSTANCE_NAME_EMPTY = "实例名称不可为空!";

    public static final String INSTANCE_NAME_EXIST = "实例名称：%s，已存在!";

    public static final String INSTANCE_DIR_CONFLICT = "安装目录 %s 存在冲突: %s";

    public static final String INSTANCE_DIR_CONFLICT_INFO = "实例%s下%s服务器目录";

    public static final String VERSION_EXIT_ERROR = "版本不存在：%s";

    public static final String VERSION_SIZE_EMPTY = "版本[%s]异常：版本大小为空";

    public static final String DEVICE_CONN_ERROR = "设备%s连接失败!";

    public static final String DEVICE_PORT_IN_USE = "设备%s下的%s端口号已被占用!";

    public static final String INSTANCE_TYPE_ERROR = "实例类型不可未空!";

    public static final String REDUPLICATE_NAME = "名称重复：%s";

    public static final String REDUPLICATE_FILE = "数据文件已存在：%s下的%s";

    public static final String SET_VERSION_SUCCESS = "任务[%s]：设置版本成功";

    public static final String SET_VERSION_ERROR = "任务[%s]：设置版本异常，请在[版本管理]维护版本";

    public static final String INSTANCE_INFO = "实例创建失败：";

    public static final String TASK_BLOCK_ERROR = "任务[%s]阻塞，执行失败！";

    public static final String GET_DEVICE_ONLINE_ERROR = "设备在线资源查询错误：%s";

    public static final String UPDATE_CONF_ERROR = "配置文件出错:%s";

    public static final String CONF_NOT_EXIST = "配置文件不存在";

    public static final String READ_CONF_ERROR = "读取配置文件失败:%s";

    public static final String INSTANCE_EXIST = "实例存在,无法删除：%s";

    public static final String INSTANCE_NOT_EXIST = "实例不存在";

    public static final String NODE_OPERATE_EMPTY = "节点为空，无法操作";

    public static final String STATUS_ERROR = "任务状态不成功,无法删除：%s";

    public static final String INSTANCE_INSTALL_COMPLETE = "配置文件生成与传输完成，安装流程完毕";

    public static final String INSTANCE_INSTALL_FAILED = "实例安装失败";

    public static final String INSTANCE_INFO_SAVED_COMPLETE = "实例节点信息保存完毕";

    public static final String INSTANCE_CREATE_TASK_COMPLETE = "实例启动成功，创建任务完毕";

    public static final String INSTANCE_UNINSTALL_INVALID_NODE_LIVING = "实例下存在已启动节点，无法卸载";

    public static final String INSTANCE_STARTING = "实例正在启动中，请等待操作完成";

    public static final String INSTANCE_STOPPING = "实例正在停止中，请等待操作完成";

    public static final String INSTANCE_START_TIMEOUT = "实例节点启动超时失败";

    public static final String INSTANCE_STOP_TIMEOUT = "实例节点停止超时失败";

    public static final String INSTANCE_START_FAILED = "实例启动失败";

    public static final String PORT_ENGINE_ROUTER_CONFLICT = "engine节点与router节点端口冲突！";

    public static final String PORT_RANGE_ERROR = "结束端口号应大于开始端口号！";

    public static final String PORT_RANGE_MATCH_ERROR = "端口范围过小！";

    public static final String OFFLINE_AUTO_FAILED = "检测到manager离线，任务失败";

    public static final String DEVICE_PYTHON3_NOT_INSTALLED = "设备 %s 未安装python3";

    public static final String DEVICE_USER_DIFF = "设备 %s 连接用户名不同,无法进行部署";

    public static final String DEVICE_AUTH_DIFF = "设备 %s 连接密码不同,无法进行部署";

    public static final String INSTANCE_OPERATE_FAILED_NOT_FOUND = "操作失败，文件%s不存在";

    public static final String CLUSTER_INSTANCE_HOST_NUM_ERROR = "分布式实例搭建需指定多个节点设备ip";

    public static final String GET_BLOCKED_SIZE_FAILED = "获取数据库页大小错误";

    public static final String PAGE_NUM_CONVERT_ERROR = "页转换错误:%s";

    public static final String QUERY_ID_EMPTY = "执行id不可为空";

    public static final String EXPORT_DATA_NAME_EMPTY = "数据名称不可为空";

    public static final String EXPORT_DATA_FILE_NOT_EXIST = "数据文件不存在";

    public static final String DATA_FILE_TRANSPORT_FILED = "数据文件传输失败";

    public static final String EXPORT_DATA_FILE_EXIST = "目标导出文件已存在";

    public static final String EXPORT_DATA_FILE_DIR_CREATE_FAILED = "目标导出文件目录创建失败";

    public static final String GET_REMOTE_FILE_SIZE_FAILED = "获取远程文件大小失败:%s";

    public static final String ALL_ROUTER_OFFLINE = "实例上router节点均已离线";

    public static final String IMPORT_FAILED_DB_EXIST = "实例 %s 中数据库 %s 已存在，无法导入";

    public static final String IMPORT_FAILED_TABLE_EXIST = "实例 %s 中数据库 %s 下表 %s 已存在，无法导入";

    public static final String IMPORT_FAILED_TARGET_DB_NOT_EXIST = "实例 %s 中目标数据库 %s 不存在，无法导入CSV";

    public static final String IMPORT_FAILED_TARGET_TABLE_NOT_EXIST = "实例 %s 中目标数据库 %s 下目标表 %s 不存在，无法导入CSV";

    public static final String MAKE_TEMP_DIR_FAILED = "创建临时目录%s失败";

    public static final String GET_PATH_DISK_FAILED = "获取磁盘空间大小失败";

    public static final String EXPORT_FAILED_MANAGER_DISK_EXCEED = "manager服务磁盘空间不足,无法导出";

    public static final String DATA_FILE_UPLOAD_DIFF = "文件上传结果存在差异";

    public static final String EXPORT_TASK_START = "导出任务开始,导出命令执行中";

    public static final String EXPORT_COMMAND_SUCCESS = "导出命令执行成功，开始传输文件";

    public static final String EXPORT_TRANSMIT_SUCCESS = "文件传输成功，导出任务结束";

    public static final String IMPORT_TASK_START = "导入任务开始,文件传输中";

    public static final String IMPORT_TRANSMIT_SUCCESS = "文件传输成功，开始执行导入命令";

    public static final String IMPORT_COMMAND_SUCCESS = "导入命令执行成功，导入任务结束";

    public static final String IMPORT_DATA_IN_USE = "文件正在导入,请稍后再试";

    public static final String CSV_EXPORT_TABLE_LIMIT = "只支持单张表作为csv导出对象";

    public static final String UNSUPPORTED_EXPORT_IMPORT_FILE_TYPE = "不支持的导入导出文件类型";

    public static final String GET_TABLE_COUNT_FAILED = "获取表数据信息失败";

    public static final String EXPORT_EMPTY_TABLE = "目标表数据为空,无法导出csv";

    public static final String BASE_MAIL_NOTICE_CONTENT_DEVICE = "尊敬的客户您好：设备[%s]产生[%s]级别告警：[%s]，请尽快处理";

    public static final String BASE_MAIL_NOTICE_RELEASE_CONTENT_DEVICE = "尊敬的客户您好：设备[%s]上[%s]的告警已经解除";

}