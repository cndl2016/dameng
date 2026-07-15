<!-- 侧边菜单栏 -->
<template>
   <div :class="{ 'has-logo': showLogo }" :style="{ backgroundColor: sideTheme === 'theme-dark' ? variables.menuBackground : variables.menuLightBackground }">
    <!-- 滚动条 -->
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <!-- 菜单 -->
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :background-color="sideTheme === 'theme-dark' ? variables.menuBackground : variables.menuLightBackground"
        :text-color="sideTheme === 'theme-dark' ? variables.menuColor : variables.menuLightColor"
        :unique-opened="true"
        :collapse-transition="false"
        mode="vertical"
      >
        <sidebar-item
          v-for="(route, index) in sidebarRouters"
          :key="route.path + index"
          :item="route"
          :base-path="route.path"
        />
      </el-menu>
    </el-scrollbar>
    <!-- 开关 -->
    <hamburger id="hamburger-container" :is-active="appStore.sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" />
  </div>
</template>

<script setup>
import SidebarItem from './SidebarItem'
import Logo from './Logo'
import useAppStore from '@/store/modules/app'
import useSettingsStore from '@/store/modules/settings'
import usePermissionStore from '@/store/modules/permission'
import Hamburger from '@/components/Hamburger'
import variables from '@/assets/styles/variables.module.scss'

const route = useRoute();
const appStore = useAppStore()
const settingsStore = useSettingsStore()
const permissionStore = usePermissionStore()

// 侧边栏路由
const sidebarRouters =  computed(() => permissionStore.sidebarRouters);

// 是否显示Logo栏
const showLogo = computed(() => settingsStore.sidebarLogo);
// 侧边栏主题
const sideTheme = computed(() => settingsStore.sideTheme);
// 主题
const theme = computed(() => settingsStore.theme);
// 侧边栏是否关闭
const isCollapse = computed(() => !appStore.sidebar.opened);
// 选中菜单 高亮
const activeMenu = computed(() => {
  const { meta, path } = route;
  if (meta.activeMenu) {
    return meta.activeMenu;
  }
  return path;
})


// 侧边菜单开关事件
function toggleSideBar() {
  appStore.toggleSideBar()
}
</script>

<style lang="scss" scoped>
  // 开关容器

  .el-scrollbar {
}

  .hamburger-container {
    width: 40px;
    height: 48px;
    margin: 10px;
    float: left;
    cursor: pointer;
    transition: background .3s;
    -webkit-tap-highlight-color:transparent;
  }
</style>