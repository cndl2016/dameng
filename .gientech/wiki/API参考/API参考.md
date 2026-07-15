# API 参考

> 本文档汇总 NCDB 平台后端所有 API 模块的接口概览，包括认证授权、系统管理、设备管理、基础模块、定时任务五大模块的 Controller 层接口定义、请求方式、路径前缀等。

---

## 项目概述

- **项目名称**：NCDB（Network Configuration DataBase）服务模块
- **基础包路径**：`com.dm.cn`
- **启动入口**：[NcdbApplication.java](../../../ncdb-admin/src/main/java/com/dm/cn/NcdbApplication.java)
- **服务端口**：`1081`
- **上下文路径**：`/`（根路径）
- **Swagger 接口文档**：已启用，请求前缀为 `/dev-api`
- **认证方式**：Token 认证（Header 名：`Authorization`）
- **数据源**：达梦数据库（DM8），支持 MyBatis-Plus 分页
- **后端日志路径**：`/home/ncdb-admin/logs`

> 来源：[application.yml](../../../ncdb-admin/src/main/resources/application.yml)（端口、上下文、Swagger、Token 配置）；[README.md](../../../README.md)（项目架构说明）；[NcdbApplication.java](../../../ncdb-admin/src/main/java/com/dm/cn/NcdbApplication.java)（启动类、包扫描配置）

---

## 一、认证授权模块

认证授权模块提供用户登录、验证码生成、Token 颁发等接口，无统一路径前缀（直接映射至根路径）。

### 1.1 Token 管理

**Controller**：[TokenController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/system/TokenController.java)

| 接口路径 | 请求方式 | 功能说明 | 参数 |
|---------|---------|---------|------|
| `/login` | POST | 用户登录，返回 Token | `LoginBody`（username, password, code, uuid, language） |
| `/getToken` | POST | 获取 Token（无验证码模式） | `LoginBody`（username, password, language） |

### 1.2 验证码

**Controller**：[CaptchaController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/system/CaptchaController.java)

| 接口路径 | 请求方式 | 功能说明 |
|---------|---------|---------|
| `/captchaImage` | GET | 生成验证码图片 |

> 验证码类型（`application.yml` 配置）：`math`（数字计算）或 `char`（字符验证）

---

## 二、系统管理模块

系统管理模块提供用户、角色、菜单、部门、字典、参数配置、日志等基础管理功能，路径前缀统一为 `/system/`。

### 2.1 用户管理

**Controller**：[SysUserController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/system/SysUserController.java)（`@RequestMapping("/system/user")`）

| 接口路径 | 请求方式 | 功能说明 | 权限标识 |
|---------|---------|---------|---------|
| `/system/user/list` | GET | 获取用户列表（分页） | `system:user:list` |
| `/system/user/getInfo` | GET | 获取当前登录用户信息 | — |
| `/system/user/` 或 `/{userId}` | GET | 根据用户编号获取详细信息 | `system:user:list` |
| `/system/user` | POST | 新增用户 | `system:user:add` |
| `/system/user` | PUT | 修改用户 | `system:user:edit` |
| `/system/user/{userIds}` | DELETE | 删除用户 | `system:user:remove` |
| `/system/user/resetPwd` | PUT | 重置密码 | `system:user:resetPwd` |
| `/system/user/changeStatus` | PUT | 状态修改（启用/禁用） | `system:user:edit` |
| `/system/user/getUserTree` | POST | 获取用户列表树 | — |
| `/system/user/updateLanguage/{language}` | PUT | 语种切换 | — |

### 2.2 角色管理

**Controller**：[SysRoleController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/system/SysRoleController.java)（`@RequestMapping("/system/role")`）

| 接口路径 | 请求方式 | 功能说明 | 权限标识 |
|---------|---------|---------|---------|
| `/system/role/list` | GET | 根据条件分页查询角色数据 | `system:role:list` |
| `/system/role/{roleId}` | GET | 根据角色编号获取详细信息 | — |
| `/system/role` | POST | 新增角色 | `system:role:add` |
| `/system/role` | PUT | 修改保存角色 | `system:role:edit` |
| `/system/role/changeStatus` | PUT | 状态修改 | `system:role:edit` |
| `/system/role/{roleIds}` | DELETE | 删除角色 | `system:role:remove` |

### 2.3 菜单管理

**Controller**：[SysMenuController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/system/SysMenuController.java)（`@RequestMapping("/system/menu")`）

