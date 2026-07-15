<!-- 添加通知对象弹窗组件 -->
<template>
  <el-dialog title="添加通知对象" v-model="openAddEmail" width="600px" append-to-body align-center
    :close-on-click-modal="false">
    <div class="email-add" :key="refreshFlag">
      <div class="add-msg">邮箱</div>
      <div class="add-area">
        <div class="area-input">
          <el-input v-model="inputEmail" placeholder="请输入邮箱地址"></el-input>
          <el-button type="" @click="handleAddInputEmail">添加</el-button>
        </div>
        <div class="add-tag flex gap-2">
          <el-tag v-for="email in emailList" :key="email" closable @close="handleRemoveEmail(email)" type="info">
            {{ email }}
          </el-tag>
          <div class="tag-desc">{{ `共${emailList.length}个通知对象` }}</div>
        </div>
      </div>
    </div>
    <!-- 底部按钮 -->
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="cancelEmail">取 消</el-button>
        <el-button type="primary" @click="submitEmail">确 定</el-button>
      </div>
    </template>
  </el-dialog>
</template>
<script setup>
import { addNotice } from "@/api/base/alarmMessage";
const { proxy } = getCurrentInstance();
import sendMethodEnum from '@/views/enum/sendMethodEnum';
const emit = defineEmits(['refreshNoticeList']);
const openAddEmail = ref(false); //添加通知对象弹窗显示
const inputEmail = ref(''); // 输入的邮箱地址
const refreshFlag = ref(false);
const props = defineProps({
  emailList: {
    type: Array,
    default: () => [],
  }, // 邮箱集合
  emailData: {
    type: Object,
    default: () => {},
  }, // 邮箱对象
});
const { emailList, emailData } = toRefs(props);

/* 通知对象弹窗按钮：添加 */
function handleAddInputEmail() {
  if (!inputEmail.value) {
    proxy.$modal.msgError("邮箱地址不能为空");
    return;
  }
  // 校验邮箱格式
  const emailReg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9]([a-zA-Z0-9-]*[a-zA-Z0-9])?\.[a-zA-Z]{2,}$/;
  if (!emailReg.test(inputEmail.value)) {
    proxy.$modal.msgError("请输入正确的邮箱地址!");
    return;
  }
  // 邮箱是否已存在
  const target = emailList.value.find(item => item == inputEmail.value)
  if (target) {
    proxy.$modal.msgError("邮箱地址已存在!");
    return;
  }
  // 添加邮箱
  emailList.value.push(inputEmail.value);
  inputEmail.value = '';
}

/* 删除邮箱 */
function handleRemoveEmail(tag) {
  emailList.value = emailList.value.filter(item => item !== tag);
}

/* 通知对象弹窗按钮：取消 */
function cancelEmail() {
  openAddEmail.value = false;
  emit('refreshNoticeList');
}

/* 通知对象弹窗按钮：确定 */
function submitEmail() {
  let mailAddr = '';
  emailList.value.forEach((email, index) => {
    mailAddr = index == 0 ? email : `${mailAddr},${email}`;
  })
  emailData.value.httpUrl = mailAddr;
  emailData.value.sendMethod = emailData.value.sendMethod ? emailData.value.sendMethod : sendMethodEnum.MAIL;
  addNotice({alarmMessage: emailData.value}).then(() => {
    emit('refreshNoticeList');
    proxy.$modal.msgSuccess("添加成功！")
    openAddEmail.value = false;
  })
}

/* 界面初始化 */
function initPage() {
  inputEmail.value = '';
  refreshFlag.value = !refreshFlag.value;
  openAddEmail.value = true;
}

defineExpose({
  initPage,
})
</script>
<style lang="scss" scoped>
/* 通知对象添加弹窗布局 */
.email-add {
  font-family: 'Source Han Sans CN', sans-serif;
  display: flex;
  column-gap: 16px;

  // 邮箱文案描述
  .add-msg {
    color: #333;
    font-weight: 500;
    padding-top: 4px;
  }

  // 新增区域
  .add-area {
    flex: 1;

    // 新增输入框
    .area-input {
      display: flex;
      justify-content: space-between;
      column-gap: 8px;
      margin-bottom: 16px;
    }
  }

  // 邮箱展示标签
  .add-tag {
    padding: 13px;
    border: 1px solid #ddd;
    border-radius: 2px;

    .el-tag {
      margin-bottom: 8px;
    }

    .el-tag + .el-tag {
      margin-right: 8px;
    }

    .tag-desc {
      font-size: 12px;
      color: #595D66;
      height: 18px;
      line-height: 18px;
    }
  }
}
</style>