# 测试策略

> 项目：NCDB 管理系统 (manager) v3.8.6

## 测试现状

当前项目**未引入单元测试框架与自动化测试基础设施**：
- 所有模块 `pom.xml` 均未声明 `spring-boot-starter-test`、`junit`、`mockito` 等测试依赖
- 所有模块均不存在 `src/test/` 目录及对应的测试源文件
- `ncdb-admin/pom.xml` 配置了 `maven-surefire-plugin` 并设置 `<skipTests>true</skipTests>`
- 前端 `ncdb-ui/package.json` 中未定义任何测试脚本或测试依赖（无 `vitest`、`jest`、`cypress`）
- 当前构建命令 `mvn clean package -DskipTests` 直接跳过测试阶段

## 质量保障方式

### 开发阶段验证
- 本地启动验证：在 IDE 中运行 `NcdbApplication.java`，通过 Swagger UI 手动验证接口
- 前端开发验证：`npm run dev` 启动开发服务器（Vite 热更新），手动验证页面交互
- 数据库初始化验证：执行顺序 SQL 脚本（`initDM.sql` → `initDMData.sql` → `initDM-DevEnv.sql`）验证表结构与初始数据

### 部署验证
- 打包验证：`mvn clean package` 构建生成 `ncdb-admin-package.tar.gz` 部署包，解压后启动验证
- SSH 远程脚本验证：`config/` 目录下的 Shell 脚本通过 SSH 远程调用执行

## 测试建议优先级

| 优先级 | 测试类型 | 框架 | 覆盖范围 |
|--------|----------|------|----------|
| P0 | 后端单元测试 | JUnit 5 + Mockito | ncdb-common 工具类、ncdb-system 核心业务逻辑 |
| P1 | 后端集成测试 | Spring Boot Test + H2 | Mapper 层、Controller 层，利用 H2 内嵌数据库 |
| P2 | 前端自动化测试 | Vitest + Cypress/Playwright | 工具函数、核心页面组件 |
| P3 | 接口与性能测试 | Postman + JMeter | REST API 接口、Tomcat 性能、WebSocket 连接 |

## 核心测试关注点

| 模块 | 关注点 | 涉及技术 |
|------|--------|----------|
| ncdb-admin | 启动入口、Controller 聚合、Swagger 文档 | Spring Boot, Swagger 2 |
| ncdb-system | 用户/角色/菜单/部门 CRUD、字典管理、日志记录、域名管理、消息提示 | MyBatis, PageHelper, CAS |
| ncdb-common | 工具类、常量、异常处理、JWT 工具、SSH 工具、安全注解 | JWT, Hutool, ExpiringMap, Apache MINA sshd |
| ncdb-base | 告警消息、告警配置、告警规则、软探针、WebSocket 推送、定时任务 | WebSocket, Quartz, Mail |
| ncdb-job | 定时任务调度配置、邮件发送 | Quartz, Spring Mail |
| ncdb-device | 设备管理、设备节点、SSH 远程调用、设备监控 | SSH, Shell 脚本, OSHI |
| ncdb-ui | 页面渲染、路由权限、字典回显、文件上传、WebSocket 实时数据 | Vue 3, Element Plus, Pinia, ECharts |