<template>
  <div class="monitor-detail-index">
    <!--顶部容器 -->
    <div class="tab-container">
      <!--返回按钮 -->
      <div class="goBack" @click="goBack">
        <div class="icon">
          <svg-icon icon-class="dm-goBack" />
        </div>
      </div>
      <div class="total-desc">
        <div class="menu-name">设备监控</div>
      </div>
    </div>
    <!--主体容器 -->
    <div class="main-content">
      <!--顶部标题区域-->
      <div class="top-title">
        <div class="function-area">
          <!--设备节点下拉树-->
          <div class="function">
            <el-dropdown placement="bottom-start" trigger="click" ref="deviceDropdown">
              <div class="el-dropdown-link">
                <div class="name" v-if="deviceInfo">{{ deviceInfo.deviceIp }}</div>
                <div class="enable-status" v-if="deviceInfo">（
                  <span v-if="deviceInfo" :class="`device-${deviceInfo.connStatus}`" />
                  {{ deviceStatusEnum.getDescFromValue(deviceInfo.connStatus) }}）
                </div>
                <ArrowDown style="width: 1em;height: 1em;margin-top: 3px;" />
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <div class="dropdown-tree">
                    <el-input v-model="treeSearchValue" placeholder="搜索设备地址" @input="handleSearchSelected" clearable
                      suffix-icon="Search" />
                    <div class="tree">
                      <el-tree ref="selectTree" :data="treeData" @node-click="treeSelect"
                        :props="{ children: 'children', label: 'nodeName' }" highlight-current accordion
                        :filter-node-method="filterNode">
                        <template #default="{ node, data }">
                          <div class="custom-tree-node">
                            <!--树节点-->
                            <el-tooltip class="item" popper-class="device-pop" effect="light" placement="top"
                              :open-delay="400">
                              <template #content><b style="font-size: small">{{ data.nodeName }}</b></template>
                              <span class="node-label">
                                <div class="name">{{ data.nodeName }}</div>
                              </span>
                            </el-tooltip>
                          </div>
                        </template>
                      </el-tree>
                    </div>
                  </div>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
          <!--设备所属区域-->
          <div>
            <span class="icon">设备</span>
            <span class="create-msg" v-if="deviceInfo">{{ deviceInfo.deviceArea }}</span>
          </div>
          <!--节点数&创建时间-->
          <div class="message-area">
            <ul>
              <li>
                <div class="title">端口号：</div>
                <div v-if="deviceInfo"> {{ deviceInfo.port }}</div>
              </li>
              <li>
                <div class="title">节点数：</div>
                <div v-if="deviceInfo"> {{ deviceInfo.nodeNum }}</div>
              </li>
            </ul>
            <ul>
              <li>
                <div class="title">CPU架构：</div>
                <div v-if="deviceInfo"> {{ `${deviceInfo.cpuDesc}` }}</div>
              </li>
              <li>
                <div class="title">操作系统：</div>
                <div v-if="deviceInfo"> {{ deviceInfo.osName }}</div>
              </li>
            </ul>
            <ul>
              <li>
                <div class="title">创建时间：</div>
                <div v-if="deviceInfo"> {{ deviceInfo.createTime }}</div>
              </li>
              <li>
                <div class="title">创建用户：</div>
                <div v-if="deviceInfo"> {{ deviceInfo.createUser }}</div>
              </li>
            </ul>
          </div>
        </div>
      </div>

      <!--资源状态展示区域-->
      <div class="chart-content">
        <div class="header">
          <div>当前资源状态</div>
        </div>
        <!--分割线-->
        <div class="header-segment"></div>
        <!-- 资源内容 -->
        <div class="containerMessage">
          <!-- cpu信息 -->
          <div class="progress-con">
            <el-progress type="circle" :percentage="server.cpu ? server.cpu.totalUse : 0" :width="48" :stroke-width="8"
              :show-text="false" color="#3363FF" />
            <div class="msg">
              <span>CPU</span><span v-if="server.cpu">{{ '（' + server.cpu.cpuNum + '核）' }}</span>
              <div v-if="server.cpu" class="value">{{ server.cpu.totalUse }}%</div>
            </div>
          </div>
          <div class="seg"></div>
          <!-- 内存信息 -->
          <div class="progress-con">
            <el-progress type="circle" :percentage="server.mem ? server.mem.usage : 0" :width="48" :stroke-width="8"
              :show-text="false" color="#3363FF" />
            <div class="msg">
              <span>内存</span>
              <div v-if="server.mem"><span class="value">{{ server.mem.used }}GB/</span><span>{{ server.mem.total }}GB</span>
              </div>
            </div>
          </div>
          <div class="seg"></div>
          <!-- 网络信息 -->
          <div class="progress-con" style="width: 320px; justify-content: space-around;">
            <div class="msg">
              <div>网络上行</div>
              <div v-if="server.netWork" class="value">{{ server.netWork.bytesSent }}{{ unit }}</div>
            </div>
            <div class="msg">
              <div>网络下行</div>
              <div v-if="server.netWork" style="color: #389E0D">{{ server.netWork.bytesRecv }}{{ unit }}</div>
            </div>
          </div>
        </div>
      </div>

      <!--图表展示区域-->
      <div class="chart-content">
        <div class="header">
          <div>资源使用情况</div>
          <!--时间范围搜索条件-->
          <div class="search">
            <el-radio-group v-model="queryParams.timeRange" @change="getMonitorDataEchart">
              <el-radio-button :value=monitorTimeRangeEnum.ONE_HOUR_AGO label="近1小时" />
              <el-radio-button :value=monitorTimeRangeEnum.ONE_DAY_AGO label="近1天" />
              <el-radio-button :value=monitorTimeRangeEnum.THREE_DAY_AGO label="近3天" />
              <el-radio-button :value=monitorTimeRangeEnum.SEVEN_DAY_AGO label="近7天" />
            </el-radio-group>
            <!--刷新按钮-->
            <el-button icon="Refresh" @click="getMonitorDataEchart" class="refresh-button" />
          </div>
        </div>
        <!--分割线-->
        <div class="header-segment"></div>
        <div class="chart-area">
          <el-row :gutter="16">
            <el-col :span="12">
              <div class="chart-seg"><span></span>CPU</div>
              <div ref="cpuChart" id="cpuChart" style="height: 388px" />
            </el-col>
            <el-col :span="12">
              <div class="chart-seg"><span></span>内存</div>
              <div id="memChart" style="height: 388px" />
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="24">
              <div class="chart-seg"><span></span>网络IO</div>
              <div id="ioChart" style="height: 388px" />
            </el-col>
          </el-row>
        </div>
      </div>

      <!--磁盘状态展示区域-->
      <div class="chart-content">
        <div class="header">
          <div>磁盘使用状态</div>
        </div>
        <!--分割线-->
        <div class="header-segment"></div>
        <div class="el-table el-table--enable-row-hover el-table--medium diskMessage">
          <table cellspacing="0" style="width: 100%;">
            <caption></caption>
            <thead>
              <tr>
                <th class="el-table__cell el-table__cell is-leaf">
                  <div class="cell">盘符路径</div>
                </th>
                <th class="el-table__cell is-leaf">
                  <div class="cell">盘符类型</div>
                </th>
                <th class="el-table__cell is-leaf">
                  <div class="cell">使用率</div>
                </th>
                <th class="el-table__cell is-leaf">
                  <div class="cell">使用情况</div>
                </th>
              </tr>
            </thead>
            <tbody v-if="server.sysFiles">
              <tr v-for="(sysFile, index) in server.sysFiles" :key="index">
                <td class="el-table__cell is-leaf">
                  <div class="cell">{{ sysFile.dirName }}</div>
                </td>
                <td class="el-table__cell is-leaf">
                  <div class="cell">{{ sysFile.typeName }}</div>
                </td>
                <td class="el-table__cell is-leaf">
                  <div class="cell">{{ sysFile.usage }}%</div>
                </td>
                <td class="el-table__cell is-leaf">
                  <div class="cell">
                    <span style="display: flex">
                      <el-progress :stroke-width="10" :percentage="sysFile.usage" :show-text="false" />
                      <span style="color: #3363FF">{{ sysFile.used }}</span><span>{{ '/' + sysFile.total }}</span>
                    </span>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { getListDevice, getMonitorDataForEchart } from "@/api/device/index";
