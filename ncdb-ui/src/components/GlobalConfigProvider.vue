<!--全局配置组件-->
<template>
  <el-config-provider :message="messageConfig">
    <slot/>
  </el-config-provider>
</template>
<script>
export default {
  name: 'GlobalConfigProvider',
  data() {
    return {
      // 设置消息弹出为单例
      messageConfig: {
        max: 1
      }
    }
  }
}
</script>