| 接口路径 | 请求方式 | 功能说明 | 权限标识 |
|---------|---------|---------|---------|
| `/system/menu/list` | GET | 获取菜单列表 | `system:menu:list` |
| `/system/menu/{menuId}` | GET | 根据菜单编号获取详细信息 | `system:menu:list` |
| `/system/menu/treeSelect` | GET | 获取菜单下拉树列表 | — |
| `/system/menu/roleMenuTreeSelect/{roleId}` | GET | 加载对应角色菜单列表树 | — |
| `/system/menu` | POST | 新增菜单 | `system:menu:add` |
| `/system/menu` | PUT | 修改菜单 | `system:menu:edit` |
| `/system/menu/{menuId}` | DELETE | 删除菜单 | `system:menu:remove` |
| `/system/menu/getRouters` | GET | 获取路由信息 | — |

### 2.4 部门管理

**Controller**：[SysDeptController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/system/SysDeptController.java)（`@RequestMapping("/system/dept")`）

| 接口路径 | 请求方式 | 功能说明 | 权限标识 |
|---------|---------|---------|---------|
| `/system/dept/list` | GET | 获取部门列表 | `system:dept:list` |
| `/system/dept/list/exclude/{deptId}` | GET | 查询部门列表（排除节点） | `system:dept:list` |
| `/system/dept/{deptId}` | GET | 根据部门编号获取详细信息 | — |
| `/system/dept/treeSelect` | GET | 获取部门下拉树列表 | — |
| `/system/dept` | POST | 新增部门 | `system:dept:add` |
| `/system/dept` | PUT | 修改部门 | `system:dept:edit` |
| `/system/dept/{deptId}` | DELETE | 删除部门 | `system:dept:remove` |

### 2.5 参数配置

**Controller**：[SysConfigController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/system/SysConfigController.java)（`@RequestMapping("/system/config")`）

| 接口路径 | 请求方式 | 功能说明 | 权限标识 |
|---------|---------|---------|---------|
| `/system/config/list` | GET | 查询参数配置列表 | `system:config:list` |
| `/system/config/export` | POST | 导出参数配置列表 | `system:config:export` |
| `/system/config/{configId}` | GET | 根据参数编号获取详细信息 | — |
| `/system/config/configKey/{configKey}` | GET | 根据参数键名查询参数值 | — |
| `/system/config` | POST | 新增参数配置 | `system:config:add` |
| `/system/config` | PUT | 修改参数配置 | `system:config:edit` |
| `/system/config/{configIds}` | DELETE | 删除参数配置 | `system:config:remove` |
| `/system/config/refreshCache` | DELETE | 刷新参数缓存 | `system:config:remove` |

### 2.6 字典管理

**字典数据** — [SysDictDataController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/system/SysDictDataController.java)（`@RequestMapping("/system/dict/data")`）

| 接口路径 | 请求方式 | 功能说明 | 权限标识 |
|---------|---------|---------|---------|
| `/system/dict/data/list` | GET | 根据条件分页查询字典数据 | `system:dict:list` |
| `/system/dict/data/export` | POST | 导出字典数据 | `system:dict:export` |
| `/system/dict/data/{dictCode}` | GET | 根据字典数据ID查询信息 | `system:dict:list` |
| `/system/dict/data/type/{dictType}` | GET | 根据字典类型查询字典数据 | — |
| `/system/dict/data` | POST | 新增保存字典数据信息 | `system:dict:add` |
| `/system/dict/data` | PUT | 修改保存字典数据信息 | `system:dict:edit` |
| `/system/dict/data/{dictCodes}` | DELETE | 批量删除字典数据信息 | `system:dict:remove` |

**字典类型** — [SysDictTypeController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/system/SysDictTypeController.java)（`@RequestMapping("/system/dict/type")`）

| 接口路径 | 请求方式 | 功能说明 | 权限标识 |
|---------|---------|---------|---------|
| `/system/dict/type/list` | GET | 根据条件分页查询字典类型 | `system:dict:list` |
| `/system/dict/type/export` | POST | 导出字典类型 | `system:dict:export` |
| `/system/dict/type/{dictId}` | GET | 根据字典类型ID查询信息 | `system:dict:list` |
| `/system/dict/type` | POST | 新增保存字典类型信息 | `system:dict:add` |
| `/system/dict/type` | PUT | 修改保存字典类型信息 | `system:dict:edit` |
| `/system/dict/type/{dictIds}` | DELETE | 批量删除字典信息 | `system:dict:remove` |
| `/system/dict/type/refreshCache` | DELETE | 刷新字典缓存 | `system:dict:refresh` |
| `/system/dict/type/optionselect` | GET | 获取字典选择框列表 | — |

### 2.7 域管理

**Controller**：[SysDomainController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/system/SysDomainController.java)（`@RequestMapping("/system/domain")`）

