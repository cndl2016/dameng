package com.dm.cn.system.constant;

/**
 * 消息提示常量类
 * 系统所有的国际化统一处理
 *
 * @author Auto-Coder
 * @date 2023/02/28
 */
public interface MessageTipConstant {

    String UNKNOWN_FILE_TYPE = "unknown.tip.file.type";
    String UNKNOWN_MODEL_TYPE = "unknown.tip.model.type";

    /**
     * 通用提示
     */
    String COMMON_NOT_DELETE = "message.tip.common.not.delete";
    String COMMON_ID_NOT_NULL = "message.tip.common.id.not.null";
    String COMMON_TREE_ROOT_NOT_NULL = "message.tip.common.tree.root.not.null";
    String COMMON_NOT_SELECT = "message.tip.common.not.select";
    String COMMON_RECORD_NOT_EXIST = "message.tip.common.record.not.exist";
    String COMMON_DUPLICATE_NAME = "message.tip.common.duplicate.name";
    String COMMON_NAME_NOT_NULL = "message.tip.common.name.not.null";
    String COMMON_PREFIX_NOT_NULL = "message.tip.common.prefix.not.null";
    String COMMON_PREFIX_NOT_ERROR = "message.tip.common.prefix.not.error";

    /**
     * 系统提示
     */
    String SYSTEM_USER_NOT_OPERATE_MANAGER = "message.tip.user.not.operate.manager";
    String SYSTEM_USER_ROLE_NOT_OPERATE_MANAGER = "message.tip.user.role.not.operate.manager";
    String SYSTEM_USER_ROLE_NOT_DISABLE = "message.tip.user.role.not.disable";
    String SYSTEM_USER_ROLE_NOT_DELETE = "message.tip.user.role.not.delete";
    String SYSTEM_USER_NO_TOKEN_PROVIDED = "message.tip.user.no.token.provided";
    String SYSTEM_USER_INVALID_TOKEN = "message.tip.user.invalid.token";
    String SYSTEM_HTTP_REQUEST_FAIL = "message.tip.http.request.fail";
    String SYSTEM_USER_PASSWORD_RETRY_LIMIT_EXCEED = "message.tip.user.password.retry.limit.exceed";
    String SYSTEM_USER_PASSWORD_RETRY_LIMIT_COUNT = "message.tip.user.password.retry.limit.count";
    String SYSTEM_USER_ACCOUNT_NOT_EXISTS = "message.tip.user.account.not.exists";
    String SYSTEM_USER_ACCOUNT_STATUS_DELETE = "message.tip.user.account.status.delete";
    String SYSTEM_USER_ACCOUNT_STATUS_DISABLE = "message.tip.user.account.status.disable";
    String SYSTEM_CONFIG_KEY_CANNOT_DELETE = "message.tip.system.config.key.cannot.delete";
    String SYSTEM_USER_DATA_NO_PERMISSION = "message.tip.user.data.no.permission";
    String SYSTEM_ROLE_DATA_NO_PERMISSION = "message.tip.role.data.no.permission";
    String IMPORT_SYSTEM_USER_NAME_EXIST = "message.tip.import.user.name.exist";
    String SYSTEM_USER_PASSWORD_NOT_MATCH = "message.tip.user.password.not.match";
    String SYSTEM_USER_LOGIC_SUCCESS = "message.tip.user.login.success";
    String SYSTEM_USER_JCAPTCHA_EXPIRE = "message.tip.user.jcaptcha.expire";
    String SYSTEM_USER_JCAPTCHA_ERROR = "message.tip.user.jcaptcha.error";
    String SYSTEM_USER_NOT_NULL = "message.tip.user.not.null";
    String SYSTEM_USER_LOGIN_BLOCKED = "message.tip.user.login.blocked";
    String SYSTEM_DOMAIN_USER_EXIST = "message.tip.domain.user.exist";
    String SYSTEM_DOMAIN_NOT_EXIST = "message.tip.domain.not.null";
    String SYSTEM_USER_LIST_EMPTY = "message.tip.user.list.empty";
    String SYSTEM_UNKNOWN_OPERATE_TYPE = "message.tip.unknown.operate.type";
    String SYSTEM_UNKNOWN_PROP_TYPE = "message.tip.unknown.prop.type";
    String SYSTEM_DEPT_USER_DATA_NO_PERMISSION = "message.tip.dept.data.no.permission";
    String SYSTEM_SUPER_DEPT_NOT_EXIST = "message.tip.super.dept.not.exist";
    String SYSTEM_DEPT_DISABLE_NO_ADD = "message.tip.dept.disable.no.add";
    String SYSTEM_DEPT_ADD_FAILED = "message.tip.dept.add.failed";
    String SYSTEM_DEPT_UPDATE_FAILED = "message.tip.dept.update.failed";
    String SYSTEM_DEPT_SUPER_CANNOT_SELF = "message.tip.dept.super.cannot.self";
    String SYSTEM_DEPT_EXIST_SON_NOT_ALLOWED_STOP = "message.tip.dept.exist.son.not.allowed.stop";
    String SYSTEM_DEPT_EXIST_USER_NOT_ALLOWED_STOP = "message.tip.dept.exist.user.not.allowed.stop";
    String SYSTEM_DEPT_EXIST_SON_NOT_ALLOWED_DEL = "message.tip.dept.exist.son.not.allowed.del";
    String SYSTEM_DEPT_EXIST_USER_NOT_ALLOWED_DEL = "message.tip.dept.exist.user.not.allowed.del";
    String SYSTEM_ROLE_ADD_FAILED = "message.tip.role.add.failed";
    String SYSTEM_ROLE_ADD_PERMISSIONS_NOT_CONFIG = "message.tip.role.add.permissions.not.config";
    String SYSTEM_ROLE_UPDATE_FAILED = "message.tip.role.update.failed";
    String SYSTEM_DOMAIN_ADD_FAILED = "message.tip.domain.add.failed";
    String SYSTEM_DOMAIN_UPDATE_FAILED = "message.tip.domain.update.failed";
    String SYSTEM_DOMAIN_CONNECT_FAILED = "message.tip.domain.connect.failed";
    String SYSTEM_USER_ADD_ACCOUNT_EXIST = "message.tip.user.add.account.exist";
    String SYSTEM_USER_ADD_PHONE_EXIST = "message.tip.user.add.phone.exist";
    String SYSTEM_USER_ADD_EMAIL_EXIST = "message.tip.user.add.email.exist";
    String SYSTEM_USER_ADD_DEPT_EXIST = "message.tip.user.add.dept.exist";
    String SYSTEM_USER_UPDATE_PHONE_EXIST = "message.tip.user.update.phone.exist";
    String SYSTEM_USER_UPDATE_EMAIL_EXIST = "message.tip.user.update.email.exist";
    String SYSTEM_USER_UPDATE_DEPT_EXIST = "message.tip.user.update.dept.exist";
    String SYSTEM_USER_NOT_ALLOWED_DEL_SELF = "message.tip.user.not.allowed.del.self";
    String SYSTEM_PERSON_UPDATE_PASS_FAILED = "message.tip.person.update.pass.failed";
    String SYSTEM_PERSON_PASS_SAME = "message.tip.person.pass.same";
    String SYSTEM_PERSON_PIC_FORMAT_WRONG = "message.tip.person.pic.format.wrong";

