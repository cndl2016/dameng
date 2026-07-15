# NCDB 管理系统 — L1 项目全景概览

> 自动生成时间：2026-07-15
> 项目版本：3.8.6

## 项目定位

**NCDB 管理系统**（内部名：manager）是一个基于 Spring Boot 2.6.15 单体架构的达梦数据库设备管理与运维监控系统。支持 SSH 远程操作、资源监控、告警管理、WebSocket 实时推送等功能。

## 核心特性

| 特性 | 说明 |
|------|------|
| 单体架构 | 易维护易部署，所有模块运行在同一 JVM 进程 |
| SSH 远程操作 | 通过 SSH 远程调用设备 Shell 脚本，实现设备配置、升级与监控 |
| 达梦 DM8 数据库 | 主存储为达梦 DM8，通过 MyBatis-Plus 进行数据访问，支持 H2 嵌入式切换 |
| 设备全生命周期管理 | 支持设备注册、节点管理、资源监控、告警配置、拓扑展示 |
| CAS 单点登录 | 可选集成 CAS 认证协议，支持语音识别（Vosk） |
| Prometheus 监控 | 内置 Prometheus 与 Grafana 监控组件管理能力 |
| WebSocket 实时推送 | 设备监控数据通过 WebSocket 实时推送到前端 |

## 技术栈

| 层次 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 2.6.15 |
| ORM | MyBatis-Plus | 3.5.6 |
| 数据库 | 达梦 DM8 / H2 | 8.1.3.62 / 2.2.224 |
| 连接池 | Druid | 1.2.16 |
| 认证 | Spring Security + JWT / CAS | — |
| 配置加密 | Jasypt | 3.0.3 (PBEWithMD5AndDES) |
| 分页 | PageHelper | 6.1.0 |
| SSH | Apache MINA sshd | 2.9.3 |
| 前端 | Vue 3.4.31 + Vite 5.3.2 + Element Plus 2.9.3 | — |
| Java | JDK | 17 |
| 本地框架 | dm-framework-common | 1.0.50 |

## 项目结构

```
dameng/
├── ncdb-admin/       # Web 入口，Controller 聚合层，打包部署入口
├── ncdb-base/        # 基础公共能力（告警消息、告警配置、软探针、WebSocket）
├── ncdb-device/      # 设备管理、设备节点、SSH 远程执行
├── ncdb-system/      # 系统管理（用户/角色/菜单/部门/字典/日志/域名/消息提示）
├── ncdb-job/         # 定时任务调度（Quartz + Mail）
├── ncdb-common/      # 通用工具、常量、异常、安全注解、核心领域模型、过滤器
└── ncdb-ui/          # 前端 UI（Vue 3 + Vite + Element Plus）
```

## 依赖链（自底向上）

```
ncdb-common (无依赖)
    ↑
ncdb-job (依赖 ncdb-common)
    ↑
ncdb-system (依赖 ncdb-job)
    ↑
ncdb-device (依赖 ncdb-system)
    ↑
ncdb-base (依赖 ncdb-device)
    ↑
ncdb-admin (依赖 ncdb-base + jasypt)
```

## 关键配置

| 配置项 | 值 |
|--------|-----|
| 服务端口 | 1081 |
| 数据库 URL | `jdbc:dm://localhost:5236?schema=TEST_SCAFFOLD` |
| Tomcat 最大线程 | 800 |
| JWT 有效期 | 300 分钟 |
| SSH 连接池 | 最大 10000 连接，每 key 500 |
| 文件上传限制 | 单文件最大 10GB |

## 相关文档

- [模块索引](MODULE_INDEX.md)
- [测试策略](TEST_STRATEGY.md)
- [开发环境](DEV_ENV.md)
- [代码导航](CODE_NAV.md)
- [质量门禁](QUALITY_GATES.md)