| 接口路径 | 请求方式 | 功能说明 | 权限标识 |
|---------|---------|---------|---------|
| `/system/domain/list` | GET | 获取域列表 | `system:domain:list` |
| `/system/domain/` 或 `/{domainId}` | GET | 根据用户编号获取详细信息 | — |
| `/system/domain` | POST | 新增域 | `system:domain:add` |
| `/system/domain` | PUT | 修改域 | `system:domain:edit` |
| `/system/domain/{domainId}` | DELETE | 删除域 | `system:domain:delete` |
| `/system/domain/getDomainUser/{domainId}` | GET | 获取域用户选项 | — |
| `/system/domain/importDomainUser` | POST | 导入域用户 | `system:domain:import` |
| `/system/domain/syncDomainUser` | POST | 同步域用户属性 | `system:domain:sync` |
| `/system/domain/handleSyncTask` | POST | 同步任务 | `system:domain:sync` |
| `/system/domain/getSyncUserList/{domainId}` | GET | 获取域内的用户列表 | — |

### 2.8 日志管理

**登录日志** — [SysLogininforController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/system/SysLogininforController.java)（`@RequestMapping("/system/loginInfo")`）

| 接口路径 | 请求方式 | 功能说明 | 权限标识 |
|---------|---------|---------|---------|
| `/system/loginInfo/list` | GET | 查询系统登录日志集合 | `log:loginInfo:list` |
| `/system/loginInfo/export` | POST | 导出系统登录日志集合 | `log:loginInfo:export` |
| `/system/loginInfo/{infoIds}` | DELETE | 批量删除系统登录日志 | `log:loginInfo:remove` |
| `/system/loginInfo/clean` | DELETE | 清空系统登录日志 | `log:loginInfo:remove` |

**操作日志** — [SysOperlogController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/system/SysOperlogController.java)（`@RequestMapping("/system/opLog")`）

| 接口路径 | 请求方式 | 功能说明 | 权限标识 |
|---------|---------|---------|---------|
| `/system/opLog/list` | GET | 查询系统操作日志 | `log:opLog:list` |
| `/system/opLog/export` | POST | 导出系统操作日志 | `log:opLog:export` |
| `/system/opLog/{operIds}` | DELETE | 批量删除系统操作日志 | `log:opLog:remove` |
| `/system/opLog/clean` | DELETE | 清空操作日志 | `log:opLog:remove` |

### 2.9 个人信息管理

**Controller**：[SysProfileController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/system/SysProfileController.java)（`@RequestMapping("/system/user/profile")`）

| 接口路径 | 请求方式 | 功能说明 |
|---------|---------|---------|
| `/system/user/profile` | GET | 获取个人信息 |
| `/system/user/profile` | PUT | 修改用户 |
| `/system/user/profile/updatePwd` | PUT | 重置密码 |
| `/system/user/profile/avatar` | POST | 头像上传 |

### 2.10 首页面板

**Controller**：[DashboardController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/system/DashboardController.java)（`@RequestMapping("/system/dashboard")`）

| 接口路径 | 请求方式 | 功能说明 |
|---------|---------|---------|
| `/system/dashboard/alarmLog` | POST | 首页告警信息统计数据 |
| `/system/dashboard/getDeviceTopChart` | GET | 首页获取设备Top信息展示 |

---

## 三、设备管理模块

设备管理模块提供设备与设备节点的管理、监控、拓扑展示等功能，路径前缀统一为 `/device/`。

### 3.1 设备管理

**Controller**：[DeviceController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/device/DeviceController.java)（`@RequestMapping("/device")`）

| 接口路径 | 请求方式 | 功能说明 | 权限标识 |
|---------|---------|---------|---------|
| `/device/getListDevice` | GET | 查询设备列表 | — |
| `/device/getDeviceIpList` | GET | 获取在线设备IP列表 | — |
| `/device/enableDevice` | PUT | 设备启用/禁用 | `device:edit` |
| `/device/{id}` | DELETE | 删除设备 | `device:remove` |
| `/device/saveOrUpdateDevice` | POST | 保存或更新设备 | `device:add` / `device:edit` |
| `/device/getMonitorList` | POST | 获取设备监控列表 | — |
| `/device/getMonitorDataForEchart` | POST | 获取设备监控Echart数据 | — |
| `/device/testPing` | POST | 测试连接 | — |
| `/device/addDevices` | POST | 设备批量新增 | — |

### 3.2 设备节点管理

**Controller**：[DeviceNodeController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/device/DeviceNodeController.java)（`@RequestMapping("/device/deviceNode")`）

