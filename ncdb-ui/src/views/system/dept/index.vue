<!-- 部门管理 陈宁 2024/10/11 -->
<template>
  <div>
    <!--顶部页面信息概览-->
    <div class="tab-container">
      <div class="total-desc">
        <div class="menu-name">部门管理</div>
      </div>
    </div>
    <!--页面主体内容-->
    <div class="app-container">
      <div class="ncdb-tb">
        <!--搜索框 && 操作按钮区域-->
        <div class="top">
          <!--搜索表单-->
          <el-form :model="queryParams" ref="queryRef" :inline="true" :rules="queryRules">
            <el-form-item prop="deptName">
              <el-input
                v-model="queryParams.deptName"
                placeholder="搜索部门名称"
                @input="handleQuery"
                suffix-icon="Search"
              />
            </el-form-item>
            <el-form-item prop="status">
              <el-select
                v-model="queryParams.status"
                @change="handleQuery"
                clearable
                style="width: 180px;"
                >
                <template #prefix>
                  <div class="select-prefix">部门状态:</div>
                </template>
                <el-option
                  v-for="dict in sys_normal_disable"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button icon="RefreshLeft" type="primary" link @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>
          <!--操作按钮-->
          <div class="top-right">
            <el-button icon="Plus" @click="handleAdd" v-hasPermi="['system:dept:add']" type="primary" >新增</el-button>
            <el-button icon="Sort" @click="toggleExpandAll">展开/折叠</el-button>
            <el-button icon="Refresh" @click="getList" class="refresh-button"/>
          </div>
        </div>
        <!--表格-->
        <div class="my-table">
          <el-table
            v-if="refreshTable"
            v-loading="loading"
            :data="deptList"
            row-key="deptId"
            :default-expand-all="isExpandAll"
            :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
          >
            <el-table-column prop="deptName" label="部门名称" min-width="180"></el-table-column>
            <el-table-column prop="orderNum" label="排序" min-width="100"></el-table-column>
            <el-table-column prop="status" label="状态" min-width="120">
              <template #default="scope">
                <dict-tag :options="sys_normal_disable" :value="scope.row.status"/>
              </template>
            </el-table-column>
            <el-table-column label="创建时间" prop="createTime" :show-overflow-tooltip="true" min-width="180">
              <template #default="scope">
                <span>{{ parseTime(scope.row.createTime) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" class-name="fixed-width" min-width="120">
              <template #default="scope">
                <el-button
                  type="primary" link
                  @click="handleUpdate(scope.row)"
                  v-hasPermi="['system:dept:edit']"
                >修改</el-button>
                <!--dept_level 最大层级为5层-->
                <el-button
                  type="primary" link
                  @click="handleAdd(scope.row)"
                  v-hasPermi="['system:dept:add']"
                  :disabled="scope.row.level >= dept_level[0].value-1"
                >新增</el-button>
                <!--最高层 总经办 不可删除-->
                <el-button
                  v-if="scope.row.parentId != constants.TOP_DEPT"
                  type="primary" link
                  @click="handleDelete(scope.row)"
                  v-hasPermi="['system:dept:remove']"
                >删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      
      <!-- 添加或修改部门对话框 -->
      <el-drawer :title="title" v-model="open" append-to-body :size="560" :close-on-click-modal="false">
        <div class="drawer__content">
          <el-form ref="deptRef" :model="form" :rules="rules" label-width="auto">
            <!--修改时 最高部门无上级 该选项隐藏-->
            <el-form-item label="上级部门" prop="parentId" v-if="form.parentId !== constants.TOP_DEPT">
              <el-tree-select
                v-model="form.parentId"
                :data="deptOptions"
                :props="{ value: 'deptId', label: 'deptName', children: 'children' }"
                value-key="deptId"
                placeholder="选择上级部门"
                :check-strictly="true"
              />
            </el-form-item>
            <el-form-item label="部门名称" prop="deptName">
              <el-input v-model="form.deptName" placeholder="请输入部门名称" />
            </el-form-item>
            <el-form-item label="负责人" prop="leader">
              <el-input v-model="form.leader" placeholder="请输入负责人"/>
            </el-form-item>
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" maxlength="11" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
            </el-form-item>
            <el-form-item label="显示排序" prop="orderNum">
              <el-input-number style="width: 176px;" v-model="form.orderNum" controls-position="right" :min="0" :max="2147483647"/>
            </el-form-item>
            <el-form-item label="部门状态">
              <el-radio-group v-model="form.status">
                <el-radio
                  v-for="dict in sys_normal_disable"
                  :key="dict.value"
                  :value="dict.value"
                >{{dict.label}}</el-radio>
              </el-radio-group>
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

<script setup name="Dept">
import { listDept, getDept, delDept, addDept, updateDept, listDeptExcludeChild } from "@/api/system/dept";

import statusEnum from "@/views/enum/system/statusEnum";
import { constants } from '@/utils/constants';

const { proxy } = getCurrentInstance();
const { sys_normal_disable, dept_level } = proxy.useDict("sys_normal_disable", "dept_level");

const deptList = ref([]); // 表格树数据
const open = ref(false);  // 是否显示弹出层
const loading = ref(true);  // 遮罩层
const title = ref("");  // 弹出层标题
const deptOptions = ref([]);  // 部门树选项
const isExpandAll = ref(true);  // 是否展开，默认全部展开
const refreshTable = ref(true); // 重新渲染表格状态

const data = reactive({
  form: {},
  queryParams: {
    deptName: undefined,
    status: undefined
  },
  rules: {
    parentId: [{ required: true, message: "上级部门不能为空", trigger: "blur" }],
    deptName: [{ required: true, message: "部门名称不能为空", trigger: "blur" }],
    leader: [{ min: 0, max: 20, message: '负责人名称不可超过20字', trigger: 'change' }],
    orderNum: [{ required: true, message: "显示排序不能为空", trigger: "blur" }],
    email: [{ type: "email", message: "请输入正确的邮箱地址", trigger: ["blur", "change"] }],
    phone: [{ pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: "请输入正确的手机号码", trigger: "blur" }]
  },
  queryRules: {
    deptName: [{max: 30, message: '部门名称不可超过30字', trigger: 'change'}]
  }
});

const { queryParams, form, rules, queryRules } = toRefs(data);

/** 查询部门列表 */
function getList() {
  loading.value = true;
  listDept(queryParams.value).then(response => {
    //构造部门树
    deptList.value = proxy.handleTree(response.data, "deptId");
    //设置树层级
    deptList.value.forEach(root => {
      setLevel(root,-1)
    })
  }).finally(() => {
    loading.value = false;
  })
}

/** 设置树层级 层级从0开始 */
function setLevel(root,level){
  root.level = level + 1;
  if(root.children != null && root.children.length > 0){
    root.children.forEach(child => {
      setLevel(child,root.level)
    })
  }
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

/** 表单重置 */
function reset() {
  form.value = {
    deptId: undefined,
    parentId: undefined,
    deptName: undefined,
    orderNum: 0,
    leader: undefined,
    phone: undefined,
    email: undefined,
    status: "0"
  };
  proxy.resetForm("deptRef");
}

/** 搜索按钮操作 */
function handleQuery() {
  proxy.$refs['queryRef'].validate(valid => {
    if (valid) {
      getList();
    }
  })
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

/** 新增按钮操作 */
function handleAdd(row) {
  reset();
  if (row != undefined) {
    form.value.parentId = row.deptId;
  }
  open.value = true;
  title.value = "添加部门";
  listDept().then(response => {
    deptOptions.value = proxy.handleTree(response.data, "deptId");
    deptOptions.value.forEach(root => {
      setLevel(root,-1)
    })
  });
}

/** 展开/折叠操作 */
function toggleExpandAll() {
  refreshTable.value = false;
  isExpandAll.value = !isExpandAll.value;
  nextTick(() => {
    refreshTable.value = true;
  });
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  getDept(row.deptId).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改部门";
  });
  listDeptExcludeChild(row.deptId).then(response => {
    deptOptions.value = proxy.handleTree(response.data, "deptId");
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["deptRef"].validate(valid => {
    if (valid) {
      if (form.value.deptId != undefined) {
        updateDept(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addDept(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  proxy.$modal.confirm('是否确认删除名称为"' + row.deptName + '"的数据项?').then(function() {
    return delDept(row.deptId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

getList();
</script>
