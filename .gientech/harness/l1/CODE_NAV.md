# 代码导航

> 项目：NCDB 管理系统 (manager) v3.8.6

## 代码入口

| 文件 | 路径 | 用途 |
|------|------|------|
| 启动类 | `ncdb-admin/.../NcdbApplication.java` | Spring Boot 应用入口，含 MyBatis-Plus 分页配置、Jasypt、Swagger、AOP 启用 |
| 根 POM | `pom.xml` | 父模块声明与公共依赖管理 |
| 主配置 | `ncdb-admin/src/main/resources/application.yml` | 核心配置（端口、Tomcat、JWT、SSH、日志、Mail、分页、XSS、资源限制等） |
| 数据源配置 | `ncdb-admin/src/main/resources/application-druid.yml` | Druid 连接池与达梦数据库配置 |

## 关键代码路径

### ncdb-admin (Controller 聚合层)

| 路径 | 关键文件 |
|------|----------|
| controller/system/ | `SysUser/ Role/ Menu/ Dept/ DictData/ DictType/ Config/ Domain/ Logininfor/ Operlog/ Profile/ Dashboard/ Token/ Captcha` |
| controller/device/ | `Device/ DeviceNode` |
| controller/base/ | `AlarmMessage/ AlertConfig/ AlertNoticeMessage/ SoftAgentConfig/ SysMessage` |

### ncdb-system (系统业务层)

| 路径 | 关键文件 |
|------|----------|
| service/impl/ | `SysUser/ Role/ Menu/ Dept/ DictData/ DictType/ Config/ OperLog/ LoginInfor/ Domain/ DomainMapping/ MessageTip/ LoginService/ Permission` 实现类 |
| config/security/ | `SecurityConfig/ JwtAuthenticationTokenFilter/ AuthLogic/ AuthenticationEntryPointImpl/ LogoutSuccessHandlerImpl/ CasAuthenticationSuccessHandler` |
| config/security/filter/ | `JwtAuthenticationTokenFilter` JWT 令牌校验过滤器 |
| config/aspectj/ | `LogAspect` 操作日志切面 |
| config/service/ | `UserDetailsServiceImpl/ SysPasswordService` |
| mapper/ | `SysUser/ Role/ Menu/ Dept/ DictData/ DictType/ Config/ OperLog/ Logininfor/ Domain/ DomainMapping/ MessageTip/ RoleMenu/ UserRole` Mapper 接口 |
| entity/server/ | `SysConfig/ Domain/ DomainMapping/ Logininfor/ OperLog/ RoleMenu/ UserOnline/ UserRole/ MessageTip/ UserProp` |
| entity/vo/ | `RouterVO/ MetaVO/ TreeSelectOpt/ UserTreeVO` |
| service/job/ | `DomainUserSyncJob` 域用户同步定时任务 |

### ncdb-common (基础设施层)

| 路径 | 关键文件 |
|------|----------|
| core/domain/ | `SysUser/ Role/ Menu/ Dept/ DictData/ DictType/ LoginUser` 核心领域模型 |
| core/constant/ | `Constants/ UserConstants/ TokenConstants/ CommandConstants/ MgConstants/ NumberConstants/ SymbolConstants/ FileConstants` |
| utils/ssh/ | `SshExecUtil/ SshBuilderConfig` SSH 连接池工具（Apache MINA sshd） |
| utils/server/ | `Server/ Cpu/ Mem/ Jvm/ Sys/ NetWork/ DiskIo/ Arith/ SysFileServer` 服务器监控 |
| security/annotation/ | `RequiresPermissions/ RequiresRoles/ RequiresLogin/ InnerAuth/ Logical` 安全注解 |
| security/aspect/ | `InnerAuthAspect` 内部认证切面 |
| security/utils/ | `SecurityUtils/ SecurityContextHolder` |
| filter/ | `XssFilter/ RepeatableFilter/ RepeatedlyRequestWrapper/ XssHttpServletRequestWrapper/ PropertyPreExcludeFilter` |
| config/ | `CaptchaConfig/ FilterConfig/ ThreadPoolConfig/ InfoConfig/ NcdbApiConfig/ ServerConfig/ KaptchaTextCreator` |
| config/properties/ | `CasProperties/ PermitAllUrlProperties` |
| config/manager/ | `AsyncManager/ ThreadPoolConfig/ ShutdownManager` 异步任务框架 |
| log/annotation/ | `Log` 日志注解定义 |
| exception/ | `GlobalExceptionHandler/ BaseException/ ServiceException/ DemoModeException/ InnerAuthException/ UtilException` |
| exception/user/ | `BlackListException/ CaptchaException/ CaptchaExpireException/ UserException/ UserNotExistsException/ UserPasswordNotMatchException/ UserPasswordRetryLimitExceedException` |
| exception/file/ | `FileException/ FileSizeLimitExceededException/ FileUploadException/ InvalidExtensionException/ FileNameLengthLimitExceededException` |
| utils/ | `CmdUtil/ DecodeUtil/ I18nMessageUtil/ IniConfUtils/ LogUtils/ MessageUtils/ PageUtils/ RestTemplateUtil/ ThreadPoolManager/ Threads` |
| utils/http/ | `HttpHelper/ HttpUtils` |
| utils/ip/ | `IpUtils/ AddressUtils` |
| core/utils/ | `DateUtils/ IdWorker/ SpringUtils/ StringUtils/ ServletUtils/ ManagerStringUtils` |
| core/web/ | `BaseController/ AjaxResult/ TableDataInfo/ TableSupport` |

