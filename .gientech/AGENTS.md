# NCDB 管理系统 — AGENTS.md

## 项目概览

- **项目名称**: NCDB 管理系统 (manager)
- **版本**: 3.8.6
- **架构**: Spring Boot 2.6.15 单体架构
- **数据库**: 达梦 DM8（主库）/ H2（开发测试）
- **前端**: Vue 3.4.31 + Vite 5.3.2 + Element Plus 2.9.3

## 模块依赖链（自底向上）

```
ncdb-common → ncdb-job → ncdb-system → ncdb-device → ncdb-base → ncdb-admin
```

## 关键路径

| 模块 | 路径 |
|------|------|
| 启动类 | `ncdb-admin/src/main/java/com/dm/cn/NcdbApplication.java` |
| 根 POM | `pom.xml` |
| 主配置 | `ncdb-admin/src/main/resources/application.yml` |
| 数据源配置 | `ncdb-admin/src/main/resources/application-druid.yml` |
| Controller 层 | `ncdb-admin/src/main/java/com/dm/cn/controller/` |
| 系统业务层 | `ncdb-system/src/main/java/com/dm/cn/system/` |
| 公共模块 | `ncdb-common/src/main/java/com/dm/cn/common/` |
| 设备管理 | `ncdb-device/src/main/java/com/dm/cn/device/` |
| 基础能力 | `ncdb-base/src/main/java/com/dm/cn/base/` |
| 前端源码 | `ncdb-ui/src/` |
| SQL 脚本 | `doc/initDM.sql`, `doc/initDMData.sql`, `doc/initDM-DevEnv.sql` |
| Shell 脚本 | `config/` (server.sh, device_monitor.sh, alarm_message.sh 等) |
| 本地框架 | `framework/` (dm-framework, dm-framework-common, dm-framework-service) |

## 构建与启动

### 首次构建（必须安装本地框架依赖）
```bash
mvn install:install-file -Dfile=framework/dm-framework/1.0.50/dm-framework-1.0.50.pom \
  -DgroupId=com.dm.framework -DartifactId=dm-framework -Dversion=1.0.50 -Dpackaging=pom

mvn install:install-file -Dfile=framework/dm-framework-common/1.0.50/dm-framework-common-1.0.50.jar \
  -DgroupId=com.dm.framework -DartifactId=dm-framework-common -Dversion=1.0.50 -Dpackaging=jar

mvn install:install-file -Dfile=framework/dm-framework-service/1.0.50/dm-framework-service-1.0.50.jar \
  -DgroupId=com.dm.framework -DartifactId=dm-framework-service -Dversion=1.0.50 -Dpackaging=jar
```

### 打包
```bash
mvn clean package -DskipTests
```

### 启动后端
```bash
mvn spring-boot:run -pl ncdb-admin
```

### 启动前端
```bash
cd ncdb-ui && npm install && npm run dev
```

## 代码规范

- 控制器继承 `BaseController`，使用 `@RestController` + `@RequestMapping`
- 接口使用 `@RequiresPermissions` 权限注解
- 修改操作使用 `@Log` 日志注解
- 响应格式：列表 `TableDataInfo`，单条 `AjaxResult`
- 分页查询调用 `startPage()`
- 依赖注入使用 `@Resource`（javax.annotation）
- 业务异常使用 `ServiceException`
- 密码使用 `BCryptPasswordEncoder`
- 配置敏感信息使用 Jasypt 加密 `ENC(...)`

## 测试现状

项目当前**无自动化测试**。所有模块均无 `src/test/` 目录、无测试依赖。`maven-surefire-plugin` 配置了 `<skipTests>true</skipTests>`。