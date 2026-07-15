<template>
  <div class="monitor-page-home">
    <!--顶部容器 -->
    <div class="tab-container">
      <div class="total-desc">
        <div class="menu-name">设备监控</div>
      </div>
    </div>
    <!--主体容器 -->
    <div class="app-container">
      <div class="ncdb-tb">
        <!--顶部操作栏 -->
        <div class="top">
          <!--监控列表切换按钮 -->
          <el-radio-group v-model="queryParams.monitorType" @input="selectChange">
            <el-radio-button value="1" label="基础监控" />
            <el-radio-button value="0" label="网络监控" />
          </el-radio-group>
          <!--搜索条件 -->
          <el-form :inline="true">
            <el-form-item>
              <el-tree-select v-model="queryParams.nodeId" :data="treeData" node-key="id" value-key="id" highlight-current
                :props="{ label: 'nodeName', value: 'id' }" placeholder="请选择所属区域" @change="treeChange"
                style="width: 200px;" :check-strictly="true">
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
              </el-tree-select>
            </el-form-item>
            <el-form-item>
              <el-select v-model="queryParams.timeRange" @change="getList" clearable style="width: 200px;">
                <template #prefix>
                  <div class="select-prefix">监控范围:</div>
                </template>
                <el-option v-for="option in timeRangeOptions" :key="option.optionValue" :label="option.optionLabel"
                  :value="option.optionValue" />
              </el-select>
            </el-form-item>
            <el-form-item style="margin-right: 0px !important">
              <el-button icon="Refresh" @click="getList" class="refresh-button" />
            </el-form-item>
          </el-form>
        </div>
        <!--设备监控信息卡片列表 -->
        <div class="monitor-container" v-show="deviceList.length > 0">
          <div class="monitor-index">
            <div class="common" v-for="item in deviceList" @click="goToDetail(item)" :key="item.id">
              <!--卡片顶部：基本信息 -->
              <div :class="`device-detail-${item.connStatus}`" class="top-detail">
                <!--图片 -->
                <img :src="item.connStatus == deviceStatusEnum.FAIL_CONN ? disableImg : enableImg" alt="404">
                <div class="text-desc">
                  <!--设备IP地址 -->
                  <div class="name">{{ item.deviceIp }}</div>
                  <div class="create-area">
                    <!--设备所属区域 -->
                    <el-tooltip :content="item.deviceArea" placement="top" effect="light" :disabled="!item.areaOverflow">
                      <div class="area" :ref="`area_${item.deviceIp}`">{{ item.deviceArea }}</div>
                    </el-tooltip>
                    <div class="segment"></div>
                    <!--设备节点数 -->
                    <div class="node">节点数：{{ `${item.nodeNum}` }}</div>
                  </div>
                </div>
                <!--设备状态 -->
                <div class="enable-status">
                  {{ item.connStatus == deviceStatusEnum.SUCCESS_CONN ? '运行中' : '未启用' }}
                </div>
              </div>
              <!--卡片主体：图表 -->
              <div class="chart" ref="mychart" :id="'chart_' + item.id" v-loading="item.isLoading"></div>
            </div>
          </div>
        </div>
        <el-empty description="暂无数据" v-show="deviceList.length == 0"/>
      </div>
    </div>
  </div>
</template>
<script setup name="DeviceMonitor">
import { getDeviceNodeTree } from "@/api/deviceNode/index"
import { getMonitorList, getMonitorDataForEchart } from "@/api/device/index"
import { formatDate } from "@/utils/index"
import * as echarts from "echarts"
import elementResizeDetectorMaker from "element-resize-detector"
import deviceStatusEnum from "@/views/enum/deviceStatusEnum"
import disableImg from "@/assets/images/monitor/device-disable.png";
import enableImg from "@/assets/images/monitor/device-enable.png";
const { proxy } = getCurrentInstance();

