<!-- 告警模板配置界面 -->
<template>
  <div>
    <div class="ncdb-tb">
      <!--顶部搜索/操作栏-->
      <div class="top">
        <!--左侧搜索条件-->
        <div></div>
        <!--右侧新增按钮-->
        <div class="top-right">
          <el-button @click="handleNotice" v-if="checkPermission(addNoticePermission)">
            <svg-icon icon-class="dm-notice" style="margin-right: 4px" />通知方式
          </el-button>
          <el-button @click="handleApply" v-if="!isSingleInstance && checkPermission(applyPermission)">
            <svg-icon icon-class="tree" style="margin-right: 4px" />
            {{ alarmType === 'device' ? '应用到其他设备' : '应用到其他实例' }}
          </el-button>
          <el-button @click="toAlarmHis" v-hasPermi="['log:alarm:list']" v-if="!isSingleInstance">
            <svg-icon icon-class="dm-alarmLog" style="margin-right: 4px" />告警日志
          </el-button>
          <el-button icon="Refresh" @click="getList" class="refresh-button" />
        </div>
      </div>
      <!--告警项列表-->
      <div class="my-table">
        <el-table v-loading="loading" :data="alertConfigList">
          <el-table-column label="告警项" :show-overflow-tooltip="true" prop="ruleName" />
          <el-table-column label="是否启用" width="120" prop="status">
            <template #default="scope">
              <el-switch v-model="scope.row.status" :active-value="alertRuleStatusEnum.ENABLE"
                :inactive-value="alertRuleStatusEnum.DISABLE" @change="handleStatusChange(scope.row)">
              </el-switch>
            </template>
          </el-table-column>
          <el-table-column label="阈值" width="240">
            <template #default="scope">
              <div class="alarmThreshold">
                <div class="fatal" v-if="scope.row.alertParameters[0].fatalStatus !== null">
                  <div
                    :class="[(scope.row.status === alertRuleStatusEnum.DISABLE || scope.row.alertParameters[0].fatalStatus === false) ? 'value-black' : 'value']"
                    v-if="scope.row.alertParameters[0].configType === 'on-off'">
                    高风险
                  </div>
                  <div
                    :class="[(scope.row.status === alertRuleStatusEnum.DISABLE || scope.row.alertParameters[0].fatalStatus === false) ? 'value-black' : 'value']"
                    v-else>
                    {{
                      alarmConditionEscapeEnum.getDescFromValue(scope.row.alertParameters[0].logic) +
                      scope.row.alertParameters[0].fatal + ' ' +
                      scope.row.alertParameters[0].valueUnitCn
                    }}
                  </div>
                </div>
                <div class="warning" v-if="scope.row.alertParameters[0].warningStatus !== null">
                  <div
                    :class="[(scope.row.status === alertRuleStatusEnum.DISABLE || scope.row.alertParameters[0].warningStatus === false) ? 'value-black' : 'value']"
                    v-if="scope.row.alertParameters[0].configType === 'on-off'">
                    警告
                  </div>
                  <div
                    :class="[(scope.row.status === alertRuleStatusEnum.DISABLE || scope.row.alertParameters[0].warningStatus === false) ? 'value-black' : 'value']"
                    v-else>
                    {{
                      alarmConditionEscapeEnum.getDescFromValue(scope.row.alertParameters[0].logic) +
                      scope.row.alertParameters[0].warning + ' ' +
                      scope.row.alertParameters[0].valueUnitCn
                    }}
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="持续时间" width="120" align="left">
            <template #default="scope">
              <span>{{ scope.row.alertParameters[0].duration }}
                {{ alertDurationUnitEnum.getDescFromValue(scope.row.alertParameters[0].durationUnit) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="通知方式" width="240" align="left">
            <template #default="scope">
              <span class="method-des" v-for="(item, index) in scope.row.sendMethodCheckedList">
                {{ sendMethodEnum.getDescFromValue(item) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="scope">
              <el-button type="primary" style="padding: 0px !important" link @click="handleConfig(scope.row)" v-if="checkPermission(configPermission)">配置
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 配置对话框-->
    <el-dialog title="配置告警项" v-model="openConfig" width="600px" append-to-body align-center :close-on-click-modal="false">
      <!-- 主体表单 -->
      <el-form ref="formConfigRef" :model="formConfig" label-width="200px">
        <el-form-item :label="formParam.memo">
          <div v-if="formParam.fatalStatus !== null">
            <el-input-number v-model="formParam.fatal" class="number" v-if="formParam.configType !== 'on-off'" :min="1"
              controls-position="right" style="width: 140px;" :max="formParam.valueMax" @change="handleFatalValueChange(formParam)"/>
            <span class="ant-input-suffix row-span-five">{{ formParam.valueUnitCn }}</span>
            <el-checkbox label="fatal" v-model="formParam.fatalStatus" class="row-span">高风险</el-checkbox>
          </div>
          <div v-if="formParam.warningStatus !== null" style="padding-top: 5px;">
            <el-input-number v-model="formParam.warning" class="number" v-if="formParam.configType !== 'on-off'"
              :min="1" controls-position="right" style="width: 140px;" :max="formParam.valueMax" @change="handleWarnValueChange(formParam)"/>
            <span class="ant-input-suffix row-span-five">{{ formParam.valueUnitCn }}</span>
            <el-checkbox label="warning" v-model="formParam.warningStatus" class="row-span">低风险</el-checkbox>
          </div>
        </el-form-item>
        <el-form-item label="持续时间" prop="duration">
          <el-input-number v-model="formParam.duration" class="number" :disabled="formParam.configType === 'on-off'"
          :min="0" :max="30" controls-position="right" style="width: 140px;" @change="handleDurationValueChange(formParam)"/>
          <span class="ant-input-suffix row-span-five">{{ alertDurationUnitEnum.getDescFromValue(formParam.durationUnit)
          }}</span>
        </el-form-item>

        <el-form-item label="邮箱">
          <el-select v-model="mailCheckeds" multiple collapse-tags>
            <el-option :label="item" :value="item" v-for="item in emailList"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <!-- 底部按钮 -->
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelConfig">取 消</el-button>
          <el-button type="primary" @click="submitConfig">确 定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 应用到其他设备对话框 -->
    <el-drawer :title="alarmType === 'device' ? '应用到其他设备' : '应用到其他实例'" v-model="openApply" @open="handleOpenDialog"
      :wrapper-closable="false" @close="cancelApply" append-to-body :size="560">
      <div class="drawer__content">
        <!--主体表单-->
        <el-form ref="form" label-width="auto">
          <el-input v-model="filterTextApply" maxlength="30" placeholder="请输入IP" clearable prefix-icon="Search" />
          <div class="head-container">
            <el-tree :data="treeData" node-key="id" ref="applyTree" show-checkbox highlight-current default-expand-all
              :expand-on-click-node="false" :filter-node-method="filterNodeApply">
              <template #default="{ node, data }">
                <div class="custom-tree-node">
                  <!--树节点-->
                  <span class="node-label">
                    <div class="name">{{ alarmType === 'device' ? data.nodeName : data.label }}</div>
                  </span>
                </div>
              </template>
            </el-tree>
          </div>
        </el-form>
      </div>
      <!--底部按钮-->
      <div class="drawer__footer">
        <el-button @click="cancelApply">取 消</el-button>
        <el-button type="primary" @click="submitApply">确 定</el-button>
      </div>
    </el-drawer>
    <!-- 添加通知对象弹窗 -->
    <AddNotice ref="addNotice" :emailData="emailData" :emailList="emailList" @refreshNoticeList="getNoticeList"/>
  </div>
</template>
<script setup>
import { getListByObjectId, addConfig, enableConfig, addApply } from "@/api/base/alertConfig";
import { getAll } from "@/api/base/alarmMessage";
import sendMethodEnum from '@/views/enum/sendMethodEnum'
import alertRuleStatusEnum from "@/views/enum/alertRuleStatusEnum"
import alertDurationUnitEnum from "@/views/enum/alertDurationUnitEnum"
import alarmConditionEscapeEnum from "@/views/enum/alarmConditionEscapeEnum"
import AddNotice from "@/views/components/addNotice"
import { checkPermission } from "@/utils/checkPremi"
const { proxy } = getCurrentInstance();
// 是否显示应用到其他设备
const openApply = ref(false);
// 通知方式列表
const noticeConfigList = ref([]);
// 配置列表
const alertConfigList = ref([]);
// 遮罩层
const loading = ref(true);
// 是否打开配置对话框
const openConfig = ref(false);
// 是否启用
const status = ref("0");
// 通知方式添加权限
const addNoticePermission = ref([]);
// 告警应用权限
const applyPermission = ref([]);
// 告警配置权限
const configPermission = ref([]);
// 邮箱数据集合
const emailList = ref([]);
// 邮箱对象
const emailData = ref({});
// 选择的邮箱
const mailCheckeds = ref([]);
// 邮箱--通知方式的id
const noticeMailId = ref(undefined);
const selectNoticeList = ref([]);
// 默认通知方式
const sendMethod = ref("0");
// 左侧树过滤文本
const filterTextApply = ref('');
const data = reactive({
  // 配置表单
  formConfig: {},
  // 配置参数对象
  formParam: {},
  // 查询参数
  queryParams: {
    ruleName: undefined,
    objectId: undefined,
    tableName: undefined
  },
})
const { formConfig, formParam, queryParams } = toRefs(data);

/** 节点树过滤 */
watch(filterTextApply, (val) => {
  proxy.$refs["applyTree"].filter(val);
});

const props = defineProps({
  alarmType: {
    type: String,
    default: () => '',
  }, // 告警类型：设备/实例
  selectNodeId: {
    default: () => '',
  }, // 左侧选中的节点id
  tableName: {
    type: String,
    default: () => '',
  }, // 表名称
  treeData: {
    type: Array,
    default: () => [],
  }, // 列表树数据
  isSingleInstance: {
    type: Boolean,
    default: () => false,
  }, // 是否为单实例展示
  selectDb: {
    default: () => null,
  }, // 左侧选中的实例数据库
});
const { alarmType, selectNodeId, tableName, treeData, isSingleInstance, selectDb } = toRefs(props);

watch(selectNodeId, (val) => {
  getList();
});

watch(selectDb, (val) => {
  getList();
});

/** 通知方式按钮操作 */
function handleNotice() {
  getNoticeList();
  proxy.$refs.addNotice.initPage();
}

/** 应用到其他设备按钮操作 */
function handleApply() {
  openApply.value = true
}

/** 告警日志跳转 */
function toAlarmHis() {
  proxy.$router.push({
    path: '/log/alarmLog',
    query: { type: alarmType.value }
  })
}

/** 查询配置列表 */
function getList() {
  loading.value = true;
  queryParams.value.objectId = selectNodeId.value;
  queryParams.value.dbName = selectDb.value;
  queryParams.value.tableName = tableName.value;
  // 查询配置列表
  getListByObjectId(queryParams.value).then(response => {
    alertConfigList.value = response.data;
  }).finally(() => {
    loading.value = false
  })
}

/** 启用状态修改 */
function handleStatusChange(row) {
  if (row.isDefaultRule && selectNodeId != -1) {
    // 通过指定设备修改默认告警项
    row.alertParameters[0].fatalStatus = row.status === alertRuleStatusEnum.ENABLE;
    if(row.alertParameters[0].warningStatus != null) row.alertParameters[0].warningStatus = row.status === alertRuleStatusEnum.ENABLE;
    let arr = []
    arr.push(JSON.parse(JSON.stringify(row.alertParameters[0])))
    let data = {
      alertType: alarmType.value,
      alertConfig: JSON.parse(JSON.stringify(row)),
      alertParameters: arr,
      selectNoticeList: [],
      dbName: selectDb.value
    }
    // 新增配置项
    addConfig(data).then(() => {
      proxy.$modal.msgSuccess("配置成功")
    }).finally(() => {
      getList()
    })
  } else {
    // 默认告警项修改
    const data = {
      id: row.id,
      status: row.status,
      alertType: alarmType.value,
      selectObjectId: selectNodeId.value,
    }
    enableConfig(data).then(() => {
      proxy.$modal.msgSuccess("配置成功")
    }).finally(() => {
      getList()
    })
  }
}

/** 配置按钮操作 */
function handleConfig(row) {
  openConfig.value = true;
  mailCheckeds.value = []
  formConfig.value = row;
  formParam.value = JSON.parse(JSON.stringify(row.alertParameters[0]));
  status.value = formConfig.value.status
  if (row.emailRecipient != null && row.emailRecipient.length > 0) {
    let maillArr = row.emailRecipient.split(',')
    for (let i = 0; i < maillArr.length; i++) {
      let flag = isInArray(emailList.value, maillArr[i])
      if (flag) {
        mailCheckeds.value.push(maillArr[i])
      }
    }
  }
}

/** 取消按钮 */
function cancelConfig() {
  openConfig.value = false
  getList()
  resetConfig()
}

/** 清空配置页面 */
function resetConfig() {
  mailCheckeds.value = []
}

/** 配置确定按钮 */
function submitConfig() {
  selectNoticeList.value = [];
  formConfig.value.status = status.value
  // 校验
  if (
    (formParam.value.logic === alarmConditionEscapeEnum.GT || formParam.value.logic === alarmConditionEscapeEnum.GE)
    && (formParam.value.configType === "on-off-value" || formParam.value.configType === "on-off-value-time")
    && formParam.value.fatal <= formParam.value.warning
  ) {
    proxy.$modal.msgError("规则配置错误：高风险阈值必须大于低风险阈值！");
    return
  }

  if (
    (formParam.value.logic === alarmConditionEscapeEnum.LT || formParam.value.logic === alarmConditionEscapeEnum.LE)
    && (formParam.value.configType === "on-off-value" || formParam.value.configType === "on-off-value-time")
    && formParam.value.warning <= formParam.value.fatal
  ) {
    proxy.$modal.msgError("规则配置错误：低风险阈值必须大于高风险阈值！");
    return
  }

  // 组装参数
  let arr = []
  arr.push(formParam.value)

  if (mailCheckeds.value != null && mailCheckeds.value.length > 0) {
    selectNoticeList.value.push({ alarmMessageId: noticeMailId.value, recipient: mailCheckeds.value.join(",") })
  }
  let data = {
    alertType: alarmType.value,
    alertConfig: formConfig.value,
    alertParameters: arr,
    selectNoticeList: selectNoticeList.value,
    dbName: selectDb.value
  }
  // 配置
  addConfig(data).then(response => {
    getList()
    proxy.$modal.msgSuccess("配置成功")
  }).finally(() => {
    openConfig.value = false
    resetConfig()
  })
}

/** 查询通知方式列表 */
function getNoticeList() {
  // 通知方式选择数据清空
  emailList.value = []
  getAll().then(response => {
    noticeConfigList.value = response.data;
    noticeConfigList.value.forEach(item => {
      if (item.sendMethod === sendMethodEnum.MAIL) {
        if (item.httpUrl != null && item.httpUrl.length > 0 && typeof item.httpUrl == "string") {
          emailList.value = item.httpUrl.split(',')
          emailData.value = item;
          noticeMailId.value = item.id
        }
      }
    });
  })
}

/** 字符串是否存在在数组中 */
function isInArray(arr, value) {
  for (let i = 0; i < arr.length; i++) {
    if (value === arr[i]) {
      return true;
    }
  }
  return false;
}

/** 表单打开操作 */
function handleOpenDialog() {
  filterTextApply.value = "";
  nextTick(() => {
    // 清空提示
    proxy.$refs['form'].clearValidate()
  })
}

/** 应用到其他设备取消按钮 */
function cancelApply() {
  proxy.$refs.applyTree.setCheckedKeys([])
  openApply.value = false
}

/** 节点过滤 */
function filterNodeApply(value, data) {
  if (!value) return true;
  // 节点名称模糊匹配
  return data.nodeName.indexOf(value) !== -1
}

// 遍历树形结构，全选或反选
function checkApplyNodes(rootNode, flag) {
  rootNode.checked = flag
  if (rootNode.childNodes.length === 0) {
    return;
  } else {
    let childNodes = rootNode.childNodes
    for (let i = 0; i < childNodes.length; i++) {
      checkApplyNodes(childNodes[i], flag)
    }
  }
}

/** 应用到其他设备确定按钮 */
function submitApply() {
  let arr = []
  let checkedNodes = proxy.$refs.applyTree.getCheckedNodes()
  for (let i = 0; i < checkedNodes.length; i++) {
    if (alarmType.value === 'device') {
      if (checkedNodes[i].isDevice) arr.push(checkedNodes[i].id)
    } else {
      if (checkedNodes[i].level === 2) arr.push(checkedNodes[i].id)
    }
  }
  if (arr.length === 0) {
    proxy.$modal.msgError("请选择应用对象！");
    return
  }

  let data = {
    alertType: alarmType.value,
    selectObjectId: selectNodeId.value,
    tableName: tableName.value,
    objectIdList: arr
  }

  // 应用
  addApply(data).then(response => {
    proxy.$modal.msgSuccess("设置成功")
  }).finally(() => {
    openApply.value = false
  })
}

/* 低风险阈值非空设置 */
function handleWarnValueChange(data) {
  if(data.warning == null) {
    data.warning = 1;
  }
}

/* 高风险阈值非空设置 */
function handleFatalValueChange(data) {
  if(data.fatal == null) {
    data.fatal = 1;
  }
}

/* 持续时长非空设置 */
function handleDurationValueChange(data) {
  if(data.duration == null) {
    data.duration = 0;
  }
}

onMounted(() => {
  console.log(alarmType.value)
  addNoticePermission.value = ['device:alarm:add']
  applyPermission.value = ['device:alarm:apply']
  configPermission.value = ['device:alarm:config']
      // 获取配置列表
  getList()
  // 获取通知方式列表
  getNoticeList()
})
</script>
<style scoped lang="scss">
/* 实例树 */
.head-container {
  margin-top: 16px;

  /* 自定义树节点 */
  .custom-tree-node {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    padding-right: 8px;
    margin-left: -4px;

    /* 树节点名称 */
    .node-label {
      display: flex;

      /* 名称 */
      .name {
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }
  }

  /* 树控件，当前选中高亮样式 */
  :deep(.el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content) {
    background-color: #eef2fe;

    .node-label {
      .name {
        color: #3363FF;
      }
    }
  }

  /* 树控件内容高度 */
  :deep(.el-tree-node__content) {
    height: 40px !important;
  }
}


.alarmThreshold {
  display: flex;
  justify-content: flex-start;

  .fatal {
    display: flex;
    align-items: center;
    margin-right: 8px;

    &.disabled {
      .value {
        background: #f5f5f5;
        color: rgba(0, 0, 0, 0.25);
      }

      .icon {
        color: rgba(0, 0, 0, 0.25) !important;
      }

      .time {
        color: rgba(0, 0, 0, 0.25);
      }
    }

    .value {
      height: 24px;
      background: #ff4f4f1a;
      border-radius: 2px;
      text-align: center;
      line-height: 24px;
      color: #ff4f4f;
      padding: 2px 10px;
    }

    .value-black {
      height: 24px;
      background: #A3A1A11a;
      border-radius: 2px;
      color: #99999980;
      margin-right: 8px;
      padding: 2px 10px;
    }

    .situation {
      background: #CCC888;
    }

    .icon {
      margin-right: 4px;
    }

    .time {
      font-weight: 400;
      color: rgba(0, 0, 0, 0.65);
    }
  }

  .warning {
    display: flex;
    align-items: center;

    &.disabled {
      .value {
        background: #f5f5f5;
        color: rgba(0, 0, 0, 0.25);
      }

      .icon {
        color: rgba(0, 0, 0, 0.25) !important;
      }

      .time {
        color: rgba(0, 0, 0, 0.25);
      }
    }

    .value {
      height: 24px;
      background: #faaa0a1a;
      border-radius: 2px;
      color: #faaa0a;
      margin-right: 8px;
      padding: 2px 10px;
    }

    .value-black {
      height: 24px;
      background: #A3A1A11a;
      border-radius: 2px;
      color: #99999980;
      margin-right: 8px;
      padding: 2px 10px;
    }

    .situation {
      background: #CCC888;
    }

    .icon {
      margin-right: 4px;
    }

    .time {
      font-weight: 400;
      color: rgba(0, 0, 0, 0.65);
    }
  }
}

/* 数字文本输入框 */
:deep(.el-input-number) {
  width: 100%;
}

/* 企业版：穿梭框样式 */
:deep(.el-transfer-panel) {
  width: 168px !important;
}

:deep(.el-transfer__buttons) {
  padding: 0 20px;
  width: 96px !important;
}

:deep(.el-transfer .el-button + .el-button) {
  margin-left: 0 !important;
}

.row-span {
  padding-left: 20px;
}

.row-span-five {
  padding-left: 20px;
}

.selectMethod .el-radio-button__inner {
  width: 60px;
}

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
</style>
