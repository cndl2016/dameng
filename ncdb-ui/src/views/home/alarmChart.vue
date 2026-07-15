<template>
  <div class="alarmMessage" :class="{ 'instance-overview-alarm': fatherComponentType === 'instance' }">
    <!-- 告警信息 -- 标题 刷新时间 -->
    <div class="top-message">
      <span class="top-message-one">告警信息</span>
      <span class="top-message-two" v-show="fatherComponentType === 'home'">
        <span>刷新时间：{{ flushTime }} </span>
        <Refresh v-if="!loading" @click="getAlarmInfo" class="time-refresh"
          style="width: 1em ;height: 1em; margin-left: 8px;vertical-align: -1.5px" />
        <Loading v-else style="width: 1em ;height: 1em; margin-left: 8px;" />
      </span>
    </div>
    <!-- 告警信息 -- 主体内容 -->
    <div class="main-message">
      <!-- 告警信息 -- 左侧告警数量统计 + 告警图表 -->
      <div class="main-message-left">
        <!-- 告警信息 -- 告警图表 -->
        <div class="main-message-left-chart">
          <div id="alarm-chart" ref="mychart">
          </div>
          <div class="alarm-info">
            <div class="top">
              <span class="num" v-if="totalNum > 0">{{ totalNum }}</span>
              <span class="num zero" v-if="totalNum == 0">0</span>
              <span class="unit">个</span>
            </div>
            <div class="bottom">近24小时共新增</div>
          </div>
        </div>
        <div class="main-message-left-info">
          <div class="title">
            <span class="level-status" style="background-color: #FF2836;"></span>
            <span class="word">高风险</span>
            <span class="num" @click="jumpAlarmHisPage(alarmLevelEnum.SERIOUS)">{{ seriousNum }}</span>
          </div>
          <div class="title">
            <span class="level-status" style="background-color: #FAAD14;"></span>
            <span class="word">低风险</span>
            <span class="num" @click="jumpAlarmHisPage(alarmLevelEnum.NORMAL)">{{ warnNum }}</span>
          </div>
        </div>
      </div>
      <!-- 告警信息 -- 右侧告警信息表格 -->
      <div class="main-message-right">
        <div class="my-table">
          <CustomTable :dataInfo="slicedTable" :config="tableConfig[fatherComponentType]" style="height: 330px;">
            <!-- 告警结束时间列 -->
            <template #endTime="{ row }">
              <span v-if="row.activeState === 'live'">-</span>
              <span v-else>{{ row.endTime }}</span>
            </template>
            <!-- 告警级别列 -->
            <template #msgLevel="{ row }">
              <span class="el-tag el-tag--warning" v-if="row.msgLevel === 'warning'">低风险</span>
              <span class="el-tag el-tag--danger" v-else>高风险</span>
            </template>
            <!-- 告警状态列 -->
            <template #activeState="{ row }">
              <span v-if="row.activeState === 'live'">未结束</span>
              <span v-else>已结束</span>
            </template>
            <!-- 操作按钮列 -->
            <template #button="{ row }">
              <el-button type="primary" link @click="viewNow(row)">查看
              </el-button>
            </template>
          </CustomTable>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
const { proxy } = getCurrentInstance();
import * as echarts from 'echarts';
import { listAlarm } from '@/api/system/dashboard'
import deviceStatusEnum from "@/views/enum/deviceStatusEnum";
import { checkPermission } from "@/utils/checkPremi";
import alarmLevelEnum from '@/views/enum/alarmLevelEnum'
import alarmTableNameEnum from '@/views/enum/alarmTableNameEnum'
const tableConfig = ref({
  home: [
    {
      label: '告警信息',
      prop: 'title',
      width: 200
    },
    {
      label: '告警对象',
      prop: 'resourceName',
    },
    {
      label: '告警开始时间',
      prop: 'createTime',
      width: 180
    },
    {
      label: '告警结束时间',
      prop: 'endTime',
      slotName: 'endTime',
      width: 180
    },
    {
      label: '告警级别',
      prop: 'msgLevel',
      slotName: 'msgLevel',
      width: 120
    },
    {
      label: '告警状态',
      prop: 'activeState',
      slotName: 'activeState',
      width: 120
    },
    {
      label: '操作',
      slotName: 'button',
      width: 80
    },
  ],
}); // 表格配置项