// 日期范围
const dateRange = ref([]);
// 设备列表
const deviceList = ref([]);
// 设备节点树
const treeData = ref([]);
// 监控时间范围参数
const timeRangeOptions = [
  {
    optionLabel: '近1小时',
    optionValue: 0,
  },
  {
    optionLabel: '近1天',
    optionValue: 1,
  },
  {
    optionLabel: '近3天',
    optionValue: 2,
  },
  {
    optionLabel: '近7天',
    optionValue: 3,
  },
];
// 基础监控Echart参数
const baseOption = {
  // 图例
  legend: {
    // 单个图例内容
    data: [{ icon: 'roundRect', name: 'CPU' }, { icon: 'roundRect', name: '内存' }],
    // 在X轴位置
    x: 'center',
    // 在Y轴位置
    y: 'bottom',
    // 图例相对容器位置
    left: "34%",
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
  color: ['#4399FF', '#4d66c9'],
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
    // 坐标轴分割段数（预估值）
    splitNumber: 2,
  },
  // 网格
  grid: {
    // 左侧间距
    left: "1%",
    // 右侧间距
    right: "2%",
    // 底部间距
    bottom: "16%",
    // 顶部间距
    top: "4%",
    // 是否包含坐标轴标签
    containLabel: true
  },
  // Y坐标轴
  yAxis: {
    // 刻度最大值
    max: 100,
    // 刻度最小值
    min: 0,
    // 刻度间隔值
    interval: 25,
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
      formatter: '{value}%'
    }
  },
  // 系列
  series: [
    {
      name: "CPU",
      data: [],
      // 折线图
      type: 'line',
      // 点形状：无
      symbol: 'none',
    },
    {
      name: "内存",
      data: [],
      // 折线图
      type: 'line',
      // 点形状：无
      symbol: 'none',
    },
  ]
};
// 进程监控Echart参数
const ioOption = {
  // 图例
  legend: {
    // 单个图例内容
    data: [{ icon: 'roundRect', name: '网络上行' }, { icon: 'roundRect', name: '网络下行' }],
    // 在X轴位置
    x: 'center',
    // 在Y轴位置
    y: 'bottom',
    // 图例相对容器位置
    left: "34%",
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
      // 轴线颜色
      lineStyle: {
        color: '#999999'
      }
    },
    // 坐标轴类型
    type: 'time',
    // 坐标轴分割段数（预估值）
    splitNumber: 2,
  },
  // 网格
  grid: {
    // 左侧间距
    left: "1%",
    // 右侧间距
    right: "2%",
    // 底部间距
    bottom: "16%",
    // 顶部间距
    top: "4%",
    // 是否包含坐标轴标签
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
      name: "网络上行",
      data: [],
      // 折线图
      type: 'line',
      // 点形状：无
      symbol: 'none',
    },
    {
      name: "网络下行",
      data: [],
      // 折线图
      type: 'line',
      // 点形状：无
      symbol: 'none',
    },
  ]
};
// 设备节点树查询参数
const nodeParam = {
  rootId: 0,
};
// 查询参数
const queryParams = ref({
  timeRange: 0,
  nodeId: undefined,
  monitorType: '1',
});

//#region 设备监控列表
function getList() {
  proxy.$modal.loading("正在加载设备监控数据，请稍候！")
  getDataRange()
  getMonitorList(proxy.addDateRange(queryParams.value, dateRange.value)).then(response => {
    deviceList.value = response.data
    // 所有卡片显示加载样式，强制刷新
    deviceList.value.forEach(device => {
      device.isLoading = true
    })
  }
  ).then(() => {
    // 检测地域显示
    checkAreaOverflow();
    // 防止页面卡顿，优化用户体验，逐个生成图表
    let step = 9
    for (let i = 0; i < step; i++) {
      getChartData(i, step)
    }
  }).finally(() => {
    proxy.$modal.closeLoading()
  })
}

/* 检测地域显示是否超出宽度 */
function checkAreaOverflow() {
  deviceList.value.forEach(device => {
    const ele =  proxy.$refs[`area_${device.deviceIp}`]?.[0];
    device.areaOverflow = ele.scrollWidth > ele.clientWidth
  })
}

// 逐个获取Echart数据:index数据在集合中的索引，step并行请求个数
function getChartData(index, step) {
  // 递归结束条件：index超出deviceList长度
  if (index < deviceList.value.length) {
    let device = deviceList.value[index]
    // 查询设备监控Echart数据
    getMonitorDataForEchart(proxy.addDateRange({ deviceId: device.id }, dateRange.value)).then(response => {
      // 初始化Echart
      initChart(device.id, response.data)
    }).then(() => {
      // 结束当前卡片的加载样式
      device.isLoading = false
      // 递归，继续获取下一组数据
      index += step
      getChartData(index, step)
    })
  }
}

