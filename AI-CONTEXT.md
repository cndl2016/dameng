# AI 上下文文档

> 填写后放置于项目根目录，供 AI 生成代码时参考。
> 上传外网前务必脱敏：删除内网 IP、密钥、真实人名、数据库连接串。

---

## 1. 项目基础信息

| 项目 | 内容 |
|------|------|
| 项目名称 | 达梦 NCDB 管理系统（XXX 管理系统） |
| 项目版本 | 后端 3.8.6 / 前端 3.5.0 |
| 前端框架 | Vue 3.4.31 |
| 脚本语言 | JavaScript（ES2020+），非 TypeScript |
| 构建工具 | Vite 5.3.2 |
| 包管理器 | npm |
| 后端技术栈 | Spring Boot 2.6.15 + Java 17 + Maven + DM8 数据库 + MyBatis + PageHelper |
| Node 版本要求 | >= 18.0.0（Vite 5 官方要求） |

### 路径别名
```
~           -> 项目根目录（ncdb-ui/）
@/          -> src/
```

---

## 2. 组件清单

### 2.1 第三方组件库
- **Element Plus**：^2.9.3，全量引入，中文语言包 `zh-cn`，全局 size 取 Cookie `size` 或 `default`
- **@smallwei/avue**：^3.7.4，按需场景使用
- **@element-plus/icons-vue**：^2.3.1，Element Plus 官方图标
- **vue-element-plus-x**：^1.2.0，扩展组件

### 2.2 自定义全局组件

#### 组件名：Pagination
- **路径**：`@/components/Pagination/index.vue`
- **用途**：统一分页组件，基于 `el-pagination`
- **Props**：`total`、`page`、`limit`、`pageSizes`、`layout`、`background`、`autoScroll`、`hidden`
- **使用示例**：`<pagination v-show="total > 10" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />`

#### 组件名：CustomTable
- **路径**：`@/components/CustomTable/index.vue`
- **用途**：通过配置项渲染表格，支持选择列、序号列、枚举/字典/时间/按钮列
- **Props**：`dataInfo`、`config`、`total`、`pageParams`、`showSelection`、`showIndex`、`showPagination`、`showTable`
- **Slots**：`column.slotName` 对应的具名插槽
- **Events**：`buttonCommand(command, row)`、`paginationChange()`

#### 组件名：DictTag
- **路径**：`@/components/DictTag/index.vue`
- **用途**：根据字典数据渲染标签
- **Props**：`options`（字典数组）、`value`

#### 组件名：RightToolbar
- **路径**：`@/components/RightToolbar/index.vue`
- **用途**：表格右上角工具栏（刷新、显隐列、搜索切换等）

#### 组件名：TreeSelect
- **路径**：`@/components/TreeSelect/index.vue`
- **用途**：树形下拉选择封装

#### 组件名：FileUpload / ImageUpload / ImagePreview / Editor
- **路径**：`@/components/xxx/index.vue`
- **用途**：文件/图片上传、图片预览、富文本编辑器封装

#### 组件名：SvgIcon
- **路径**：`@/components/SvgIcon/index.vue`
- **用途**：SVG 图标组件，图标源位于 `@/assets/icons/svg`
- **使用示例**：`<svg-icon icon-class="dm-prompt-info" />`

### 2.3 组件使用规则
- 表格优先使用 `el-table` 或 `CustomTable`，分页统一使用 `Pagination`
- 操作按钮权限统一使用 `v-hasPermi="['module:func:action']"`
- 全局挂载方法通过 `getCurrentInstance().proxy` 访问，如 `proxy.$modal`、`proxy.parseTime`、`proxy.addDateRange`
- 字典回显优先使用 `dict-tag` 组件或 `dictFormat` 方法

---

## 3. 样式规范

### 3.1 设计 Token
```scss
// 主色（Element Plus 默认）
$--color-primary: #409EFF;
$--color-success: #67C23A;
$--color-warning: #E6A23C;
$--color-danger: #F56C6C;
$--color-info: #909399;

// 侧边栏（浅色主题默认）
$base-menu-color: rgba(0, 0, 0, 0.7);
$base-menu-light-color: rgba(0, 0, 0, 0.7);
$base-menu-background: #ffffff;
$base-sub-menu-background: #1f2d3d;
$base-sub-menu-hover: #001528;
$base-sidebar-width: 200px;

// 基础色
$blue: #324157;
$light-blue: #3A71A8;
$red: #C03639;
$green: #30B08F;
$yellow: #FEC171;
```

