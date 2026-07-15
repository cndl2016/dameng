export const constants = {
    DEBOUNCE_LIMIT: 1000,  // 防止按钮连续点击，设置防抖时间间隔，单位：毫秒
    ADMIN_ID: 1,  // 管理员id
    TOP_DEPT: 0, // 最高部门
    DEFAULT_ID: 0,
    DEFAULT_OPTION: 'all',
    RESPONSE_ERROR_PREFIX: 'Error: ',
    GRAPH_LAYOUT_COLOR: ['89, 140, 255', '82, 189, 130', '79, 195, 247', '232, 117, 117', '255, 183, 77', '255, 241, 118', '121, 134, 203',
        '149, 117, 205', '161, 136, 127', '240, 98, 146', '129, 199, 132', '174, 213, 129', '220, 231, 117', '186, 104, 200',
        '255, 213, 79', '144, 233, 208', '255, 138, 101', '144, 164, 174', '77, 182, 172', '164, 102, 115'], //图布局颜色集合

    OBJECT: "object", // 对象类型
    SLASH: "/", // 斜杠
    TITLE_OPTIONS: [
        '设备维护',
        '版本管理',
        '告警规则',
        '采集频率',
        '登录日志',
        '操作日志',
        '个人信息',
        '用户管理',
        '角色管理',
        '部门管理',
    ], //审计日志功能模块选项
    SUCCESS: 0, // 状态码
    FAILED: 1, // 状态码

    ADD:"add", // 实例编辑类型：添加
    UPDATE:"update", // 实例编辑类型：修改

    START:'start', // 节点操作类型：启动
    STOP:'stop', // 节点操作类型：停止

    MODEL_UNUSED:'0', //模板是否被使用字典，0为未使用
    MODEL_USED:'1', //模板是否被使用字典，1为被使用

    ROOT_DEVICE_PARENT_NODE: 0, // 设备节点默认根节点的父id
    ROOT_DEVICE_NODE: 1, // 设备节点默认根节点id

    TRANSFER_LEFT: 0, // 节点在穿梭框中位于左侧
    TRANSFER_RIGHT: 1, // 节点在穿梭框中位于右侧
    ECHART_COLOR_ARR: [["#3363FF", "rgba(51, 99, 255, 0.1)"], ["#1890FF", "rgba(24, 144, 255, 0.1)"],
    ["#94cc6d", "rgba(148, 204, 109, 0.1)"], ["#4D66C9", "rgba(77, 102, 201, 0.1)"]], // echart图表颜色数组

    SORT_ORDER_SUFFIX: 'ending', // 排序关键字后缀
};

Object.freeze(constants); // 冻结常量对象，使其不可修改