| 接口路径 | 请求方式 | 功能说明 | 权限标识 |
|---------|---------|---------|---------|
| `/device/deviceNode/getDeviceNodeTree` | POST | 设备节点树查询 | — |
| `/device/deviceNode/saveOrUpdateDeviceNode` | POST | 保存或修改设备节点 | `device:node:add` / `device:node:edit` |
| `/device/deviceNode/deleteDeviceNode/{id}` | DELETE | 删除设备节点 | `device:node:remove` |
| `/device/deviceNode/getDeviceMonitorTree` | POST | 设备监控节点树 | — |
| `/device/deviceNode/getDeviceOnlineTree` | GET | 在线设备节点树查询 | — |
| `/device/deviceNode/getRegionTree` | POST | 两地三中心部署用地域节点树查询 | — |

---

## 四、基础模块

基础模块提供告警配置、通知方式、采集频率、告警历史等基础功能，路径前缀统一为 `/base/`。

### 4.1 告警规则配置

**Controller**：[AlertConfigController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/base/AlertConfigController.java)（`@RequestMapping("/base/alert/config")`）

| 接口路径 | 请求方式 | 功能说明 | 权限标识 |
|---------|---------|---------|---------|
| `/base/alert/config/list` | GET | 根据告警对象ID获取指定的告警配置 | — |
| `/base/alert/config/add` | POST | 保存告警规则 | `device:alarm:config` |
| `/base/alert/config/enableConfig` | PUT | 修改告警规则启用状态 | `device:alarm:config` |
| `/base/alert/config/apply` | POST | 批量应用告警规则 | `device:alarm:apply` |
| `/base/alert/config/loadDeviceCache` | POST | 刷新设备告警配置缓存数据 | — |

### 4.2 告警通知方式管理

**Controller**：[AlarmMessageController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/base/AlarmMessageController.java)（`@RequestMapping("/base/alarmMessage")`）

| 接口路径 | 请求方式 | 功能说明 |
|---------|---------|---------|
| `/base/alarmMessage/getAll` | POST | 获取所有的告警通知方式信息 |
| `/base/alarmMessage/add` | POST | 保存告警通知方式 |

### 4.3 告警通知消息记录

**Controller**：[AlertNoticeMessageController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/base/AlertNoticeMessageController.java)（`@RequestMapping("/base/alert/notice/message")`）

| 接口路径 | 请求方式 | 功能说明 | 权限标识 |
|---------|---------|---------|---------|
| `/base/alert/notice/message/list` | GET | 告警通知消息记录分页查询 | — |
| `/base/alert/notice/message/export` | POST | 告警通知消息记录导出 | `log:alarm:list:export` |

### 4.4 采集频率管理

**Controller**：[SoftAgentConfigController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/base/SoftAgentConfigController.java)（`@RequestMapping("/base/agent")`）

| 接口路径 | 请求方式 | 功能说明 | 权限标识 |
|---------|---------|---------|---------|
| `/base/agent/getAgentConfigPage` | GET | 采集频率列表查询 | — |
| `/base/agent/update` | POST | 修改采集频率 | `base:agent:edit` |

### 4.5 告警历史管理

**Controller**：[SysMessageController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/base/SysMessageController.java)（`@RequestMapping("/base/sysMessage")`）

| 接口路径 | 请求方式 | 功能说明 | 权限标识 |
|---------|---------|---------|---------|
| `/base/sysMessage/getPageList` | GET | 查询告警历史记录 | — |
| `/base/sysMessage/exportSysMessage` | POST | 告警历史记录导出 | `log:alarm:list:export` |

---

## 五、定时任务模块

定时任务模块基于 Quartz 框架实现任务调度，相关代码位于 `ncdb-job` 模块中。

在 `ncdb-admin` 工程中未找到定时任务相关的 Controller 接口文件。定时任务的后台调度逻辑封装在 ncdb-job 模块的 `quartz` 包下，包括任务管理（[SysJob](../../../ncdb-job/src/main/java/com/dm/cn/quartz/entity/server/SysJob.java)、[SysJobService](../../../ncdb-job/src/main/java/com/dm/cn/quartz/service/impl/SysJobServiceImpl.java)）和任务日志（[QuartzJobLog](../../../ncdb-job/src/main/java/com/dm/cn/quartz/entity/server/QuartzJobLog.java)、[QuartzJobLogService](../../../ncdb-job/src/main/java/com/dm/cn/quartz/service/impl/QuartzJobLogServiceImpl.java)）等。

> 根据工程结构推断，定时任务的前端 API 调用可能通过 ncdb-ui 前端项目中 `src/api/job/` 目录下的接口定义进行，需结合前端调用代码进一步确认具体接口路径。

