<!--域管理页面 wys  2024/10/30-->
<template>
  <div>
    <!-- 域总数 -->
    <div class="tab-container">
      <div class="total-desc">
        <div class="menu-name">域管理</div>
      </div>
    </div>
    <!--页面主内容-->
    <div class="app-container">
      <div class="ncdb-tb">
        <!--搜索表单 && 操作按钮-->
        <div class="top">
          <!--搜索表单-->
          <el-form :model="queryParams" ref="queryForm" :inline="true" @submit.native.prevent>
            <el-form-item>
              <el-date-picker v-model="dateRange" value-format="YYYY-MM-DD" type="daterange" range-separator="-"
                              start-placeholder="开始日期" end-placeholder="结束日期"
                              :prefix-icon="dateRange == null || (Array.isArray(dateRange) && dateRange.length == 0) ? 'Calendar' : 'null'"
                              @change="handleQuery" clearable></el-date-picker>
            </el-form-item>
            <el-form-item>
              <el-button icon="RefreshLeft" type="primary" link @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>
          <!--操作按钮-->
          <div class="top-right">
            <el-button type="primary" icon="Plus" @click="handleAdd" v-hasPermi="['system:domain:add']">新增
            </el-button>
            <el-button icon="Refresh" @click="getList" class="refresh-button"/>
          </div>
        </div>
        <!--表格-->
        <div class="my-table">
          <el-table
              v-if="refreshTable"
              v-loading="loading"
              :data="domainList"
              row-key="domainId"
          >
            <el-table-column prop="domainAddress" label="ad域地址" width="260"/>
            <el-table-column prop="baseDn" label="Base DN" width="260"/>
            <el-table-column prop="userNo" label="已映射用户数量" width="260"/>
            <el-table-column prop="createUser" label="创建人" width="200"/>
            <el-table-column prop="createTime" label="创建时间" width="200"/>
            <el-table-column label="操作" align="left" class-name="fixed-width">
              <template #default="scope">
                <el-button
                    type="primary"
                    link
                    @click="handleUpdate(scope.row)"
                    v-hasPermi="['system:domain:edit']"
                >修改域
                </el-button>
                <el-button
                    type="primary"
                    link
                    @click="handleImport(scope.row)"
                    v-hasPermi="['system:domain:import']"
                >导入用户
                </el-button>
                <el-button
                    type="primary"
                    link
                    @click="handleSync(scope.row)"
                    v-hasPermi="['system:domain:sync']"
                >同步管理
                </el-button>
                <el-button
                    type="primary"
                    link
                    @click="handleTask(scope.row)"
                    v-hasPermi="['system:domain:sync']"
                >定时同步设置
                </el-button>
                <el-button
                    type="primary"
                    link
                    @click="handleDelete(scope.row)"
                    v-hasPermi="['system:domain:delete']"
                >删除域
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <!--分页-->
        <pagination v-show="total > 10" :total="total" v-model:page="queryParams.pageNum"
                    v-model:limit="queryParams.pageSize" @pagination="getList"/>

      </div>

      <!-- 添加或修改域对话框 -->
      <el-drawer :title="title" v-model="open" append-to-body :size="560" :close-on-click-modal="false">
        <div class="drawer__content">
          <div class="ins-step">
            <a-steps :current="active">
              <a-step title="设置ad域信息">
              </a-step>
              <a-step title="用户属性映射">
              </a-step>
            </a-steps>
          </div>
          <el-form ref="form" :model="form" :rules="rules" label-width="auto">
            <!--步骤一-->
            <div v-show="active === 0">
              <el-row>
                <el-col :span="24">
                  <el-form-item label="服务地址" prop="serverHost">
                    <el-input v-model="form.serverHost" placeholder="请输入服务地址"/>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="24">
                  <el-form-item label="服务端口" prop="serverPort">
                    <el-input v-model="form.serverPort" placeholder="请输入服务端口"/>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="24">
                  <el-form-item label="Base DN" prop="baseDn">
                    <el-input v-model="form.baseDn" placeholder="请输入基准目录 (例：dc=xxx,dc=com)"/>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="24">
                  <el-form-item label="管理员账号" prop="adminAccount">
                    <el-input v-model="form.adminAccount" placeholder="请输入管理员账号"/>
                  </el-form-item>
                </el-col>
                <el-col :span="24">
                  <el-form-item label="管理员密码" prop="adminPass">
                    <el-input show-password v-model="form.adminPass"
                              placeholder="请输入管理员密码"/>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="24">
                  <el-form-item label="过滤条件" prop="userFilter">
                    <el-input v-model="form.userFilter" placeholder="请输入过滤条件 (例：objectClass=person)"/>
                  </el-form-item>
                </el-col>
              </el-row>
            </div>
            <!--步骤二-->
            <div v-show="active === 1">
              <el-alert title="修改属性映射后已导入的用户信息不会立即生效,请手动进行同步" type="warning"
                        show-icon :closable="false"
                        v-show="form.id !== undefined" style="height: 41px;margin-bottom: 15px"/>
              <div class="my-table">
                <el-table v-loading="loading" :data="mappingData">
                  <el-table-column label="Manager用户属性" align="left" prop="managerUserProp">
                    <template #default="scope">
                      <dict-tag :options="manager_user_prop" :value="scope.row.managerUserProp"/>
                    </template>
                  </el-table-column>
                  <el-table-column label="ad域用户属性" align="left" prop="domainUserProp">
                    <template #default="scope">
                      <el-select v-model="scope.row.domainUserProp"
                                 clearable filterable allow-create
                                 :placeholder="defaultPlaceHolder[scope.row.managerUserProp]">
                        <el-option v-for="item in propOptions[scope.row.managerUserProp]" :key="item.value"
                                   :label="item.value" :value="item.value">
                        </el-option>
                      </el-select>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>
          </el-form>
        </div>
        <div class="drawer__footer">
          <el-button v-if="active === 1" @click="prev">上一步</el-button>
          <el-button v-if="active === 0" @click="next" type="primary">下一步</el-button>
          <el-button v-if="active === 1" type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </el-drawer>
      <!-- 导入用户对话框 -->
      <el-drawer title="导入ad域用户" :visible.sync="importOpen" append-to-body :size="960"
                 :wrapper-closable="false">
        <div class="drawer__content">
          <el-form ref="importForm" :model="importForm" :rules="importRules" label-width="auto">
            <div class="segment-detail">
              <div class="segment"></div>
              <div class="detail">基础信息</div>
            </div>
            <el-form-item label="ad域" prop="domainAddress">
              <el-input :value="importForm.domainAddress" :disabled="true" style="width: 426px"/>
            </el-form-item>
            <el-form-item label="默认部门" prop="defaultDeptId">
              <treeselect append-to-body zIndex="8888" v-model="importForm.defaultDeptId" :normalizer="myNormalizer"
                          :options="deptOptions" :show-count="true" placeholder="请选择默认部门"
                          style="width: 426px"/>
            </el-form-item>
            <el-form-item label="默认角色" prop="defaultRoleId">
              <el-select v-model="importForm.defaultRoleId"
                         clearable filterable allow-create
                         placeholder="请选择默认角色"
                         style="width: 426px">
                <el-option v-for="item in roleOptions"
                           :key="item.roleId"
                           :label="item.roleName"
                           :value="item.roleId">
                </el-option>
              </el-select>
            </el-form-item>
            <div class="my-table">
              <div class="segment-detail">
                <div class="segment"></div>
                <div class="detail">配置详情</div>
              </div>
              <el-table v-loading="importLoading" :data="importUserOption">
                <el-table-column :resizable="false" align="left" prop="itemCheck" width="50px">
                  <template #header>
                    <el-checkbox :value="isCheck" @change="selectBox"/>
                  </template>
                  <template #default="scope">
                    <el-checkbox v-model="scope.row.itemCheck" @change="toggleCheck(scope.row)"/>
                  </template>
                </el-table-column>
                <el-table-column label="账号" align="left" prop="userName" show-overflow-tooltip/>
                <el-table-column label="昵称" align="left" prop="nickName" width="120px"
                                 show-overflow-tooltip/>
                <el-table-column label="手机" align="left" prop="phoneNumber" width="150px"
                                 show-overflow-tooltip/>
                <el-table-column label="邮箱" align="left" prop="email" width="180px" show-overflow-tooltip/>
                <el-table-column label="部门" align="left" prop="deptId" width="140px">
                  <template #default="scope">
                    <treeselect append-to-body zIndex="8888" v-model="scope.row.deptId" :options="deptOptions"
                                :normalizer="myNormalizer" :show-count="true" placeholder="请选择部门"/>
                  </template>
                </el-table-column>
                <el-table-column label="角色" align="left" prop="roleId" width="140px">
                  <template #default="scope">
                    <el-select size="medium" v-model="scope.row.roleId"
                               clearable filterable allow-create
                               placeholder="请选择角色">
                      <el-option v-for="item in roleOptions"
                                 :key="item.roleId"
                                 :label="item.roleName"
                                 :value="item.roleId">
                      </el-option>
                    </el-select>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-form>
        </div>
        <div class="drawer__footer">
          <el-button @click="importCancel">取 消</el-button>
          <el-button type="primary" @click="submitImport" :disabled="importLoading"
                     key="submitImport">确 定
          </el-button>
        </div>
      </el-drawer>
      <!-- 同步管理对话框 -->
      <el-drawer title="ad域用户同步管理" :visible.sync="syncOpen" append-to-body :size="960"
                 :wrapper-closable="false">
        <div class="drawer__content">
          <el-form ref="syncForm" :model="syncForm" label-width="auto">
            <div class="my-table">
              <el-table v-loading="syncLoading" :data="syncUserList">
                <el-table-column :resizable="false" width="50px" align="left" prop="itemCheck">
                  <template #header>
                    <el-checkbox :value="isCheck" @change="selectSyncBox"/>
                  </template>
                  <template #default="scope">
                    <el-checkbox v-model="scope.row.itemCheck" @change="toggleCheck(scope.row)"/>
                  </template>
                </el-table-column>
                <el-table-column label="账号" align="left" prop="userName" show-overflow-tooltip
                                 width="180px"/>
                <el-table-column label="昵称" align="left" prop="nickName" show-overflow-tooltip
                                 width="120px"/>
                <el-table-column label="手机" align="left" prop="phoneNumber" show-overflow-tooltip
                                 width="120px"/>
                <el-table-column label="邮箱" align="left" prop="email" show-overflow-tooltip width="170px"/>
                <el-table-column label="创建时间" align="left" prop="createTime" show-overflow-tooltip
                                 width="160px"/>
                <el-table-column label="操作" align="left">
                  <template #default="scope">
                    <el-button
                        type="text"
                        @click="submitSync(scope.row)"
                    >同步
                    </el-button>
                    <el-button
                        type="text"
                        @click="submitUnbind(scope.row)"
                    >解绑
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-form>
        </div>
        <div class="drawer__footer">
          <el-button @click="syncClose">关 闭</el-button>
          <el-button type="primary" @click="submitSync" :disabled="syncLoading">同 步
          </el-button>
        </div>
      </el-drawer>
      <!-- 定时任务对话框 -->
      <el-dialog title="定时同步设置" :visible.sync="taskOpen" append-to-body width="560px"
                 :close-on-click-modal="false">
        <el-form ref="taskForm" :model="taskForm" :rules="taskRules" label-width="auto">
          <el-form-item label="定时同步" prop="syncEnable">
            <el-switch v-model="taskForm.syncEnable" placeholder="请输入服务地址"
                       active-value="1" inactive-value="0" @change="taskForm.syncCron = undefined"/>
          </el-form-item>
          <el-form-item label="同步周期" prop="syncCron" v-if="taskForm.syncEnable === syncEnableEnum.ENABLE">
            <el-select v-model="taskForm.syncCron" placeholder="请选择同步周期"
                       clearable
            >
              <el-option
                  v-for="dict in user_sync_cron"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
              />
            </el-select>
          </el-form-item>
          <el-alert title="信息同步提示" type="info" show-icon :closable="false"
                    style="margin-bottom: 20px">
            <template #title>
              <div>1.信息变化：已映射用户信息将自动同步</div>
              <div>2.用户删除：已映射用户信息将同步删除</div>
              <div>3.用户新增：未映射用户信息需手动导入</div>
            </template>
          </el-alert>
        </el-form>
        <div class="dialog-footer">
          <el-button @click="taskCancel">取 消</el-button>
          <el-button type="primary" @click="submitTask">确 定</el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script setup name="Domain">
