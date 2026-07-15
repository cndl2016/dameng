<!-- 新增设备表单界面 -->
<template>
  <div>
    <el-drawer title="新增设备" v-model="openAdd" @open="handleAddDialog" append-to-body size="1040"
      :close-on-click-modal="false">
      <div class="drawer__content">
        <!--主体表单-->
        <el-form ref="addRef" :model="addForm" :rules="rules" label-width="auto">
          <el-form-item label="所属机房" prop="nodeId">
            <el-tree-select v-model="addForm.nodeId" :data="editTreeData" default-expand-all node-key="id" value-key="id"
              highlight-current :props="{ label: 'nodeName', value: 'id' }" placeholder="请选择所属机房" @change="addTreeChange"
              style="width: 400px;" :check-strictly="true" />
          </el-form-item>
          <!-- 设备列表 -->
          <el-form-item label="设备信息">
            <div class="device-msg-header device-msg">
              <div class="col">设备地址</div>
              <div class="col-s">设备端口</div>
              <div class="col">用户名称</div>
              <div class="col">用户密码</div>
              <div class="col-icon"></div>
            </div>
            <div class="device-msg" v-for="(device, index) in addForm.deviceList">
              <div class="col">
                <el-form-item :prop="'deviceList.' + index + '.deviceIp'" :rules="rules.deviceIp">
                  <el-input v-model="device.deviceIp" placeholder="请输入设备IP"
                    @blur="handleValidateDeviceIp(device, index)" />
                </el-form-item>
              </div>
              <div class="col-s">
                <el-form-item :prop="'deviceList.' + index + '.port'" :rules="rules.port">
                  <el-input-number v-model="device.port" placeholder="请输入端口" :controls="false" />
                </el-form-item>
              </div>
              <div class="col">
                <el-form-item :prop="'deviceList.' + index + '.deviceSshUsr'" :rules="rules.deviceSshUsr"
                  >
                  <el-input v-model="device.deviceSshUsr" placeholder="请输入用户名" />
                </el-form-item>
              </div>
              <div class="col">
                <el-form-item :prop="'deviceList.' + index + '.deviceSshPwd'" :rules="rules.deviceSshPwd"
                  >
                  <el-input v-model="device.deviceSshPwd" type="password" placeholder="请输入密码" />
                </el-form-item>
              </div>
              <div class="col-icon">
                <Close style="width: 17px; height: 17px" @click="addForm.deviceList.splice(index, 1)" />
              </div>
            </div>
            <div class="add-device" @click="handleAddDeviceObject">
              + 添加设备
            </div>
          </el-form-item>
          <!-- 告警规则 -->
          <el-form-item label="告警规则">
            <div class="device-msg-header device-msg alarm-msg">
              <div style="width: 154px;">告警项</div>
              <div class="col-2">低风险阈值</div>
              <div class="col-2">高风险阈值</div>
              <div style="width: 120px;">持续时间</div>
              <div class="col">通知对象</div>
            </div>
            <div class="device-msg alarm-msg" v-for="(alarm, index) in addForm.alarmRuleList" :key="index">
              <div style="width: 154px;">{{ alarm.ruleName }}</div>
              <div class="col-2" v-if="alarm.alertParameters[0].configType !== 'on-off'">
                <el-checkbox v-model="alarm.alertParameters[0].warningStatus" />
                <el-input-number v-model="alarm.alertParameters[0].warning" controls-position="right" :min="1"
                  :max="alarm.alertParameters[0].fatal - 1" @change="handleWarnValueChange(alarm.alertParameters[0])">
                  <template #suffix>
                    <span>{{ alarm.alertParameters[0].valueUnitCn }}</span>
                  </template>
                </el-input-number>
              </div>
              <div class="col-2" v-else>--</div>
              <div class="col-2">
                <el-checkbox v-model="alarm.alertParameters[0].fatalStatus" />
                <el-input-number v-model="alarm.alertParameters[0].fatal" controls-position="right"
                  :min="alarm.alertParameters[0].warning + 1" v-if="alarm.alertParameters[0].configType !== 'on-off'"
                  :max="alarm.alertParameters[0].valueMax + 1" @change="handleFatalValueChange(alarm.alertParameters[0])">
                  <template #suffix>
                    <span>{{ alarm.alertParameters[0].valueUnitCn }}</span>
                  </template>
                </el-input-number>
                <span v-else>严重</span>
              </div>
              <div style="width: 120px;">
                <el-input-number v-model="alarm.alertParameters[0].duration" controls-position="right"
                  @change="handleDurationValueChange(alarm.alertParameters[0])"
                  :disabled="alarm.alertParameters[0].configType == 'on-off'" style="width: 100%;" :min="0" :max="30">
                  <template #suffix>
                    <span>{{ alertDurationUnitEnum.getDescFromValue(alarm.alertParameters[0].durationUnit) }}</span>
                  </template>
                </el-input-number>
              </div>
              <!-- 通知对象 -->
              <div class="col">
                <el-select v-model="alarm.selectNoticeList" multiple collapse-tags>
                  <el-option :label="item" :value="item" v-for="item in emailList"></el-option>
                  <!-- 通知方式下拉列表中的添加通知对象按钮 -->
                  <template #footer>
                    <el-button link type="primary" @click="handleAddEmail">+ 添加通知对象</el-button>
                  </template>
                </el-select>
              </div>
            </div>
          </el-form-item>
        </el-form>
      </div>
      <!--底部按钮-->
      <div class="drawer__footer">
        <el-button @click="addCancel">取消</el-button>
        <el-button @click="testConnection">测试连接</el-button>
        <el-button type="primary" @click="submitAddForm">确定</el-button>
      </div>
    </el-drawer>
    <!-- 添加通知对象弹窗 -->
    <AddNotice ref="addNotice" :emailData="emailData" :emailList="emailList" @refreshNoticeList="getNoticeList" />
  </div>