### 3.2 布局规范
- 侧边栏宽度：`200px`
- 顶部导航高度：由 `layout/Navbar.vue` 自适应
- 内容区 class：`app-container`，内部常用 `tab-container` 做页面标题区
- 表格区域常用 class：`ncdb-tb`、`my-table`
- 页面内间距以 Element Plus 的 `el-row :gutter="20"` 为主

### 3.3 CSS 方案
- 预处理器：SCSS
- 全局变量文件：`src/assets/styles/variables.scss`、`src/assets/styles/variables.module.scss`
- 组件样式隔离方式：`scoped`，部分组件使用 `:deep()` 覆盖 Element Plus 样式
- 全局样式入口：`src/assets/styles/index.scss`、`common.scss`、`global.scss`、`table.scss`

---

## 4. 目录结构与命名约定

### 4.1 目录结构
```
ncdb-ui/src/
  api/              # 接口定义（按模块分子目录）
  assets/           # 静态资源（icons、images、styles、font、lottie）
  components/       # 全局组件（PascalCase 目录）
  directive/        # 自定义指令
  layout/           # 布局组件
  plugins/          # 全局插件（$modal、$tab、$auth、$cache、$download）
  router/           # 路由配置
  store/            # Pinia 状态（modules/ 按功能拆分）
  utils/            # 工具函数
  views/            # 页面组件（按业务模块分子目录）
```

### 4.2 命名约定
- 页面组件：PascalCase，如 `User/index.vue`、`Role/index.vue`
- 业务组件：PascalCase 目录 + `index.vue`
- 接口文件：小写，与模块对应，如 `user.js`、`dept.js`
- 枚举文件：`PascalCase` + `Enum`，如 `statusEnum.js`
- Pinia Store：`useXxxStore`，如 `useUserStore`
- 工具函数：camelCase，如 `parseTime`、`addDateRange`

---

## 5. 状态管理规范

- **方案**：Pinia 2.1.7
- **Store 组织**：按模块拆分至 `src/store/modules/`
- **核心 Store**：
  - `useUserStore`：token、用户信息、角色、权限、登录/登出
  - `usePermissionStore`：动态路由生成、侧边栏路由
  - `useSettingsStore`：主题、布局配置、标题
  - `useDictStore`：字典缓存
  - `useAppStore`：侧边栏折叠、设备类型
  - `useTagsViewStore`：标签页
- **页面局部状态**：使用 `ref` / `reactive`，不滥用全局 Store

---

## 6. API 与数据请求规范

### 6.1 HTTP 封装
- 封装文件位置：`src/utils/request.js`
- 使用方式：
  ```js
  import request from '@/utils/request'
  export function listUser(query) {
    return request({ url: '/system/user/list', method: 'get', params: query })
  }
  ```
- 默认 `baseURL` 取 `import.meta.env.VITE_APP_BASE_API`
- 开发环境代理前缀 `/dev-api`，生产环境 `/prod-api`

### 6.2 统一返回格式
```js
{
  code: 200,      // 200 为成功，401 未登录，500 服务端错误，601 业务提示
  data: {},       // 单条/树形/字典等数据
  rows: [],       // 列表数据
  total: 0,       // 列表总条数
  msg: '消息'
}
```

### 6.3 拦截器行为
- 请求拦截：自动附加 `Authorization: Bearer token`；GET 参数序列化；POST/PUT 防重复提交（1 秒内相同数据视为重复）
- 响应拦截：`code === 401` 弹出重新登录；`code === 500` 错误提示；`code === 601` 警告提示
- 通用下载方法：`download(url, params, filename, config)`，基于 `file-saver`

### 6.4 接口函数命名约定
| 操作 | 命名方式 | 示例 |
|------|----------|------|
| 查询列表 | listXxx | listUser |
| 查询详情 | getXxx | getUser |
| 新增 | addXxx | addUser |
| 修改 | updateXxx | updateUser |
| 删除 | delXxx | delUser |
| 状态修改 | changeXxxStatus | changeUserStatus |
| 重置密码 | resetXxxPwd | resetUserPwd |
| 导出 | exportXxx | exportUser |

---

## 7. 路由与权限

- **路由模式**：History（`createWebHistory`）
- **配置文件位置**：`src/router/index.js`
- **路由结构**：
  - `constantRoutes`：公共路由（登录、首页、404、401、个人中心）
  - `dynamicRoutes`：需要权限的动态路由（如字典数据、设备监控详情页）
  - 动态菜单通过 `getRouters()` 从后端获取并递归转换为组件