import { delUser, getUser } from "@/api/system/user";
import {treeSelect} from "@/api/system/dept"
import {
  listDomain,
  getDomain,
  delDomain,
  addDomain,
  updateDomain,
  getDomainUser,
  importDomainUser,
  syncDomainUser,
  handleSyncTask,
  getSyncUserList
} from "@/api/system/domain/index"
import Treeselect from "vue3-treeselect"
import "vue3-treeselect/dist/vue3-treeselect.css"
import syncEnableEnum from "@/views/enum/system/syncEnableEnum"

const router = useRouter();
const {proxy} = getCurrentInstance();
const {manager_user_prop, user_sync_cron} = proxy.useDict(
    "manager_user_prop",
    "user_sync_cron"
);

const dateRange = ref([]); // 日期范围
const deptOptions = ref(undefined); // 部门选项
const roleOptions = ref([]); // 角色选项
const total = ref(0); // 域总数
const importUserOption = ref([]); // 导入用户列表
const syncUserList = ref([]); // 同步用户列表
const defaultPlaceHolder = ref([
  "默认为userPrincipalName", "默认为cn", "默认不同步", "默认不同步"
]);// ad域用户属性选择框PlaceHolder文案
const propOptions = ref([
  [
    {
      value: 'userPrincipalName'
    },
    {
      value: 'sAMAccountName'
    },
    {
      value: 'distinguishedName'
    },
    {
      value: 'cn'
    },
    {
      value: 'uid'
    },
    {
      value: 'displayName'
    },
    {
      value: 'name'
    },
    {
      value: 'description'
    }
  ],
  [
    {
      value: 'cn'
    },
    {
      value: 'displayName'
    },
    {
      value: 'name'
    },
    {
      value: 'uid'
    },
    {
      value: 'userPrincipalName'
    },
    {
      value: 'sAMAccountName'
    },
    {
      value: 'distinguishedName'
    },
    {
      value: 'description'
    }
  ],
  [
    {
      value: 'mobile'
    },
    {
      value: 'telephoneNumber'
    },
    {
      value: 'homePhone'
    }
  ],
  [
    {
      value: 'mail'
    },
    {
      value: 'email'
    }
  ]
]);// ad域用户属性配置项
const indeterminate = ref(true);
const isCheck = ref(false); //同步管理表格全选
const mappingData = ref([]); //用户映射属性数据
const active = ref(0); //步骤进度
const loading = ref(true); // 遮罩层
const importLoading = ref(true); //导入用户加载遮罩
const syncLoading = ref(true); //同步用户加载遮罩
const domainList = ref([]); // 表格树数据
const title = ref(""); // 弹出层标题
const open = ref(false); // 是否显示弹出层
const importOpen = ref(false); //导入用户对话框是否显示
const syncOpen = ref(false); //同步用户对话框是否显示
const taskOpen = ref(false); //定时任务对话框显示
const refreshTable = ref(true); // 重新渲染表格状态
// 表单信息
const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    deptName: undefined,
    status: undefined
  }, // 查询参数
  form: {}, // 修改新增表单参数
  importForm: {}, //导入表单参数
  syncForm: {}, //同步用户表单参数
  taskForm: {
    domainId: undefined,
    syncEnable: syncEnableEnum.DISABLE,
    syncCron: undefined
  }, //定时任务表单参数
  rules: {
    serverHost: [
      {required: true, message: "服务地址不能为空", trigger: "blur"},
      {
        pattern: /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/,
        message: "IP地址格式不正确",
        trigger: "blur"
      },
    ],
    serverPort: [
      {required: true, message: "服务端口不能为空", trigger: "blur"},
      {pattern: /^\d*$/, message: '服务端口格式不正确', trigger: 'blur'},
      {
        pattern: /^([1-9](\d{0,3}))$|^([1-5]\d{4})$|^(6[0-4]\d{3})$|^(65[0-4]\d{2})$|^(655[0-2]\d)$|^(6553[0-5])$/,
        message: '端口号范围为1~65535',
        trigger: 'blur'
      }
    ],
    baseDn: [
      {required: true, message: "基准目录不能为空", trigger: "blur"}
    ],
    adminAccount: [
      {required: true, message: "管理员账号不能为空", trigger: "blur"}
    ],
    adminPass: [
      {required: true, message: "管理员密码不能为空", trigger: "blur"}
    ],
    userFilter: [
      {required: true, message: "过滤条件不能为空", trigger: "blur"}
    ]
  }, // 新增修改表单校验
  importRules: {
    defaultDeptId: [
      {required: true, message: "默认部门不能为空", trigger: "blur"}
    ],
    defaultRoleId: [
      {required: true, message: "默认角色不能为空", trigger: "blur"}
    ]
  }, // 导入表单校验
  taskRules: {
    syncCron: [
      {required: true, message: "同步周期不能为空", trigger: "blur"}
    ]
  }// 定时任务表单校验
})
const {queryParams, form, importForm, syncForm, taskForm, rules, importRules, taskRules} = toRefs(data);

