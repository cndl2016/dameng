# 开发环境配置

> 项目：NCDB 管理系统 (manager) v3.8.6

## 环境要求

| 工具 | 版本 | 检查命令 |
|------|------|----------|
| JDK | 17+ | `java -version` |
| Maven | 3.x | `mvn -v` |
| Node.js | >= 18.0.0 | `node -v` |
| 数据库 | 达梦 DM8 | — |

## 本地依赖安装

先安装本地框架依赖（否则 Maven 编译会失败）：

```bash
# 安装 dm-framework POM
mvn install:install-file -Dfile=framework/dm-framework/1.0.50/dm-framework-1.0.50.pom \
  -DgroupId=com.dm.framework -DartifactId=dm-framework -Dversion=1.0.50 -Dpackaging=pom

# 安装 dm-framework-common JAR
mvn install:install-file -Dfile=framework/dm-framework-common/1.0.50/dm-framework-common-1.0.50.jar \
  -DgroupId=com.dm.framework -DartifactId=dm-framework-common -Dversion=1.0.50 -Dpackaging=jar

# 安装 dm-framework-service JAR
mvn install:install-file -Dfile=framework/dm-framework-service/1.0.50/dm-framework-service-1.0.50.jar \
  -DgroupId=com.dm.framework -DartifactId=dm-framework-service -Dversion=1.0.50 -Dpackaging=jar
```

## 构建命令

```bash
# 打包（跳过测试，生成 ncdb-admin-package.tar.gz）
mvn clean package -DskipTests

# 启动后端（开发模式，热部署开启）
mvn spring-boot:run -pl ncdb-admin
# 或
java -jar ncdb-admin/target/ncdb-admin.jar

# 启动前端
cd ncdb-ui
npm install
npm run dev
```

## 数据库初始化

按顺序执行 `doc/` 目录下的 SQL 脚本：

1. `doc/initDM.sql` — 初始化基础表结构
2. `doc/initDMData.sql` — 初始化基础数据
3. `doc/initDM-DevEnv.sql` — 初始化开发环境数据

默认数据源配置：
- URL: `jdbc:dm://localhost:5236?schema=TEST_SCAFFOLD`
- 用户名: SYSDBA
- 密码: Dameng@8888（已用 Jasypt 加密）

## 关键配置

| 配置文件 | 作用 |
|----------|------|
| `ncdb-admin/src/main/resources/application.yml` | 主配置文件 |
| `ncdb-admin/src/main/resources/application-druid.yml` | 数据源配置 |
| `ncdb-ui/.env.development` | 前端开发环境配置 |
| `ncdb-ui/vite.config.js` | Vite 构建配置 |

## 辅助脚本

| 脚本 | 路径 | 作用 |
|------|------|------|
| 服务管理 | `config/server.sh` | 启动/停止/重启/查看状态 |
| 设备监控 | `config/device_monitor.sh` | SSH 远程采集 CPU/内存/磁盘/网络 |
| 告警消息 | `config/alarm_message.sh` | 告警消息推送 |
| 端口扫描 | `config/port_range.sh` | 获取服务可用端口范围 |
| 文件写入 | `config/write_file.sh` | 远程文件内容新增或修改 |
| Toml 写入 | `config/toml_write_file.sh` | 远程 toml 配置文件修改 |
| 配置加密 | `config/encryptor/encryptor.sh` | Jasypt 敏感信息加密 |
| 创建 root 用户 | `config/createRootUser.sh` | 创建 root 用户脚本 |