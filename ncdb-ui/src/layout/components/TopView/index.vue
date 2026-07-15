<template>
  <div class="main-top-view">
    <div class="left-logo">
      <img :src="logoImg" alt="" class="logo"/>
      <div class="text-content">XXX管理平台</div>
    </div>
    <!-- 右侧菜单组件 -->
    <div class="right-menu">
      <!-- 用户头像下拉菜单 -->
      <el-dropdown @command="handleCommand" class="avatar-container right-menu-item hover-effect" trigger="click">
        <!-- 用户头像 -->
        <div class="avatar-wrapper">
          <img alt="" :src="userStore.avatarBinary" class="user-avatar">
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <!-- 用户信息 -->
            <el-dropdown-item>
              <div class="dp">
                <el-row>
                  <!-- 左侧头像 -->
                  <el-col :span="6">
                    <div class="dp-div"><img alt="" :src="userStore.avatarBinary" class="user-avatar dp-avatar"></div>
                  </el-col>
                  <!-- 右侧信息 -->
                  <el-col :span="18">
                    <el-row>
                      <el-col :span="24">
                        <div class="dp-info">
                          <!-- 用户名称 -->
                          <span class="dp-name">{{ user.userName }}</span>
                          <!-- 用户角色 -->
                          <span v-if="roleGroup == '超级管理员'"><img src="../../../assets/images/home/user_admin.svg"
                              alt="超级管理员"></span>
                          <span v-if="roleGroup == '普通角色'"><img src="../../../assets/images/home/user_normal.svg"
                              alt="普通角色"></span>
                          <span v-if="roleGroup == '只读角色'"><img src="../../../assets/images/home/user_readonly.svg"
                              alt="只读角色"></span>
                          <span class="dp-user" v-if="!['超级管理员', '普通角色', '只读角色'].includes(roleGroup)">{{ roleGroup
                          }}</span>
                        </div>
                      </el-col>
                      <!-- 手机号码 -->
                      <el-col :span="24">
                        <div class="dp-info">{{ user.phoneNumber }}</div>
                      </el-col>
                    </el-row>
                  </el-col>
                </el-row>
              </div>
            </el-dropdown-item>
            <!-- 个人中心 -->
            <router-link to="/profile">
              <el-dropdown-item divided>个人中心</el-dropdown-item>
            </router-link>
            <!-- 退出登录 -->
            <el-dropdown-item command="logout">
              <span>退出登录</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>
<script setup>
import { getUserProfile } from "@/api/system/profile"
import { ElMessageBox } from 'element-plus'
import logoImg from '@/assets/images/logo-single.svg'
import useUserStore from '@/store/modules/user'

const userStore = useUserStore()
const user = ref({})
const roleGroup = ref({})

// 指令触发事件
function handleCommand(command) {
  switch (command) {
    // 登出
    case "logout":
      logout();
      break;
    default:
      break;
  }
}

// 登出事件
function logout() {
  ElMessageBox.confirm('确定注销并退出系统吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logOut().then(() => {
      location.href = '/index';
    })
  }).catch(() => { });
}

// 获取用户角色信息
function getUser() {
  getUserProfile().then(response => {
    user.value = response.data;
    roleGroup.value = response.roleGroup;
  });
}

getUser();
</script>
<style lang="scss" scoped>
.main-top-view {
  margin-bottom: 8px;
  width: 100%;
  height: 50px;
  box-shadow: 0 2px 8px 0 #00000014;
  display: flex;
  justify-content: space-between;

  .left-logo {
    display: flex;

    .logo {
      width: 32px;
      height: 32px;
      margin: auto 8px auto 16px;
    }
    .text-content {
      font-size: 18px;
      height: 50px;
      line-height: 50px;
      font-weight: 500;
      color: #333;
      font-family: 'Source Han Sans CN', sans-serif;
    }
  }

  // 右侧组件
  .right-menu {
    height: 50px;
    line-height: 50px;

    &:focus {
      outline: none;
    }

    // 组件对象
    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background .3s;

        &:hover {
          background: rgba(0, 0, 0, .025)
        }
      }

      // AI头像
      .chat-icon {
        cursor: pointer;
        font-size: 18px;
        margin-top: 8px;
        width: 24px !important;
        height: 24px !important;
      }
    }

    // 用户头像下拉菜单
    .avatar-container {
      margin-right: 8px;
      position: relative;
      top: -5px;

      // 头像容器
      .avatar-wrapper {
        margin-top: 5px;
        position: relative;

        // 头像Img
        .user-avatar {
          cursor: pointer;
          width: 30px;
          height: 30px;
          border-radius: 10px;
          margin-top: 6px;
        }
      }
    }
  }
}

// 下拉组件
.dp {
  padding-top: 6px;
  width: 200px;

  // 头像容器
  .dp-div {
    height: 44px;

    // 头像Img
    .dp-avatar {
      margin-top: 6px;
      width: 40px;
    }
  }

  // 下拉组件详情
  .dp-info {
    height: 24px;
    line-height: 24px;
    color: rgba(0, 0, 0, 0.45);

    // 用户名称
    .dp-name {
      font-weight: 500;
      font-size: 16px;
      color: rgba(0, 0, 0, 0.85);
      padding-right: 6px;
    }

    // 角色名称
    .dp-user {
      vertical-align: -4px;
      height: 20px;
      background: #F0F3F7;
      border-radius: 2px;
      font-size: 12px;
      padding: 4px 6px 4px 4px;
    }
  }

  .dp-info i {
    margin-left: 4px;
    font-weight: bold;
  }

  .dp-info img {
    border-radius: 2px;
    vertical-align: -5px;
  }
}
</style>