// watch(open, (val) => {
//   if (!val) {
//     setTimeout(() => {
//       active.value = 0
//     }, 200)
//   }
// });
//
// watch(syncOpen, (val) => {
//   if (!val) {
//     getList()
//   }
// });

/** 转换部门结构 */
function myNormalizer(node) {
  return {
    id: node.id,
    label: node.label,
    children: node.children,
    parentId: node.parentId,
    isDisabled: node.status !== "0"
  }
}

/** 同步管理对话框关闭 */
function syncClose() {
  syncOpen.value = false;
}

/** 导入用户表格全选 */
function selectBox() {
  isCheck.value = !isCheck.value;
  let list = [...importUserOption.value]
  for (let val of list) {
    val.itemCheck = isCheck.value
  }
  importUserOption.value = list;
}

/** 同步管理表格全选 */
function selectSyncBox() {
  isCheck.value = !isCheck.value;
  let list = [...syncUserList.value]
  for (let val of list) {
    val.itemCheck = isCheck.value
  }
  syncUserList.value = list;
}

/** 上一步 */
function prev() {
  active.value--;
}

/** 下一步 */
function next() {
  proxy.$refs["form"].validate(valid => {
    if (valid) {
      active.value++;
    }
  });
}

/** 同步管理表格勾选 */
function toggleCheck(row) {
  let list = importUserOption.value.filter(item => item.itemCheck);
  isCheck.value = list.length === importUserOption.value.length;
  indeterminate.value = list.length > 0 && list.length < importUserOption.value.length;
}