### ncdb-device (设备管理)

| 路径 | 关键文件 |
|------|----------|
| entity/server/ | `Device/ DeviceNode` 设备实体 |
| entity/vo/ | `DeviceInstanceNumVO/ DeviceMonitorInfoVO/ DeviceNodeVO/ DeviceOptionVO/ DeviceResourceVo/ DeviceTopChartVO/ DeviceNodeCountVO` |
| service/impl/ | `DeviceServiceImpl/ DeviceNodeServiceImpl/ SshdServiceImpl` |
| service/ | `DeviceService/ DeviceNodeService/ SshdService/ DeviceInitService` |
| mapper/ | `DeviceMapper/ DeviceNodeMapper` |
| entity/enums/ | `DeviceMonitorEnum` |

### ncdb-base (基础能力层)

| 路径 | 关键文件 |
|------|----------|
| entity/server/ | `AlarmMessage/ AlertConfig/ AlertNoticeConfig/ AlertNoticeMessage/ AlertRule/ SoftAgentConfig/ SysJobLog/ SysMessage` |
| service/impl/ | `AlarmMessageServiceImpl/ AlertConfigServiceImpl/ AlertRuleServiceImpl/ AlertNoticeConfigServiceImpl/ AlertNoticeMessageServiceImpl/ SoftAgentConfigServiceImpl/ SysMessageServiceImpl/ SysJobLogServiceImpl/ DeviceAlarmServiceImpl/ AlertBaseServiceImpl` |
| service/websocket/ | `DeviceMonitorWebSocketService` 设备监控 WebSocket 实时推送 |
| service/job/ | `DeviceModelMonitorJob/ DeviceOfflineJob/ ClearLogJob` |
| config/ | `WebSocketConfig` |
| mapper/ | `AlarmMessageMapper/ AlertConfigMapper/ AlertRuleMapper/ AlertNoticeConfigMapper/ AlertNoticeMessageMapper/ SoftAgentConfigMapper/ SysMessageMapper/ SysJobLogMapper` |

### ncdb-job (定时任务层)

| 路径 | 关键文件 |
|------|----------|
| — | 当前模块仅有 pom.xml（无 Java 源文件），实际任务在 ncdb-base 和 ncdb-system 中 |

### ncdb-ui (前端)

| 路径 | 关键文件 |
|------|----------|
| src/views/system/ | 用户、角色、菜单、部门、字典等管理页面 |
| src/views/device/ | 设备管理页面 |
| src/views/home/ | 仪表盘/首页 |
| src/views/log/ | 日志查看页面 |
| src/views/enum/ | 枚举管理页面 |
| src/api/ | REST API 请求封装（login, menu, base, device, deviceNode, job, system） |
| src/router/ | 路由配置（静态 + 动态权限路由） |
| src/store/ | Pinia 状态管理 |
| src/directive/ | 自定义指令（permission 权限指令） |
| src/plugins/ | 插件（auth, cache, download, modal, tab 等） |
| src/utils/ | 工具函数（auth, request, validate, permission, dict, jsencrypt 等） |

## 认证授权流程

```
登录请求 → CaptchaController(验证码) → SysLoginService(认证)
  → JwtAuthenticationTokenFilter(Token 校验)
  → PreAuthorizeAspect(@RequiresPermissions 拦截)
  → Controller 方法执行
```

## 操作日志流程

```
@Log 注解 → LogAspect 切面 → 请求参数过滤(排除password等)
  → AsyncManager 异步 → AsyncFactory.recordOper()
  → SysOperLogMapper 写入数据库
```

## 设备 SSH 远程操作流程

```
DeviceController → DeviceServiceImpl → SshdServiceImpl
  → SshExecUtil (SSH 连接池, Apache MINA sshd) → 远程 Shell 脚本执行
  → 结果返回并持久化
```

## WebSocket 实时监控推送流程

```
DeviceMonitorWebSocketService (ncdb-base)
  → WebSocket 会话管理 → 设备监控数据实时推送
  → 前端 DeviceController.getMonitorDataForEchart 消费