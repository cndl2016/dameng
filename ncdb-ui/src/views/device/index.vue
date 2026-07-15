<!-- 设备管理界面 -->
<template>
  <div>
    <!--顶部容器 -->
    <div class="tab-container">
      <div class="total-desc">
        <div class="menu-name">设备维护</div>
      </div>
    </div>
    <!--主体容器 -->
    <div class="app-container">
      <el-row :gutter="20">
        <!--设备树-->
        <el-col :span="4">
          <el-input v-model="filterText" maxlength="30" placeholder="请输入资源节点名称" clearable prefix-icon="Search" />
          <div class="head-container">
            <el-tree :data="treeData" node-key="id" ref="tree" :current-node-key="selectNodeId" highlight-current
              default-expand-all :expand-on-click-node="false" :filter-node-method="filterNode" @node-click="selectNode">
              <!-- 节点展示 -->
              <template #default="{ node, data }">
                <div class="custom-tree-node" v-if="!data.isEdit">
                  <el-tooltip class="item" popper-class="device-pop" effect="light" placement="top" :open-delay="400">
                    <template #content><b style="font-size: small">{{ data.nodeName }}</b></template>
                    <span class="node-label">
                      <div class="name">{{ data.nodeName }}</div>
                      <div class="num"><span>{{ data.deviceNum }}</span></div>
                    </span>
                  </el-tooltip>
                  <!--右侧按钮-->
                  <span class="node-icon">
                    <!--新增按钮：超过最大树层级or机房节点隐藏-->
                    <el-button type="primary" link icon="Plus" @click="() => appendNode(data)"
                      v-hasPermi="['device:node:add']"
                      v-show="!device_treelevel[0] || data.level < device_treelevel[0].value" />
                    <!--编辑按钮：根节点隐藏-->
                    <el-button type="primary" link icon="Edit" @click="() => editNode(data)"
                      v-hasPermi="['device:node:edit']" v-show="Number(data.id) !== constants.ROOT_DEVICE_NODE" />
                    <!--删除按钮：根节点隐藏-->
                    <el-button type="primary" link icon="Delete" @click="() => removeNode(data)"
                      v-hasPermi="['device:node:remove']" v-show="Number(data.id) !== constants.ROOT_DEVICE_NODE" />
                  </span>
                </div>
                <!-- 编辑状态 -->
                <div v-else>
                  <el-input :ref="data.inputRef || `inputRef${data.id}`" v-model="data.nodeName" placeholder="请输入节点名称"
                    @blur="handleUpdateNodeName(data)" />
                </div>
              </template>
            </el-tree>
          </div>
        </el-col>
        <!--设备数据-->
        <el-col :span="20">
          <div class="ncdb-tb">
            <!-- 设备统计信息 -->
            <div class="device-count-msg">
              <!-- 连接状态 -->
              <div class="msg-div">
                <div class="item">
                  <p class="text">连接正常</p>
                  <p class="number">{{ connSuccess }}</p>
                </div>
                <div class="divider"></div>
                <div class="item">
                  <p class="text">连接异常</p>
                  <p class="number">{{ connError }}</p>
                </div>
              </div>
              <!-- 安全状态 -->
              <div class="msg-div">
                <div class="item">
                  <p class="text">安全</p>
                  <p class="number">{{ safeCount }}</p>
                </div>
                <div class="divider"></div>
                <div class="item">
                  <p class="text">低风险</p>
                  <p class="number">{{ lowRisk }}</p>
                </div>
                <div class="divider"></div>
                <div class="item">
                  <p class="text">高风险</p>
                  <p class="number">{{ highRisk }}</p>
                </div>
              </div>
            </div>
            <!--顶部搜索/操作栏-->
            <div class="top">
              <!--左侧搜索条件-->
              <el-form :model="queryParams" ref="queryForm" :inline="true" :rules="queryRules">
                <el-form-item prop="deviceIp">
                  <el-input v-model="queryParams.deviceIp" placeholder="搜索设备IP" @input="handleQuery"
                    suffix-icon="Search" />
                </el-form-item>
                <el-form-item prop="connStatus">
                  <el-select v-model="queryParams.connStatus" @change="handleQuery" clearable style="width: 180px;">
                    <template #prefix>
                      <div class="select-prefix">连接状态:</div>
                    </template>
                    <el-option v-for="dict in device_connstatus" :key="dict.value" :label="dict.label"
                      :value="dict.value" />
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
              <!--右侧新增按钮-->
              <div class="top-right">
                <el-button type="primary" icon="Plus" @click="handleAdd" v-hasPermi="['device:add']">新增
                </el-button>
                <el-button icon="Refresh" @click="getList" class="refresh-button" />
              </div>
            </div>
            <!--设备列表-->
            <div class="my-table">
              <CustomTable :dataInfo="deviceList" :config="tableConfig" :total="total" :pageParams="queryParams"
                showPagination @paginationChange="getList" v-loading="loading" @buttonCommand="handleButtonCommand">
                <template #deviceIp="{ row }">
                  <el-button link type="primary" @click="deviceMonitor(row)">{{ row.deviceIp }}
                  </el-button>
                </template>
                <template #memUsing="{ row }">
                  <span v-if="row.memUsing !== null">{{ row.memUsing + "/" + row.memTotal }}</span>
                  <span v-else>{{ "-/" + row.memTotal }}</span>
                </template>
                <template #cpuUsing="{ row }">
                  <span v-if="row.cpuUsing !== null">{{ row.cpuUsing + "/" + row.cpuCore + '核'
                  }}</span>
                  <span v-else>{{ "-/" + row.cpuCore + '核' }}</span>
                </template>
                <template #diskUsing="{ row }">
                  <span v-if="row.diskUsing !== null">{{ row.diskUsing + "/" + row.diskTotal }}</span>
                  <span v-else>{{ "-/" + row.diskTotal }}</span>
                </template>
                <template #safeStatus="{ row }">
                  <el-tooltip placement="top" effect="light" v-if="row.safeStatus !== safeStatusTypeEnum.SAFE">
                    <template #content>
                      <div v-for="msg in row.messageList">{{ msg }}</div>
                    </template>
                    <dict-tag :options="safe_status" :value="row.safeStatus" />
                  </el-tooltip>
                  <dict-tag :options="safe_status" :value="row.safeStatus" v-else/>
                </template>
              </CustomTable>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
    <!-- 添加或修改对话框 -->
    <el-drawer title="修改设备" v-model="openUpdate" @open="handleUpdateDialog" append-to-body size="560"
      :close-on-click-modal="false">
      <div class="drawer__content">
        <!--主体表单-->
        <el-form ref="updateRef" :model="updateForm" :rules="updateRules" label-width="auto">
          <el-form-item label="所属机房" prop="nodeId">
            <el-tree-select v-model="updateForm.nodeId" :data="editTreeData" default-expand-all node-key="id"
              value-key="id" highlight-current :props="{ label: 'nodeName', value: 'id' }" :disabled="isUsed"
              placeholder="请选择所属机房" @change="treeChange" :check-strictly="true"/>
          </el-form-item>
          <!-- 手动输入设备信息 -->
          <el-form-item label="设备地址" prop="deviceIp">
            <el-input v-model="updateForm.deviceIp" disabled placeholder="请输入设备IP" style="width: 212px;" />
            <el-input-number v-model="updateForm.port" disabled controls-position="right" placeholder="请输入端口"
              style="width: 120px; margin-left: 16px;" />
          </el-form-item>
          <el-form-item label="用户名" prop="deviceSshUsr">
            <el-input disabled v-model="updateForm.deviceSshUsr" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="密码" prop="deviceSshPwd">
            <el-input v-model="updateForm.deviceSshPwd" show-password placeholder="请输入密码" clearable />
          </el-form-item>
        </el-form>
      </div>
      <!--底部按钮-->
      <div class="drawer__footer">
        <el-button @click="updateCancel">取 消</el-button>
        <el-button type="primary" @click="submitUpdateForm" :disabled="submitDisable">确 定</el-button>
      </div>
    </el-drawer>
    <!-- 新增设备抽屉框 -->
    <AddDevice ref="addDeviceRef" @addDeviceAfter="handleAddDeviceAfter" :editTreeData="editTreeData" />
  </div>
