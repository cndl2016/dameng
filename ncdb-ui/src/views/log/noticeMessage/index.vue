<template>
  <div>
    <div class="tab-container">
      <div class="total-desc">
        <div class="menu-name">告警通知</div>
      </div>
    </div>
    <div class="app-container">
      <div class="ncdb-tb">
        <div class="top">
          <!-- 搜索框-->
          <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="68px" :rules="queryRules">
            <el-form-item prop="title">
              <el-input v-model="queryParams.title" placeholder="搜索消息标题" @input="handleQuery" suffix-icon="Search"
                style="width:200px !important" />
            </el-form-item>
            <!-- 告警级别 -->
            <el-form-item prop="alertLevel">
              <el-select v-model="queryParams.alertLevel" @change="handleQuery" clearable style="width: 200px;">
                <template #prefix>
                  <div class="select-prefix">告警级别:</div>
                </template>
                <el-option v-for="option in alarmLevelOption" :key="option.optionValue" :label="option.optionLabel"
                  :value="option.optionValue" />
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
          <!-- 操作按钮-->
          <div class="top-right">
            <el-button icon="Download" @click="handleExport" v-hasPermi="['log:noticeMessage:export']">导出
            </el-button>
            <el-button icon="Refresh" @click="getList" class="refresh-button" />
          </div>
        </div>
        <!-- table 数据渲染 -->
        <div class="my-table">
          <el-table ref="tables" v-loading="loading" :data="list">
            <el-table-column label="消息标题" prop="title" :show-overflow-tooltip="true" width="240" />
            <el-table-column label="消息内容" prop="content" :show-overflow-tooltip="true" min-width="240" />
            <el-table-column label="告警级别" prop="alertLevel" width="100">
              <template #default="scope">
                <span class="el-tag el-tag--danger" v-if="scope.row.alertLevel === 'fatal'">
                  高风险
                </span>
                <span class="el-tag el-tag--warning" v-else>
                  低风险
                </span>
              </template>
            </el-table-column>
            <el-table-column label="接收人" prop="recipient" :show-overflow-tooltip="true" width="240" />
            <el-table-column label="通知方式" prop="noticeMethod" width="160" :show-overflow-tooltip="true">
              <template #default="scope">
                <span class="method-des">
                  {{ sendMethodEnum.getDescFromValue(scope.row.noticeMethod) }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="通知时间" prop="noticeTime" width="160">
              <template #default="scope">
                <span>{{ parseTime(scope.row.noticeTime) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="发送状态" prop="isSuccess" width="100">
              <template #default="scope">
                <span v-if="scope.row.isSuccess == 1">
                  <svg-icon icon-class="dm-success" style="font-size: 16px" />
                  成功
                </span>
                <span v-else>
                  <svg-icon icon-class="dm-warning" style="font-size: 16px" />
                  异常
                </span>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <!-- 分页组件 -->
        <pagination v-show="total > 10" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
          @pagination="getList" />
      </div>
    </div>
  </div>
</template>
<script setup>
import { getNoticeMessagePage } from "@/api/base/noticeMessage/index"
import sendMethodEnum from '@/views/enum/sendMethodEnum'
const { proxy } = getCurrentInstance();
const { job_result_status } = proxy.useDict("job_result_status");

// 日期范围
const dateRange = ref([]);
// loading
const loading = ref(true);
// 总条数
const total = ref(0);
// 表格数据
const list = ref([]);
// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  title: undefined
});
// 告警发生日期选项集合
const timeOption = [
  {
    optionLabel: "近6个小时",
    optionValue: 6,
  },
  {
    optionLabel: "近24个小时",
    optionValue: 24,
  },
];
// 告警级别选项集合
const alarmLevelOption = [
  {
    optionLabel: "低风险",
    optionValue: 'warning',
  },
  {
    optionLabel: "高风险",
    optionValue: 'fatal',
  }
];
const queryRules = ref({
  title: [
    { max: 30, message: "过滤条件过长", trigger: 'change' },
  ],
})

/** 查询告警通知 */
function getList() {
  loading.value = true;
  getNoticeMessagePage(proxy.addDateRange(queryParams.value, dateRange.value)).then(response => {
    list.value = response.data.records;
    total.value = response.data.total;
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
      getList();
    }
  })
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  proxy.resetForm("queryForm");
  handleQuery();
}

/** 导出按钮操作 */
function handleExport() {
  if (total.value > 0) {
    proxy.$modal.confirm("即将导出" + total.value + "条记录，是否确认？").then(() => {
      proxy.download('/base/alert/notice/message/export', {
        ...queryParams.value
      }, `noticeMessage_${new Date().getTime()}.xlsx`)
    })
  } else {
    proxy.$modal.msgWarning("当前查询条件下的记录为空！");
  }
}

onMounted(() => {
  getList();
})
</script>
<style lang="scss" scoped>
.method-des {
  display: inline-block;
  width: 80px;
  height: 32px;
  border-radius: 2px;
  border: 1px solid #DDD;
  color: #666666;
  font-family: 'Source Han Sans CN', sans-serif;
  font-size: 14px;
  font-style: normal;
  font-weight: 400;
  line-height: 32px;
  text-align: center;
  margin-right: 10px;
}

// ncdb-tb 顶部操作时间样式
:deep(.ncdb-tb) {
  .top {
    .el-range-editor {
      &::before {
        content: '通知时间：' !important;
      }
    }

    .el-range-editor-en {
      &::before {
        content: 'Date：' !important;
      }
    }
  }
}

// detail-tb 标题样式
.detail-tb .title {
  width: 92px;
}
</style>