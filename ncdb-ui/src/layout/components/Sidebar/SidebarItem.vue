<!-- 菜单组件  -->
<template>
  <div v-if="!item.hidden">
    <!-- 单级菜单 -->
    <template
      v-if="hasOneShowingChild(item.children, item) && (!onlyOneChild.children || onlyOneChild.noShowingChildren) && !item.alwaysShow">
      <app-link v-if="onlyOneChild.meta" :to="resolvePath(onlyOneChild.path, onlyOneChild.query)">
        <el-menu-item :index="resolvePath(onlyOneChild.path)" class="submenu-title-noDropdown" 
        :class="{'home-title' : onlyOneChild.meta.title === '首页'}">
          <!-- 图标 -->
          <svg-icon v-if="hasIcon(onlyOneChild)" :icon-class="onlyOneChild.meta.icon" />
          <span class="menu-title" :title="onlyOneChild.meta.title">{{ onlyOneChild.meta.title }}</span>
        </el-menu-item>
      </app-link>
    </template>
    <!-- 多级菜单 -->
    <el-sub-menu v-else ref="subMenu" :index="resolvePath(item.path)" teleported>
      <template v-if="item.meta" #title>
        <svg-icon :icon-class="item.meta && item.meta.icon" />
        <span class="menu-title" :title="item.meta.title">{{ item.meta.title }}</span>
      </template>
      <sidebar-item v-for="(child, index) in item.children" :key="child.path + index" :is-nest="true" :item="child"
        :base-path="resolvePath(child.path)" class="nest-menu" />
    </el-sub-menu>
  </div>
</template>

<script setup>
import { isExternal } from '@/utils/validate'
import AppLink from './Link'
import { getNormalPath } from '@/utils/common'

const props = defineProps({
  // 路由对象
  item: {
    type: Object,
    required: true
  },
  // 路由地址
  basePath: {
    type: String,
    default: ''
  }
})

const onlyOneChild = ref({});

// 是否单级菜单
function hasOneShowingChild(children = [], parent) {
  if (!children) {
    children = [];
  }
  const showingChildren = children.filter(item => {
    if (item.hidden) {
      return false
    } else {
      // Temp set(will be used if only has one showing child)
      // 临时设置（如果只有一个子级时用）
      onlyOneChild.value = item
      return true
    }
  })

  // When there is only one child router, the child router is displayed by default
  // 如果只有一个子级路径，该子级路径显示为默认路径
  if (showingChildren.length === 1) {
    return true
  }

  // Show parent if there are no child router to display
  // 如果没有子级菜单路径，显示父级
  if (showingChildren.length === 0) {
    onlyOneChild.value = { ...parent, path: '', noShowingChildren: true }
    return true
  }

  return false
};

// 创建跳转路径
function resolvePath(routePath, routeQuery) {
  if (isExternal(routePath)) {
    return routePath
  }
  if (isExternal(props.basePath)) {
    return props.basePath
  }
  if (routeQuery) {
    let query = JSON.parse(routeQuery);
    return { path: getNormalPath(props.basePath + '/' + routePath), query: query }
  }
  return getNormalPath(props.basePath + '/' + routePath)
}

function hasIcon(item) {
  if (item.name === "Index") {
    return true
  }
  return false
}
</script>

<style lang="scss" scoped>
  // 首页菜单样式

  .home-title {
   padding-left: 28px !important;
}

</style>