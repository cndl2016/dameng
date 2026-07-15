<template>
  <div style="height: 100%" class="cron-tab">
    <div class="popup-div">
      <el-tabs>
        <el-tab-pane label="秒" v-if="shouldHide('second')">
          <CrontabSecond
            @update="updateCrontabValue"
            :check="checkNumber"
            :cron="crontabValueObj"
            ref="cronsecond"
          />
        </el-tab-pane>

        <el-tab-pane label="分" v-if="shouldHide('min')">
          <CrontabMin
            @update="updateCrontabValue"
            :check="checkNumber"
            :cron="crontabValueObj"
            ref="cronmin"
          />
        </el-tab-pane>

        <el-tab-pane label="时" v-if="shouldHide('hour')">
          <CrontabHour
            @update="updateCrontabValue"
            :check="checkNumber"
            :cron="crontabValueObj"
            ref="cronhour"
          />
        </el-tab-pane>

        <el-tab-pane label="日" v-if="shouldHide('day')">
          <CrontabDay
            @update="updateCrontabValue"
            :check="checkNumber"
            :cron="crontabValueObj"
            ref="cronday"
          />
        </el-tab-pane>

        <el-tab-pane label="月" v-if="shouldHide('month')">
          <CrontabMonth
            @update="updateCrontabValue"
            :check="checkNumber"
            :cron="crontabValueObj"
            ref="cronmonth"
          />
        </el-tab-pane>

        <el-tab-pane label="周" v-if="shouldHide('week')">
          <CrontabWeek
            @update="updateCrontabValue"
            :check="checkNumber"
            :cron="crontabValueObj"
            ref="cronweek"
          />
        </el-tab-pane>

        <el-tab-pane label="年" v-if="shouldHide('year')">
          <CrontabYear
            @update="updateCrontabValue"
            :check="checkNumber"
            :cron="crontabValueObj"
            ref="cronyear"
          />
        </el-tab-pane>
      </el-tabs>
      <div class="popup-main">
        <div class="popup-result">
          <p class="title">时间表达式</p>
          <table summary="table">
            <thead>
              <th id="a" v-for="item of tabTitles" width="60" :key="item">{{item}}</th>
            </thead>
            <tbody>
              <tr>
                <td>{{crontabValueObj.second}}</td>
                <td>{{crontabValueObj.min}}</td>
                <td>{{crontabValueObj.hour}}</td>
                <td>{{crontabValueObj.day}}</td>
                <td>{{crontabValueObj.month}}</td>
                <td>{{crontabValueObj.week}}</td>
                <td>{{crontabValueObj.year}}</td>
              </tr>
            </tbody>
          </table>
          <div class="cron-res">Corn表达式：{{crontabValueString}}</div>
        </div>
        <CrontabResult :ex="crontabValueString"></CrontabResult>
      </div>
    </div>
    <div class="drawer__footer">
      <el-button @click="hidePopup">取消</el-button>
      <el-button @click="clearCron">重置</el-button>
      <el-button type="primary" @click="submitFill">确定</el-button>
    </div>
  </div>
</template>