import { getToken } from '@/utils/auth'
import { formatDate } from "@/utils/index"
import { getDeviceMonitorTree } from "@/api/deviceNode/index";
import deviceStatusEnum from "@/views/enum/deviceStatusEnum"
import statusEnum from "@/views/enum/system/statusEnum";
import monitorTimeRangeEnum from "@/views/enum/monitorTimeRangeEnum"
import { constants } from '@/utils/constants'
import * as echarts from "echarts";
const { proxy } = getCurrentInstance();
// 设备信息
const deviceInfo = ref({});
// 选中的设备IP
const deviceIp = ref(undefined);
// 设备节点树
const treeData = ref([]);
// 树过滤内容
const treeSearchValue = ref("");
// 选中节点ID
const selectNodeId = ref(undefined);
// webSocket连接
const webSocket = ref(undefined);
// 服务器信息
const server = ref({});
// 单位
const unit = ref("KB/s");
// 日期范围
const dateRange = ref([]);
// CPU信息Echart参数
const cpuOption = ref({
  // 图例
  legend: {
    // 单个图例内容
    data: [{ icon: 'roundRect', name: 'CPU' }],
    // 在X轴位置
    x: 'center',
    // 在Y轴位置
    y: '90%',
    // 取消悬浮样式
    selectedMode: false,
  },
  // 提示框
  tooltip: {
    // 触发类型 item：数据项触发，axis:坐标轴触发
    trigger: 'axis',
    // 指示器配置
    axisPointer: {
      // 类型 line：直线，shadow：阴影，cross：十字准线，none：无
      type: 'cross'
    },
    // 值格式化
    valueFormatter: (value) => value + '%'
  },
  // 折线颜色
  color: ['#3363FF'],
  // X坐标轴
  xAxis: {
    // 坐标轴两边留白
    boundaryGap: false,
    // 轴线
    axisLine: {
      lineStyle: {
        // 轴线颜色
        color: '#999999'
      }
    },
    // 坐标轴类型
    type: 'time',
  },
  // 位置
  grid: {
    left: "0%",
    right: "2%",
    bottom: "14%",
    top: "7%",
    containLabel: true
  },
  // Y坐标轴
  yAxis: {
    axisLine: {
      // 是否显示坐标轴轴线
      show: false,
      lineStyle: {
        // 轴线颜色
        color: '#999999'
      }
    },
    // 标签
    axisLabel: {
      // 格式化
      formatter: '{value} %'
    }
  },
  // 系列
  series: [
    {
      name: 'CPU',
      data: [],
      // 折线图
      type: 'line',
      // 点形状：无
      symbol: 'none',
      areaStyle: {
        // 背景颜色
        color: 'rgba(51,99,255,0.15)'
      },
    },
  ]
});
// 内存信息Echart参数
const memOption = ref({
  // 图例
  legend: {
    // 单个图例内容
    data: [{ icon: 'roundRect', name: '内存' }],
    // 在X轴位置
    x: 'center',
    // 在Y轴位置
    y: '90%',
    // 取消悬浮样式
    selectedMode: false,
  },
  // 提示框
  tooltip: {
    // 触发类型 item：数据项触发，axis:坐标轴触发
    trigger: 'axis',
    // 指示器配置
    axisPointer: {
      // 类型 line：直线，shadow：阴影，cross：十字准线，none：无
      type: 'cross'
    },
    // 值格式化
    valueFormatter: (value) => value + '%'
  },
  // 折线颜色
  color: ['#1890FF'],
  // X坐标轴
  xAxis: {
    // 坐标轴两边留白
    boundaryGap: false,
    // 轴线
    axisLine: {
      lineStyle: {
        // 轴线颜色
        color: '#999999'
      }
    },
    // 坐标轴类型
    type: 'time',
  },
  // 位置
  grid: {
    left: "0%",
    right: "2%",
    bottom: "14%",
    top: "7%",
    containLabel: true
  },
  // Y坐标轴
  yAxis: {
    axisLine: {
      // 是否显示坐标轴轴线
      show: false,
      lineStyle: {
        // 轴线颜色
        color: '#999999'
      }
    },
    // 标签
    axisLabel: {
      // 格式化
      formatter: '{value} %'
    }
  },
  // 系列
  series: [
    {
      name: '内存',
      data: [],
      // 折线图
      type: 'line',
      // 点形状：无
      symbol: 'none',
      areaStyle: {
        // 背景颜色
        color: 'rgba(24,144,255,0.15)'
      },
    },
  ]
});
// IO信息Echart参数
const ioOption = ref({
  legend: {
    // 单个图例内容
    data: [{ icon: 'roundRect', name: '上行' }, { icon: 'roundRect', name: '下行' }],
    // 在X轴位置
    x: 'center',
    // 在Y轴位置
    y: '90%',
    // 取消悬浮样式
    selectedMode: false,
  },
  // 提示框
  tooltip: {
    // 触发类型 item：数据项触发，axis:坐标轴触发
    trigger: 'axis',
    // 指示器配置
    axisPointer: {
      // 类型 line：直线，shadow：阴影，cross：十字准线，none：无
      type: 'cross'
    },
    // 值格式化
    valueFormatter: (value) => value + 'KB/s'
  },
  // 折线颜色
  color: ['#3363FF', '#45BD09'],
  // X坐标轴
  xAxis: {
    // 坐标轴两边留白
    boundaryGap: false,
    // 轴线
    axisLine: {
      lineStyle: {
        // 轴线颜色
        color: '#999999'
      }
    },
    // 坐标轴类型
    type: 'time',
  },
  // 位置
  grid: {
    left: "0%",
    right: "2%",
    bottom: "14%",
    top: "7%",
    containLabel: true
  },
  // Y坐标轴
  yAxis: {
    axisLine: {
      // 是否显示坐标轴轴线
      show: false,
      lineStyle: {
        // 轴线颜色
        color: '#999999'
      }
    },
    // 标签
    axisLabel: {
      // 格式化
      formatter: '{value} KB/s'
    }
  },
  // 系列
  series: [
    {
      name: '上行',
      data: [],
      // 折线图
      type: 'line',
      // 点形状：无
      symbol: 'none',
      areaStyle: {
        // 背景颜色
        color: 'rgba(51,99,255,0.15)'
      },
    },
    {
      name: "下行",
      data: [],
      // 折线图
      type: 'line',
      // 点形状：无
      symbol: 'none',
      areaStyle: {
        // 背景颜色
        color: 'rgba(56,158,13,0.15)'
      },
    },
  ]
});
// 搜索条件
const queryParams = ref({
  timeRange: monitorTimeRangeEnum.ONE_HOUR_AGO,
  rootId: constants.ROOT_DEVICE_PARENT_NODE,
  deviceId: undefined,
});

