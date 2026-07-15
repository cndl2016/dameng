<template>
  <div>
    <!-- 标题容器 -->
    <div class="tab-container" v-if="!isSingleInstance">
      <!-- 返回按钮 -->
      <div class="goBack" @click="goBack" v-if="queryType != undefined">
        <div class="icon">
          <svg-icon icon-class="dm-goBack" />
        </div>
        <!-- 动态显示标题名称 -->
        {{ queryType === 'device' ? "设备告警" : "实例告警" }}
      </div>
      <div class="total-desc" v-else>
        <div class="menu-name">{{ "告警日志" }}</div>
      </div>
    </div>
    <!-- 主体容器 -->
    <div class="app-container">
      <div class="ncdb-tb">
        <!-- 搜索+操作栏 -->
        <div class="top">
          <el-form :model="queryParams" ref="queryForm" :inline="true" @submit.native.prevent>
            <!-- 告警对象 -->
            <el-form-item prop="searchValue">
              <el-input v-model="queryParams.searchValue" placeholder="搜索告警对象/告警内容" @input="handleQuery"
                suffix-icon="Search" />
            </el-form-item>
            <!-- 告警级别 -->
            <el-form-item prop="alarmLevel">
              <el-select v-model="queryParams.alarmLevel" @change="handleQuery" clearable style="width: 200px;">
                <template #prefix>
                  <div class="select-prefix">告警级别:</div>
                </template>
                <el-option v-for="option in alarmLevelOption" :key="option.optionValue" :label="option.optionLabel"
                  :value="option.optionValue" />
              </el-select>
            </el-form-item>
            <!-- 发生时间 -->
            <el-form-item prop="time">
              <el-select v-model="queryParams.time" @change="handleQuery" clearable style="width: 200px;">
                <template #prefix>
                  <div class="select-prefix">发生时间:</div>
                </template>
                <el-option v-for="option in timeOption" :key="option.optionValue" :label="option.optionLabel"
                  :value="option.optionValue" />
              </el-select>
            </el-form-item>
            <!-- 重置按钮 -->
            <el-form-item>
              <el-button icon="RefreshLeft" type="primary" link @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>
          <div class="top-right">
            <el-button icon="Download" @click="handleExport" v-hasPermi="['log:alarm:list:export']">导出</el-button>
            <!-- 刷新按钮 -->
            <el-button icon="Refresh" @click="handleQuery" class="refresh-button" />
          </div>
        </div>
        <!-- table 数据渲染 -->
        <div class="my-table">
          <el-table v-loading="loading" :data="alarmHisList">
            <el-table-column label="告警项" prop="title" :show-overflow-tooltip="true" width="180">
            </el-table-column>
            <el-table-column label="告警对象" prop="resourceName" min-width="180" show-overflow-tooltip >
            </el-table-column>
            <el-table-column label="告警内容" prop="content" min-width="200" :show-overflow-tooltip="true" />
            <el-table-column label="告警开始时间" prop="createTime" width="180" />
            <el-table-column label="告警结束时间" prop="endTime" width="180">
              <template #default="scope">
                <span v-if="scope.row.activeState === 'live'">-</span>
                <span v-else>{{ scope.row.endTime }}</span>
              </template>
            </el-table-column>
            <el-table-column label="告警级别" prop="msgLevel" width="100">
              <template #default="scope">
                <span class="el-tag el-tag--warning" v-if="scope.row.msgLevel === 'warning'">低风险</span>
                <span class="el-tag el-tag--danger" v-else>高风险</span>
              </template>
            </el-table-column>
            <el-table-column label="告警状态" prop="activeState" width="100">
              <template #default="scope">
                <span v-if="scope.row.activeState === 'live'">未结束</span>
                <span v-else>已结束</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <!-- 分页组件 -->
        <pagination
          v-show="total>10"
          :total="total"
          v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize"
          @pagination="getList"
        />
      </div>
    </div>

    <!-- 实际阈值列表对话框 -->
    <el-dialog title="实际阈值列表" v-model="valueOpen" width="700px" append-to-body :close-on-click-modal="false"
      style="padding: 20px">
      <div class="my-table">
        <el-table :data="monitorValueList">
          <el-table-column label="发生时间" prop="createTime" min-width="120">
            <template #default="scope">
              <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="告警对象" prop="jobMessage" min-width="180" show-overflow-tooltip />
          <el-table-column label="实际阈值" prop="monitorValues" min-width="100">
            <template #default="scope">
              <!--  如果是设备或者节点离线告警，实际阈值展示空白-->
              <span v-if="scope.row.alarmCondition == '8' || scope.row.alarmCondition == '13'"> </span>
              <span v-else>{{ scope.row.monitorValues.toFixed(2) }}</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>
