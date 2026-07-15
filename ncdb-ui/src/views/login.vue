<!-- 登录页 陈宁 2024/09/24 -->
<template>
  <div class="login-main">
    <!-- 左侧背景图 -->
    <div class="img">
      <!-- 左上角Logo -->
      <img src="../assets/images/logo-single.svg" alt="">
    </div>
    <!-- 右侧内容 -->
    <div class="login-desc">
      <div class="content">
        <!-- 上方标题 -->
        <div class="des" style="margin-bottom: 16px">欢迎使用</div>
        <div class="des" style="margin-bottom: 48px">XXX管理平台</div>
        <!-- 中部和底部内容 -->
        <div class="form-des">
          <el-form ref="loginRef" :model="loginForm" :rules="loginRules">
            <div class="login-input">
              <!-- 账号 -->
              <el-form-item prop="username">
                <el-input
                  v-model="loginForm.username"
                  type="text"
                  auto-complete="off"
                  placeholder="账号"
                >
                  <template #prefix><svg-icon icon-class="loginUser" class="el-input__icon input-icon" /></template>
                </el-input>
              </el-form-item>
              <!-- 密码 -->
              <el-form-item prop="password">
                <el-input
                  v-model="loginForm.password"
                  type="password"
                  auto-complete="off"
                  placeholder="密码"
                >
                  <template #prefix><svg-icon icon-class="password" class="el-input__icon input-icon" /></template>
                </el-input>
              </el-form-item>
              <!-- 验证码 -->
              <el-form-item prop="code" v-if="captchaEnabled">
                <el-input
                  v-model="loginForm.code"
                  auto-complete="off"
                  placeholder="验证码"
                  style="width: 63%"
                  @keyup.enter="handleLogin"
                >
                  <template #prefix><svg-icon icon-class="validCode" class="el-input__icon input-icon" /></template>
                </el-input>
                <div class="login-code">
                  <img :src="codeUrl" @click="getCode" class="login-code-img" alt=""/>
                </div>
              </el-form-item>
              <!-- 记住密码 -->
              <el-checkbox v-model="loginForm.rememberMe" style="margin:0px 0px 25px 0px;">记住密码</el-checkbox>
              <!-- 登录按钮 -->
              <el-form-item style="width:100%;">
                <el-button
                  :loading="loading"
                  type="primary"
                  style="width:100%;"
                  @keyup.enter="handleLogin"
                  @click="handleLogin"
                >
                  <span v-if="!loading">登 录</span>
                  <span v-else>登 录 中...</span>
                </el-button>
              </el-form-item>
            </div>
          </el-form>
          <!-- copyright -->
          <div class="copyright">
            Copyright测试
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { getCodeImg } from "@/api/login";
import Cookies from "js-cookie";
import { encrypt, decrypt } from "@/utils/jsencrypt";
import useUserStore from '@/store/modules/user';

const userStore = useUserStore();
const route = useRoute();
const router = useRouter();
const { proxy } = getCurrentInstance();

// sonar扫描误判：登录表单 cn
const loginForm = ref({
  username: "admin",
  password: "admin123",
  rememberMe: false,
  code: "",
  uuid: ""
});
// 表单校验规则
const loginRules = {
  username: [{ required: true, trigger: "blur", message: "请输入您的账号" }],
  password: [{ required: true, trigger: "blur", message: "请输入您的密码" }],
  code: [{ required: true, trigger: "change", message: "请输入验证码" }]
};
// 验证码图片地址
const codeUrl = ref("");
// loading
const loading = ref(false);
// 验证码开关
const captchaEnabled = ref(true);
// 路由重定向
const redirect = ref(undefined);

watch(route, (newRoute) => {
    redirect.value = newRoute.query && newRoute.query.redirect;
}, { immediate: true });