</template>
<script setup>
const { proxy } = getCurrentInstance();
import { debounce } from 'lodash'
import { getListDevice, saveOrUpdateDevice, deleteDeviceById } from "@/api/device/index";
import { getDeviceNodeTree, saveOrUpdateDeviceNode, deleteDeviceNode } from "@/api/deviceNode/index";
import { constants } from '@/utils/constants';
import deviceNodeTypeEnum from '@/views/enum/deviceNodeTypeEnum'
import deviceConnEnum from "@/views/enum/deviceConnEnum"
import safeStatusTypeEnum from "@/views/enum/safeStatusTypeEnum"
import { nextTick } from "vue";
import AddDevice from "@/views/device/addDevice"
const { device_connstatus, device_treelevel, arch_type, safe_status } = proxy.useDict(
  'device_connstatus', 'device_treelevel', 'arch_type', 'safe_status'
);
// 左侧树过滤文本
const filterText = ref('');
// 设备树
const treeData = ref([]);
// 左侧树选中ID
const selectNodeId = ref(undefined);
// 遮罩层
const loading = ref(true);
// 设备列表
const deviceList = ref([]);
// 总条数
const total = ref(0);
// 日期范围
const dateRange = ref([]);
// 遮罩层计时器
const timer = ref(undefined);
// 修改设备弹窗显示
const openUpdate = ref(false);
// 选择所属机房下列树数据源
const editTreeData = ref([]);
// 是否有节点使用
const isUsed = ref(false);
// 提交按钮禁用
const submitDisable = ref(true);
// 连接正常个数
const connSuccess = ref(0);
// 连接丢失个数
const connError = ref(0);
// 低风险个数
const lowRisk = ref(0);
// 高风险个数
const highRisk = ref(0);
// 安全个数
const safeCount = ref(0);
const data = reactive({
  // 查询参数
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    deviceIp: undefined,
    connStatus: undefined,
    nodeId: undefined,
  },
  // 查询校验
  queryRules: {
    deviceIp: [
      { max: 15, message: "ip过滤条件过长", trigger: "change" }
    ],
  },
  // 设备树过滤参数
  nodeParam: {
    rootId: constants.ROOT_DEVICE_PARENT_NODE,
  },
  // 修改表单
  updateForm: {},
  // 修改表单校验
  updateRules: {
    deviceSshUsr: [{ required: true, message: "用户名不能为空", trigger: "change" }],
    nodeId: [
      { required: true, message: "设备机房不能为空", trigger: "change" },
    ],
    deviceIp: [
      { required: true, message: "设备IP不能为空", trigger: "change" },
      {
        pattern: /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/,
        message: "设备IP格式不正确",
        trigger: "change"
      },
    ],
    port: [
      { required: true, message: "设备端口不能为空", trigger: "change" }
    ],
  },
})
const { queryParams, queryRules, nodeParam, updateForm, updateRules } = toRefs(data);
// 表格配置项
const tableConfig = ref([
  {
    label: '设备IP',
    prop: 'deviceIp',
    slotName: 'deviceIp',
    minWidth: 120
  },
  {
    label: '连接状态',
    prop: 'connStatus',
    type: 'enum',
    enumData: deviceConnEnum,
    minWidth: 100
  },
  {
    label: '安全状态',
    prop: 'safeStatus',
    slotName: 'safeStatus',
    minWidth: 100
  },
  {
    label: '节点数',
    prop: 'nodeNum',
  },
  {
    label: '架构类型',
    prop: 'archType',
    type: 'dict',
    dictData: arch_type
  },
  {
    label: '操作系统',
    prop: 'osName',
    minWidth: 120
  },
  {
    label: 'CPU信息',
    prop: 'cpuUsing',
    slotName: 'cpuUsing',
    minWidth: 140,
  },
  {
    label: '内存信息',
    prop: 'memUsing',
    slotName: 'memUsing',
    minWidth: 140,
  },
  {
    label: '磁盘信息',
    prop: 'diskUsing',
    slotName: 'diskUsing',
    minWidth: 140
  },
  {
    label: '创建时间',
    prop: 'createTime',
    width: 160,
  },
  {
    label: '操作',
    type: 'button',
    buttons: [{ type: 'primary', limits: ['device:edit'], command: 'update', name: '修改' },
    { type: 'primary', limits: ['device:remove'], command: 'remove', name: '删除', disabled: (row) => row.nodeNum > 0 }
    ],
    width: 120
  },
])

