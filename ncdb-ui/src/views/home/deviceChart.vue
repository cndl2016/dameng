<template>
  <div class="device-chart">
    <div class="top-info">
      <!-- 设备信息 -- 标题 刷新时间 -->
      <span class="title">设备信息</span>
      <span class="time">
        <span>刷新时间：{{ flushTime }} </span>
        <Refresh v-if="!loading" @click="getDeviceMessage" class="time-refresh"
          style="width: 1em ;height: 1em; margin-left: 8px;vertical-align: -1.5px" />
        <Loading v-else style="width: 1em ;height: 1em; margin-left: 8px;" />
      </span>
    </div>
    <!-- 设备信息 -- 主体内容为空展示图片 -->
    <div v-show="isEmpty" class="empty-img">
      <el-empty :image-size=200 />
    </div>
    <!-- 设备信息 -- 主体内容表单 -->
    <div class="main-info">
      <!-- 各设备CPU信息表单 -->
      <div class="card">
        <div class="title">CPU使用率</div>
        <div id="cpu" class="chart" ref="chartCpu"></div>
      </div>
      <!-- 各设备内存信息表单 -->
      <div class="card">
        <div class="title">内存使用率</div>
        <div id="memory" class="chart" ref="chartMemory"></div>
      </div>
      <!-- 各设备磁盘信息表单 -->
      <div class="card">
        <div class="title">磁盘使用率</div>
        <div id="disk" class="chart" ref="chartDisk"></div>
      </div>
      <!-- 各设备节点数信息表单 -->
      <div class="card">
        <div class="title">节点数</div>
        <div id="node" class="chart" ref="chartNode"></div>
      </div>
    </div>
  </div>
</template>
<script setup>
const { proxy } = getCurrentInstance();
import * as echarts from "echarts";
import { getDeviceTopChart } from '@/api/system/dashboard'
import elementResizeDetectorMaker from "element-resize-detector";

const loading = ref(false); // 加载中标志位
const flushTime = ref(undefined); // 刷新时间
const isEmpty = ref(false); // 是否为空
const cpuData = ref([]); // 设备cpu数据
const diskData = ref([]); // 设备磁盘数据
const nodeData = ref([]); // 设备节点数数据
const memoryData = ref([]); // 设备cpu数据
const percentOption = {
  color: ['#3363FF'], // 颜色
  tooltip: {
    trigger: 'axis', // 坐标x轴触发
    showDelay: 0, // 展示延迟
    axisPointer: { // 坐标x轴点样式设置
      type: 'shadow', // 点击数据条形柱有阴影
      shadowStyle: {
        color: 'rgba(51, 98, 245, 0.2)', // 阴影颜色
        width: '1' // 宽度
      }
    },
    // 数值展示格式设置
    valueFormatter: (value) => {
      return value == null ? 0 : value + "%"
    }
  },
  // 柱状图距离容器距离设置
  grid: {
    // 距离容器顶部
    top: '4%',
    // 距离容器左部
    left: '0%',
    // 距离容器右部
    right: '9%',
    // 距离容器底部
    bottom: "3%",
    // 加入横纵标签
    containLabel: true
  },
  // x轴设置
  xAxis: {
    // 位置
    position: "bottom",
    // 大小
    max: 100,
    min: 0,
    // 间距
    interval: 25,
    // 类型
    type: 'value',
    // x轴标签设置
    axisLabel: {
      // 颜色
      color: '#999999',
      // 展示格式
      formatter: '{value}%',
      // 字体宽度
      fontWeight: 400,
      // 字体大小
      fontSize: 14,
    },
    // 控制x轴分割线显示
    splitLine: {
      lineStyle: {
        color: '#d9d9d9',
      },
    },
    // 标轴文字和标准字号的间距设置
    boundaryGap: [0, 0.01]
  },
  // y轴样式设置
  yAxis: {
    inverse: true,
    max: 9,
    axisTick: {
      show: false,
    },
    type: 'category',
    axisLine: {
      lineStyle: {
        color: '#d9d9d9',
      },
    },
    axisLabel: {
      color: '#999999',
      fontSize: 14,
    },
  },
  // 展示数据映射的图样式设置
  series: [
    {
      name: undefined,
      type: 'bar',
      barGap: 0,
      barWidth: 10,
      data: undefined,
      barMinHeight: 10
    }
  ]
};
const numberOption = {
  color: ['#3363FF'],
  tooltip: {
    trigger: 'axis',
    showDelay: 0,
    axisPointer: {
      type: 'shadow',
      shadowStyle: {
        color: 'rgba(24, 144, 255, 0.2)',
        width: '1'
      }
    },
    valueFormatter: (value) => {
      return value == null ? 0 : value
    }
  },
  grid: {
    top: '4%',
    left: '0%',
    right: '6%',
    bottom: "3%",
    containLabel: true
  },
  xAxis: {
    position: "bottom",
    max: undefined,
    min: 0,
    interval: undefined,
    type: 'value',
    axisLabel: {
      color: '#999999',
      fontWeight: 400,
      fontSize: 14,
      padding: [5, 15, 0, 15],
    },
    splitLine: {
      lineStyle: {
        color: '#d9d9d9',
      },
    },
    boundaryGap: [0, 0.01]
  },
  yAxis: {
    inverse: true,
    max: 9,
    axisTick: {
      show: false,
    },
    type: 'category',
    axisLine: {
      lineStyle: {
        color: '#d9d9d9',
      },
    },
    axisLabel: {
      color: '#999999',
      fontSize: 14,
    },
  },
  series: [
    {
      name: undefined,
      type: 'bar',
      barGap: 0,
      barWidth: 10,
      data: undefined,
      barMinHeight: 5
    }
  ]
}
const emptyOption = {
  title: {
    text: '暂无数据',
    x: 'center',
    y: 'center',
    textStyle: {
      fontSize: 14,
      fontWeight: 'normal'
    }
  }
}
let cpuChart = undefined;
let memoryChart = undefined;
let diskChart = undefined;
let nodeChart = undefined;