// 登录点击事件
function handleLogin() {
  proxy.$refs.loginRef.validate(valid => {
    if (valid) {
      loading.value = true;
      // 勾选了需要记住密码设置在 cookie 中设置记住用户名和密码
      if (loginForm.value.rememberMe) {
        Cookies.set("username", loginForm.value.username, { expires: 30 });
        Cookies.set("password", encrypt(loginForm.value.password), { expires: 30 });
        Cookies.set("rememberMe", loginForm.value.rememberMe, { expires: 30 });
      } else {
        // 否则移除
        Cookies.remove("username");
        Cookies.remove("password");
        Cookies.remove("rememberMe");
      }
      // 调用action的登录方法
      userStore.login(loginForm.value).then(() => {
        const query = route.query;
        const otherQueryParams = Object.keys(query).reduce((acc, cur) => {
          if (cur !== "redirect") {
            acc[cur] = query[cur];
          }
          return acc;
        }, {});
        router.push({ path: redirect.value || "/", query: otherQueryParams });
      }).catch(() => {
        loading.value = false;
        // 重新获取验证码
        if (captchaEnabled.value) {
          getCode();
        }
      });
    }
  });
}

// 获取验证码
function getCode() {
  getCodeImg().then(res => {
    captchaEnabled.value = res.captchaEnabled === undefined ? true : res.captchaEnabled;
    if (captchaEnabled.value) {
      codeUrl.value = "data:image/gif;base64," + res.img;
      loginForm.value.uuid = res.uuid;
    }
  });
}

// 获取Cookie信息
function getCookie() {
  const username = Cookies.get("username");
  const password = Cookies.get("password");
  const rememberMe = Cookies.get("rememberMe");
  loginForm.value = {
    username: username === undefined ? loginForm.value.username : username,
    password: password === undefined ? loginForm.value.password : decrypt(password),
    rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
  };
}

getCode();
getCookie();
</script>

<style lang='scss' scoped>

  // 登录页容器
  .login-main {
    display: flex;
    width: 100%;
    height: 100%;

    // 左侧背景图
    .img {
      width: 63%;
      height: 100%;
      background-image: url("../assets/images/login-image.png");
      background-size: cover;

      .lottie-container {
       width: 100%;
       height: 100%;
       position: relative;
       overflow: hidden;
       min-width:1237px;
       min-height:969px;
}

      // 左上角Logo
      img {
        position: absolute;
        width: 56px;
        height: 56px;
        margin-left: 24px;
        margin-top: 24px;
        z-index: 10;
      }
    }

    // 右侧内容
    .login-desc {
      width: 37%;
      height: 100%;
      min-width: 500px;
      display: flex;
      justify-content: center;
      align-items: center;

      // 容器
      .content {
        width: 430px;

        // 上方标题
        .des {
          color: #262626;
          font-size: 40px;
          font-weight: 600;
          font-style: normal;
          line-height: 40px;
        }

        // 中部和底部内容
        .form-des {
          .login-input {
            width: 430px;
            height: 352px;

            // 表单对象
            .el-form-item {
              margin-bottom: 24px;
            }

            // 前置icon
            .el-input__icon.input-icon.svg-icon {
              width: 16px;
              height: 16px;
              color: #FFFFFF;
            }

            // 输入框
            .el-input {
              :deep(.el-input__inner) {
                height: 46px;
                border-radius: 4px;
                box-sizing: border-box;
              }
            }

            // 按钮
            .el-button {
              height: 46px;
            }

            // 验证码
            .login-code {
              width: 33%;
              height: 50px;
              position: absolute;
              right: 0px;
            }

            // 验证码图片
            .login-code-img {
              height: 46px;
              width: 144px;
              background: #D3E6FF;
              box-sizing: border-box;
            }
          }

          // copyright
          .copyright {
            position: absolute;
            margin-left:80px;
            font-style: normal;
            font-weight: 400;
            font-size: 12px;
            line-height: 20px;
            color: rgba(0, 0, 0, 0.65);
            bottom: 32px;
          }
        }
      }
    }
  }
</style>