const pager = ref({
  pageSize: 8,
  pageNum: 1
}); // 无限滚动加载分页，默认第一页，每页8条数据
const loading = ref(false); // 加载标志
const flushTime = ref(null);  // 刷新时间的值
const alarmTable = ref([]); //分页前的告警信息记录
const slicedTable = ref([]);  //分页后的告警信息记录
const myChart = ref(undefined); //echart图
const option = {
  series: [
    {
      type: 'pie',
      radius: ['50%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 0,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      labelLine: {
        show: false
      },
      data: [
        { value: 0, name: '空', itemStyle: { color: '#F5F5F5' } },
        { value: 0, name: '警告告警', itemStyle: { color: '#FAAD14' } },
        { value: 0, name: '严重告警', itemStyle: { color: '#FF2836' } },
      ]
    }
  ]
};  //饼图配置项
const totalNum = ref(0); //24小时告警总数
const warnNum = ref(0); //24小时提醒告警个数
const seriousNum = ref(0); //24小时严重告警个数

const props = defineProps({
  fatherComponentType: {
    type: String,
    default: () => 'home',
  }, // 引用当前组件的父组件类型
  instanceId: {
    type: String,
    default: () => '',
  }, // 要查询过滤的实例id值
});
const { fatherComponentType, instanceId } = toRefs(props);

/* 跳转告警历史页面 */
function jumpAlarmHisPage(alarmLevel) {
  //权限校验
  if (!checkPermission(['log:alarm:list'])) {
    proxy.$modal.notifyWarning({
      title: '缺少权限',
      message: '您对[告警信息]下内容没有访问权限，请联系管理员授权',
      type: 'warning',
      offset: 100
    });
    return
  }
  //页面跳转
  proxy.$router.push({ path: '/log/alarm', query: { alarmLevel: alarmLevel, time: 24 } })
}