    /**
     * 设备提示
     */
    String DEVICE_EXIST_CHILD_NODE = "message.tip.device.exist.child.node";
    String DEVICE_EXIST_CHILD_DEVICE = "message.tip.device.exist.child.device";
    String DEVICE_EXIST_CHILD_INSTANCE = "message.tip.device.exist.child.instance";
    String DEVICE_IP_EXIST = "message.tip.device.ip.exist";
    String DEVICE_DISABLED_FOR_TASK = "message.tip.device.disabled.for.task";
    String DEVICE_NOT_EXIST_IN_RANGE = "message.tip.device.not.exist.in.range";
    String DEVICE_PORT_START_END = "message.tip.device.port.start.end";
    String DEVICE_SELECT_ENABLE = "message.tip.device.select.enable";
    String DEVICE_SELECT_ONE_MODEL = "message.tip.device.select.one.model";
    String DEVICE_ALL_NOT_ONLINE = "message.tip.device.all.not.online";
    String DEVICE_CONNECT_PORT_FAILED = "message.tip.device.connect.port.failed";
    String DEVICE_PASS_FAILED = "message.tip.device.pass.failed";

    /**
     * base提示
     */
    String BASE_VERSION_DIR_FAILED = "message.tip.base.version.dir.failed";
    String BASE_VERSION_EXIST = "message.tip.base.version.exist";
    String BASE_VERSION_NOT_EXIST = "message.tip.base.version.not.exist";
    String BASE_VERSION_UPLOAD_FAILED = "message.tip.base.version.upload.failed";
    String BASE_VERSION_DOWNLOAD_FAILED = "message.tip.base.version.download.failed";
    String BASE_VERSION_FILE_TOO_LARGE = "message.tip.base.version.file.too.large";
    String BASE_VERSION_FILE_DECOMPRESS_TIMEOUT = "message.tip.base.version.file.decompress.timeout";
    String BASE_MOUNT_C_GROUP_FAILED = "message.tip.base.mount.c.group.failed";
    String BASE_NO_ROOT_AUTH = "message.tip.base.no.root.auth";
    String BASE_UNSUPPORTED_ARCH_TYPE = "message.tip.base.unsupported.arch.type";
    String BASE_ALERT_CONFIG_PARAM_NOT_NULL = "message.tip.base.alert.config.param.not.null";
    String BASE_INSTANCE_TYPE_NOT_NULL = "message.tip.base.instance.type.not.null";
    String BASE_NO_DEFAULT_PARAM = "message.tip.base.no.default.param";
    String BASE_INSTANCE_NOT_DELETE = "message.tip.base.instance.not.delete";
    String BASE_INSTANCE_NOT_EXIST = "message.tip.base.instance.not.exist";
    String BASE_INSTANCE_DUPLICATE_NAME = "message.tip.base.instance.duplicate.name";
    String BASE_NODE_DUPLICATE_NAME = "message.tip.base.node.duplicate.name";
    String BASE_CREATE_INSTANCE_FAILED = "message.tip.base.create.instance.failed";
    String BASE_PORT_FREEZE = "message.tip.base.port.freeze";
    String BASE_PORT_EXIST = "message.tip.base.port.exist";
    String BASE_STOP_START_SERVER_ERROR = "message.tip.base.stop.start.server.error";
    String BASE_NODE_REGISTER_GOON = "message.tip.base.node.register.goon";
    String BASE_HAND_TYPE_ERROR = "message.tip.base.hand.type.error";
    String BASE_INSTANCE_EXIST = "message.tip.base.instance.exist";
    String BASE_STATUS_ERROR = "message.tip.base.status.error";
    String BASE_MODEL_NOT_EXIST = "message.tip.base.model.not.exist";
    String BASE_SET_DEFAULT_VERSION_SUCCESS = "message.tip.base.set.default.version.success";
    String BASE_SET_VERSION_SUCCESS = "message.tip.base.set.version.success";
    String BASE_SET_VERSION_ERROR = "message.tip.base.set.version.error";
    String BASE_CREATE_NODE_EXPIRED = "message.tip.base.create.node.expired";
    String BASE_CREATE_NODE_FAILED = "message.tip.base.create.node.failed";
    String BASE_DEVICE_RESOURCE_DISABLE = "message.tip.base.device.resource.disable";
    String BASE_TASK_TYPE_NOT_NULL = "message.tip.base.task.type.not.null";
    String BASE_INSTANCE_NUM_NOT_ZERO = "message.tip.base.instance.num.not.zero";
    String BASE_INSTANCE_TYPE_NOT_EXIST = "message.tip.base.instance.type.not.exist";
    String BASE_DEVICE_NODE_START_FAILED = "message.tip.base.device.node.start.failed";
    String BASE_DEVICE_NODE_STOP_FAILED = "message.tip.base.device.node.stop.failed";
    String BASE_NODE_START_SUCCESS = "message.tip.base.node.start.success";
    String BASE_NODE_START_FAILED = "message.tip.base.node.start.failed";
    String BASE_NODE_STOP_SUCCESS = "message.tip.base.node.stop.success";
    String BASE_NODE_STOP_FAILED = "message.tip.base.node.stop.failed";
    String BASE_INSTANCE_NODE_START_SUCCESS = "message.tip.base.instance.node.start.success";
    String BASE_INSTANCE_NODE_START_FAILED = "message.tip.base.instance.node.start.failed";
    String BASE_CREATE_INSTANCE_COMPLETE = "message.tip.base.create.instance.complete";
    String BASE_TASK_CLUSTER_NODE_COMPLETE = "message.tip.base.task.cluster.node.complete";
    String BASE_NUM_CREATE_INSTANCE_FAILED = "message.tip.base.num.create.instance.failed";
    String BASE_DB_PORT_EXIST = "message.tip.base.db.port.exist";
    String BASE_PORT_SPEND = "message.tip.base.port.spend";
    String BASE_MAIL_NOTICE_CONTENT_DEVICE = "message.tip.base.mail.notice.content.device";
    String BASE_MAIL_NOTICE_RELEASE_CONTENT_DEVICE = "message.tip.base.mail.notice.release.content.device";

    /**
     * 常量
     */
    String USER_TYPE_NORMAL = "message.tip.type.normal";
    String USER_TYPE_AD = "message.tip.type.ad";
    String DEVICE = "message.tip.device";
    String NODE = "message.tip.node";
    String GLOBAL_RESOURCE = "message.tip.global.resource";
    String OUT_IMPORT_VERSION = "message.tip.out.import.version";
    String LOGOUT_SUCCESS = "message.tip.logout.success";
    String COMMON_PORT = "message.tip.common.port";
    String COMMON_MEMORY = "message.tip.common.memory";
}