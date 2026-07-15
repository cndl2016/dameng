<!-- 字典管理 陈宁 2024/10/12 -->
<template>
  <div>
    <!--顶部页面信息概览-->
    <div class="tab-container">
      <div class="total-desc">
        <div class="menu-name">字典管理</div>
        <div class="segment"></div>
        <div class="menu-total">字典总数：{{total}}</div>
      </div>
    </div>
    <!--页面主体内容-->
    <div class="app-container">
      <div class="ncdb-tb">
        <div class="top">
          <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="68px">
            <el-form-item prop="dictName">
              <el-input
                v-model="queryParams.dictName"
                placeholder="搜索字典名称/类型"
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
                  <div class="select-prefix">字典状态:</div>
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
          <div class="top-right">
            <el-button type="primary" icon="Plus" @click="handleAdd" v-hasPermi="['system:dict:add']">新增</el-button>
            <el-button icon="Edit" @click="handleUpdate" v-hasPermi="['system:dict:edit']" :disabled="single">修改</el-button>
            <el-button icon="Delete" @click="handleDelete" v-hasPermi="['system:dict:remove']" :disabled="multiple">删除</el-button>
            <el-button icon="Download" @click="handleExport" v-hasPermi="['system:dict:export']">导出</el-button>
            <el-button icon="RefreshLeft" @click="handleRefreshCache" v-hasPermi="['system:dict:refresh']">刷新缓存</el-button>
            <el-button icon="Refresh" @click="getList" class="refresh-button"/>
          </div>
        </div>
        <div class="my-table">
        <el-table v-loading="loading" :data="typeList" @selection-change="handleSelectionChange">
          <el-table-column align="left" type="selection" width="70"/>
          <el-table-column align="left" label="字典编号" prop="dictId" min-width="80"/>
          <el-table-column align="left" label="字典名称" prop="dictName" :show-overflow-tooltip="true" min-width="140"/>
          <el-table-column align="left" label="字典类型" :show-overflow-tooltip="true" min-width="160">
            <template #default="scope">
              <router-link :to="'/system/dict-data/index/' + scope.row.dictId" class="link-type">
                <span>{{ scope.row.dictType }}</span>
              </router-link>
            </template>
          </el-table-column>
          <el-table-column align="left" label="状态" prop="status" min-width="100">
            <template #default="scope">
              <dict-tag :options="sys_normal_disable" :value="scope.row.status"/>
            </template>
          </el-table-column>
          <el-table-column align="left" label="备注" prop="remark" :show-overflow-tooltip="true" min-width="120"/>
          <el-table-column align="left" label="创建时间" prop="createTime" :show-overflow-tooltip="true" min-width="180">
            <template #default="scope">
              <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column align="left" label="操作"  class-name="fixed-width" min-width="100">
            <template #default="scope">
              <el-button
                type="primary" link
                @click="handleUpdate(scope.row)"
                v-hasPermi="['system:dict:edit']"
              >修改
              </el-button>
              <el-button
                type="primary" link
                @click="handleDelete(scope.row)"
                v-hasPermi="['system:dict:remove']"
              >删除
              </el-button>
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
          <el-form ref="dictRef" :model="form" :rules="rules" label-width="80px">
            <el-form-item label="字典名称" prop="dictName">
              <el-input v-model="form.dictName" placeholder="请输入字典名称"/>
            </el-form-item>
            <el-form-item label="字典类型" prop="dictType">
              <el-input v-model="form.dictType" placeholder="请输入字典类型"/>
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio
                  v-for="dict in sys_normal_disable"
                  :key="dict.value"
                  :value="dict.value"
                >{{dict.label}}
                </el-radio>
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

<script setup name="Dict">
import useDictStore from "@/store/modules/dict";
import {
  listType,
  getType,
  delType,
  addType,
  updateType,
  refreshCache,
} from "@/api/system/dict/type";

const { proxy } = getCurrentInstance();
const { sys_normal_disable } = proxy.useDict("sys_normal_disable");

const typeList = ref([]); // 字典表格数据
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
    dictName: undefined,
    status: undefined,
  },
  rules: {
    dictName: [
      { required: true, message: "字典名称不能为空", trigger: "blur" },
    ],
    dictType: [
      { required: true, message: "字典类型不能为空", trigger: "blur" },
    ],
  },
});

const { queryParams, form, rules } = toRefs(data);

/** 查询字典类型列表 */
function getList() {
  loading.value = true;
  listType(proxy.addDateRange(queryParams.value, dateRange.value)).then(
    (response) => {
      typeList.value = response.rows;
      total.value = response.total;
      loading.value = false;
    }
  );
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

/** 表单重置 */
function reset() {
  form.value = {
    dictId: undefined,
    dictName: undefined,
    dictType: undefined,
    status: "0",
    remark: undefined,
  };
  proxy.resetForm("dictRef");
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

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加字典类型";
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.dictId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const dictId = row.dictId || ids.value;
  getType(dictId).then((response) => {
    form.value = response.data;
    open.value = true;
    title.value = "修改字典类型";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["dictRef"].validate((valid) => {
    if (valid) {
      if (form.value.dictId != undefined) {
        updateType(form.value).then((response) => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addType(form.value).then((response) => {
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
  const dictIds = row.dictId || ids.value;
  proxy.$modal
    .confirm('是否确认删除字典编号为"' + dictIds + '"的数据项？')
    .then(function () {
      return delType(dictIds);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess("删除成功");
    })
    .catch(() => {});
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download(
    "system/dict/type/export",
    {
      ...queryParams.value,
    },
    `dict_${new Date().getTime()}.xlsx`
  );
}

/** 刷新缓存按钮操作 */
function handleRefreshCache() {
  refreshCache().then(() => {
    proxy.$modal.msgSuccess("刷新成功");
    useDictStore().cleanDict();
  });
}

getList();
</script>
