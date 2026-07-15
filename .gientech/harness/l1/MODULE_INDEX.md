# 模块索引

> 项目：NCDB 管理系统 (manager) v3.8.6

## 模块一览

| 模块 | 类型 | 端口 | 直接依赖 | 职责 |
|------|------|------|----------|------|
| ncdb-admin | JAR（Web 启动入口） | 1081 | ncdb-base, jasypt | 启动入口、Controller 聚合层、打包部署 |
| ncdb-base | JAR 业务模块 | — | ncdb-device, spring-boot-starter-websocket | 告警消息、告警配置、告警规则、软探针、WebSocket 推送 |
| ncdb-device | JAR 业务模块 | — | ncdb-system | 设备管理、节点管理、SSH 远程执行 |
| ncdb-system | JAR 业务模块 | — | ncdb-job, cas-client-core, vosk | 用户/角色/菜单/部门/字典/日志/域名管理/消息提示/语音识别 |
| ncdb-job | JAR 业务模块 | — | ncdb-common, quartz, mail | 定时任务调度 |
| ncdb-common | JAR 基础模块 | — | 无（自含所有依赖） | 工具类、核心领域模型、异常、安全注解、过滤器、SSH 工具 |
| ncdb-ui | 前端 SPA | 80 | — | Vue 3 前端界面 |

## 模块依赖关系（自底向上）

```
ncdb-common (无依赖)
    ↑
ncdb-job (依赖 ncdb-common)
    ↑
ncdb-system (依赖 ncdb-job + CAS + Vosk)
    ↑
ncdb-device (依赖 ncdb-system)
    ↑
ncdb-base (依赖 ncdb-device + WebSocket)
    ↑
ncdb-admin (依赖 ncdb-base + 热部署)
```

## 后端 API 路由总表

### 系统管理 (system/)

| 控制器 | 路由 | 主要接口 |
|--------|------|----------|
| SysUserController | `/system/user` | list, getInfo, add, edit, remove, resetPwd, changeStatus, getUserTree, updateLanguage |
| SysRoleController | `/system/role` | list, getInfo, add, edit, changeStatus, remove |
| SysMenuController | `/system/menu` | list, getInfo, treeSelect, roleMenuTreeSelect, add, edit, remove, getRouters |
| SysDeptController | `/system/dept` | list, excludeChild, getInfo, treeSelect, add, edit, remove |
| SysDictDataController | `/system/dict/data` | list, getInfo, add, edit, remove, export |
| SysDictTypeController | `/system/dict/type` | list, getInfo, add, edit, remove, export, optionSelect, refreshCache |
| SysConfigController | `/system/config` | list, getInfo, add, edit, remove, export, refreshCache |
| SysDomainController | `/system/domain` | 域名管理 |
| SysLogininforController | `/system/logininfor` | list, remove, clean, export, unlock |
| SysOperlogController | `/system/operlog` | list, remove, clean, export |
| SysProfileController | `/system/user/profile` | avatar, updatePwd, update, getInfo |
| DashboardController | `/dashboard` | 仪表盘数据 |
| TokenController | `/token` | Token 管理 |
| CaptchaController | `/captchaImage` | 获取验证码 |

### 设备管理 (device/)

| 控制器 | 路由 | 主要接口 |
|--------|------|----------|
| DeviceController | `/device` | getListDevice, getDeviceIpList, enableDevice, removeDeviceById, saveOrUpdateDevice, getMonitorList, getMonitorDataForEchart, testPing, addDevices |
| DeviceNodeController | `/device/node` | 设备节点管理 |

### 基础管理 (base/)

| 控制器 | 路由 | 主要接口 |
|--------|------|----------|
| AlarmMessageController | `/alarmMessage` | 告警消息管理 |
| AlertConfigController | `/alertConfig` | 告警配置管理 |
| AlertNoticeMessageController | `/alertNoticeMessage` | 告警通知消息 |
| SoftAgentConfigController | `/softAgentConfig` | 软探针配置 |
| SysMessageController | `/sysMessage` | 系统消息 |

## 核心领域模型

| 实体 | 所属模块 | 说明 |
|------|----------|------|
| SysUser | ncdb-common | 系统用户 |
| SysRole | ncdb-common | 系统角色 |
| SysMenu | ncdb-common | 系统菜单 |
| SysDept | ncdb-common | 系统部门 |
| SysDictData | ncdb-common | 字典数据 |
| SysDictType | ncdb-common | 字典类型 |
| SysConfig | ncdb-system | 系统配置 |
| SysOperLog | ncdb-common | 操作日志 |
| SysLogininfor | ncdb-common | 登录日志 |
| SysDomain | ncdb-system | 域名实体 |
| SysDomainMapping | ncdb-system | 域名映射 |
| SysMessageTip | ncdb-system | 消息提示 |
| SysUserOnline | ncdb-system | 在线用户 |
| UserProp | ncdb-system | 用户属性 |
| Device | ncdb-device | 设备实体 |
| DeviceNode | ncdb-device | 设备节点 |
| AlarmMessage | ncdb-base | 告警消息 |
| AlertConfig | ncdb-base | 告警配置 |
| AlertRule | ncdb-base | 告警规则 |
| AlertNoticeConfig | ncdb-base | 告警通知配置 |
| AlertNoticeMessage | ncdb-base | 告警通知消息 |
| SoftAgentConfig | ncdb-base | 软探针配置 |
| SysMessage | ncdb-base | 系统消息 |
| SysJobLog | ncdb-base | 任务日志 |