/** 查询域列表 */
function getList() {
  getTreeSelect();
  getRoleSelect();
  loading.value = true;
  listDomain(proxy.addDateRange(queryParams.value, dateRange.value)).then(response => {
    domainList.value = response.rows;
    total.value = response.total;
  }).finally(() => {
    loading.value = false;
  });
}

/** 添加或修改域对话框取消按钮 */
function cancel() {
  open.value = false;
  active.value = 0;
  reset();
}

/** 导入对话框取消按钮 */
function importCancel() {
  importOpen.value = false;
  importReset();
}

/** 任务话框取消按钮 */
function taskCancel() {
  taskOpen.value = false;
  taskReset();
}

/** 表单重置 */
function reset() {
  form.value = {
    serverHost: undefined,
    serverPort: undefined,
    sslEnable: "0",
    baseDn: undefined,
    deptFilter: undefined,
    userFilter: undefined,
    adminAccount: undefined,
    adminPass: undefined
  };
  proxy.resetForm("form");
}

/** 导入表单重置 */
function importReset() {
  importForm.value = {};
  proxy.resetForm("importForm");
  importUserOption.value = [];
  isCheck.value = false;
}

/** 任务表单重置 */
function taskReset() {
  taskForm.value = {
    domainId: undefined,
    syncEnable: syncEnableEnum.DISABLE,
    syncCron: undefined

  };
  proxy.resetForm("taskForm");
}