/** 返回按钮 */
function goBack() {
  // 根据参数判断来源，确定返回的路由
  let fromPath = '';
  if (proxy.$route.query.from === 'device-list') {
    // 返回到设备管理页面
    fromPath = '/device/device'
  } else if (proxy.$route.query.from === 'device-monitor') {
    // 返回到设备监控列表页面
    fromPath = '/device/device-list'
  } else if (thproxyis.$route.query.from === 'dashboard') {
    // 返回到首页
    fromPath = '/index'
  }
  proxy.$router.push({
    path: fromPath
  })
}

/* 下拉搜索框 */
function handleSearchSelected() {
  proxy.$refs.selectTree.filter(treeSearchValue.value)
}

/* 下拉树过滤 */
function filterNode(value, data, node) {
  if (!value) return true;
  let parentNode = node.parent;
  let labels = [node.label];
  let level = constants.ROOT_DEVICE_NODE;
  // 遍历树，把所有标签放入labels
  while (level < node.level) {
    labels = [...labels, parentNode.label];
    parentNode = parentNode.parent;
    level++
  }
  // 返回包含搜索关键词的labels
  return labels.some(label => label.indexOf(value) !== -1)
}

/* 设备节点树选中事件 */
function treeSelect(node) {
  // 是设备并且发生了改变
  if (node.isDevice && node.nodeName !== deviceIp.value) {
    // 关闭webSocket
    closeWebSocket();
    // 初始化
    webSocket.value = undefined;
    deviceIp.value = node.nodeName;
    queryParams.value.deviceId = node.id;
    //展示设备信息
    getList(deviceIp.value);
    // 获取节点监控历史数据
    getMonitorDataEchart();
    proxy.$refs.deviceDropdown.handleClose()
  }
}

