<!-- 菜单管理 陈宁 2024/10/12 -->
<template>
  <div>
    <!--顶部页面信息概览-->
    <div class="tab-container">
      <div class="total-desc">
        <div class="menu-name">菜单管理</div>
      </div>
    </div>
    <!--页面主体内容-->
    <div class="app-container">
      <div class="ncdb-tb">
        <div class="top">
          <el-form :model="queryParams" ref="queryRef" :inline="true">
            <el-form-item prop="menuName">
              <el-input v-model="queryParams.menuName" placeholder="搜索菜单名称" @input="handleQuery" suffix-icon="Search" />
            </el-form-item>
            <el-form-item prop="status">
              <el-select v-model="queryParams.status" @change="handleQuery" clearable style="width: 180px;">
                <template #prefix>
                  <div class="select-prefix">菜单状态:</div>
                </template>
                <el-option v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button icon="RefreshLeft" type="primary" link @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>
          <div class="top-right">
            <el-button type="primary" icon="Plus" @click="handleAdd" v-hasPermi="['system:menu:add']">新增</el-button>
            <el-button icon="Sort" @click="toggleExpandAll">展开/折叠</el-button>
            <el-button icon="Refresh" @click="getList" class="refresh-button" />
          </div>
        </div>
        <div class="my-table">
          <el-table v-if="refreshTable" v-loading="loading" :data="menuList" row-key="menuId"
            :default-expand-all="isExpandAll" :tree-props="{ children: 'children', hasChildren: 'hasChildren' }">
            <el-table-column prop="menuName" align="left" label="菜单名称" :show-overflow-tooltip="true"
              width="180"></el-table-column>
            <el-table-column prop="icon" label="图标" align="left" min-width="80">
              <template #default="scope">
                <svg-icon :icon-class="scope.row.icon" />
              </template>
            </el-table-column>
            <el-table-column align="left" prop="orderNum" label="排序" min-width="80"></el-table-column>
            <el-table-column align="left" prop="perms" label="权限标识" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column align="left" prop="component" label="组件路径" :show-overflow-tooltip="true"></el-table-column>
            <el-table-column align="left" prop="status" label="状态" min-width="100">
              <template #default="scope">
                <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
              </template>
            </el-table-column>
            <el-table-column label="创建时间" align="left" prop="createTime" :show-overflow-tooltip="true" min-width="180">
              <template #default="scope">
                <span>{{ parseTime(scope.row.createTime) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" align="left" class-name="fixed-width" min-width="140">
              <template #default="scope">
                <el-button type="primary" link @click="handleUpdate(scope.row)"
                  v-hasPermi="['system:menu:edit']">修改</el-button>
                <el-button type="primary" link @click="handleAdd(scope.row)"
                  v-hasPermi="['system:menu:add']">新增</el-button>
                <el-button type="primary" link @click="handleDelete(scope.row)"
                  v-hasPermi="['system:menu:remove']">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <!-- 添加或修改菜单对话框 -->
      <el-drawer :title="title" v-model="open" append-to-body :size="800" :close-on-click-modal="false">
        <div class="drawer__content">
          <el-form ref="menuRef" :model="form" :rules="rules" label-width="100px">
            <el-row>
              <el-col :span="24">
                <el-form-item label="上级菜单">
                  <el-tree-select v-model="form.parentId" :data="menuOptions"
                    :props="{ value: 'menuId', label: 'menuName', children: 'children' }" value-key="menuId"
                    placeholder="选择上级菜单" check-strictly />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="菜单类型" prop="menuType">
                  <el-radio-group v-model="form.menuType">
                    <el-radio value="M">目录</el-radio>
                    <el-radio value="C">菜单</el-radio>
                    <el-radio value="F">按钮</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="24" v-if="form.menuType != 'F'">
                <el-form-item label="菜单图标" prop="icon">
                  <el-popover placement="bottom-start" :width="540" trigger="click">
                    <template #reference>
                      <el-input v-model="form.icon" placeholder="点击选择图标" @blur="showSelectIcon" readonly>
                        <template #prefix>
                          <svg-icon v-if="form.icon" :icon-class="form.icon" class="el-input__icon"
                            style="height: 32px;width: 16px;" />
                          <el-icon v-else style="height: 32px;width: 16px;">
                            <search />
                          </el-icon>
                        </template>
                      </el-input>
                    </template>
                    <icon-select ref="iconSelectRef" @selected="selected" :active-icon="form.icon" />
                  </el-popover>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="菜单名称" prop="menuName">
                  <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="显示排序" prop="orderNum">
                  <el-input-number v-model="form.orderNum" controls-position="right" :min="0" :max="2147483647" />
                </el-form-item>
              </el-col>
              <el-col :span="12" v-if="form.menuType != 'F'">
                <el-form-item>
                  <template #label>
                    <span>
                      <el-tooltip content="选择是外链则路由地址需要以`http(s)://`开头" placement="top">
                        <el-icon><question-filled /></el-icon>
                      </el-tooltip>
                      是否外链
                    </span>
                  </template>
                  <el-radio-group v-model="form.isFrame">
                    <el-radio value="0">是</el-radio>
                    <el-radio value="1">否</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="12" v-if="form.menuType != 'F'">
                <el-form-item prop="path">
                  <template #label>
                    <span>
                      <el-tooltip content="访问的路由地址，如：`user`，如外网地址需内链访问则以`http(s)://`开头" placement="top">
                        <el-icon><question-filled /></el-icon>
                      </el-tooltip>
                      路由地址
                    </span>
                  </template>
                  <el-input v-model="form.path" placeholder="请输入路由地址" />
                </el-form-item>
              </el-col>
              <el-col :span="12" v-if="form.menuType == 'C'">
                <el-form-item prop="component">
                  <template #label>
                    <span>
                      <el-tooltip content="访问的组件路径，如：`system/user/index`，默认在`views`目录下" placement="top">
                        <el-icon><question-filled /></el-icon>
                      </el-tooltip>
                      组件路径
                    </span>
                  </template>
                  <el-input v-model="form.component" placeholder="请输入组件路径" />
                </el-form-item>
              </el-col>
              <el-col :span="12" v-if="form.menuType != 'M'">
                <el-form-item>
                  <el-input v-model="form.perms" placeholder="请输入权限标识" maxlength="100" />
                  <template #label>
                    <span>
                      <el-tooltip content="控制器中定义的权限字符，如：@PreAuthorize(`@ss.hasPermi('system:user:list')`)"
                        placement="top">
                        <el-icon><question-filled /></el-icon>
                      </el-tooltip>
                      权限字符
                    </span>
                  </template>
                </el-form-item>
              </el-col>
              <el-col :span="12" v-if="form.menuType == 'C'">
                <el-form-item>
                  <el-input v-model="form.query" placeholder="请输入路由参数" maxlength="255" />
                  <template #label>
                    <span>
                      <el-tooltip content='访问路由的默认传递参数，如：`{"id": 1, "name": "ry"}`' placement="top">
                        <el-icon><question-filled /></el-icon>
                      </el-tooltip>
                      路由参数
                    </span>
                  </template>
                </el-form-item>
              </el-col>
              <el-col :span="12" v-if="form.menuType == 'C'">
                <el-form-item>
                  <template #label>
                    <span>
                      <el-tooltip content="选择是则会被`keep-alive`缓存，需要匹配组件的`name`和地址保持一致" placement="top">
                        <el-icon><question-filled /></el-icon>
                      </el-tooltip>
                      是否缓存
                    </span>
                  </template>
                  <el-radio-group v-model="form.isCache">
                    <el-radio value="0">缓存</el-radio>
                    <el-radio value="1">不缓存</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="12" v-if="form.menuType != 'F'">
                <el-form-item>
                  <template #label>
                    <span>
                      <el-tooltip content="选择隐藏则路由将不会出现在侧边栏，但仍然可以访问" placement="top">
                        <el-icon><question-filled /></el-icon>
                      </el-tooltip>
                      显示状态
                    </span>
                  </template>
                  <el-radio-group v-model="form.visible">
                    <el-radio v-for="dict in sys_show_hide" :key="dict.value"
                      :value="dict.value">{{ dict.label }}</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="12" v-if="form.menuType != 'F'">
                <el-form-item>
                  <template #label>
                    <span>
                      <el-tooltip content="选择停用则路由将不会出现在侧边栏，也不能被访问" placement="top">
                        <el-icon><question-filled /></el-icon>
                      </el-tooltip>
                      菜单状态
                    </span>
                  </template>
                  <el-radio-group v-model="form.status">
                    <el-radio v-for="dict in sys_normal_disable" :key="dict.value"
                      :value="dict.value">{{ dict.label }}</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
            </el-row>
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

<script setup name="Menu">
import { addMenu, delMenu, getMenu, listMenu, updateMenu } from "@/api/system/menu";
import SvgIcon from "@/components/SvgIcon";
import IconSelect from "@/components/IconSelect";

const { proxy } = getCurrentInstance();
const { sys_show_hide, sys_normal_disable } = proxy.useDict("sys_show_hide", "sys_normal_disable");

const menuList = ref([]); // 菜单表格树数据
const open = ref(false);  // 是否显示弹出层
const loading = ref(true);  // 遮罩层
const title = ref("");  // 弹出层标题
const menuOptions = ref([]);  // 菜单树选项
const isExpandAll = ref(false); // 是否展开，默认全部折叠
const refreshTable = ref(true); // 重新渲染表格状态
const iconSelectRef = ref(null);  // icon选择控件

const data = reactive({
  form: {},
  queryParams: {
    menuName: undefined,
    visible: undefined
  },
  rules: {
    menuName: [{ required: true, message: "菜单名称不能为空", trigger: "blur" }],
    orderNum: [{ required: true, message: "菜单顺序不能为空", trigger: "blur" }],
    path: [{ required: true, message: "路由地址不能为空", trigger: "blur" }]
  },
});

const { queryParams, form, rules } = toRefs(data);

/** 查询菜单列表 */
function getList() {
  loading.value = true;
  listMenu(queryParams.value).then(response => {
    menuList.value = proxy.handleTree(response.data, "menuId");
    loading.value = false;
  });
}

/** 查询菜单下拉树结构 */
function getTreeselect() {
  menuOptions.value = [];
  listMenu().then(response => {
    const menu = { menuId: 0, menuName: "主类目", children: [] };
    menu.children = proxy.handleTree(response.data, "menuId");
    menuOptions.value.push(menu);
  });
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

/** 表单重置 */
function reset() {
  form.value = {
    menuId: undefined,
    parentId: 0,
    menuName: undefined,
    icon: undefined,
    menuType: "M",
    orderNum: undefined,
    isFrame: "1",
    isCache: "0",
    visible: "0",
    status: "0"
  };
  proxy.resetForm("menuRef");
}

/** 展示下拉图标 */
function showSelectIcon() {
  iconSelectRef.value.reset();
}

/** 选择图标 */
function selected(name) {
  form.value.icon = name;
}

/** 搜索按钮操作 */
function handleQuery() {
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

/** 新增按钮操作 */
function handleAdd(row) {
  reset();
  getTreeselect();
  if (row != null && row.menuId) {
    form.value.parentId = row.menuId;
  } else {
    form.value.parentId = 0;
  }
  open.value = true;
  title.value = "添加菜单";
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
async function handleUpdate(row) {
  reset();
  await getTreeselect();
  getMenu(row.menuId).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改菜单";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["menuRef"].validate(valid => {
    if (valid) {
      if (form.value.menuId != undefined) {
        updateMenu(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addMenu(form.value).then(response => {
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
  proxy.$modal.confirm('是否确认删除名称为"' + row.menuName + '"的数据项?').then(function () {
    return delMenu(row.menuId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => { });
}

// 页面初次挂载或在缓存中被激活时调用
onMounted(() => {
  getList();
})
</script>