/** 同步表单重置 */
function syncReset() {
  syncForm.value = {};
  proxy.resetForm("syncForm");
  isCheck.value = false;
}

/** 搜索按钮操作 */
function handleQuery() {
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  proxy.resetForm("queryForm");
  handleQuery();
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加ad域配置"
  form.value.serverPort = '389'
  mappingData.value = [
    {
      managerUserProp: "0",
      domainUserProp: undefined
    }, {
      managerUserProp: "1",
      domainUserProp: undefined
    }, {
      managerUserProp: "2",
      domainUserProp: undefined
    }, {
      managerUserProp: "3",
      domainUserProp: undefined
    },
  ]
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  getDomain(row.id).then(response => {
    form.value = response.data;
    mappingData.value = response.data.mappingList;
    open.value = true;
    title.value = "修改ad域配置";
  });
}

/** 查询部门下拉树结构 */
function getTreeSelect() {
  treeSelect().then(response => {
    deptOptions.value = response.data;
  });
}

/** 查询角色选项 */
function getRoleSelect() {
  getUser().then(response => {
    roleOptions.value = response.roles;
  });
}

/** 导入用户操作 */
function handleImport(row) {
  importReset();
  importForm.value.domainAddress = row.domainAddress;
  importOpen.value = true;
  importLoading.value = true;
  getTreeSelect();
  getRoleSelect();
  importForm.value.defaultDeptId = deptOptions.value[0].id;
  importForm.value.defaultRoleId = roleOptions.value[0].roleId;
  getDomainUser(row.id).then(response => {
    importUserOption.value = response.data
    if (importUserOption.value.length === 0) {
      proxy.$modal.msgWarning("没有可导入的用户，请修改ad域配置或修改过滤条件")
    }
    for (let option of importUserOption.value) {
      option.itemCheck = false;
    }
  }).finally(() => {
    importLoading.value = false;
  });
}

