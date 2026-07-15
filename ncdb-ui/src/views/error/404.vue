<template>
  <div class="http404-container">
    <!-- 404图片区域 -->
    <div ref="errorAnimation" class="pic-404" />
    <!-- 404描述信息 -->
    <div class="message">
      <div class="message__title">
        404错误!
      </div>
      <div class="message__info">
        很抱歉，您要访问的页面地址有误，或者该页面不存在。
      </div>
      <!-- 返回首页按钮 -->
      <router-link to="/index" class="message__return-home">
        返回首页
      </router-link>
    </div>
  </div>
</template>

<script setup>
import lottie from 'lottie-web';
import errorAnimationData from '@/assets/404_images/404.json';
const { proxy } = getCurrentInstance();
const errorAnimationOption = ref(undefined);

/* 播放404动画效果 */
function loadErrorAnimation() {
  let option = {
    container: proxy.$refs.errorAnimation, // 动画播放容器
    renderer: 'svg', // 渲染方式
    loop: false, // 循环播放
    autoplay: true, // 自动播放
    animationData: errorAnimationData, // json数据
  }
  errorAnimationOption.value = lottie.loadAnimation(option)
}

/* 页面初始化 */
onMounted(() => {
  loadErrorAnimation();
});

/* 页面销毁 */
onBeforeUnmount(() => {
  if (errorAnimationOption.value) {
    errorAnimationOption.value.destroy()
  }
})

</script>

<style lang="scss" scoped>
// 404页面
.http404-container {
  margin: auto;
  display: flex;
  width: 1000px;
  font-family: 'Source Han Sans CN', sans-serif;

  // 404图片
  .pic-404 {
    width: 620px;
    margin-top: -100px;
  }

  // 文字描述
  .message {
    width: 360px;
    margin-top: 280px;

    &__title {
      color: #3363FF;
      font-size: 56px;
      font-weight: 500;
    }

    &__info {
      color: #000000;
      font-size: 20px;
      font-weight: 500;
      line-height: 40px;
      margin-bottom: 24px;
    }

    &__return-home {
      display: block;
      width: 108px;
      height: 40px;
      background: #3363FF;
      border-radius: 2px;
      color: #ffffff;
      text-align: center;
      line-height: 40px;
      font-size: 16px;
    }
  }
}
</style>