<script setup>
import { getListAlarmHis } from "@/api/base/alarmHis/index"
const { proxy } = getCurrentInstance();
// 来源
const queryType = ref(undefined);
// 数据库类型
const dbType = ref(undefined);
// 日期范围
const dateRange = ref([]);
// loading
const loading = ref(false);
// 告警日志列表
const alarmHisList = ref([]);
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
// 告警日志总数
const total = ref(0);
// 搜索条件
const queryParams = ref({
  pageNum: 1, // 页码
  pageSize: 10, // 每页条数
  dbType: undefined,  // 数据库类型
  searchValue: '',  // 告警对象
  time: '', // 告警发生时间
  alarmLevel: '',  // 告警级别
  resolveStatus: '1' // 是否发送告警通知：1 已发送，0 未发送
});
// 实际阈值列表
const monitorValueList = ref([]);
// 实际阈值列表对话框 是否打开
const valueOpen = ref(false)
const props = defineProps({
  isSingleInstance: {
    type: Boolean,
    default: () => false,
  }, // 是否为单对象展示
  curInstance: {
    default: () => {},
  }, // 左侧选中的节点id
});
const { curInstance, isSingleInstance } = toRefs(props);

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
  // 查询参数清空
  dateRange.value = [];
  queryParams.value.searchValue = '';
  queryParams.value.time = '';
  queryParams.value.alarmLevel = '';
  // 查询
  handleQuery();
}
// 导出按钮
function handleExport() {
  let tmpPageSize = queryParams.value.pageSize;
  if (total.value > 0) {
    proxy.$modal.confirm("即将导出" + total.value + "条记录，是否确认？").then(() => {
      queryParams.value.pageSize = total.value;
      proxy.download('/base/sysMessage/exportSysMessage', {
        ...queryParams.value
      }, `alarmHis_${new Date().getTime()}.xlsx`)
    }).catch(() => {
    }).finally(() => {
      queryParams.value.pageSize = tmpPageSize;
    });
  } else {
    proxy.$modal.msgWarning("当前查询条件下的告警日志记录为空！");
  }
}

// 查询列表
function getList() {
  loading.value = true;
  queryParams.value.dbType = dbType.value;
  if(isSingleInstance.value) queryParams.value.instanceId = curInstance.value.id;
  getListAlarmHis(proxy.addDateRange(queryParams.value, dateRange.value)).then(response => {
    alarmHisList.value = response.data.records;
    total.value = response.data.total;
  }).finally(() => {
    loading.value = false;
  });
}
// 返回
function goBack() {
    // 设备告警设置
    proxy.$router.push({
      path: '/device/deviceAlarmModel',
      query: { source: '0' }
    })

}

onMounted(() => {
  // 数据类型
  dbType.value = proxy.$route.query.source
  // 告警级别
  queryParams.value.alarmLevel = proxy.$route.query.alarmLevel === undefined ? '' : proxy.$route.query.alarmLevel
  // 时间范围
  queryParams.value.time = proxy.$route.query.time === undefined ? '' : parseInt(proxy.$route.query.time)
  // 类型
  queryType.value = proxy.$route.query.type
  // 查询
  getList();
})
</script>
