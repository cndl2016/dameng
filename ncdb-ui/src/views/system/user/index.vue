<!-- 用户管理  -->
<template>
  <div>
    <!--顶部页面信息概览-->
    <div class="tab-container">
      <div class="total-desc">
        <div class="menu-name">用户管理</div>
      </div>
    </div>
    <!--页面主体内容-->
    <div class="app-container">
      <el-row :gutter="20">
        <!--部门数据-->
        <el-col :span="4">
          <el-input v-model="deptName" maxlength="30" placeholder="请输入部门名称" clearable prefix-icon="Search"
            style="margin-bottom: 16px;" />
          <div class="dept-tree">
            <!--部门列表树-->
            <el-tree :data="deptOptions" :props="{ label: 'label', children: 'children' }" node-key="id" ref="deptTreeRef"
              :expand-on-click-node="false" :filter-node-method="filterNode" default-expand-all highlight-current
              @node-click="handleNodeClick">
              <!--插槽 自定义树节点内容-->
              <template #default="{ node, data }">
                <span class="custom-tree-node">
                  <!--节点内容提示框-->
                  <el-tooltip class="item" popper-class="custom-popper" effect="light" placement="top" :open-delay="400">
                    <template #content><strong>{{ data.label }}</strong></template>
                    <!--树节点内容-->
                    <span class="node-label">
                    {{ data.label }}
                    </span>
                  </el-tooltip>
                </span>
              </template>
            </el-tree>
          </div>
        </el-col>
        <!--用户数据-->
        <el-col :span="20">
          <div class="ncdb-tb">
            <!--搜索框 && 操作按钮区域-->
            <div class="top">
              <!--搜索表单-->
              <el-form :model="queryParams" ref="queryForm" :rules="queryRules" :inline="true" @submit.prevent>
                <el-form-item prop="userName">
                  <el-input v-model="queryParams.userName" placeholder="搜索用户" @input="handleQuery" suffix-icon="Search" />
                </el-form-item>
                <el-form-item prop="status">
                  <el-select v-model="queryParams.status" @change="handleQuery" clearable style="width: 180px;">
                    <template #prefix>
                      <div class="select-prefix">用户状态:</div>
                    </template>
                    <el-option v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.label"
                      :value="dict.value" />
                  </el-select>
                </el-form-item>
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
                <el-button type="primary" icon="Plus" @click="handleAdd" v-hasPermi="['system:user:add']">新增
                </el-button>
                <el-button icon="Delete" @click="handleDelete" v-hasPermi="['system:user:remove']"
                  :disabled="deleteDisable">删除
                </el-button>
<!--                <el-button icon="Download" @click="handleExport" v-hasPermi="['system:user:export']">导出-->
<!--                </el-button>-->
                <el-button icon="Refresh" @click="getUserList" class="refresh-button" />
              </div>
            </div>
            <!--表格-->
            <div class="my-table">
              <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="60" />
                <el-table-column label="用户账号" key="userName" prop="userName" :show-overflow-tooltip="true" />
                <el-table-column label="用户姓名" key="nickName" prop="nickName" :show-overflow-tooltip="true" />
                <el-table-column label="角色" key="roleName">
                  <template #default="scope">
                    <div v-if="scope.row.roles[0] != null">
                      <span v-if="scope.row.roles[0].roleName == '超级管理员'"><img src="@/assets/images/home/user_admin.svg"
                          alt="" style="vertical-align: -4px"></span>
                      <span v-if="scope.row.roles[0].roleName == '普通角色'"><img src="@/assets/images/home/user_normal.svg"
                          alt="" style="vertical-align: -4px"></span>
                      <span v-if="scope.row.roles[0].roleName == '只读角色'"><img src="@/assets/images/home/user_readonly.svg"
                          alt="" style="vertical-align: -4px"></span>
                      <span class="dp-user" v-if="!['超级管理员', '普通角色', '只读角色'].includes(scope.row.roles[0].roleName)">
                        {{ scope.row.roles[0].roleName }}
                      </span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="部门" key="deptName" prop="dept.deptName" :show-overflow-tooltip="true" />
                <el-table-column label="状态" key="status" width="100">
                  <template #default="scope">
                    <el-switch v-model="scope.row.status" :active-value="statusEnum.ENABLE"
                      :inactive-value="statusEnum.DISABLE" @change="handleStatusChange(scope.row)"
                      :disabled="!hasPermission(['system:user:edit']) || scope.row.userId == constants.ADMIN_ID" />
                  </template>
                </el-table-column>
