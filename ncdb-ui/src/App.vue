<!--全局组件配置包裹组件树-->
<template>
  <GlobalConfigProvider>
    <router-view/>
  </GlobalConfigProvider>

</template>

<script setup>
import useSettingsStore from '@/store/modules/settings'
import {handleThemeStyle} from '@/utils/theme'
import GlobalConfigProvider from "@/components/GlobalConfigProvider.vue";

onMounted(() => {
  nextTick(() => {
    // 初始化主题样式
    handleThemeStyle(useSettingsStore().theme)
  })
})
</script>
