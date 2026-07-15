# 质量门禁

> 项目：NCDB 管理系统 (manager) v3.8.6

## 构建门禁

| 检查项 | 命令 | 通过标准 | 严重级别 |
|--------|------|----------|----------|
| Maven 编译 | `mvn compile` | 编译成功，无错误 | 阻塞 |
| Maven 打包 | `mvn package -DskipTests` | 打包成功，生成 JAR + tar.gz | 阻塞 |
| 后端启动 | `mvn spring-boot:run -pl ncdb-admin` | 启动成功，端口 1081 监听 | 阻塞 |
| 前端构建 | `cd ncdb-ui && npm run build:prod` | 构建成功，无报错 | 阻塞 |
| 前端 Dev Server | `cd ncdb-ui && npm run dev` | 启动成功，端口 80 监听 | 高 |
| 本地依赖 | `mvn install:install-file` 安装 dm-framework 三组件 | 本地仓库存在对应 JAR/POM | 阻塞 |

## 代码质量门禁

| 检查项 | 标准 | 严重级别 |
|--------|------|----------|
| 控制器规范 | 继承 `BaseController`，使用 `@RestController` + `@RequestMapping` | 高 |
| 权限注解 | 接口标注 `@RequiresPermissions` | 高 |
| 日志注解 | 修改操作标注 `@Log` | 中 |
| API 文档 | 接口标注 `@ApiOperation` | 中 |
| 响应格式 | 列表返回 `TableDataInfo`，单条返回 `AjaxResult` | 高 |
| 分页查询 | 调用 `startPage()` 开启分页 | 高 |
| 依赖注入 | 使用 `@Resource`（javax.annotation）而非 `@Autowired` | 中 |
| 异常处理 | 业务异常使用 `ServiceException` | 中 |
| 密码加密 | 使用 `BCryptPasswordEncoder` | 高 |
| 敏感信息 | 配置文件中密码使用 Jasypt 加密 `ENC(...)` | 高 |
| 热部署 | 开发环境开启 `spring.devtools.restart.enabled: true` | 低 |

## 安全门禁

| 检查项 | 标准 | 严重级别 |
|--------|------|----------|
| JWT 认证 | Token 有效期合理（当前 300 分钟） | 高 |
| 密码策略 | 最大错误次数 5 次，锁定 10 分钟 | 中 |
| 验证码 | 登录接口需验证码校验（支持 math/char 两种模式） | 中 |
| XSS 过滤 | XSS 过滤器已启用，排除 `/system/notice` | 高 |
| 重复提交 | 重复提交过滤器已启用 | 低 |
| 匿名访问 | 使用 `@Anonymous` 控制匿名接口 | 高 |
| 内部认证 | 内部调用使用 `@InnerAuth` 注解保护 | 高 |
| 内部 IP 校验 | `@EncryptIp` 注解校验调用来源 IP | 中 |

## 性能门禁

| 检查项 | 当前配置 | 参考标准 |
|--------|----------|----------|
| Tomcat 最大线程 | 800 | >= 200 |
| Tomcat 最小空闲线程 | 100 | >= 10 |
| 连接排队数 | 1000 | >= 100 |
| 数据库连接池最大活跃 | 20 | >= 10 |
| 文件上传大小 | 10GB | 根据业务需求 |
| JVM 堆内存 | Xms512m / Xmx1024m | 根据服务器配置 |
| SSH 连接池最大连接 | 10000 | >= 100 |

## 数据库门禁

| 检查项 | 标准 |
|--------|------|
| SQL 脚本 | 按顺序执行 `initDM.sql` → `initDMData.sql` → `initDM-DevEnv.sql` |
| 连接验证 | `validationQuery: SELECT 1 FROM DUAL` |
| 慢 SQL 阈值 | 5000ms |
| 连接池监控 | Druid StatViewServlet 已启用（admin/123456） |

## 部署门禁

| 检查项 | 标准 |
|--------|------|
| 环境检查 | JDK 17、达梦 DM8 服务、SSH 免密登录 |
| 本地依赖 | dm-framework / dm-framework-common / dm-framework-service 已安装到本地仓库 |
| 部署包 | `ncdb-admin-package.tar.gz` 包含 `lib/`、`config/`、`ncdb-admin.jar` |
| 服务管理 | 使用 `config/server.sh` 管理启停 |
| 日志路径 | 部署后日志生成在 `./logs/ncdb-admin/` |