/* 获取设备树 */
function getDeviceTree() {
  deviceIp.value = proxy.$route.query.deviceIp;
  proxy.$modal.loading("正在加载服务监控数据，请稍候！");
  getDeviceMonitorTree({ rootId: constants.ROOT_DEVICE_PARENT_NODE, enableStatus: statusEnum.ENABLE }).then(response => {
    treeData.value = response.data;
    // 根据设备IP从树结构中找出节点
    findNodeFromTree(treeData.value);
    // 查询设备信息
    getList(deviceIp.value);
    // 给查询参数的设备ID赋值
    queryParams.value.deviceId = selectNodeId.value;
    // 获取监控历史数据
    getMonitorDataEchart()
  }).finally(() => {
    proxy.$modal.closeLoading()
  })
}

/* 查询设备信息 */
function getList(deviceIp) {
  if (deviceIp === undefined) {
    return
  }
  // 查询参数
  let queryParams = {
    pageNum: 1,
    pageSize: 1,
    deviceIp: deviceIp
  };
  // 查询设备列表
  getListDevice(queryParams).then(response => {
    deviceInfo.value = response.data.records[0]
  });
  // 初始化webSocket
  initWebSocket()
}

/* 根据设备IP从树结构中找出节点 */
function findNodeFromTree(list) {
  if (list != null && list.length > 0) {
    let node = list.find(x => x.isDevice && (x.nodeName === deviceIp.value || deviceIp.value == null));
    if (node != null) {
      // 将设备ID赋值给selectNodeId
      selectNodeId.value = node.id;
      // 设备IP为空时默认选中第一个
      if (deviceIp.value == null) {
        deviceIp.value = node.nodeName
      }
    } else {
      list.forEach(item => findNodeFromTree(item.children))
    }
  }
}