<script setup>
import CrontabSecond from "./second.vue";
import CrontabMin from "./min.vue";
import CrontabHour from "./hour.vue";
import CrontabDay from "./day.vue";
import CrontabMonth from "./month.vue";
import CrontabWeek from "./week.vue";
import CrontabYear from "./year.vue";
import CrontabResult from "./result.vue";
const { proxy } = getCurrentInstance();
const emit = defineEmits(["hide", "fill"]);
const props = defineProps({
  hideComponent: {
    type: Array,
    default: () => [],
  },
  expression: {
    type: String,
    default: "",
  },
});
const tabTitles = ref(["秒", "分钟", "小时", "日", "月", "周", "年"]);
const tabActive = ref(0);
const hideComponent = ref([]);
const expression = ref("");
const crontabValueObj = ref({
  second: "*",
  min: "*",
  hour: "*",
  day: "*",
  month: "*",
  week: "?",
  year: "",
});
const crontabValueString = computed(() => {
  const obj = crontabValueObj.value;
  return (
    obj.second +
    " " +
    obj.min +
    " " +
    obj.hour +
    " " +
    obj.day +
    " " +
    obj.month +
    " " +
    obj.week +
    (obj.year === "" ? "" : " " + obj.year)
  );
});
watch(expression, () => resolveExp());
function shouldHide(key) {
  return !(hideComponent.value && hideComponent.value.includes(key));
}
function resolveExp() {
  // 反解析 表达式
  if (expression.value) {
    const arr = expression.value.split(/\s+/);
    if (arr.length >= 6) {
      //6 位以上是合法表达式
      let obj = {
        second: arr[0],
        min: arr[1],
        hour: arr[2],
        day: arr[3],
        month: arr[4],
        week: arr[5],
        year: arr[6] ? arr[6] : "",
      };
      crontabValueObj.value = {
        ...obj,
      };
    }
  } else {
    // 没有传入的表达式 则还原
    clearCron();
  }
}
// tab切换值
function tabCheck(index) {
  tabActive.value = index;
}
// 由子组件触发，更改表达式组成的字段值
function updateCrontabValue(name, value, from) {
  crontabValueObj.value[name] = value;
}
// 表单选项的子组件校验数字格式（通过-props传递）
function checkNumber(value, minLimit, maxLimit) {
  // 检查必须为整数
  value = Math.floor(value);
  if (value < minLimit) {
    value = minLimit;
  } else if (value > maxLimit) {
    value = maxLimit;
  }
  return value;
}
// 隐藏弹窗
function hidePopup() {
  emit("hide");
}
// 填充表达式
function submitFill() {
  emit("fill", crontabValueString.value);
  hidePopup();
}
function clearCron() {
  // 还原选择项
  crontabValueObj.value = {
    second: "*",
    min: "*",
    hour: "*",
    day: "*",
    month: "*",
    week: "?",
    year: "",
  };
}
onMounted(() => {
  expression.value = props.expression;
  hideComponent.value = props.hideComponent;
});
</script>

<style lang="scss">
  .cron-tab{
    .popup-div {
      /*padding: 24px 24px 0px 24px;*/
      height: calc(100% - 80px);
      overflow: auto;
      font-family: 'Source Han Sans CN', sans-serif;
      font-size: 14px;
      font-style: normal;
      font-weight: 400;
      color: rgba(0, 0, 0, 0.85);
    }
    .pop_btn {
      text-align: center;
      margin-top: 20px;
    }
    .popup-main {
      position: relative;
      margin: 8px auto;
      background: #fff;
      border-radius: 5px;
      overflow: hidden;
      padding: 0 24px;
    }
    .popup-title {
      overflow: hidden;
      line-height: 34px;
      padding-top: 6px;
      background: #f2f2f2;
    }
    .popup-result {
      box-sizing: border-box;
      margin: 10px 0px 20px 0px;
      padding: 22px 12px 12px 16px;
      border: 1px solid #ccc;
      position: relative;
    }
    .popup-result .title {
      position: absolute;
      top: -26px;
      left: 16%;
      width: 100px;
      margin-left: -70px;
      text-align: center;
      line-height: 30px;
      background: #fff;
      font-size: 12px;
      color: rgba(0, 0, 0, 0.45);
    }
    .popup-result table {
      text-align: center;
      width: 100%;
      margin: 0 auto;

      td,th {
        border: 1px solid #EEEEEE;
        border-collapse: collapse;
        height: 44px;
        color: #333333;
        font-weight: 400;
      }

      th{
        background-color: #FAFAFA;
      }
    }
    .popup-result-scroll {
      line-height: 24px;
      height: 10.2em;
      overflow-y: auto;
      padding-left: 0px;
      margin-bottom: 0px;
      li{
        list-style-type: none;
        height: 22px;
        line-height: 22px;
        margin-bottom: 8px;
        span {
          display: inline-block;
          width: 6px;
          height: 6px;
          border-radius: 50%;
          background-color: #dcdfe6;
          margin-right: 8px;
          margin-bottom: 2px;
        }
      }
      li:last-child{
        margin-bottom: 0px;
      }
    }
    .popup-result-scroll ::marker {
      font-size: 18px;
      color: rgba(220, 223, 230, 1);
    }

    .popup-result-second {
      margin-top: 34px;

      .title {
        width: 108px;
      }
    }
    .cron-res{
      font-weight: 400;
      color: #333333;
      margin-top: 12px;
    }
    .el-tabs__content{
      padding: 0 24px;
    }
    .el-tabs__nav{
      margin-left: 45px;
    }
    .el-tabs__header{
      margin-bottom: 24px;
    }

    .popup-div{
      .el-form-item{
        margin-bottom: 24px !important;
      }
      .el-form-item__content {
        line-height: normal;
      }
      .el-radio {
        color: #262626;
        font-weight: 400;
      }
    }
  }
</style>