- **权限控制方式**：
  - 路由守卫 `permission.js`：判断 token、拉取用户信息、生成动态路由
  - 指令 `v-hasPermi="['system:user:add']"`：按钮级权限
  - 函数 `checkPermission(value)` 或 `hasPermission(value)`：组件内权限判断
- **菜单与路由映射**：后端返回 `component` 字段为 `'Layout'` / `'ParentView'` / `'InnerLink'` 或具体 `views/xxx` 路径

---

## 8. 类型定义规范

- 项目为 JavaScript，无 TypeScript 类型文件
- 通用数据约定：
  ```js
  // 分页请求参数
  {
    pageNum: 1,
    pageSize: 10
  }

  // 分页响应数据
  {
    rows: [],
    total: 0
  }
  ```
- 是否自动生成 API 类型：否

---

## 9. 工具函数与常量

### 9.1 工具函数位置
- `src/utils/common.js`：高频工具函数
- `src/utils/validate.js`：URL、邮箱等校验
- `src/utils/auth.js`：Token 读写
- `src/utils/request.js`：HTTP 请求与下载
- `src/utils/dict.js`：字典获取与缓存
- `src/utils/enum.js`：枚举工厂函数 `createEnum`

### 9.2 高频函数清单
| 函数名 | 用途 | 示例 |
|--------|------|------|
| parseTime | 日期格式化 | parseTime(new Date(), '{y}-{m}-{d} {h}:{i}:{s}') |
| addDateRange | 给查询参数添加 beginTime/endTime | proxy.addDateRange(queryParams, dateRange) |
| handleTree | 数组转树 | handleTree(list, 'id', 'parentId') |
| resetForm | 重置 el-form | proxy.resetForm('queryForm') |
| tansParams | 对象转 URL 查询字符串 | tansParams(params) |
| dictFormat | 字典值转标签 | dictFormat(value, dictArray) |
| selectDictLabel | 字典回显（兼容对象数组） | selectDictLabel(datas, value) |
| getNormalPath | 路径规范化 | getNormalPath(p) |

### 9.3 常量枚举
- 枚举位置：`src/views/enum/system/`
- 常用枚举示例：
  ```js
  // src/views/enum/system/statusEnum.js
  import { createEnum } from '@/utils/enum'
  export default createEnum({
    ENABLE: ['0', '启用'],
    DISABLE: ['1', '停用'],
  })
  ```

### 9.4 外部工具库
- `axios`：HTTP 请求
- `js-cookie`：Cookie 读写
- `file-saver`：前端文件下载
- `nprogress`：路由切换进度条
- `fuse.js`：模糊搜索
- `screenfull`：全屏
- `spark-md5`：文件 MD5
- `dayjs`：未显式引入，日期格式化使用 `parseTime`

---

## 10. 代码风格约束

- `<script setup>`：必须使用 Composition API 风格，页面组件声明 `name` 属性，如 `<script setup name="User">`
- TypeScript 严格模式：否（项目为 JS）
- 全局方法/属性通过 `getCurrentInstance().proxy` 访问，如 `const { proxy } = getCurrentInstance()`
- 引号：字符串优先双引号（项目代码中模板与 JS 均以双引号为主）
- 分号：JS 代码中分号可选，项目以无分号为主
- 最大行宽：无强制约束
- 注释要求：复杂业务函数建议加简短注释

---

## 11. 典型页面示例

### 11.1 列表页示例
> 以 `views/system/user/index.vue` 为代表，包含左侧部门树、搜索表单、表格、分页、抽屉表单。

```vue
<script setup name="User">
import { listUser, delUser, updateUser, addUser, changeUserStatus } from "@/api/system/user";
import statusEnum from "@/views/enum/system/statusEnum";
import { constants } from '@/utils/constants';

const { proxy } = getCurrentInstance();
const { sys_normal_disable } = proxy.useDict("sys_normal_disable");

const loading = ref(true);
const total = ref(0);
const userList = ref([]);
const open = ref(false);
const title = ref("");
const dateRange = ref([]);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    userName: undefined,
    status: undefined
  },
  rules: {
    nickName: [{ required: true, message: "用户昵称不能为空", trigger: "change" }]
  }
});
const { queryParams, form, rules } = toRefs(data);

function getList() {
  loading.value = true;
  listUser(proxy.addDateRange(queryParams.value, dateRange.value))
    .then(res => {
      userList.value = res.rows;
      total.value = res.total;
    })
    .finally(() => loading.value = false);
}

function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

function resetQuery() {
  dateRange.value = [];
  proxy.resetForm("queryForm");
  handleQuery();
}

function handleDelete(row) {
  proxy.$modal.confirm('是否确认删除该用户？').then(() => delUser(row.userId))
    .then(() => { getList(); proxy.$modal.msgSuccess("删除成功"); });
}

function handleStatusChange(row) {
  const text = statusEnum.getDescFromValue(row.status);
  proxy.$modal.confirm(`确认要${text}"${row.userName}"用户吗?`).then(() => {
    return changeUserStatus(row.userId, row.userName, row.status);
  }).then(() => proxy.$modal.msgSuccess(text + "成功"));
}

getList();
</script>
```

