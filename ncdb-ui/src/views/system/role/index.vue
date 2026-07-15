<!-- 角色管理 -->
<template>
  <div>
    <!--顶部页面信息概览-->
    <div class="tab-container">
      <div class="total-desc">
        <div class="menu-name">角色管理</div>
      </div>
    </div>
    <!-- 主体内容 -->
    <div class="app-container">
      <div class="ncdb-tb">
        <!--角色、日期搜索框，重置按钮 -->
        <div class="top">
          <el-form :model="queryParams" ref="queryRef" :inline="true" :rules="queryRules">
            <el-form-item prop="roleName">
              <el-input v-model="queryParams.roleName" placeholder="搜索角色名称" @input="handleQuery" suffix-icon="Search" />
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
          <!--新增、删除、导出、重置按钮 -->
          <div class="top-right">
            <el-button type="primary" icon="Plus" @click="handleAdd" v-hasPermi="['system:role:add']">新增
            </el-button>
            <el-button icon="Delete" @click="handleDelete" v-hasPermi="['system:role:remove']"
              :disabled="multipleDisable">删除
            </el-button>
<!--            <el-button icon="Download" @click="handleExport" v-hasPermi="['system:role:export']">导出-->
<!--            </el-button>-->
            <el-button icon="Refresh" @click="getList" class="refresh-button" />
          </div>
        </div>
        <!--主体table表单 -->
        <div class="my-table">
          <el-table v-loading="loading" :data="roleList" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="60" />
            <el-table-column label="角色名称" prop="roleName" :show-overflow-tooltip="true" min-width="160" align="left" />
<!--            <el-table-column label="显示顺序" prop="roleSort" min-width="100" align="left" />-->
            <el-table-column label="状态" min-width="100">
              <!-- 主体table表单 -->
              <template #default="scope">
                <el-switch v-model="scope.row.status" :active-value="statusEnum.ENABLE" :inactive-value="statusEnum.DISABLE"
                  @change="handleStatusChange(scope.row)"
                  :disabled="!hasPermission(['system:role:edit']) || scope.row.roleId == constants.ADMIN_ID"></el-switch>
              </template>
            </el-table-column>
            <el-table-column label="创建时间" align="left" prop="createTime" min-width="180" :show-overflow-tooltip="true">
              <template #default="scope">
                <span>{{ parseTime(scope.row.createTime) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="备注" prop="remark" :show-overflow-tooltip="true" min-width="200" align="left" />
            <el-table-column label="操作" class-name="fixed-width" min-width="100">
              <template #default="scope">
                <el-button type="primary" link @click="handleUpdate(scope.row)" v-hasPermi="['system:role:edit']"
                  v-show="scope.row.roleId != constants.ADMIN_ID">修改
                </el-button>
                <el-button type="primary" link @click="handleDelete(scope.row)" v-hasPermi="['system:role:remove']"
                  v-show="scope.row.roleId != constants.ADMIN_ID">删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <pagination v-show="total > 10" :total="total" v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize" @pagination="getList" />
      </div>

      <!-- 添加或修改角色配置对话框 -->
      <el-drawer :title="title" v-model="open" append-to-body :size="560" :close-on-click-modal="false">
        <div class="drawer__content">
          <el-form ref="roleRef" :model="form" :rules="rules" label-width="auto">
            <el-form-item label="角色名称" prop="roleName">
              <el-input v-model="form.roleName" placeholder="请输入角色名称" />
            </el-form-item>
<!--            <el-form-item label="角色顺序" prop="roleSort">-->
<!--              <el-input-number style="width: 176px;" v-model="form.roleSort" controls-position="right" :min="0"-->
<!--                :max="2147483647" />-->
<!--            </el-form-item>-->
            <el-form-item label="状态" style="margin-bottom: 10px">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">{{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="数据权限" prop="dataScope">
              <el-select v-model="form.dataScope" placeholder="请选择数据权限范围">
                <el-option v-for="item in dataScopeOptions" :key="item.value" :label="item.label"
                  :value="item.value"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="菜单权限">
              <el-checkbox v-model="menuExpand" @change="handleCheckedTreeExpand($event)">展开/折叠</el-checkbox>
              <el-checkbox v-model="menuNodeAll" @change="handleCheckedTreeNodeAll($event)">全选/全不选</el-checkbox>
              <div class="menu-permissions-tree">
                <el-tree :data="menuOptions" show-checkbox ref="menuRef" node-key="id"
                  check-strictly empty-text="加载中，请稍候" @check="handleCheck"
                  :props="{ label: 'label', children: 'children' }"></el-tree>
              </div>
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

<script setup name="Role">
import { listRole, getRoleInfo, delRole, addRole, updateRole, changeRoleStatus } from "@/api/system/role";
import { roleMenuTreeSelect, menuTreeselect } from "@/api/system/menu";
import { checkPermission } from "@/utils/checkPremi";
import { constants } from '@/utils/constants';
import statusEnum from "@/views/enum/system/statusEnum";

const router = useRouter();
const { proxy } = getCurrentInstance();
const { sys_normal_disable } = proxy.useDict("sys_normal_disable");

const roleList = ref([]); // 角色表格数据
const open = ref(false);  // 是否显示弹出层
const loading = ref(true);  // 遮罩层
const ids = ref([]);  // 选中roleId数组
const multipleDisable = ref(true);  // 非多个禁用
const total = ref(0); // 总条数
const title = ref("");  // 弹出层标题
const dateRange = ref([]);  // 日期范围
const menuOptions = ref([]);  // 菜单列表
const menuExpand = ref(false);  // 展开/折叠
const menuNodeAll = ref(false); // 全选/全不选
const menuRef = ref(null);  // 菜单树

/** 数据范围选项 */
const dataScopeOptions = ref([
  { value: "1", label: "全部数据权限" },
  // { value: "3", label: "本部门数据权限" },
  // { value: "4", label: "本部门及以下数据权限" },
  { value: "5", label: "仅本人数据权限" }
]);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    roleName: undefined,
    status: undefined
  },
  rules: {
    roleName: [{ required: true, message: "角色名称不能为空", trigger: "blur" }],
    roleSort: [{ required: true, message: "角色顺序不能为空", trigger: "blur" }],
    dataScope: [{ required: true, message: "数据权限不能为空", trigger: "blur" }]
  },
  queryRules: {
    roleName: [{ max: 30, message: '角色名称不可超过30字', trigger: 'change' }],
  },
});

