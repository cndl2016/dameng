<!-- 操作日志 陈宁 2024/10/14 -->
<template>
  <div>
    <div class="tab-container">
      <div class="total-desc">
        <div class="menu-name">操作日志</div>
      </div>
    </div>
    <!--页面主体内容-->
    <div class="app-container">
      <div class="ncdb-tb">
        <!--搜索框 && 操作按钮区域-->
        <div class="top">
          <!--搜索表单-->
          <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="68px" :rules="queryRules">
            <el-form-item prop="operName">
              <el-input
                  v-model="queryParams.operName"
                  placeholder="搜索操作人"
                  @input="handleQuery"
                  suffix-icon="Search"
              />
            </el-form-item>
            <el-form-item prop="title">
              <el-select
                  v-model="queryParams.title"
                  @change="handleQuery"
                  clearable
                  style="width: 180px;"
              >
                <template #prefix>
                  <div class="select-prefix">系统模块:</div>
                </template>
                <el-option
                    v-for="option in titleOptions"
                    :key="option"
                    :label="option"
                    :value="option"
                />
              </el-select>
            </el-form-item>
            <el-form-item prop="businessType">
              <el-select
                  v-model="queryParams.businessType"
                  @change="handleQuery"
                  clearable
                  style="width:180px"
              >
                <template #prefix>
                  <div class="select-prefix">操作类型:</div>
                </template>
                <el-option
                    v-for="dict in sys_oper_type"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item prop="status">
              <el-select
                  v-model="queryParams.status"
                  @change="handleQuery"
                  clearable
                  style="width:180px"
              >
                <template #prefix>
                  <div class="select-prefix">状态:</div>
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
            <el-button icon="Delete" @click="handleDelete" v-hasPermi="['log:opLog:remove']" :disabled="multiple">删除
            </el-button>