</template>
<script setup>
import alertDurationUnitEnum from "@/views/enum/alertDurationUnitEnum";
import sendMethodEnum from '@/views/enum/sendMethodEnum';
import { getAll } from "@/api/base/alarmMessage";
import { getListByObjectId, loadAllCacheDevice } from "@/api/base/alertConfig";
import { testPing, addDevices } from "@/api/device/index";
import { debounce } from 'lodash';
import { constants } from '@/utils/constants';
import AddNotice from "@/views/components/addNotice"

const { proxy } = getCurrentInstance();
const emit = defineEmits(['addDeviceAfter']);
const openAdd = ref(false); // 新增表单显示
const emailList = ref([]); // 邮箱地址集合
const emailData = ref({}); // 邮箱对象
const data = reactive({
  addForm: {
    deviceList: [{}],
    alarmRuleList: [],
  }, // 新增表单
  rules: {
    deviceSshUsr: [{ required: true, message: "用户名不能为空", trigger: "change" }],
    nodeId: [
      { required: true, message: "设备机房不能为空", trigger: "change" },
    ],
    deviceIp: [
      { required: true, message: "设备IP不能为空", trigger: "change" },
      {
        pattern: /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/,
        message: "设备IP格式不正确",
        trigger: "change"
      },
    ],
    port: [
      { required: true, message: "设备端口不能为空", trigger: "change" },
      { pattern: /^[1-9]\d*$/, message: "端口号必须为正整数", trigger: "change" },
      {
        validator: (rule, value, callback) => {
          const port = Number(value)
          if (port < 1 || port > 65535) {
            callback(new Error("端口号填写范围为1-65535"))
          } else {
            callback()
          }
        }, trigger: "change"
      }
    ],
    deviceSshPwd: [
      { required: true, message: "密码不能为空", trigger: "change" }
    ],
  }, // 表单校验
  alarmParams: {
    objectId: -1,
    tableName: 't_device'
  }, // 设备告警信息查询参数
})
const { addForm, rules, alarmParams } = toRefs(data);
const props = defineProps({
  editTreeData: {
    type: Array,
    default: () => [],
  }, // 设备节点树
});
const { editTreeData } = toRefs(props);

/* 新增表单打开操作 */
function handleAddDialog() {
  nextTick(() => {
    // 清空提示
    proxy.$refs['addRef'].clearValidate()
  })
}

/* 设备编辑：设备节点树改变事件 */
function addTreeChange(id,) {
  addForm.value.nodeId = id;
  proxy.$refs['addRef'].clearValidate("nodeId")
}

/* 校验设备ip */
function handleValidateDeviceIp(device, index) {
  if (device.deviceIp) {
    // 校验ip是否重复
    addForm.value.deviceList.forEach((item, itemIndex) => {
      if (itemIndex !== index && device.deviceIp === item.deviceIp) {
        proxy.$modal.msgError(`设备${device.deviceIp}已存在！`);
        device.deviceIp = '';
        return;
      }
    })
  }
}