/** 获取同步列表 */
function getSyncList(id) {
  getSyncUserList(id).then(response => {
    syncUserList.value = response
  }).finally(() => {
    syncLoading.value = false;
  });
}

/** 同步管理 */
function handleSync(row) {
  syncReset();
  syncOpen.value = true;
  syncLoading.value = true;
  getTreeSelect();
  getRoleSelect();
  importForm.value.defaultDeptId = deptOptions.value[0].id;
  importForm.value.defaultRoleId = roleOptions.value[0].roleId;
  getSyncList(row.id);
  syncLoading.value = false;
}

/** 定时同步设置 */
function handleTask(row) {
  taskOpen.value = true;
  taskForm.value.domainId = row.id;
  taskForm.value.syncEnable = row.syncEnable;
  taskForm.value.syncCron = row.syncCron;
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["form"].validate(valid => {
    if (valid) {
      form.value.mappingList = mappingData.value;
      if (form.value.id !== undefined) {
        updateDomain(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addDomain(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        }).catch(e => {
          proxy.$modal.msgError('添加域失败：' + e.toString().split(": ")[1])
        });
      }
    }
  });
}

/** 新增修改提交按钮 */
function submitImport() {
  if (importUserOption.value.filter(item => item.itemCheck).length === 0) {
    proxy.$modal.msgWarning("请选择要导入的用户")
    return;
  }
  importUserOption.value.forEach(option => {
    option.defaultDeptId = importForm.value.defaultDeptId;
    option.defaultRoleId = importForm.value.defaultRoleId;
  })
  proxy.$refs["importForm"].validate(valid => {
    if (valid) {
      importDomainUser(importUserOption.value.filter(item => item.itemCheck)).then(response => {
        proxy.$modal.msgSuccess("导入成功");
        importOpen.value = false;
        getList();
      });
    }
  });
}