---

## 模块结构总览

```
com.dm.cn.controller
├── base/                          # 基础模块 (/base/)
│   ├── AlertConfigController.java      # 告警规则配置
│   ├── AlarmMessageController.java     # 告警通知方式
│   ├── AlertNoticeMessageController.java # 告警通知消息记录
│   ├── SoftAgentConfigController.java  # 采集频率管理
│   └── SysMessageController.java       # 告警历史
├── device/                        # 设备管理模块 (/device/)
│   ├── DeviceController.java           # 设备管理
│   └── DeviceNodeController.java       # 设备节点管理
└── system/                        # 系统管理模块 (/system/)
    ├── CaptchaController.java          # 验证码
    ├── TokenController.java            # Token 认证
    ├── DashboardController.java        # 首页面板
    ├── SysUserController.java          # 用户管理
    ├── SysRoleController.java          # 角色管理
    ├── SysMenuController.java          # 菜单管理
    ├── SysDeptController.java          # 部门管理
    ├── SysConfigController.java        # 参数配置
    ├── SysDictDataController.java      # 字典数据
    ├── SysDictTypeController.java      # 字典类型
    ├── SysDomainController.java        # 域管理
    ├── SysLogininforController.java    # 登录日志
    ├── SysOperlogController.java       # 操作日志
    └── SysProfileController.java       # 个人信息
```

---

## 接口通用说明

### 请求/响应格式

- **请求体**：POST/PUT 请求使用 `application/json` 格式的 JSON 请求体
- **响应格式**：统一返回 `AjaxResult` 或 `TableDataInfo` 对象
  - `AjaxResult`：`{ code, msg, data }`
  - `TableDataInfo`：`{ code, msg, total, rows }`（分页查询）

### 认证方式

- 接口使用 Token 认证，在请求头中携带 `Authorization: Bearer {token}`
- 部分接口需配合 `@RequiresPermissions` 注解进行权限校验，权限标识符格式为 `模块:功能:操作`（如 `system:user:list`）

### 分页参数

- 分页查询通过 `startPage()` 方法自动从请求参数中获取分页信息
- 前端需传递 `pageNum`（页码）和 `pageSize`（每页条数）参数

---

## 本文档引用的文件

| 文件路径 | 用途 |
|---------|------|
| [README.md](../../../README.md) | 项目架构说明 |
| [application.yml](../../../ncdb-admin/src/main/resources/application.yml) | 服务端口、Swagger、Token 配置 |
| [NcdbApplication.java](../../../ncdb-admin/src/main/java/com/dm/cn/NcdbApplication.java) | 启动入口、包扫描 |
| [SwaggerConfig.java](../../../ncdb-admin/src/main/java/com/dm/cn/config/SwaggerConfig.java) | Swagger 配置 |
| [TokenController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/system/TokenController.java) | 认证接口 |
| [CaptchaController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/system/CaptchaController.java) | 验证码接口 |
| [SysUserController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/system/SysUserController.java) | 用户管理接口 |
| [SysRoleController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/system/SysRoleController.java) | 角色管理接口 |
| [SysMenuController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/system/SysMenuController.java) | 菜单管理接口 |
| [SysDeptController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/system/SysDeptController.java) | 部门管理接口 |
| [SysConfigController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/system/SysConfigController.java) | 参数配置接口 |
| [SysDictDataController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/system/SysDictDataController.java) | 字典数据接口 |
| [SysDictTypeController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/system/SysDictTypeController.java) | 字典类型接口 |
| [SysDomainController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/system/SysDomainController.java) | 域管理接口 |
| [SysLogininforController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/system/SysLogininforController.java) | 登录日志接口 |
| [SysOperlogController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/system/SysOperlogController.java) | 操作日志接口 |
| [SysProfileController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/system/SysProfileController.java) | 个人信息接口 |
| [DashboardController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/system/DashboardController.java) | 首页面板接口 |
| [DeviceController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/device/DeviceController.java) | 设备管理接口 |
| [DeviceNodeController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/device/DeviceNodeController.java) | 设备节点管理接口 |
| [AlertConfigController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/base/AlertConfigController.java) | 告警规则配置接口 |
| [AlarmMessageController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/base/AlarmMessageController.java) | 告警通知方式接口 |
| [AlertNoticeMessageController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/base/AlertNoticeMessageController.java) | 告警通知消息记录接口 |
| [SoftAgentConfigController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/base/SoftAgentConfigController.java) | 采集频率管理接口 |
| [SysMessageController.java](../../../ncdb-admin/src/main/java/com/dm/cn/controller/base/SysMessageController.java) | 告警历史接口 |