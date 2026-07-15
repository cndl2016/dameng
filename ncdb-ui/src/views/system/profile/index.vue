<!-- 个人中心 陈宁 2024/09/26 -->
<template>
  <div>
   <!-- 顶部标题 -->
    <div class="tab-container">
      <div class="total-desc">
        <div class="menu-name">个人中心</div>
      </div>
    </div>
    <!-- 主体内容 -->
    <div class="user-info">
      <el-row :gutter="20">
         <!-- 个人信息 -->
        <el-col :span="6" :xs="24">
         <!-- 卡片 -->
          <el-card class="box-card" style="height: 464px;">
            <!-- 卡片标题 -->
            <template v-slot:header>
               <div class="clearfix">
                  <span class="user-info-title">个人信息</span>
               </div>
            </template>
            <div>
               <!-- 上传头像 -->
              <div class="text-center">
                <userAvatar :user="user" />
              </div>
              <!-- 用户信息 -->
              <ul class="list-group list-group-striped">
                <li class="list-group-item">
                  <span class="list_left">用户名称:</span>
                  <div class="pull-right">{{ user.userName }}</div>
                </li>
                <li class="list-group-item">
                  <span class="list_left">手机号码:</span>
                  <div class="pull-right">{{ user.phoneNumber }}</div>
                </li>
                <li class="list-group-item">
                  <span class="list_left">用户邮箱:</span>
                  <div class="pull-right">{{ user.email }}</div>
                </li>
                <li class="list-group-item">
                  <span class="list_left">所属部门:</span>
                  <div class="pull-right" v-if="user.dept">{{ user.dept.deptName }}</div>
                </li>
                <li class="list-group-item">
                  <span class="list_left">所属角色:</span>
                  <div class="pull-right">{{ roleGroup }}</div>
                </li>
                <li class="list-group-item">
                  <span class="list_left">创建日期:</span>
                  <div class="pull-right">{{ user.createTime }}</div>
                </li>
              </ul>
            </div>
          </el-card>
        </el-col>
        <!-- 基本资料+修改密码 -->
        <el-col :span="18" :xs="24">
         <!-- 卡片 -->
          <el-card  style="height: 464px;">
            <!-- 卡片标题 -->
            <template v-slot:header >
               <div class="clearfix">
                  <el-tabs v-model="activeTab" @tab-click="handleTabClick">
                  <!-- tab页 基本资料 -->
                  <el-tab-pane label="基本资料" name="userinfo">
                  </el-tab-pane>
                  <!-- tab页 修改密码 -->
                  <el-tab-pane label="修改密码" name="resetPwd">
                  </el-tab-pane>
                  </el-tabs>
               </div>
            </template>
               <div class="card-content">
               <userInfo v-if="activeTab === 'userinfo'" :user="user" />
               <resetPwd v-else-if="activeTab === 'resetPwd'" :user="user" />
               </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>


<script setup name="Profile">
import userAvatar from "./userAvatar";
import userInfo from "./userInfo";
import resetPwd from "./resetPwd";
import { getUserProfile } from "@/api/system/profile";

const activeTab = ref("userinfo");
const user = ref({});
const roleGroup = ref({});
const postGroup = ref({});

// 获取用户信息
function getUser() {
  getUserProfile().then(response => {
    user.value = response.data;
    roleGroup.value = response.roleGroup;
    postGroup.value = response.postGroup;
  });
};

getUser();
</script>

<style lang='scss' scoped>
   // 主体容器
  .user-info{
    margin: 16px;
   :deep(.el-card__header) {
    height:58px !important;
    margin: 0px;
  
   }
    
   // 卡片标题
   .user-info-title{
      font-size: 16px;
      font-weight: 500;
   }

   // 用户信息对象
   .list-group-item{
      border: none;
      
      // 左侧标题
      .list_left{
         font-size: 14px;
         color: #999999;
         font-style: normal;
         font-weight: 400;
         font-family: 'Source Han Sans CN', sans-serif;
      }
      
      // 右侧内容
      .pull-right{
         font-size: 14px;
         color: #333333;
         font-style: normal;
         font-weight: 400;
         font-family: 'Source Han Sans CN', sans-serif;
      }
   }
  }

   // 卡片样式重写
  .el-card.is-always-shadow {
    border: none;
    box-shadow: none;
  }

  //导航样式
  :deep(.el-tabs__nav-wrap:after) {
  height: 0 !important;
  }

  :deep(.el-tabs__item) {
  display: block !important;
  font-size:16px !important;
  }

  .card-content{
  margin-top:16px
  }

</style>