const { queryParams, form, rules, queryRules } = toRefs(data);

/** 权限校验 */
function hasPermission(value) {
  return checkPermission(value);
}

/** 查询角色列表 */
function getList() {
  loading.value = true;
  listRole(proxy.addDateRange(queryParams.value, dateRange.value)).then(response => {
    roleList.value = response.rows;
    total.value = response.total;
  }).finally(() => {
    loading.value = false;
  });
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  proxy.$refs['queryRef'].validate(valid => {
    if (valid) {
      getList();
    }
  })
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  proxy.resetForm("queryRef");
  handleQuery();
}

/** 删除按钮操作 */
function handleDelete(row) {
  const roleIds = row.roleId || ids.value;
  let msg = ""
  if (row.roleId != null) {
    msg = `是否确认删除角色名称为${row.roleName}的数据项？`
  } else {
    msg = `是否确认删除这${ids.value.length}条数据？`
  }
  proxy.$modal.confirm(msg).then(function () {
    return delRole(roleIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.roleId);
  multipleDisable.value = !selection.length;
}

/** 角色状态修改 */
function handleStatusChange(row) {
  let text = row.status === statusEnum.ENABLE ? "启用" : "停用";
  proxy.$modal.confirm('确认要"' + text + '""' + row.roleName + '"角色吗?').then(function () {
    return changeRoleStatus(row.roleId, row.status);
  }).then(() => {
    proxy.$modal.msgSuccess(text + "成功");
  }).catch(function () {
    row.status = row.status === statusEnum.ENABLE ? statusEnum.DISABLE : statusEnum.ENABLE;
  });
}

/** 查询菜单树结构 */
function getMenuTreeSelect() {
  menuTreeselect().then(response => {
    menuOptions.value = response.data.filter(x => x.label != "系统管理");
  });
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

/** 重置新增的表单以及其他数据  */
function reset() {
  if (menuRef.value != undefined) {
    menuRef.value.setCheckedKeys([]);
  }
  menuExpand.value = false;
  menuNodeAll.value = false;
  form.value = {
    roleId: undefined,
    roleName: undefined,
    roleSort: 0,
    status: statusEnum.ENABLE,
    menuIds: [],
    remark: undefined
  };
  proxy.resetForm("roleRef");
}

/** 添加角色 */
function handleAdd() {
  reset();
  getMenuTreeSelect();
  open.value = true;
  title.value = "添加角色";
}

/** 修改角色 */
function handleUpdate(row) {
  reset();
  const roleId = row.roleId;
  const roleMenu = getRoleMenuTreeSelect(roleId);
  getRoleInfo(roleId).then(response => {
    form.value = response.data;
    open.value = true;
    nextTick(() => {
      // 更新菜单树
      roleMenu.then(res => {
        let checkedKeys = res.checkedKeys;
        checkedKeys.forEach((v) => {
          nextTick(() => {
            menuRef.value.setChecked(v, true, false);
          })
        })
      });
    });
    title.value = "修改角色";
  });
}

/** 根据角色ID查询菜单树结构 */
function getRoleMenuTreeSelect(roleId) {
  return roleMenuTreeSelect(roleId).then(response => {
    menuOptions.value = response.menus.filter(x => x.label != "系统管理");
    return response;
  });
}

/** 树权限（展开/折叠）*/
function handleCheckedTreeExpand(value) {
  let treeList = menuOptions.value;
  for (let i = 0; i < treeList.length; i++) {
    menuRef.value.store.nodesMap[treeList[i].id].expanded = value;
  }
}

/** 树权限（全选/全不选） */
function handleCheckedTreeNodeAll(value) {
  menuRef.value.setCheckedNodes(value ? menuOptions.value : []);
  menuOptions.value.forEach(option => {
    handleCheck(option)
  })
}

/** 选中方法 */
function handleCheck(data) {
  // 获取当前节点是否被选中
  const isChecked = menuRef.value.getNode(data).checked
  // 如果当前节点被选中，则遍历上级节点和下级子节点并选中，如果当前节点取消选中，则遍历下级节点并取消选中
  if (isChecked) {
    // 判断是否有上级节点，如果有那么遍历设置上级节点选中
    data.parentId && setParentChecked(data.parentId)
    // 判断该节点是否有下级节点，如果有那么遍历设置下级节点为选中
    data.children && setChildrenChecked(data.children, true)
  } else {
    // 如果节点取消选中，则取消该节点下的子节点选中
    data.children && setChildrenChecked(data.children, false)
  }
}

/** 设置父级节点选中状态 */
function setParentChecked(parentId) {
  // 获取该id的父级node
  const parentNode = menuRef.value.getNode(parentId)
  // 如果该id的父级node存在父级id则继续遍历
  parentNode && parentNode.data && parentNode.data.parentId && setParentChecked(parentNode.data.parentId)
  //  设置该id的节点为选中状态
  menuRef.value.setChecked(parentId, true)
}

/** 设置子级节点选中状态 */
function setChildrenChecked(node, isChecked) {
  node.forEach(item => {
    item.children && setChildrenChecked(item.children, isChecked)
    menuRef.value.setChecked(item.id, isChecked)
  })
}

/** 所有菜单节点数据 */
function getMenuAllCheckedKeys() {
  // 目前被选中的菜单节点
  let checkedKeys = menuRef.value.getCheckedKeys();
  // 半选中的菜单节点
  let halfCheckedKeys = menuRef.value.getHalfCheckedKeys();
  checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys);
  return checkedKeys;
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["roleRef"].validate(valid => {
    if (valid) {
      if (form.value.roleId != undefined) {
        form.value.menuIds = getMenuAllCheckedKeys();
        updateRole(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        form.value.menuIds = getMenuAllCheckedKeys();
        addRole(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

// 页面初始化时调用
onMounted(() => {
  getList();
})

</script>

<style scoped lang="scss">
// table 状态选项框样式
.menu-permissions-tree {
  margin-top: 10px;
  width: 100%;
  height: 280px;
  overflow: auto;
  border: 1px solid #DEDFE6;
  border-radius: 2px;
  padding: 6px 10px;

  :deep(.el-tree-node__content) {
    margin-bottom: 6px;
  }
}

// 展开/折叠 全选/全不选 checkbox 样式
.el-checkbox {
  color: #333333;
  font-weight: 400;
}
</style>