/* 查询告警信息 -- 刷新告警信息 */
function getAlarmInfo() {
  loading.value = true;
  flushTime.value = (new Date(Date.now()).toLocaleString().replace(/\//g, "-"));
  // 调用后端接口获取告警信息
  let param = {}
  if (fatherComponentType.value === 'instance') {
    // 父组件为实例时  添加过滤参数
    param.instanceId = instanceId.value;
    param.activeState = 'live';
  }
  listAlarm(param).then(res => {
    //设置告警历史表格数据
    alarmTable.value = res.data.tableMessage;
    slicedTable.value = alarmTable.value.slice(0, pager.value.pageNum * pager.value.pageSize)
    // 绘制饼图
    totalNum.value = res.data.totalNum
    warnNum.value = res.data.warnNum
    seriousNum.value = res.data.seriousNum
    option.series[0].data[0].value = totalNum.value > 0 ? 0 : 1
    option.series[0].data[1].value = warnNum.value
    option.series[0].data[2].value = seriousNum.value
    drawAlarmChart();
  }).finally(() => {
    loading.value = false;
  })
}

/* 绘制告警饼图 */
function drawAlarmChart() {
  if (myChart.value === undefined) {
    myChart.value = echarts.init(proxy.$refs.mychart)
  }
  myChart.value.setOption(option)
  window.addEventListener('resize', myChart.value.resize)
}

/* 表格操作栏 “立即查看” 按钮功能 */
function viewNow(row) {
  //判断是否是设备告警
  if (alarmTableNameEnum.T_DEVICE === row.tableName) {
    //设备监控查看权限校验
    if (checkPermission(['device:monitor'])) {
      //设备告警，判断当前设备是否在线
      if (row.deviceStatus === deviceStatusEnum.SUCCESS_CONN) {
        // 在线
        proxy.$router.push({
          path: '/device/device-info/index',
          query: { deviceIp: row.nodeIp, from: 'dashboard' }
        })
      } else {
        //不在线
        proxy.$modal.msgWarning("当前设备不在线！");
      }
    } else {
      proxy.$modal.notifyWarning({
        title: '缺少权限',
        message: '您对[设备监控]下内容没有访问权限，请联系管理员授权',
        type: 'warning',
        offset: 100
      });
    }
  } else {
      proxy.$modal.notifyWarning({
        title: '缺少权限',
        message: '您对[节点监控信息]下内容没有访问权限，请联系管理员授权',
        type: 'warning',
        offset: 100
      });

  }
}

onMounted(() => {
  getAlarmInfo()
})

</script>
<style scoped lang="scss">
//告警信息模块样式
.alarmMessage {
  padding: 16px 24px 16px 24px;
  margin-top: 16px;
  background-color: #FFFFFF;
  height: 400px;
  width: 100%;
  font-size: 16px;
  font-weight: 500;
  font-family: 'Source Han Sans CN', sans-serif;
  color: #333333;

  //顶部告警标题 && 刷新时间
  .top-message {
    width: 100%;
    height: 24px;

    //标题
    .top-message-one {
      color: rgba(0, 0, 0, 0.85);
      font-family: 'Source Han Sans CN', sans-serif;
      font-size: 16px;
      font-style: normal;
      font-weight: 500;
    }

    //刷新时间
    .top-message-two {
      margin-left: 12px;
      font-family: 'Source Han Sans CN', sans-serif;
      font-weight: 400;
      font-size: 14px;
      line-height: 14px;
      color: #999999;

      //刷新时间图标
      .time-refresh {
        cursor: pointer;
      }
    }
  }

  //告警信息模块主体内容
  .main-message {
    width: 100%;
    height: 370px;
    display: flex;
    margin-top: 16px;

    //左侧图表区域
    .main-message-left {
      width: 390px;
      min-width: 390px;
      height: 323px;

      // 告警饼图
      .main-message-left-chart {
        height: 270px;
        width: 100%;

        #alarm-chart {
          height: 270px;
          width: 100%;
          margin-left: -10px;
        }

        .alarm-info {
          width: 86px;
          height: 44px;
          z-index: 999;
          margin-top: -162px;
          margin-left: 37%;

          .top {
            display: flex;
            justify-content: center;
            align-items: baseline;

            .num {
              font-family: DIN, serif;
              font-weight: 500;
              font-size: 24px;
              margin-right: 10px;
              color: #FF0000;
            }

            .zero {
              color: #3363FF !important;
            }
          }

          .bottom {
            font-weight: 400;
            font-size: 12px;
            color: #999999;
          }
        }
      }

      // 告警数据统计
      .main-message-left-info {
        width: 100%;
        height: 20px;
        display: flex;
        justify-content: center;

        .title {
          margin-right: 40px;

          .level-status {
            width: 6px;
            height: 6px;
            border-radius: 50%;
            display: inline-block;
            margin-right: 8px;
            margin-bottom: 2px;
          }

          .word {
            font-weight: 400;
            font-size: 14px;
          }

          .num {
            font-family: DIN, serif;
            font-weight: 500;
            font-size: 20px;
            margin-left: 12px;
            cursor: pointer;
          }
        }
      }
    }

    //右侧表格区域
    .main-message-right {
      width: calc(100% - 390px);
      height: 323px;
    }
  }
}

// 实例概览界面组件样式
.instance-overview-alarm {
  margin-top: 0;
  height: 100%;
  display: flex;
  flex-direction: column;

  .top-message {
    height: 20px;

    .top-message-one {
      font-size: 14px;
    }
  }

  .main-message {
    flex: 1;
    margin-bottom: 8px;
    height: 100%;

    //左侧图表区域
    .main-message-left {
      width: 300px;
      min-width: 300px;
      height: 100%;
    }

    //右侧表格区域
    .main-message-right {
      width: calc(100% - 300px);
      height: 100%;
    }
  }
}
</style>