// 设备节点树过滤
watch(filterText, (val) => {
  proxy.$refs.tree.filter(val);
})

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  proxy.$refs["queryForm"].validate(valid => {
    if (valid) {
      getList()
    }
  })
}

/* 查询设备列表 */
function getList() {
  loading.value = true;
  queryParams.value.nodeId = selectNodeId;
  // 查询设备列表
  getListDevice(proxy.addDateRange(queryParams.value, dateRange.value)).then(response => {
    deviceList.value = response.data.records || [];
    // 统计设备告警和连接信息
    let success = 0;
    let error = 0;
    let safe = 0;
    let low = 0;
    let high = 0;
    deviceList.value.forEach(device => {
      // 统计连接状态
      device.connStatus == deviceConnEnum.SUCCESS_CONN ? success++ : error++;
      // 统计安全状态
      if (device.safeStatus == safeStatusTypeEnum.SAFE) {
        safe++;
      } else if (device.safeStatus == safeStatusTypeEnum.LOW_RISK) {
        low++;
      } else {
        high++;
      }
    });
    // 设置统计信息(先计算再统一赋值，防止页面统计值重置)
    connSuccess.value = success;
    connError.value = error;
    safeCount.value = safe;
    lowRisk.value = low;
    highRisk.value = high;
    total.value = response.data.total;
    getInfoDev()
  }
  ).finally(() => {
    loading.value = false
  })
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  proxy.resetForm("queryForm");
  handleQuery()
}