/* 初始化WebSocket连接 */
function initWebSocket() {
  // 若WebSocket连接为空则新建连接
  if (webSocket.value === undefined) {
    let wsUrl = "ws://" + location.host + "/websocket/ws/monitor?token=" + getToken() + "?deviceIp=" + deviceIp.value;
    webSocket.value = new WebSocket(wsUrl);
    webSocket.value.onopen = function () {
      console.info("has connected")
    }
  }
  // 若连接新建成功
  if (webSocket.value !== undefined) {
    webSocket.value.onmessage = function (msg) {
      // 获取服务信息
      server.value = JSON.parse(msg.data).server;
      if (msg.data.indexOf("monitorCPU") > 0) {
        let result = JSON.parse(msg.data);
        if (server.value && server.value.cpu) {
          server.value.cpu.totalUse = result.cpuUsage;
          server.value.mem.usage = result.memUsage;
          server.value.mem.used = result.memUse;
          // 单位转换
          if (result.input > 512) {
            unit.value = 'MB/s';
            server.value.netWork.bytesRecv = (result.input / 1024).toFixed(2);
            server.value.netWork.bytesSent = (result.output / 1024).toFixed(2)
          } else {
            unit.value = 'KB/s';
            server.value.netWork.bytesRecv = result.input;
            server.value.netWork.bytesSent = result.output
          }
        }
      }
    }
  }
  webSocket.value.onclose = function () {
    console.log("关闭连接")
  };
  webSocket.value.onerror = function () {
    console.log("连接错误")
  };
  // 窗口关闭前关闭websocket连接
  window.onbeforeunload = function () {
    webSocket.value.close()
  };
}

/* 关闭webSocket连接 */
function closeWebSocket() {
  if (webSocket.value !== undefined) {
    webSocket.value.close();
    console.log("webSocket连接关闭")
  }
}

