<!-- 可点击的消息提示组件 -->
<template>
  <div>
    <Transition name="down">
      <div class="message" v-show="visible">
        <svg class="icon" aria-hidden="true">
          <use xlink:href="#icon-dm-success" />
        </svg>
        <span class="text">{{ text }}</span>
        <span class="buttonMsg" @click="buttonFunction">{{ buttonMsg }}</span>
      </div>
    </Transition>
  </div>
</template>
<script setup>
const props = defineProps({
  text: {
    text: String,
    default: '',
  },
  buttonMsg: {
    text: String,
    default: '',
  },
  buttonFunction: {
    text: Function,
    default: () => { },
  },
});
const { text, buttonMsg, buttonFunction } = toRefs(props);
const visible = ref(false);

function close() {
  visible.value = false;
}
setTimeout(() =>{
  close();
}, 3000)
onMounted(() => {
  visible.value = true;
})
</script>
<style lang="scss" scoped>
// 页面动画
.down-enter-active,
.down-leave-active {
  transition: all 0.5s ease-out;
}

.down-enter-from, .down-leave-to {
  transform: translate3d(0, -75px, 0);
  opacity: 0;
}

// 提示内容布局样式
.message {
  font-family: 'Source Han Sans CN', sans-serif;
  font-size: 14px;
  position: fixed;
  z-index: 9999;
  left: 50%;
  margin-left: -150px;
  top: 25px;
  padding: 11px 16px;
  border: 1px solid rgba(69, 189, 9, 0.3);
  background: #EDF9E7;
  color: #45BD09;
  border-radius: 4px;

  .icon {
    vertical-align: middle;
    width: 14px;
    height: 14px;
    margin-right: 8px;
  }

  .text {
    vertical-align: middle;
  }

  .buttonMsg {
    margin-left: 8px;
    color: #3363ff;
    border-bottom: 1px solid #3363ff;
    cursor: pointer;
    vertical-align: middle;
  }
}
</style>