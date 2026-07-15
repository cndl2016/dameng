<!-- 登录日志 陈宁 2024/10/14 -->
<template>
  <div>
    <!--顶部页面信息概览-->
    <div class="tab-container">
      <div class="total-desc">
        <div class="menu-name">登录日志</div>
        <div class="segment"></div>
        <div class="menu-total">日志总数：{{total}}</div>
      </div>
    </div>
    <!--页面主体内容-->
    <div class="app-container">
      <div class="ncdb-tb">
        <!--搜索框 && 操作按钮区域-->
        <div class="top">
          <!--搜索表单-->
          <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="68px" :rules="queryRules">
            <el-form-item prop="userName">
              <el-input
                v-model="queryParams.userName"
                placeholder="搜索用户/登录地址"
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
                  <div class="select-prefix">登录状态:</div>
                </template>
                <el-option
                  v-for="dict in sys_common_status"
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
          <!--操作按钮-->
          <div class="top-right">
            <el-button icon="Delete" @click="handleDelete" v-hasPermi="['log:loginInfo:remove']" :disabled="multiple">删除
            </el-button>
<!--            <el-button icon="Close" @click="handleClean" v-hasPermi="['log:loginInfo:remove']">清空-->
<!--            </el-button>-->
            <el-button icon="Download" @click="handleExport" v-hasPermi="['log:loginInfo:export']">导出
            </el-button>
            <el-button icon="Refresh" @click="getList" class="refresh-button"/>
          </div>
        </div>
        <!--表格-->
        <div class="my-table">
          <el-table ref="tables" v-loading="loading" :data="logList" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="70"/>
            <el-table-column label="访问编号" prop="infoId"/>
            <el-table-column label="用户名称" prop="userName" :show-overflow-tooltip="true"/>
            <el-table-column label="地址" prop="ipaddr" width="220" :show-overflow-tooltip="true"/>
            <el-table-column label="登录状态" prop="status">
              <template #default="scope">
                <div class="col-status">
                  <el-icon class="green" v-if="scope.row.status == 0"><SuccessFilled/></el-icon>
                  <el-icon class="red" v-if="scope.row.status != 0"><CircleCloseFilled/></el-icon>
                  <span>{{ dictFormat(scope.row.status + '', sys_common_status) }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="描述" prop="msg"/>
            <el-table-column label="访问时间" prop="accessTime" width="200">
              <template #default="scope">
                <span>{{ parseTime(scope.row.accessTime) }}</span>
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
    </div>
  </div>
</template>

<script setup name="LoginInfo">
import {list, delLogininfor, cleanLogininfor} from "@/api/system/loginInfo";

const router = useRouter();
const { proxy } = getCurrentInstance();
const { sys_common_status } = proxy.useDict("sys_common_status");

const loading = ref(true);  // 遮罩层
const ids = ref([]);  // 选中数组
const multiple = ref(true);  // 非多个禁用
const total = ref(0); // 总条数
const logList = ref([]); // 表格数据
const dateRange = ref([]);  // 日期范围

// 表单信息
const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    title: undefined,
    status: undefined
  },
  queryRules: {
    userName: [
      {max: 30, message: '过滤条件过长', trigger: 'change'},
    ]
  },
});

const { queryParams, queryRules } = toRefs(data);

/** 查询登录日志列表 */
function getList() {
  loading.value = true;
  list(proxy.addDateRange(queryParams.value, dateRange.value)).then(response => {
      logList.value = response.rows;
      total.value = response.total;
      loading.value = false;
    }
  );
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
  ids.value = selection.map(item => item.infoId)
  multiple.value = !selection.length
}

/** 删除按钮操作 */
function handleDelete(row) {
  const infoIds = row.infoId || ids.value;
  proxy.$modal.confirm('是否确认删除访问编号为"' + infoIds + '"的数据项？').then(function () {
    return delLogininfor(infoIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 清空按钮操作 */
function handleClean() {
  proxy.$modal.confirm('是否确认清空所有登录日志数据项？').then(function () {
    return cleanLogininfor();
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("清空成功");
  }).catch(() => {});
}

/** 导出按钮操作 */
function handleExport() {
  if (total.value > 0) {
    proxy.$modal.confirm('即将导出' + total.value + '条记录，是否确认？').then(() => {
      proxy.download('system/loginInfo/export', {
        ...queryParams.value
      }, `logininfor_${new Date().getTime()}.xlsx`)
    })
  } else {
    proxy.$modal.msgWarning("当前查询条件下的记录为空！");
  }
}

getList();
</script>

<style scoped lang="scss">
// 日期选择控件
:deep(.ncdb-tb) {
  .top {
    .el-range-editor {
      &::before {
        content: '访问时间：' !important;
      }
    }
  }
}

//图标对齐
.el-icon{
  top:2px
}
</style>