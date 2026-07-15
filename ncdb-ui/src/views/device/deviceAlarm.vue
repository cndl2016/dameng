<template>
  <div>
    <!--顶部容器 -->
    <div class="tab-container">
      <div class="total-desc">
        <div class="menu-name">设备告警</div>
      </div>
    </div>
    <!--主体容器 -->
    <div class="app-container">
      <el-row :gutter="20">
        <!--设备树-->
        <el-col :span="4">
          <el-input v-model="filterText" maxlength="30" placeholder="请输入IP" clearable prefix-icon="Search" />
          <div class="head-container">
            <el-tree :data="treeData" node-key="id" ref="tree" :current-node-key="selectNodeId" highlight-current
              default-expand-all :expand-on-click-node="false"  :filter-node-method="filterNode" @node-click="selectNode">
              <template #default="{ node, data }">
                <div class="custom-tree-node">
                  <!--树节点-->
                  <el-tooltip class="item" popper-class="device-pop" effect="light" placement="top" :open-delay="400">
                    <template #content><b style="font-size: small">{{ data.nodeName }}</b></template>
                    <span class="node-label">
                      <div class="name">{{ data.nodeName }}</div>
                    </span>
                  </el-tooltip>
                </div>
              </template>
            </el-tree>
          </div>
        </el-col>
        <!--告警项-->
        <el-col :span="20">
          <AlarmModal ref="deviceAlarm" :alarmType="'device'" :selectNodeId="selectNodeId" :tableName="'t_device'" :treeData="treeData"/>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script setup>
import { getDeviceMonitorTree } from "@/api/deviceNode";
import { constants } from '@/utils/constants';
import AlarmModal from "@/views/components/alarm"
const { proxy } = getCurrentInstance();
const prevNodeTreeId = ref(null);
// 左侧树过滤文本
const filterText = ref('');
// 设备树过滤参数
const nodeParam = ref({
  rootId: constants.ROOT_DEVICE_PARENT_NODE,
});
// 设备树
const treeData = ref([]);
// 左侧树选中ID
const selectNodeId = ref(-1);

/** 设备节点树过滤 */
watch(filterText, (val) => {
  proxy.$refs["tree"].filter(val);
});

/** 获取节点树 */
function getNodeTree() {
  getDeviceMonitorTree(nodeParam.value).then(response => {
    treeData.value = response.data;
    proxy.$refs.tree.setCurrentKey(1);
    prevNodeTreeId.value = 1;
  })
}

/** 节点过滤 */
function filterNode(value, data) {
  if (!value) return true;
  // 节点名称模糊匹配
  return data.nodeName.indexOf(value) !== -1
}

/** 树节点点击事件 */
function selectNode(data, node) {
  // 点击的为顶层全局资源，树不收缩并查询默认模板
  if(node.level === 1) {
    node.expanded = true;
    selectNodeId.value = -1;
    prevNodeTreeId.value = 1;
    return;
  }
  if(data.isDevice) {
    selectNodeId.value = data.id;
    prevNodeTreeId.value = data.id;
  } else {
    node.expanded = !node.expanded;
    proxy.$refs.tree.setCurrentKey(prevNodeTreeId.value, false);
  }
}


onMounted(() => {
  // 获取节点树
  getNodeTree()
})
</script>
<style scoped lang="scss">
/*主体容器*/
.app-container {
  padding: 16px;
  .el-row {
    height: 100%;
    .el-col {
      height: 100%;
    }
  }

  /* 设备节点树 */
  .head-container {
    margin-top: 16px;
    height: calc(100% - 50px);

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
          max-width: 100px;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }

        /* 设备数量 */
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

      /* 树节点右侧操作icon */
      .node-icon {
        display: none;

        :deep(.el-button + .el-button) {
          margin-left: 6px;
        }

        button {
          color: #3363FF;
        }
      }
    }

    /* 自定义树节点：悬停样式 */
    .custom-tree-node:hover .node-icon {
      display: block;
    }

    /* 树控件，当前选中高亮样式 */
    :deep(.el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content) {
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
          border: 1px solid rgba(24, 144, 255, 0.1);
        }
      }
    }

    /* 树控件内容高度 */
    :deep(.el-tree-node__content) {
      height: 40px !important;
    }

    /* 树控件隐藏前置展开折叠图标 */
    :deep(.is-leaf::before) {
      opacity: 0;
    }
  }
}
</style>