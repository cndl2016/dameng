<!-- 字典管理详情 陈宁 2024/10/12 -->
<template>
  <div>
    <!--顶部页面信息概览-->
    <div class="tab-container">
      <!-- 返回导航 -->
      <div class="goBack" @click="goBack">
        <div class="icon">
          <svg-icon icon-class="dm-goBack"/>
        </div>
      </div>
      <div class="total-desc">
        <div class="menu-name">字典管理</div>
      </div>
    </div>
    <!--页面主体内容-->
    <div class="app-container">
      <div class="ncdb-tb">
        <div class="top">
          <!--搜索表单-->
          <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="68px">
            <el-form-item prop="dictLabel">
              <el-input
                v-model="queryParams.dictLabel"
                placeholder="搜索字典标签"
                @input="handleQuery"
                suffix-icon="Search"
              />
            </el-form-item>
            <el-form-item prop="dictType">
              <el-select
                v-model="queryParams.dictType"
                @change="handleQuery"
                clearable
                style="width: 180px;"
              >
                <template #prefix>
                  <div class="select-prefix">字典名称:</div>
                </template>
                <el-option
                  v-for="item in typeOptions"
                  :key="item.dictId"
                  :label="item.dictName"
                  :value="item.dictType"
                />
              </el-select>
            </el-form-item>
            <el-form-item prop="status">
              <el-select
                v-model="queryParams.status"
                @change="handleQuery"
                clearable
                style="width: 180px;"
              >
                <template #prefix>
                  <div class="select-prefix">数据状态:</div>
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
            <el-button type="primary" icon="Plus" @click="handleAdd" v-hasPermi="['system:dict:add']">新增</el-button>
            <el-button icon="Edit" @click="handleUpdate" v-hasPermi="['system:dict:edit']" :disabled="single">修改</el-button>
            <el-button icon="Delete" @click="handleDelete" v-hasPermi="['system:dict:remove']" :disabled="multiple">删除</el-button>
            <el-button icon="Download" @click="handleExport" v-hasPermi="['system:dict:export']">导出</el-button>
            <el-button icon="Close" @click="handleClose">关闭</el-button>
            <el-button icon="Refresh" @click="getList" class="refresh-button"/>
          </div>
        </div>
        <!--表格-->
        <div class="my-table">
          <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="70"/>
            <el-table-column label="字典编码" align="left" prop="dictCode" />
            <el-table-column label="字典标签" align="left" prop="dictLabel">
              <template #default="scope">
                <span v-if="scope.row.listClass == '' || scope.row.listClass == 'default'">{{scope.row.dictLabel}}</span>
                <el-tag v-else :type="scope.row.listClass == 'primary' ? '' : scope.row.listClass">{{scope.row.dictLabel}}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="字典键值" align="left" prop="dictValue" />
            <el-table-column label="字典排序" align="left" prop="dictSort" />
            <el-table-column label="状态" prop="status">
              <template #default="scope">
                <dict-tag :options="sys_normal_disable" :value="scope.row.status"/>
              </template>
            </el-table-column>
            <el-table-column label="备注" align="left" prop="remark" :show-overflow-tooltip="true" />
            <el-table-column label="创建时间" align="left" prop="createTime" min-width="180" :show-overflow-tooltip="true">
              <template #default="scope">
                <span>{{ parseTime(scope.row.createTime) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" align="left" class-name="fixed-width" min-width="120">
              <template #default="scope">
                <el-button
                  type="primary" link
                  @click="handleUpdate(scope.row)"
                  v-hasPermi="['system:dict:edit']"
                >修改</el-button>
                <el-button
                  type="primary" link
                  @click="handleDelete(scope.row)"
                  v-hasPermi="['system:dict:remove']"
                >删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <pagination
          v-show="total>10"
          :total="total"
          v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize"
          @pagination="getList"
        />
      </div>

      <!-- 添加或修改参数配置对话框 -->
      <el-drawer :title="title" v-model="open" append-to-body :size="560" :close-on-click-modal="false">
        <div class="drawer__content">
          <el-form ref="dataRef" :model="form" :rules="rules" label-width="80px">
            <el-form-item label="字典类型">
              <el-input v-model="form.dictType" :disabled="true" />
            </el-form-item>
            <el-form-item label="数据标签" prop="dictLabel">
              <el-input v-model="form.dictLabel" placeholder="请输入数据标签" />
            </el-form-item>
            <el-form-item label="数据键值" prop="dictValue">
              <el-input v-model="form.dictValue" placeholder="请输入数据键值" />
            </el-form-item>
            <el-form-item label="样式属性" prop="cssClass">
              <el-input v-model="form.cssClass" placeholder="请输入样式属性" />
            </el-form-item>
            <el-form-item label="显示排序" prop="dictSort">
              <el-input-number v-model="form.dictSort" controls-position="right" :min="0" :max="2147483647"/>
            </el-form-item>
            <el-form-item label="回显样式" prop="listClass">
              <el-select v-model="form.listClass">
                <el-option
                  v-for="item in listClassOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio
                  v-for="dict in sys_normal_disable"
                  :key="dict.value"
                  :value="dict.value"
                >{{dict.label}}</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" maxlength="50" show-word-limit></el-input>
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

<script setup name="Data">
import useDictStore from '@/store/modules/dict'
import { optionselect as getDictOptionselect, getType } from "@/api/system/dict/type";
import { listData, getData, delData, addData, updateData } from "@/api/system/dict/data";

const { proxy } = getCurrentInstance();
const { sys_normal_disable } = proxy.useDict("sys_normal_disable");

const dataList = ref([]); // 字典表格数据
const open = ref(false);  // 是否显示弹出层
const loading = ref(true);  // 遮罩层
const ids = ref([]);  // 选中数组
const single = ref(true); // 非单个禁用
const multiple = ref(true); // 非多个禁用
const total = ref(0); // 总条数
const title = ref("");  // 弹出层标题
const defaultDictType = ref("");  // 默认字典类型
const typeOptions = ref([]);  // 类型数据字典
const route = useRoute(); // 路由参数
const router = useRouter(); // 路由跳转
// 数据标签回显样式
const listClassOptions = ref([
  { value: "default", label: "默认" }, 
  { value: "primary", label: "主要" }, 
  { value: "success", label: "成功" },
  { value: "info", label: "信息" },
  { value: "warning", label: "警告" },
  { value: "danger", label: "危险" }
]);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    dictType: undefined,
    dictLabel: undefined,
    status: undefined
  },
  rules: {
    dictLabel: [{ required: true, message: "数据标签不能为空", trigger: "blur" }],
    dictValue: [{ required: true, message: "数据键值不能为空", trigger: "blur" }],
    dictSort: [{ required: true, message: "数据顺序不能为空", trigger: "blur" }]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询字典类型详细 */
function getTypes(dictId) {
  getType(dictId).then(response => {
    queryParams.value.dictType = response.data.dictType;
    defaultDictType.value = response.data.dictType;
    getList();
  });
}

/** 查询字典类型列表 */
function getTypeList() {
  getDictOptionselect().then(response => {
    typeOptions.value = response.data;
  });
}

/** 查询字典数据列表 */
function getList() {
  loading.value = true;
  listData(queryParams.value).then(response => {
    dataList.value = response.rows;
    total.value = response.total;
    loading.value = false;
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
    dictCode: undefined,
    dictLabel: undefined,
    dictValue: undefined,
    cssClass: undefined,
    listClass: "default",
    dictSort: 0,
    status: "0",
    remark: undefined
  };
  proxy.resetForm("dataRef");
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 返回按钮操作 */
function handleClose() {
  const obj = { path: "/system/dict" };
  proxy.$tab.closeOpenPage(obj);
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  queryParams.value.dictType = defaultDictType.value;
  handleQuery();
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加字典数据";
  form.value.dictType = queryParams.value.dictType;
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.dictCode);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const dictCode = row.dictCode || ids.value;
  getData(dictCode).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改字典数据";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["dataRef"].validate(valid => {
    if (valid) {
      if (form.value.dictCode != undefined) {
        updateData(form.value).then(response => {
          useDictStore().removeDict(queryParams.value.dictType);
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addData(form.value).then(response => {
          useDictStore().removeDict(queryParams.value.dictType);
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
  const dictCodes = row.dictCode || ids.value;
  proxy.$modal.confirm('是否确认删除字典编码为"' + dictCodes + '"的数据项？').then(function() {
    return delData(dictCodes);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
    useDictStore().removeDict(queryParams.value.dictType);
  }).catch(() => {});
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download("system/dict/data/export", {
    ...queryParams.value
  }, `dict_data_${new Date().getTime()}.xlsx`);
}

/** 返回按钮操作 */
function goBack() {
  router.push({path: '/system/dict'});
}

getTypes(route.params && route.params.dictId);
getTypeList();
</script>