/* 新增表单取消按钮功能 */
function addCancel() {
  addForm.value.nodeId = '';
  addForm.value.deviceList = [{}];
  addForm.value.alarmRuleList = [];
  openAdd.value = false;
}

/* 新增表单测试连接按钮功能 */
const testConnection = debounce(async function () {
  if (await handleValidateAddForm()) {
    // 调用测试连接接口
    testPing(addForm.value.deviceList).then(res => {
      if (res.data) proxy.$modal.msgSuccess("设备测试连接成功！");
    })
  }
}, constants.DEBOUNCE_LIMIT, { leading: true, trailing: false })

/* 新增表单提交 */
const submitAddForm = debounce(async function () {
  const result = await handleValidateAddForm();
  if (result) {
    // 调用新增接口
    addDevices(addForm.value).then(() => {
      proxy.$modal.msgSuccess("操作成功");
      openAdd.value = false;
      // 更新设备缓存
      loadAllCacheDevice();
      // 回调父组件方法 刷新设备列表
      emit('addDeviceAfter');
    })
  }
}, constants.DEBOUNCE_LIMIT, { leading: true, trailing: false })

/* 校验新增表单 */
async function handleValidateAddForm() {
  if (addForm.value.deviceList.length === 0) {
    proxy.$modal.msgError("至少添加一个设备");
    return false;
  }
  return await proxy.$refs["addRef"].validate(valid => {
    return valid;
  })
}

/* 获取告警规则 */
function getAlarmList() {
  getListByObjectId(alarmParams.value).then(response => {
    addForm.value.alarmRuleList = response.data;
    getNoticeList();
  })
}

/* 查询通知方式列表 */
function getNoticeList() {
  // 通知方式选择数据清空
  emailList.value = []
  getAll().then(response => {
    response.data.forEach(item => {
      if (item.sendMethod === sendMethodEnum.MAIL) {
        if (item.httpUrl != null && item.httpUrl.length > 0 && typeof item.httpUrl == "string") {
          emailList.value = item.httpUrl.split(',')
          emailData.value = item;
          addForm.value.alarmRuleList.forEach(rule => rule.noticeId = emailData.value.id)
        }
      }
    });
  })
}

/* 添加通知对象 */
function handleAddEmail() {
  getNoticeList();
  proxy.$refs.addNotice.initPage();
}

/* 初始化界面(父组件调用) */
function initPage() {
  addForm.value = {
    deviceList: [{}],
    alarmRuleList: [],
  }
  getAlarmList();
  openAdd.value = true;
}

/* 表单中新增设备对象 */
function handleAddDeviceObject() {
  if (addForm.value.deviceList.length >= 5) {
    proxy.$modal.msgError("单次最多新增5个设备");
  } else {
    addForm.value.deviceList.push({})
  }
}

/* 低风险阈值非空设置 */
function handleWarnValueChange(data) {
  if (data.warning == null) {
    data.warning = 1;
  }
}

/* 高风险阈值非空设置 */
function handleFatalValueChange(data) {
  if (data.fatal == null) {
    data.fatal = 1;
  }
}

/* 持续时长非空设置 */
function handleDurationValueChange(data) {
  if (data.duration == null) {
    data.duration = 0;
  }
}

defineExpose({
  initPage,
})

</script>
<style lang="scss" scoped>
// 设备信息标题
.device-msg-header {
  padding-top: 0;
  line-height: 38px;
  color: #666666;
  background: #f5f5f5;
  height: 38px;
}

/* 告警信息布局 */
.alarm-msg {

  .col {
    width: 160px;
  }

  .col-2 {
    width: 160px;
    display: flex;
    column-gap: 8px;
  }
}

/* 添加设备 */
.add-device {
  margin-top: 8px;
  width: 100%;
  height: 36px;
  line-height: 36px;
  text-align: center;
  border: 1px dashed rgba(153, 153, 153, 0.5);
  color: #999;
  cursor: pointer;
}


/* 设备信息布局 */
.device-msg {
  padding: 8px 24px;
  display: flex;
  height: 56px;
  width: 100%;
  justify-content: space-between;
  box-shadow: 0px -1px 0px 0px #e5e5e5 inset;

  .col {
    width: 200px;
  }

  .col-2 {
    width: 160px;
  }

  .col-s {
    width: 120px;

    :deep(.el-input .el-input__inner) {
      text-align: left;
    }
  }

  .col-icon {
    width: 20px;
  }
}
</style>