/* 刷新cpu等值 */
function getInfoDev() {
  for (let i = 0; i < deviceList.value.length; i++) {
    // 指定设备IP
    if (deviceList.value[i].connStatus !== deviceConnEnum.SUCCESS_CONN) {
      deviceList.value[i].cpuUsing = "-";
      deviceList.value[i].diskUsing = "-";
      deviceList.value[i].memUsing = "-"
    }
  }
}

/* 节点树过滤 */
function filterNode(value, data) {
  if (!value) return true;
  // 节点名称模糊匹配
  return data.nodeName.indexOf(value) !== -1
}

/* 树节点点击事件 */
function selectNode(data) {
  selectNodeId.value = data.id;
  getList()
}

/* 新增节点 */
function appendNode(data) {
  // 构建子节点
  const obj = {
    nodeName: '',
    upperId: data.id,
    isEdit: true,
    nodeType: deviceNodeTypeEnum.ROOM,
    isAdd: true,
    inputRef: `inputRef${data.id}_${data.children.length}`,
  };
  data.children.push(obj);
  // 输入框获取焦点
  nextTick(() => {
    setTimeout(() => {
      proxy.$refs[obj.inputRef].focus()
    }, 50)
  })
}

/* 编辑节点 */
function editNode(data) {
  data.isEdit = true;
  data.oldNodeName = data.nodeName;
  // 输入框获取焦点
  nextTick(() => {
    setTimeout(() => {
      proxy.$refs[`inputRef${data.id}`].focus()
    }, 50)
  })
}

/* 删除节点 */
function removeNode(data) {
  proxy.$modal.confirm('确认要删除该节点吗?').then(function () {
    return deleteDeviceNode(data.id)
  }).then(() => {
    proxy.$modal.msgSuccess("删除成功");
    getNodeTree()
  })
}

/* 更新节点名称 */
function handleUpdateNodeName(data) {
  if (data.nodeName == null) {
    proxy.$modal.msgError("节点名称不可为空");
    return;
  }
  // 删除空格
  data.nodeName = data.nodeName.replace(/\s+/g, '')
  // 校验名称是否符合规范
  let flag = validateNodeName(data.nodeName);
  if (!flag) {
    if (data.isAdd) {
      // 刷新列表
      getNodeTree()
    } else {
      // 重置节点名称
      data.isEdit = false;
      data.nodeName = data.oldNodeName;
    }
    return;
  }
  // 修改节点名称
  saveOrUpdateDeviceNode(data).then(() => {
    proxy.$modal.msgSuccess("操作成功");
    getNodeTree()
  }).finally(() =>{
    getNodeTree()
  })
}

/* 校验节点名称 */
function validateNodeName(nodeName) {
  if (nodeName == '' || nodeName == null) {
    proxy.$modal.msgError("节点名称不可为空");
    return false;
  }
  if (nodeName.length > 10) {
    proxy.$modal.msgError("节点名称不可超过10字");
    return false;
  }
  return true;
}

/* 获取节点树 */
function getNodeTree() {
  getDeviceNodeTree(nodeParam.value).then(response => {
    treeData.value = response.data;
    editTreeData.value = JSON.parse(JSON.stringify(treeData.value));
  })
}

/* 设备新增 */
function handleAdd() {
  proxy.$refs.addDeviceRef.initPage();
}

/* 设备编辑：设备节点树改变事件 */
function treeChange(id) {
  updateForm.value.nodeId = id;
  proxy.$refs['updateRef'].clearValidate("nodeId")
}

/* 设备修改 */
function handleUpdate(row) {
  openUpdate.value = true;
  submitDisable.value = false
  // 如果设备正在使用中，即设备下有节点，只可对备注信息进行修改
  isUsed.value = row.nodeNum > 0
  // 表单对象赋值
  updateForm.value = Object.assign({}, row);
}