function getDeviceMessage() {
  diskData.value = [] // 设备磁盘数据
  cpuData.value = []  // 设备cpu数据
  memoryData.value = [] // 设备内存数据
  nodeData.value = [] // 设备节点个数数据
  let n = new Date(Date.now());
  flushTime.value = (n.toLocaleString().replace(/\//g, "-"));
  loading.value = true
  let erd = elementResizeDetectorMaker()
  getDeviceTopChart().then(res => {
    percentOption.series[0].data = [] // 百分比数据序列设置
    numberOption.series[0].data = [] // 数字数据序列设置
    const result = res.data
    // 四种数据都为空直接返回
    if (result.cpuDataList === null && result.memoryDataList === null
      && result.diskDataList === null && result.nodeNumberDataList === null) {
      isEmpty.value = true
      return
    } else {
      isEmpty.value = false
    }
    // cpu信息展示
    if (cpuChart === undefined) {
      cpuChart = echarts.init(proxy.$refs.chartCpu)
    }
    if (result.cpuDataList != null && result.cpuDataList.length > 0) {
      // 后端获得cpu数据
      cpuData.value = handleDataFormat(result.cpuDataList);
      // 百分比数据序列设置
      percentOption.series[0].data = cpuData.value
      // 百分比标签设置
      percentOption.series[0].name = 'CPU使用率'
      // 展示
      cpuChart.setOption(percentOption, true)
    } else {
      cpuChart.setOption(emptyOption, true)
    }
    // cpuChart = cpuChart
    // erd.listenTo(cpuDom, () => {
    //   nextTick(() => {
    //     // dom尺寸监听
    //     cpuChart.resize()
    //   })
    // })
    // 内存信息展示
    if (memoryChart === undefined) {
      memoryChart = echarts.init(proxy.$refs.chartMemory)
    }
    if (result.memoryDataList != null && result.memoryDataList.length > 0) {
      memoryData.value = handleDataFormat(result.memoryDataList)
      percentOption.series[0].data = memoryData.value
      percentOption.series[0].name = "内存使用率";
      memoryChart.setOption(percentOption, true)
    } else {
      memoryChart.setOption(emptyOption, true)
    }
    // erd.listenTo(cpuDom, () => {
    //   nextTick(() => {
    //     // dom尺寸监听
    //     memoryChart.resize()
    //   })
    // })
    // 磁盘信息展示
    if (diskChart === undefined) {
      diskChart = echarts.init(proxy.$refs.chartDisk)
    }
    if (result.diskDataList != null && result.diskDataList.length > 0) {
      diskData.value = handleDataFormat(result.diskDataList)
      percentOption.series[0].data = diskData.value
      percentOption.series[0].name = '磁盘使用率';
      diskChart.setOption(percentOption, true)
    } else {
      diskChart.setOption(emptyOption, true)
    }
    // erd.listenTo(cpuDom, () => {
    //   nextTick(() => {
    //     // dom尺寸监听
    //     diskChart.resize()
    //   })
    // })
    // 设备节点数
    if (nodeChart === undefined) {
      nodeChart = echarts.init(proxy.$refs.chartNode)
    }
    if (result.nodeNumberDataList != null && result.nodeNumberDataList.length > 0) {
      nodeData.value = handleNodeDataFormat(result.nodeNumberDataList)
      numberOption.series[0].data = nodeData.value
      numberOption.series[0].name = '节点数'
      // y轴最大值动态设置
      let max = nodeData.value[0][0] + (4 - Math.floor(nodeData.value[0][0] % 4))
      // x轴间距动态设置
      let interval = max / 4
      numberOption.xAxis.max = max
      numberOption.xAxis.interval = interval
      nodeChart.setOption(numberOption, true)
    } else {
      nodeChart.setOption(emptyOption, true)
    }
    // erd.listenTo(nodeDom, () => {
    //   nextTick(() => {
    //     // dom尺寸监听
    //     nodeChart.resize()
    //   })
    // })
  }).finally(() => {
    loading.value = false;
  })
}

// cpu 内存 磁盘 数据处理
function handleDataFormat(data) {
  let result = []
  data.forEach(val => {
    let monitorInfo = []
    val.monitorValues > 0 ? monitorInfo.push(val.monitorValues) : monitorInfo.push(null)
    let deviceIps = val.jobMessage
    monitorInfo.push(deviceIps)
    result.push(monitorInfo)
  })
  // 从大到小排序
  result.sort((a, b) => {
    return b[0] - a[0]
  })
  // 长度大于10分片
  if (result.length > 10) {
    return result.slice(0, 10)
  }
  return result
}

// 设备节点数数据处理
function handleNodeDataFormat(data) {
  let result = []
  data.forEach(val => {
    let deviceNodeInfo = []
    val.nodeNum > 0 ? deviceNodeInfo.push(val.nodeNum) : deviceNodeInfo.push(null)
    deviceNodeInfo.push(val.deviceIp)
    result.push(deviceNodeInfo)
  })
  if (result.length > 0) {
    result.sort((a, b) => {
      return b[0] - a[0]
    })
  }
  if (result.length > 10) {
    return result.slice(0, 10)
  }
  return result
}

onMounted(() => {
  getDeviceMessage()
  window.onresize = () => {
    //宽度大于1700px 条形图y轴展示5列
    let numberObj = { xAxis: { max: 0, interval: 0 } }
    let percentObj = { xAxis: { interval: 0 } }
    if (document.documentElement.clientWidth < 1700 && document.documentElement.clientWidth >= 1500) {
      //宽度小于1700px 条形图y轴展示4列
      if (nodeData.value.length !== 0) {
        // y轴最大值动态设置
        numberObj.xAxis.max = nodeData.value[0][0] + (3 - Math.floor(nodeData.value[0][0] % 3))
        // x轴间距动态设置
        numberObj.xAxis.interval = numberObj.xAxis.max / 3
      }
      percentObj.xAxis.interval = 34
    } else if (document.documentElement.clientWidth < 1500 && document.documentElement.clientWidth > 1400) {
      //宽度小于1500px 条形图y轴展示3列
      if (nodeData.value.length !== 0) {
        // y轴最大值动态设置
        numberObj.xAxis.max = nodeData.value[0][0] + (2 - Math.floor(nodeData.value[0][0] % 2))
        // x轴间距动态设置
        numberObj.xAxis.interval = numberObj.xAxis.max / 2
      }
      percentObj.xAxis.interval = 50
    } else {
      if (nodeData.value.length !== 0) {
        // y轴最大值动态设置
        numberObj.xAxis.max = nodeData.value[0][0] + (4 - Math.floor(nodeData.value[0][0] % 4))
        // x轴间距动态设置
        numberObj.xAxis.interval = numberObj.xAxis.max / 4
      }
      percentObj.xAxis.interval = 25
    }
    if (cpuChart !== undefined) {
      cpuChart.setOption(percentObj)
    }
    if (memoryChart !== undefined) {
      memoryChart.setOption(percentObj)
    }
    if (diskChart !== undefined) {
      diskChart.setOption(percentObj)
    }
    if (nodeChart !== undefined) {
      nodeChart.setOption(numberObj)
    }
  }
})

onUnmounted(() => {
  window.onresize = null
})
</script>
<style scoped lang="scss">
// 设备信息图表
.device-chart {
  padding: 16px 24px 16px 24px;
  background-color: #FFFFFF;
  width: 100%;
  margin-top: 16px;
  font-size: 16px;
  font-weight: 500;
  font-family: 'Source Han Sans CN', sans-serif;
  color: #333333;

  // 顶部信息
  .top-info {
    width: 100%;
    height: 16px;
    margin-bottom: 24px;

    // 标题
    .title {
      color: rgba(0, 0, 0, 0.85);
      font-family: Source Han Sans CN, serif;
      font-size: 16px;
      font-style: normal;
      font-weight: 500;
    }

    // 时间部分
    .time {
      margin-left: 12px;
      font-family: 'Source Han Sans CN', sans-serif;
      font-weight: 400;
      font-size: 14px;
      line-height: 14px;
      color: #999999;

      // 刷新图标
      .time-refresh {
        cursor: pointer;
      }

      // 载入图标
      .time-loading {
        margin-left: 10px;
      }
    }
  }

  // 空白占位图
  .empty-img {
    width: 100%;
    height: 394px;
    display: flex;

    .el-empty {
      width: 100%;
    }
  }

  // 主体信息
  .main-info {
    width: 100%;
    display: flex;
    gap: 16px;

    // 图表卡片
    .card {
      padding: 24px;
      border-radius: 4px;
      border: 1px solid #f5f5f5;
      width: 24.5%;
      height: 394px;

      // 标题
      .title {
        font-family: 'Source Han Sans CN', sans-serif;
        font-weight: 500;
        font-size: 14px;
        color: #333333;
        height: 20px;
        line-height: 20px;
        font-style: normal;
      }

      // 图表
      .chart {
        width: 99.5%;
        height: 340px;
      }
    }

    .card:nth-child(4) {
      margin-right: 0px;
    }
  }
}

@media (max-width: 1400px) {
  .main-info {
    flex-wrap: wrap;
  }

  .device-chart .main-info .card {
    width: 49%;
  }
}
</style>