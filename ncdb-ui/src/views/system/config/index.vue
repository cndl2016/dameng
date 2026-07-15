<!-- 参数管理 陈宁 2024/10/11 -->
<template>
  <div>
    <!-- 主体内容 -->
    <div class="app-container">
      <div class="ncdb-tb">
        <div class="top">
          <!--参数名称、系统内置、日期搜索框，重置按钮 -->
          <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="68px">
            <el-form-item prop="configName">
              <el-input
                v-model="queryParams.configName"
                placeholder="搜索参数名称/键名"
                @input="handleQuery"
                suffix-icon="Search"
              />
            </el-form-item>
            <el-form-item prop="configType">
              <el-select
                v-model="queryParams.configType"
                @change="handleQuery"
                clearable
                style="width: 180px;"
              >
                <template #prefix>
                  <div class="select-prefix">系统内置:</div>
                </template>
                <el-option
                  v-for="dict in sys_yes_no"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-date-picker
                v-model="dateRange"
                value-format="YYYY-MM-DD"
                type="daterange"
                range-separator="-"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                :prefix-icon="dateRange==null || (Array.isArray(dateRange) && dateRange.length==0) ? 'Calendar' : 'null'"
                @change="handleQuery"
                clearable
              ></el-date-picker>
            </el-form-item>
            <el-form-item>
              <el-button icon="RefreshLeft" type="primary" link @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>
          <!--新增、修改、删除、导出、刷新缓存按钮 -->
          <div class="top-right">
            <el-button type="primary" icon="Plus" @click="handleAdd" v-hasPermi="['system:config:add']">新增</el-button>
            <el-button icon="Edit" @click="handleUpdate" v-hasPermi="['system:config:edit']" :disabled="single">修改</el-button>
            <el-button icon="Delete" @click="handleDelete" v-hasPermi="['system:config:remove']" :disabled="multiple">删除</el-button>
            <el-button icon="Download" @click="handleExport" v-hasPermi="['system:config:export']">导出</el-button>
            <el-button icon="RefreshLeft" @click="handleRefreshCache" v-hasPermi="['system:config:refresh']">刷新缓存</el-button>
            <el-button icon="Refresh" @click="getList" class="refresh-button"/>
          </div>
        </div>
        <!--主体table表单 -->
        <div class="my-table">
          <el-table v-loading="loading" :data="configList" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="70" />
            <el-table-column label="参数主键" prop="configId" width="120"/>
            <el-table-column label="参数名称" prop="configName" :show-overflow-tooltip="true" />
            <el-table-column label="参数键名" prop="configKey" :show-overflow-tooltip="true" />
            <el-table-column label="参数键值" prop="configValue" width="220"/>
            <el-table-column label="系统内置" prop="configType" width="140">
              <template #default="scope">
                <dict-tag :options="sys_yes_no" :value="scope.row.configType"/>
              </template>
            </el-table-column>
            <el-table-column label="备注" prop="remark" :show-overflow-tooltip="true" />
            <el-table-column label="创建时间" prop="createTime" width="180" :show-overflow-tooltip="true">
              <template #default="scope">
                <span>{{ parseTime(scope.row.createTime) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button
                  type="primary" link
                  @click="handleUpdate(scope.row)"
                  v-hasPermi="['system:config:edit']"
                >修改</el-button>
                <el-button
                  type="primary" link
                  @click="handleDelete(scope.row)"
                  v-hasPermi="['system:config:remove']"
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
      <el-drawer :title="title" v-model="open" append-to-body size="30%" :close-on-click-modal="false">
        <div class="drawer__content">
          <el-form ref="configRef" :model="form" :rules="rules" label-width="80px">
            <el-form-item label="参数名称" prop="configName">
              <el-input v-model="form.configName" placeholder="请输入参数名称" />
            </el-form-item>
            <el-form-item label="参数键名" prop="configKey">
              <el-input v-model="form.configKey" placeholder="请输入参数键名" />
            </el-form-item>
            <el-form-item label="参数键值" prop="configValue">
              <el-input v-model="form.configValue" placeholder="请输入参数键值" />
            </el-form-item>
            <el-form-item label="系统内置" prop="configType">
              <el-radio-group v-model="form.configType">
                <el-radio
                  v-for="dict in sys_yes_no"
                  :key="dict.value"
                  :label="dict.value"
                >{{dict.label}}</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"/>
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

<script setup name="Config">
import { listConfig, getConfig, delConfig, addConfig, updateConfig, refreshCache } from "@/api/system/config";

const { proxy } = getCurrentInstance();
const { sys_yes_no } = proxy.useDict("sys_yes_no");

const configList = ref([]); // 参数表格数据
const open = ref(false);  // 是否显示弹出层
const loading = ref(true);  // 遮罩层
const ids = ref([]);  // 选中数组
const single = ref(true); // 非单个禁用
const multiple = ref(true); // 非多个禁用
const total = ref(0); // 总条数
const title = ref("");  // 弹出层标题
const dateRange = ref([]);  // 日期范围

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    configName: undefined,
    configKey: undefined,
    configType: undefined
  },
  rules: {
    configName: [{ required: true, message: "参数名称不能为空", trigger: "blur" }],
    configKey: [{ required: true, message: "参数键名不能为空", trigger: "blur" }],
    configValue: [{ required: true, message: "参数键值不能为空", trigger: "blur" }]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询参数列表 */
function getList() {
  loading.value = true;
  listConfig(proxy.addDateRange(queryParams.value, dateRange.value)).then(response => {
    configList.value = response.rows;
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
    configId: undefined,
    configName: undefined,
    configKey: undefined,
    configValue: undefined,
    configType: "Y",
    remark: undefined
  };
  proxy.resetForm("configRef");
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  proxy.resetForm("queryRef");
  handleQuery();
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.configId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加参数";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const configId = row.configId || ids.value;
  getConfig(configId).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改参数";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["configRef"].validate(valid => {
    if (valid) {
      if (form.value.configId != undefined) {
        updateConfig(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addConfig(form.value).then(response => {
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
  const configIds = row.configId || ids.value;
  proxy.$modal.confirm('是否确认删除参数编号为"' + configIds + '"的数据项？').then(function () {
    return delConfig(configIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download("system/config/export", {
    ...queryParams.value
  }, `config_${new Date().getTime()}.xlsx`);
}

/** 刷新缓存按钮操作 */
function handleRefreshCache() {
  refreshCache().then(() => {
    proxy.$modal.msgSuccess("刷新缓存成功");
  });
}

// 页面初次挂载或在缓存中被激活时调用
onActivated(() =>{
  getList();
})
</script>