<!--                <el-table-column label="用户类型" key="sourceType" prop="sourceType" :show-overflow-tooltip="true"-->
<!--                  min-width="100">-->
<!--                  <template #header>-->
<!--                    <span>用户类型</span>-->
<!--                    <el-tooltip placement="top" :open-delay="400">-->
<!--                      <template #content>-->
<!--                        <div style="line-height: 20px">普通用户：通过手动创建<br />ad域用户：通过域管理导入</div>-->
<!--                      </template>-->
<!--                      <svg-icon icon-class="dm-prompt-info" style="cursor: pointer; left: 4px;" />-->
<!--                    </el-tooltip>-->
<!--                  </template>-->
<!--                </el-table-column>-->
                <el-table-column label="创建时间" prop="createTime" width="180">
                  <template #default="scope">
                    <span>{{ parseTime(scope.row.createTime) }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="备注" key="remark" prop="remark" :show-overflow-tooltip="true" />
                <el-table-column label="操作" width="180">
                  <template #default="scope">
                    <div v-if="scope.row.userId != constants.ADMIN_ID">
                      <el-button type="primary" link @click="handleUpdate(scope.row)" v-hasPermi="['system:user:edit']">修改
                      </el-button>
                      <el-button type="primary" link @click="handleResetPwd(scope.row)"
                        v-hasPermi="['system:user:resetPwd']">重置密码
                      </el-button>
                      <el-button type="primary" link @click="handleDelete(scope.row)"
                        v-hasPermi="['system:user:remove']">删除
                      </el-button>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <pagination v-show="total > 10" :total="total" v-model:page="queryParams.pageNum"
              v-model:limit="queryParams.pageSize" @pagination="getUserList" />
          </div>
        </el-col>
      </el-row>

      <!-- 添加或修改用户配置对话框 -->
      <el-drawer :title="title" v-model="open" append-to-body :size="560" :close-on-click-modal="false">
        <div class="drawer__content">
          <el-form ref="userRef" :model="form" :rules="rules" label-width="auto">
            <el-form-item label="用户姓名" prop="nickName">
              <el-input v-model="form.nickName" placeholder="请输入用户姓名" />
            </el-form-item>
            <el-form-item label="归属部门" prop="deptId">
              <el-tree-select v-model="form.deptId" :data="deptOptions"
                :props="{ value: 'id', label: 'label', children: 'children' }" value-key="id" placeholder="请选择归属部门"
                check-strictly />
            </el-form-item>
            <el-tooltip content="用户名仅支持字母数字与下划线，且须以字母开头" placement="top">
              <el-form-item v-if="form.userId == undefined" label="用户账号" prop="userName">
                <el-input v-model="form.userName" placeholder="请输入用户账号" />
              </el-form-item>
            </el-tooltip>
            <el-form-item v-if="form.userId == undefined" label="用户密码" prop="password">
              <!--密码显示隐藏控制-->
              <el-input class="password-input" v-model="form.password" placeholder="请输入用户密码"
                :type="showPassword ? 'text' : 'password'">
                <template #suffix>
                  <em class="el-input__icon" @click="showPassword = !showPassword">
                    <svg-icon :icon-class="showPassword ? 'showPwd' : 'closePwd'" style="font-size: 14px"></svg-icon>
                  </em>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="手机号码" prop="phoneNumber">
              <el-input v-model="form.phoneNumber" placeholder="请输入手机号码" maxlength="11" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
            </el-form-item>
            <el-row>
              <el-col :span="12">
                <el-form-item label="角色" prop="roleIds">
                  <el-select v-model="form.roleIds" placeholder="请选择角色">
                    <el-option v-for="item in roleOptions" :key="item.roleId" :label="item.roleName" :value="item.roleId"
                      :disabled="item.status == statusEnum.DISABLE"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="用户性别">
                  <el-select v-model="form.sex" placeholder="请选择性别">
                    <el-option v-for="dict in sys_user_sex" :key="dict.value" :label="dict.label"
                      :value="dict.value"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.value" :value="dict.value">{{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" maxlength="50"
                show-word-limit></el-input>
            </el-form-item>
          </el-form>
        </div>
        <div class="drawer__footer">
          <el-button @click="cancel">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </div>
      </el-drawer>

    </div>
  </div>
</template>

<script setup name="User">
import {
  changeUserStatus,
  listUser,
  resetUserPwd,
  delUser,
  getUser,
  updateUser,
  addUser,
} from "@/api/system/user";
import { treeSelect } from "@/api/system/dept";
import { checkPermission } from "@/utils/checkPremi";

import statusEnum from "@/views/enum/system/statusEnum";
import { constants } from '@/utils/constants';

const router = useRouter();
const { proxy } = getCurrentInstance();
const { sys_normal_disable, sys_user_sex } = proxy.useDict(
  "sys_normal_disable",
  "sys_user_sex"
);

const showPassword = ref(false); // 是否显示密码
const loading = ref(true);  // 遮罩层
const tableSelectIds = ref([]);  // 表格复选框选中数组
const deleteDisable = ref(true);  // 表格没有选中时禁用
const total = ref(0); // 总条数
const userList = ref([]); // 用户表格数据
const title = ref("");  // 修改或新增用户抽屉标题
const deptOptions = ref(undefined);  // 部门树选项
const open = ref(false);  // 是否打开抽屉
const deptName = ref(""); // 搜索框部门名称
const initPassword = ref(undefined);  // 默认密码
const dateRange = ref([]);  // 日期范围
const roleOptions = ref([]);  // 角色选项
// 列显隐信息
const columns = ref([
  { key: 0, label: `用户编号`, visible: true },
  { key: 1, label: `用户名称`, visible: true },
  { key: 2, label: `用户昵称`, visible: true },
  { key: 3, label: `部门`, visible: true },
  { key: 4, label: `手机号码`, visible: true },
  { key: 5, label: `状态`, visible: true },
  { key: 6, label: `创建时间`, visible: true },
]);
// 表单信息
const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    userName: undefined,
    phoneNumber: undefined,
    status: undefined,
    deptId: undefined,
  },
  rules: {
    userName: [
      { required: true, message: "用户账号不能为空", trigger: "change" },
      { min: 2, max: 30, message: '用户账号长度必须介于 2 和 30 之间', trigger: 'change' },
      {
        pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/,
        message: "请输入正确的用户名",
        trigger: "change"
      }
    ],
    deptId: [
      { required: true, message: "归属部门不能为空", trigger: "blur" }
    ],
    roleIds: [
      { required: true, message: "角色不能为空", trigger: "blur" }
    ],
    nickName: [
      { required: true, message: "用户昵称不能为空", trigger: "change" },
      { min: 0, max: 30, message: '用户昵称不可超过30字', trigger: 'change' }
    ],
    password: [
      { required: true, message: "用户密码不能为空", trigger: "blur" },
      { min: 5, max: 20, message: '用户密码长度必须介于 5 和 20 之间', trigger: 'blur' }
    ],
    email: [
      {
        type: "email",
        message: "请输入正确的邮箱地址",
        trigger: ["blur", "change"]
      }
    ],
    phoneNumber: [
      {
        pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
        message: "请输入正确的手机号码",
        trigger: "change"
      }
    ]
  },
  queryRules: {
    userName: [
      { max: 30, message: '用户账号不可超过30字', trigger: 'change' },
    ],
  },
});