// 根据单选日期推算开始和结束时间
function getDataRange() {
  let beginDate = new Date()
  let endDate = new Date()
  if (queryParams.value.timeRange == 0) {
    // 最近1小时
    beginDate = beginDate.setHours(endDate.getHours() - 1)
  } else if (queryParams.value.timeRange == 1) {
    // 最近1天
    beginDate = beginDate.setDate(endDate.getDate() - 1)
  } else if (queryParams.value.timeRange == 2) {
    // 最近3天
    beginDate = beginDate.setDate(endDate.getDate() - 3)
  } else if (queryParams.value.timeRange == 3) {
    // 最近7天
    beginDate = beginDate.setDate(endDate.getDate() - 7)
  }
  dateRange.value = [formatDate(beginDate), formatDate(endDate)]
}

// 监控内容变更事件
function selectChange(value) {
  getList()
}

// 跳转监控详情信息页面
function goToDetail(item) {
  proxy.$router.push({
    path: '/device/device-info/index',
    query: { deviceIp: item.deviceIp, from: 'device-monitor' }
  })
}
//#endregion

//#region Echart
// 初始化图表
function initChart(id, device) {
  let dom = document.getElementById('chart_' + id)
  let myChart = echarts.init(dom)
  // 监听页面大小发生变化时，刷新Echart
  // let erd = elementResizeDetectorMaker()
  // erd.listenTo(dom, () => {
  //   nextTick(() => {
  //     myChart.resize()
  //   })
  // })
  if (queryParams.value.monitorType == '0') {
    // 进程监控
    if (device.outputData.length > 0 || device.inputData.length > 0) {
      // 判断是否要把KB转换为MB
      unitTransfar(device.outputData, device.inputData)
      ioOption.series[0].data = device.outputData
      ioOption.series[1].data = device.inputData
      myChart.setOption(ioOption, true)
    } else {
      ioOption.series[0].data = []
      ioOption.series[1].data = []
      myChart.setOption(ioOption, true)
    }
  } else if (queryParams.value.monitorType == '1') {
    // 基础监控
    if (device.cpuData.length > 0 || device.memData.length > 0) {
      baseOption.series[0].data = device.cpuData
      baseOption.series[1].data = device.memData
      myChart.setOption(baseOption, true)
    } else {
      baseOption.series[0].data = []
      baseOption.series[1].data = []
      myChart.setOption(baseOption, true)
    }
  }
}

// 单位转换
function unitTransfar(outputData, inputData) {
  // 找上下行的最大值
  let max = 0
  outputData.forEach(data => {
    max = Math.max(max, data[1])
  })
  inputData.forEach(data => {
    max = Math.max(max, data[1])
  })
  // 最大值大于512，将KB转化为MB
  if (max > 512) {
    ioOption.tooltip.valueFormatter = (value) => value + 'MB/s'
    ioOption.yAxis.axisLabel.formatter = '{value} MB/s'
    outputData.forEach(data => {
      data[1] = (data[1] / 1024).toFixed(2)
    })
    inputData.forEach(data => {
      data[1] = (data[1] / 1024).toFixed(2)
    })
  } else {
    ioOption.tooltip.valueFormatter = (value) => value + 'KB/s'
    ioOption.yAxis.axisLabel.formatter = '{value} KB/s'
  }
}

//#endregion

//#region 下拉树
// 获取设备所属区域下拉树
function getTreeData() {
  getDeviceNodeTree(nodeParam).then(response => {
    treeData.value = response.data
  })
}

// 树选择事件
function treeChange(id, i) {
  queryParams.value.nodeId = id
  getList()
}
//#endregion


// 页面初次挂载或在缓存中被激活时调用
onMounted(() => {
  // 获取设备节点树
  getTreeData()
  // 获取设备列表
  getList();
  // 监听页面尺寸变化
  window.addEventListener('resize', checkAreaOverflow)
});

onUnmounted(() => {
  window.removeEventListener('resize', checkAreaOverflow)
})
</script>
<style scoped lang="scss">
// 卡片顶部所属区域和节点数样式，和其他监控页面不同所以没有写入公共样式
.create-area {
  display: flex;
  font-size: 12px;
  min-width: 0;

  .area {
    min-width: 0;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
  }

  .node {
    white-space: nowrap;
    flex-shrink: 0;
  }

  .segment {
    width: 1px;
    height: 16px;
    margin: 0px 8px;
    background-color: rgba(0, 0, 0, 0.08);
  }
}
</style>