/* 获取节点监控历史数据 */
function getMonitorDataEchart() {
  getDataRange();
  getMonitorDataForEchart(proxy.addDateRange(queryParams.value, dateRange.value)).then(response => {
    let monitorDevice = response.data;
    if (monitorDevice != null) {
      cpuOption.value.series[0].data = monitorDevice.cpuData;
      let cpuChart = echarts.init(document.getElementById('cpuChart'));
      cpuChart.setOption(cpuOption.value, true);

      memOption.value.series[0].data = monitorDevice.memData;
      let memChart = echarts.init(document.getElementById('memChart'));
      memChart.setOption(memOption.value, true);


      ioOption.value.series[0].data = monitorDevice.outputData;
      ioOption.value.series[1].data = monitorDevice.inputData;
      let ioChart = echarts.init(document.getElementById('ioChart'));
      ioChart.setOption(ioOption.value, true);
      // 判断是否要把KB转换为MB
      unitTransfer(monitorDevice.outputData, monitorDevice.inputData);
    }
  })
}

/** 监控值单位转换 */
function unitTransfer(outputData, inputData) {
  // 找到最大值
  let max = 0;
  outputData.forEach(data => {
    max = Math.max(max, data[1])
  });
  inputData.forEach(data => {
    max = Math.max(max, data[1])
  });
  // 最大值大于512，将KB转为MB
  if (max > 512) {
    ioOption.value.tooltip.valueFormatter = (value) => value + 'MB/s';
    ioOption.value.yAxis.axisLabel.formatter = '{value} MB/s';
    outputData.forEach(data => {
      data[1] = (data[1] / 1024).toFixed(2)
    });
    inputData.forEach(data => {
      data[1] = (data[1] / 1024).toFixed(2)
    })
  } else {
    ioOption.value.tooltip.valueFormatter = (value) => value + 'KB/s';
    ioOption.value.yAxis.axisLabel.formatter = '{value} KB/s'
  }
}

/* 根据单选日期推算开始和结束时间 */
function getDataRange() {
  let beginDate = new Date();
  let endDate = new Date();
  if (queryParams.value.timeRange === monitorTimeRangeEnum.ONE_HOUR_AGO) {
    // 最近1小时
    beginDate = beginDate.setHours(endDate.getHours() - 1)
  } else if (queryParams.value.timeRange === monitorTimeRangeEnum.ONE_DAY_AGO) {
    // 最近1天
    beginDate = beginDate.setDate(endDate.getDate() - 1)
  } else if (queryParams.value.timeRange === monitorTimeRangeEnum.THREE_DAY_AGO) {
    // 最近3天
    beginDate = beginDate.setDate(endDate.getDate() - 3)
  } else if (queryParams.value.timeRange === monitorTimeRangeEnum.SEVEN_DAY_AGO) {
    // 最近7天
    beginDate = beginDate.setDate(endDate.getDate() - 7)
  }
  dateRange.value = [formatDate(beginDate), formatDate(endDate)]
}

onMounted(() => {
  // 获取设备节点树
  getDeviceTree()
})

onUnmounted(() => {
  closeWebSocket()
})
</script>
<style lang="scss" scoped>
// 顶部标题区域
.top-title {
  height: 202px;
}

// 图表区域
.chart-content {
  .el-radio-group {
    vertical-align: 3px;
    margin-right: 8px;
  }
}

// 资源信息区域
.containerMessage {
  display: flex;
  padding: 24px;

  .progress-con {
    display: flex;
    width: 280px;

    .msg {
      margin-left: 24px;
      font-weight: 500;
      color: #333;

      .value {
        color: #3363ff;
      }
    }
  }

  .seg {
    width: 1px;
    height: 48px;
    background-color: rgba($color: #000000, $alpha: 0.08);
    margin: 0 24px;
  }
}

// 磁盘使用状态
.diskMessage {
  table {
    line-height: 14px;
    color: #333333;

    th {
      width: 8%;
      color: #999999;
    }
  }

  &.el-table td.el-table__cell,
  &.el-table th.el-table__cell.is-leaf {
    border-bottom: 0 solid #EBEEF5 !important;
  }

  .el-progress {
    margin: 8px 10px 0 10px;
    width: 176px;
  }
}

// 图表
.chart-area {
  padding: 24px;

  // 标题描述
  .chart-seg {
    color: #333333;
    font-size: 14px;
    font-weight: 500;

    span {
      width: 2px;
      height: 12px;
      background-color: #325DFA;
      display: inline-block;
      margin-right: 8px;
    }
  }
}
</style>