const { queryParams, form, rules, queryRules } = toRefs(data);

/** 通过条件过滤节点  */
const filterNode = (value, data) => {
  if (!value) return true;
  return data.label.indexOf(value) !== -1;
};

/** 根据名称筛选部门树 */
watch(deptName, (val) => {
  proxy.$refs["deptTreeRef"].filter(val);
});

/** 权限校验 */
function hasPermission(value) {
  return checkPermission(value);
}

/** 查询部门下拉树结构 */
function getDeptTree() {
  treeSelect().then(response => {
    deptOptions.value = response.data;
  });
}

/** 节点单击事件 */
function handleNodeClick(data) {
  queryParams.value.deptId = data.id;
  getUserList()
}

/** 查询用户列表 */
function getUserList() {
  loading.value = true;
  listUser(proxy.addDateRange(queryParams.value, dateRange.value)).then(
    (res) => {
      userList.value = res.rows;
      total.value = res.total;
    }
  ).finally(() => {
    loading.value = false;
  });
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  proxy.$refs['queryForm'].validate(valid => {
    if (valid) {
      getUserList();
    }
  })
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  proxy.resetForm("queryForm");
  queryParams.value.deptId = undefined;
  proxy.$refs.deptTreeRef.setCurrentKey(null);
  handleQuery();
}

/** 删除按钮操作 */
function handleDelete(row) {
  const userIds = row.userId || tableSelectIds.value;
  if (userIds == constants.ADMIN_ID || (typeof userIds === 'object' && userIds.some(x => x == constants.ADMIN_ID))) {
    proxy.$modal.msgError("超级管理员用户无法删除！");
    return
  }
  let msg = ""
  if (row.userId != null) {
    msg = `是否确认删除用户账号为${row.userName}的数据项？`
  } else {
    msg = `是否确认删除这${tableSelectIds.value.length}条数据？`
  }
  proxy.$modal
    .confirm(msg)
    .then(function () {
      return delUser(userIds);
    })
    .then(() => {
      getUserList();
      proxy.$modal.msgSuccess("删除成功");
    })
    .catch(() => { });
}

