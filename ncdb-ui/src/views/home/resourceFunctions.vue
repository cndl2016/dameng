<template>
  <div>
    <!-- 资源概览 -- 常用功能 -->
    <div class="resource-functions">
      <!--资源概览 -->
      <div class="resource-overview common">
        <div class="resource-desc">资源概览</div>
        <div style="display: flex">
          <div v-for="(item, index) in resourceData" class="png" @click="handleSetLineChartData(item)" :key="index"
            :style="{ 'background-image': 'url(' + getBackImg(item.img) + ')' }">
            <div class="card-text">
              <div class="card-panel-num">
                {{ item.count }}
              </div>
              {{ item.desc }}
            </div>
          </div>
        </div>
      </div>
      <!--常用功能 -->
      <div class="common-functions common">
        <div class="resource-desc">常用功能</div>
        <ul class="functions">
          <li v-for="(item) in functionData" @click="handleFunctionSkip(item)">
            <svg-icon :icon-class="item.icon" />
            {{ item.name }}
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
const { proxy } = getCurrentInstance();
import { checkPermission } from "@/utils/checkPremi"
import { constants } from '@/utils/constants'
import { debounce } from 'lodash'

const resourceData = ref([]); // 资源概览集合
const functionData = [
  { icon: "add_device", name: "设备维护", hasPermi: "device:list", path: "device/device" },
  { icon: "device_monitor", name: "设备监控", hasPermi: "device:monitor", path: "device/device-list" },
  { icon: "login_log", name: "登录日志", hasPermi: "log:loginInfo:list", path: "log/loginInfo" },
  { icon: "audit_log", name: "操作日志", hasPermi: "log:opLog:list", path: "log/opLog" }
]; // 资源概览集合

const getBackImg = (imgName) => {
  return new URL(`/src/assets/images/home/${imgName}`, import.meta.url).href
}

// 路由跳转，权限拦截
const handleSetLineChartData = debounce(function (item) {
  if (!checkPermission([item.hasPermi])) {
    proxy.$modal.notifyWarning({
      title: '缺少权限',
      message: '没有访问权限，请联系管理员授权',
      type: 'warning',
      offset: 100
    });
    return
  }
  let type = item.type
  if (type === 'device') {
    proxy.$router.push({ path: "/device/device", query: {} })
  }
}, constants.DEBOUNCE_LIMIT, { leading: true, trailing: false })

/* 常用功能跳转 */
const handleFunctionSkip = debounce(function (data) {
  // 判断是否有权限
  if (checkPermission([data.hasPermi])) {
    let query = data.query != undefined ? data.query : {}
    proxy.$router.push({
      path: data.path,
      query: query
    })
  } else {
    // 没有权限进行错误提示
    proxy.$notify({
      title: '缺少权限',
      message: `您对[${data.name}]下内容没有访问权限，请联系管理员授权`,
      type: 'warning',
      offset: 100
    });
  }
}, constants.DEBOUNCE_LIMIT, { leading: true, trailing: false })

</script>

<style scoped lang="scss">
// 资源概览
.resource-functions {
  display: flex;
  flex-direction: row;
}

// 资源概览
.common {
  padding: 16px 0px 16px 24px;
  background-color: #FFFFFF;
  height: 176px;
  flex-shrink: 0;
  border-radius: 2px;
  border: 1px solid #f5f5f5;

  .resource-desc {
    color: rgba(0, 0, 0, 0.85);
    font-family: 'Source Han Sans CN', sans-serif;
    font-size: 16px;
    font-style: normal;
    font-weight: 500;
  }
}

// 资源概览
.resource-overview {
  width: 76%;
  margin-right: 16px;

  .card-text {
    cursor: pointer;
    margin-top: 20px;
    margin-left: 36px;
    color: #999999;
    font-family: 'Source Han Sans CN', sans-serif;
    font-size: 14px;
    font-style: normal;
    font-weight: 400;

    .card-panel-num {
      color: #333333;
      font-size: 24px;
      font-style: normal;
      font-weight: 500;
      font-family: DIN, serif;
    }
  }

  .png {
    background-size: cover;
    background-position: right;
    flex: 1;
    height: 102px;
    border-radius: 4px;
    flex-shrink: 0;
    border: 1px solid #f5f5f5;
    margin-top: 15px;
    margin-right: 16px;
  }
}

// 常用功能
.common-functions {
  padding: 16px 24px;
  width: 23.2%;

  .functions {
    width: 100%;
    height: 100px;
    margin-top: 24px;
    padding: 0px;
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;

    li {
      width: 27%;
      height: 24px;
      list-style-type: none;
      float: left;
      display: flex;
      font-weight: 400;
      color: rgba(0, 0, 0, 0.85);
      font-size: 14px;
      font-style: normal;
      font-family: 'Source Han Sans CN', sans-serif;
      line-height: 24px;
      cursor: pointer;

      .svg-icon {
        width: 24px;
        height: 24px;
        color: #3363FF;
      }
    }
  }
}


//页面宽度小于1800px时 更改布局
@media (max-width: 1800px) {
  .resource-functions {
    flex-direction: column;
  }

  .resource-overview {
    width: 100%;
  }

  .common:nth-child(2) {
    height: 100px;
    margin-top: 16px;
  }

  .common-functions {
    width: 100%;

    .functions {
      flex-wrap: nowrap;
      height: auto;
      margin-top: 16px;

      li {
        width: auto;
      }
    }
  }
}
</style>