/** 同步表单提交按钮 */
function submitSync(row) {
  let domainId;
  let userIdList = [];
  if (row.userId === undefined) {
    if (syncUserList.value.filter(item => item.itemCheck).length === 0) {
      proxy.$modal.msgWarning("请选择要同步的用户")
      return;
    }
    syncUserList.value.forEach(item => {
      if (item.itemCheck) {
        userIdList.push(item.userId)
      }
    });
    domainId = syncUserList.value[0].domainId;
  } else {
    userIdList.push(row.userId)
    domainId = row.domainId;
  }
  syncLoading.value = true;
  syncDomainUser({
    'domainId': domainId,
    'userIdList': userIdList
  }).then(response => {
    if (response) {
      proxy.$modal.msgSuccess("同步成功");
    }
  }).finally(() => {
    initSyncList(domainId)
    syncLoading.value = false;
  });
}

/** 解绑提交按钮 */
function submitUnbind(row) {
  let domainId = row.domainId;
  proxy.$modal.confirm("是否确认删除：" + row.nickName).then(() => {
    syncLoading.value = true;
    return delUser(row.userId).then(res => {
      if (res) {
        proxy.$modal.msgSuccess("解绑成功");
      }
    }).finally(() => {
      initSyncList(domainId)
      syncLoading.value = false
    });
  })
}

/** 初始化数据值  获取列表 */
function initSyncList(domainId)
{
  getSyncList(domainId);
  isCheck.value = false;
  syncLoading.value = false;
}

/** 任务表单提交按钮 */
function submitTask() {
  proxy.$refs["taskForm"].validate(valid => {
    if (valid) {
      handleSyncTask(taskForm.value).then(res => {
        if (res) {
          proxy.$modal.msgSuccess("设置成功");
        }
      }).finally(() => {
        taskOpen.value = false;
        getList();
      });
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  proxy.$modal.confirm("是否确认删除：" + row.domainAddress).then(function () {
    return delDomain(row.id);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch((error) => {
    proxy.$modal.msgError("删除失败：" + error.message);
  });
}

// 页面初始化时调用
onMounted(() => {
  getList();
})

</script>

<style lang="scss" scoped>
.ncdb-tb {
  .top {
    .el-range-editor-en {
      &::before {
        content: 'Date：' !important;
      }
    }
  }
}

//配置详情分段展示样式
.segment-detail {
  height: 40px;
  display: flex;

  .segment {
    width: 2px;
    height: 12px;
    background: #3363FF;
    margin: auto 8px auto 0px;
  }

  .detail {
    line-height: 40px;
    font-weight: 500;
  }
}

//ant步骤条组件样式重写
.ins-step {
  padding: 0px 60px 26px 60px;

  :deep(.ant-steps-item-process .ant-steps-item-icon) {
    background: #3363FF;
    border-color: #3363FF;
  }

  :deep(.ant-steps-item-finish) {
    .ant-steps-item-icon > .ant-steps-icon {
      color: #3363FF;
    }

    .ant-steps-item-icon {
      border-color: #3363FF;
      color: #3363FF;
    }

    .ant-steps-item-container > .ant-steps-item-content > .ant-steps-item-title::after {
      background-color: #3363FF;
    }
  }
}
</style>