/** 用户状态修改  */
function handleStatusChange(row) {
  let text = statusEnum.getDescFromValue(row.status);
  proxy.$modal
    .confirm('确认要"' + text + '""' + row.userName + '"用户吗?')
    .then(function () {
      return changeUserStatus(row.userId, row.userName, row.status);
    })
    .then(() => {
      proxy.$modal.msgSuccess(text + "成功");
    })
    .catch(function () {
      row.status = row.status === statusEnum.ENABLE ? statusEnum.DISABLE : statusEnum.ENABLE;
    });
}

/** 重置密码按钮操作 */
function handleResetPwd(row) {
  proxy
    .$prompt('请输入"' + row.userName + '"的新密码', "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      closeOnClickModal: false,
      inputPattern: /^.{5,20}$/,
      inputErrorMessage: "用户密码长度必须介于 5 和 20 之间",
      inputValidator: (value) => {
        if (/<|>|"|'|\||\\/.test(value)) {
          return "不能包含非法字符：< > \" ' \\ |";
        }
      },
    })
    .then(({ value }) => {
      resetUserPwd(row.userId, value).then((response) => {
        proxy.$modal.msgSuccess("修改成功，新密码是：" + value);
      });
    })
    .catch(() => { });
}

/** 选择条数  */
function handleSelectionChange(selection) {
  tableSelectIds.value = selection.map(item => item.userId);
  deleteDisable.value = !selection.length;
}

/** 重置操作表单 */
function reset() {
  form.value = {
    userId: undefined,
    deptId: undefined,
    userName: undefined,
    nickName: undefined,
    password: undefined,
    phoneNumber: undefined,
    email: undefined,
    sex: undefined,
    status: statusEnum.ENABLE,
    remark: undefined,
    roleIds: undefined,
  };
  proxy.resetForm("userRef");
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

/** 新增按钮操作 */
function handleAdd() {
  //表单重置
  reset();
  //获取部门数据
  getDeptTree();
  //获取初始密码
  proxy.getConfigKey("sys.user.initPassword").then(response => {
    initPassword.value = response.msg;
  });
  getUser().then((response) => {
    roleOptions.value = response.roles;
    open.value = true;
    title.value = "添加用户";
    //根据左侧选中部门进行填充
    if (queryParams.value.deptId != undefined) {
      form.value.deptId = queryParams.value.deptId
    }
    form.value.password = initPassword.value;
  });
}

/** 修改按钮操作 */
function handleUpdate(row) {
  //表单重置
  reset();
  //获取部门数据
  getDeptTree();
  if (row.userId == constants.ADMIN_ID) {
    proxy.$modal.msgWarning('管理员用户无法修改！')
    return;
  }
  getUser(row.userId).then((response) => {
    form.value = response.data;
    roleOptions.value = response.roles;
    form.value.roleIds = String(response.roleIds[0]);
    open.value = true;
    title.value = "修改用户";
    form.password = "";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["userRef"].validate((valid) => {
    if (valid) {
      let tmpForm = Object.assign({}, form.value)
      let tmpRole = tmpForm.roleIds;
      tmpForm.roleIds = [];
      tmpForm.roleIds.push(tmpRole);
      if (form.value.userId != undefined) {
        updateUser(tmpForm).then((response) => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getUserList();
        });
      } else {
        addUser(tmpForm).then((response) => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getUserList();
        });
      }
    }
  });
}

// 页面初始化时调用
onMounted(() => {
  getDeptTree();
  getUserList();
})

</script>

<style scoped lang="scss">
//密码框
:deep(.password-input .el-input__suffix) {
  right: 9px;
}

//部门树节点高度
:deep(.el-tree-node__content) {
  height: 40px !important;
}

//部门树点击样式
:deep(.el-tree-node.is-current > .el-tree-node__content) {
  background-color: #eef2fe;
  color: #3363ff;

  .el-tree-node__expand-icon {
    color: #3363ff;
  }
}

//部门树节点内容超出隐藏
.node-label {
  //display: block;
  max-width: 170px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

//部门树 叶子节点图标透明
:deep(.is-leaf::before) {
  opacity: 0;
}
.el-button.is-link{
  padding: 0px !important;
}

//用户数量
.num{
    margin-left: 6px;
    color: #606266;
    background-color: #FFF;
    min-width: 20px;
    text-align: center;
    border: 1px solid #DDDDDD;
    border-radius: 10px;
    padding: 4px 4px;
}

//高亮样式
:deep(.el-tree--highlight-current .el-tree-node.is-current>.el-tree-node__content) {
  .num {
     color: #3363FF;
     background: #CCD8FF;
     border: 1px solid #3363ff1a;
  }
}

</style>

