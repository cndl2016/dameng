<template>
  <div>
    <!--采集频率页面-->
    <div class="tab-container">
      <div class="total-desc">
        <div class="menu-name">采集频率</div>
      </div>
    </div>
    <!--页面主体内容-->
    <div class="app-container">
      <div class="ncdb-tb">
        <div class="my-table">
          <el-table v-loading="loading" :data="agentList">
            <el-table-column label="分组" prop="groupsName" />
            <el-table-column label="参数名" prop="name" />
            <el-table-column label="执行周期" prop="cronExpression">
              <template #default="scope">
                <el-select v-model="scope.row.cronExpression" @change="changeRow(scope.row)"
                  :disabled="!hasPermission(['base:agent:edit'])" placeholder="请选择执行周期" :clearable="false"
                  style="width: 320px">
                  <el-option v-for="dict in cron_expression" :key="dict.value" :label="dict.label" :value="dict.value" />
                </el-select>
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
<script setup name="Agent">
import { getAgentConfigPage, updateAgentConfig } from "@/api/system/agent/index"
import { checkPermission } from "@/utils/checkPremi"
const { proxy } = getCurrentInstance();
const { cron_expression } = proxy.useDict("cron_expression");

const agentList = ref([]); // 数据集
const loading = ref(true); // 遮罩层
const total = ref(0); // 总条数
// 表单信息
const data = reactive({
  // 查询参数
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    keywords: undefined,
    parameterName: ""
  },
})
const { queryParams } = toRefs(data);

/* 权限校验 */
function hasPermission(value) {
  return checkPermission(value);
}

/** 查询采集列表 */
function getList() {
  loading.value = true;
  getAgentConfigPage(queryParams.value).then(response => {
    agentList.value = response.data.records;
    total.value = response.data.total;
  }
  ).finally(() => {
    loading.value = false;
  });
}

/** 列修改回调函数 */
function changeRow(row) {
  loading.value = true;
  updateAgentConfig(row).then(() => {
    // 成功
    proxy.$modal.msgSuccess("设置成功");
  }).finally(() => {
    loading.value = false;
  });
}

// 页面初始化时调用
onMounted(() => {
  getList();
})
</script>

<style lang="scss" scoped>

.el-form-item--default {
  margin-bottom: 0px;
}

</style>