### 11.2 表单页示例
> 新增/编辑使用 `el-drawer`，表单校验后提交。

```vue
<template>
  <el-drawer :title="title" v-model="open" append-to-body :size="560" :close-on-click-modal="false">
    <div class="drawer__content">
      <el-form ref="userRef" :model="form" :rules="rules" label-width="auto">
        <el-form-item label="用户姓名" prop="nickName">
          <el-input v-model="form.nickName" placeholder="请输入用户姓名" />
        </el-form-item>
      </el-form>
    </div>
    <div class="drawer__footer">
      <el-button @click="cancel">取 消</el-button>
      <el-button type="primary" @click="submitForm">确 定</el-button>
    </div>
  </el-drawer>
</template>

<script setup>
function submitForm() {
  proxy.$refs.userRef.validate(valid => {
    if (!valid) return;
    const api = form.value.userId ? updateUser : addUser;
    api(form.value).then(() => {
      proxy.$modal.msgSuccess(form.value.userId ? "修改成功" : "新增成功");
      open.value = false;
      getList();
    });
  });
}

function cancel() {
  open.value = false;
  reset();
}

function reset() {
  form.value = {};
  proxy.resetForm("userRef");
}
</script>
```

---

## 12. 特殊约定

### 12.1 国际化
- 方案：无（Element Plus 使用中文语言包，业务文本全部硬编码中文）

### 12.2 图标
- 方案：SVG Sprite + Element Plus 图标
- SVG 图标：通过 `vite-plugin-svg-icons` 注册，使用 `<svg-icon icon-class="xxx" />`
- Element 图标：直接在组件属性中使用，如 `icon="Search"`、`prefix-icon="Calendar"`

### 12.3 其他特殊封装
- 图表库：ECharts 6、D3 7、AntV L7、Three.js、Cytoscape
- 富文本编辑器：`@vueup/vue-quill`
- 代码编辑器：CodeMirror 5/6（JSON、JavaScript 语言包）
- 地图：Leaflet、three-globe
- 文件上传：自定义 `FileUpload`、`ImageUpload`
- 消息通知：自定义 `customMessage` 组件
- 防重复提交：`request.js` 中基于 `cache.session` 实现 1 秒去重

---

## 13. 生成提示词模板（可选）

基于以上上下文，向 AI 提问时可以使用以下模板：

```
基于以下项目规范，生成一个 [功能名称] 页面：

技术栈：Vue 3 + JavaScript + Pinia + Element Plus + Vite
页面类型：[列表页 / 表单页 / 详情页 / 仪表盘]
需要使用组件：[Pagination, CustomTable, DictTag, el-drawer, el-tree]
接口：[listXxx, getXxx, addXxx, updateXxx, delXxx]
特殊要求：[如需要左侧树、日期范围查询、状态开关、权限按钮 v-hasPermi、抽屉表单等]

项目上下文见 AI-CONTEXT.md。
```

---

## 14. 后端补充信息（供全栈开发参考）

- **启动类**：`ncdb-admin/src/main/java/com/dm/cn/NcdbApplication.java`
- **核心模块**：
  - `ncdb-admin`：Web 入口、Controller 聚合
  - `ncdb-system`：系统管理（用户/角色/菜单/部门/字典/日志）
  - `ncdb-base`：基础公共能力
  - `ncdb-common`：通用工具、常量、异常
  - `ncdb-job`：定时任务
  - `ncdb-device`：设备管理
- **安全**：Spring Security + JWT，Token 有效期 300 分钟
- **ORM**：MyBatis，Mapper 文件位于 `classpath*:mapper/**/*Mapper.xml`
- **分页**：PageHelper，方言 mysql
- **数据库**：达梦 DM8（通过 dm-framework 与 druid 连接）
- **配置文件**：`ncdb-admin/src/main/resources/application.yml`、`application-druid.yml`
- **打包部署**：`mvn package` 后生成 `ncdb-admin-package.tar.gz`，解压后包含 `lib/` 与 `config/`
