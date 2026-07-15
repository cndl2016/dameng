<template>
  <div class="sidebar-logo-container" :class="{'collapse':collapse}" :style="{ backgroundColor: sideTheme === 'theme-dark' ? variables.menuBackground : variables.menuLightBackground }">
    <!-- <transition name="sidebarLogoFade"> -->
      <el-tooltip placement="right" effect="light" :visible-arrow="false" popper-class="my-tooltip" :show-arrow="false">
        <!-- tooltip内容 -->
        <template #content>
          <div class="tip-content" >
            <div class="content-des" style="margin-top: 92px">版本信息：{{managerVersion}}</div>
            <div class="content-reserved">Copyright测试</div>
          </div>
        </template>
        <!-- 侧边栏关闭：只显示Logo -->
        <router-link v-if="collapse" class="sidebar-logo-link" to="/">
          <img v-if="logo2" :src="logo2" alt="" class="sidebar-logo"/>
          <h1 v-else class="sidebar-title" :style="{ color: sideTheme === 'theme-dark' ? variables.logoTitleColor : variables.logoLightTitleColor }"></h1>
        </router-link>
        <!-- 侧边栏打开：显示Logo+名称 -->
        <router-link v-else class="sidebar-logo-link" to="/">
          <img v-if="logo" :src="logo" alt="" class="sidebar-logo"/>
          <h1 v-else class="sidebar-title" :style="{ color: sideTheme === 'theme-dark' ? variables.logoTitleColor : variables.logoLightTitleColor }"></h1>
        </router-link>
      </el-tooltip>
    <!-- </transition> -->
  </div>
</template>

<script setup>
import variables from '@/assets/styles/variables.module.scss'
import logoImg from '@/assets/images/logo-deep.svg'
import logoImg2 from '@/assets/images/logo-single.svg'
import useSettingsStore from '@/store/modules/settings'

defineProps({
  collapse: {
    type: Boolean,
    required: true
  }
})

// 设置
const settingsStore = useSettingsStore();
// 侧边栏样式
const sideTheme = computed(() => settingsStore.sideTheme);
// 带标题Logo
const logo = logoImg;
// 纯Logo
const logo2 = logoImg2;
</script>

<style lang="scss">
  // tooltip浅色
  .my-tooltip.is-light{
    box-shadow: 0px 4px 32px 0px #ededed !important;
    border: 0px solid #ededed !important;
    position: relative;
    width: 410px;
    height: 184px;
    background-image: url("../../../../src/assets/images/tip.png");
    background-size: cover;
  }

  // tooltip主体容器
  .tip-content{

    .content-des{
      position: absolute;
      margin-left: 20px;
      color: #333333;
      font-size: 12px;
      font-style: normal;
      font-weight: 400;
      line-height: 22px;
    }
    .content-reserved{
      position: absolute;
      margin-top: 146px;
      width: 100%;
      text-align: center;
      color: #666666;
      font-size: 12px;
      font-style: normal;
      font-weight: 400;
      line-height: 20px;
    }
  }

  // 启用时 动画效果
  .sidebarLogoFade-enter-active {
    transition: opacity 1.5s;
  }

  // 进入，离开 动画效果
  .sidebarLogoFade-enter, .sidebarLogoFade-leave-to {
    opacity: 0;
  }

  // logo容器
  .sidebar-logo-container {
    position: relative;
    width: 280px;
    height: 100px;
    top:-20px;
    background: #2b2f3a;
    margin-left: -19px;
    margin-top: -20px;

    & .sidebar-logo-link {
      height: 100%;
      width: 100%;

      & .sidebar-title {
        display: inline-block;
        margin-left: -35px;
        color: #fff;
        vertical-align: middle;
      }
    }

    &.collapse {
      width: 90px;
      .sidebar-logo {
        margin-right: px;
        width: 48px;
        height: 48px;
      }
    }
  }

  // logo
  .sidebar-logo {
    position: absolute;
    left: 32px;
    bottom: -10px;
    width: 188px;
  }
</style>