/* 设备删除 */
function handleDelete(row) {
  proxy.$modal.confirm("确认删除选中的设备？").then(function () {
    return deleteDeviceById(row.id)
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
    // 刷新设备节点树
    getNodeTree()
  })
}

/* 修改表单打开操作 */
function handleUpdateDialog() {
  nextTick(() => {
    // 清空提示
    proxy.$refs['updateRef'].clearValidate()
  })
}

/* 修改表单取消按钮 */
function updateCancel() {
  openUpdate.value = false
  submitDisable.value = false
}

/* 提交修改表单 */
const submitUpdateForm = debounce(function () {
  //单个修改
  proxy.$refs["updateRef"].validate(valid => {
    if (valid) {
      submitDisable.value = true
      saveOrUpdateDevice(updateForm.value).then(() => {
        proxy.$modal.msgSuccess("操作成功");
        openUpdate.value = false;
        // 刷新设备节点树
        getNodeTree()
        getList()
      }).catch(() => {
        submitDisable.value = false
      });
    }
  })
}, constants.DEBOUNCE_LIMIT, { leading: true, trailing: false })

/* 表格操作列按钮 */
function handleButtonCommand(command, row) {
  switch (command) {
    case 'update':
      handleUpdate(row);
      break;
    case 'remove':
      handleDelete(row);
      break;
    default:
      break;
  }
}

/* 设备新增后回调 */
function handleAddDeviceAfter() {
  getNodeTree();
  getList();
}

onMounted(() => {
  // 获取节点树
  getNodeTree();
  // 获取设备列表
  getList();
  // 每10s刷新设备列表
  timer.value = setInterval(getList, 10000, false);
})
onUnmounted(() => {
  if (timer.value !== undefined) {
    clearInterval(timer.value);
  }
})
</script>

<style scoped lang="scss">
// 主体容器
.app-container {
  padding: 16px;

  .el-row {
    height: 100%;
    .el-col {
      height: 100%;
    }
  }
  .my-table {
    height: calc(100vh - 380px);
  }

  // 设备节点树
  .head-container {
    margin-top: 16px;
    height: calc(100% - 50px);
    overflow: auto;

    // 自定义树节点
    .custom-tree-node {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: space-between;
      font-size: 14px;
      padding-right: 8px;
      margin-left: -4px;

      // 树节点名称
      .node-label {
        display: flex;

        // 名称
        .name {
          max-width: 100px;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }

        // 设备数量
        .num {
          margin-left: 6px;
          color: #606266;
          background-color: #FFF;
          min-width: 20px;
          text-align: center;
          border: 1px solid #DDDDDD;
          border-radius: 10px;

          span {
            padding: 4px !important;
          }
        }
      }

      // 树节点右侧操作icon
      .node-icon {
        display: none;

        :deep(.el-button+.el-button) {
          margin-left: 6px;
        }

        button {
          color: #3363FF;
        }
      }
    }

    // 自定义树节点：悬停样式
    .custom-tree-node:hover .node-icon {
      display: block;
    }

    // 树控件，当前选中高亮样式
    :deep(.el-tree--highlight-current .el-tree-node.is-current>.el-tree-node__content) {
      background-color: #eef2fe;

      .el-tree-node__expand-icon {
        color: #3363FF;
      }

      .node-label {
        .name {
          color: #3363FF;
        }

        .num {
          color: #3363FF;
          background: #CCD8FF;
          border: 1px solid #3363ff1a
        }
      }
    }

    // 树控件内容高度
    :deep(.el-tree-node__content) {
      height: 40px !important;
    }

    // 树控件隐藏前置展开折叠图标
    :deep(.is-leaf::before) {
      opacity: 0;
    }
  }
}

// 设备顶部统计信息样式
.device-count-msg {
  width: 100%;
  height: 86px;
  display: flex;
  column-gap: 16px;
  margin-bottom: 16px;

  // 统计块样式
  .msg-div {
    height: 100%;
    width: 50%;
    background-color: #f5f5f5;
    display: flex;
    align-items: stretch;
    border-radius: 2px;

    // 统计项
    .item {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      margin: 24px 0;
      row-gap: 8px;

      // 文案描述
      .text {
        color: #666;
      }
      // 统计值
      .number {
        font-size: 18px;
        font-weight: 500;
        color: #333;
      }

      p {
        margin: 0;
      }
    }

    // 分割线
    .divider {
      width: 1px;
      background-color: rgba(0, 0, 0, 0.08);
      margin: 24px 0;
    }
  }
}
</style>