<!--            <el-button icon="Close" @click="handleClean" v-hasPermi="['log:opLog:remove']">清空-->
<!--            </el-button>-->
            <el-button icon="Download" @click="handleExport" v-hasPermi="['log:opLog:export']">导出
            </el-button>
            <el-button icon="Refresh" @click="getList" class="refresh-button"/>
          </div>
        </div>
        <!--表格-->
        <div class="my-table">
          <el-table ref="tables" v-loading="loading" :data="logList" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="70"/>
            <el-table-column label="日志编号" prop="operId" width="200"/>
            <el-table-column label="系统模块" prop="title" width="250"/>
            <el-table-column label="操作类型" prop="businessType" width="250">
              <template #default="scope">
                <dict-tag :options="sys_oper_type" :value="scope.row.businessType"/>
              </template>
            </el-table-column>
            <el-table-column label="状态" prop="status" width="250">
              <template #default="scope">
                <div class="col-status">
                  <el-icon class="green" v-if="scope.row.status == constants.SUCCESS">
                    <SuccessFilled/>
                  </el-icon>
                  <el-icon class="red" v-if="scope.row.status != constants.SUCCESS">
                    <CircleCloseFilled/>
                  </el-icon>
                  <span>{{ dictFormat(scope.row.status + '', sys_common_status) }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作人" prop="operName" :show-overflow-tooltip="true" width="250"/>
            <el-table-column label="操作时间" prop="operTime" min-width="200">
              <template #default="scope">
                <span>{{ parseTime(scope.row.operTime) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" class-name="fixed-width">
              <template #default="scope">
                <el-button type="primary" link @click="handleView(scope.row)" >
                详情
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

      <!-- 操作日志详情 -->
      <el-dialog title="操作日志详情" v-model="open" width="700px" append-to-body :close-on-click-modal="false" align-center>
        <table summary="table" class="detail-tb">
          <th style="display: none"></th>
          <tr>
            <td class="title" style="width: 90px">操作时间</td>
            <td class="content">{{ parseTime(form.operTime) }}</td>
          </tr>
          <tr>
            <td class="title">操作人</td>
            <td class="content">{{ form.operName }}</td>
          </tr>
          <tr>
            <td class="title">系统模块</td>
            <td class="content">{{ form.title }}</td>
          </tr>
          <tr>
            <td class="title">方法名称</td>
            <td class="content">{{ form.method }}</td>
          </tr>
          <tr>
            <td class="title">状态</td>
            <td class="content">
              <div class="col-status">
                <el-icon class="green" v-if="form.status == constants.SUCCESS">
                  <SuccessFilled/>
                </el-icon>
                <el-icon class="red" v-if="form.status != constants.SUCCESS">
                  <CircleCloseFilled/>
                </el-icon>
                <span>{{ dictFormat(form.status + '', sys_common_status) }}</span>
              </div>
            </td>
          </tr>
          <tr>
            <td class="title">请求参数</td>
            <td class="content">{{ form.operParam }}</td>
          </tr>
          <tr v-if="form.status === constants.SUCCESS">
            <td class="title">返回参数</td>
            <td class="content">{{ form.jsonResult || '-'}}</td>
          </tr>
          <tr v-if="form.status === constants.FAILED">
            <td class="title">异常信息</td>
            <td class="content">{{ form.errorMsg }}</td>
          </tr>
        </table>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="open = false">关 闭</el-button>
          </div>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup name="OpLog">
import {list, delOperlog, cleanOperlog} from "@/api/system/opLog";
// 常量
import {constants} from "@/utils/constants";
const router = useRouter();
const {proxy} = getCurrentInstance();
const {sys_common_status, sys_oper_type} = proxy.useDict("sys_common_status", "sys_oper_type");

const loading = ref(true);  // 遮罩层
const ids = ref([]);  // 选中数组
const multiple = ref(true);  // 非多个禁用
const open = ref(false);  // 是否显示弹出层
const total = ref(0); // 总条数
const logList = ref([]); // 表格数据
const dateRange = ref([]);  // 日期范围
const titleOptions = constants.TITLE_OPTIONS; // 功能模块选项
// 表单信息
const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    title: undefined,
    businessType: undefined,
    status: undefined
  },
  queryRules: {
    operName: [
      {max: 30, message: '操作人不可超过30字', trigger: 'change'},
    ],
  },
});

const {form, queryParams, queryRules} = toRefs(data);

/** 查询登录日志 */
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
  ids.value = selection.map(item => item.operId)
  multiple.value = !selection.length
}

/** 详情按钮操作 */
function handleView(row) {
  open.value = true;
  form.value = Object.assign({}, row);
}

/** 删除按钮操作 */
function handleDelete(row) {
  const operIds = row.operId || ids.value;
  proxy.$modal.confirm('是否确认删除日志编号为"' + operIds + '"的数据项？').then(function () {
    return delOperlog(operIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  });
}

/** 清空按钮操作 */
function handleClean() {
  proxy.$modal.confirm('是否确认清空所有操作日志数据项？').then(function () {
    return cleanOperlog();
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("清空成功");
  });
}

/** 导出按钮操作 */
function handleExport() {
  if (total.value > 0) {
    proxy.$modal.confirm('即将导出' + total.value + '条记录，是否确认？').then(() => {
      proxy.download('system/opLog/export', {
        ...queryParams.value
      }, `operlog_${new Date().getTime()}.xlsx`)
    })
  } else {
    proxy.$modal.msgWarning("当前查询条件下的记录为空！");
  }
}

getList();
</script>

<style lang="scss" scoped>
// 日期选择控件
:deep(.ncdb-tb) {
  .top {
    .el-range-editor {
      &::before {
        content: '操作时间：' !important;
      }
    }
  }
}

// 操作日志详情
.detail-tb {
  table-layout: fixed;

  // 操作日志详情 标题
  .title {
    text-overflow: ellipsis;
    white-space: nowrap;
    overflow: hidden;
    width: 92px !important;
  }
}

//操作列按钮边距
.el-button.is-link {
  padding: 0px !important
  }

//图标对齐
.el-icon{
  top:2px
}
</style>
