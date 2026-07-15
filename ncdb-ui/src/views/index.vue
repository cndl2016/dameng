<!-- 首页 2025/03/10 wys -->
<template>
  <div class="home-page">
    <div class="page-content">
      <ResourceFunctions/>
      <AlarmChart/>
      <DeviceChart/>
    </div>
  </div>
</template>

<script setup>
import ResourceFunctions from '@/views/home/resourceFunctions';
import AlarmChart from '@/views/home/alarmChart';
import DeviceChart from '@/views/home/deviceChart';
</script>

<style scoped lang="scss"> 
// 首页整个容器的样式
 .home-page {
   overflow-y: auto;
   overflow-x: hidden;
   margin: 16px;
   position: relative;

   .chart-wrapper {
     background: #fff;
     padding: 16px 16px 0;
     margin-bottom: 32px;
   }
 }

 .page-content {
   width: auto;
 }
</style>

