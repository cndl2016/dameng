<!-- 自定义表格组件 wys 2025-11-13 -->
<template>
  <div class="custom-table">
    <div class="table-area">
      <el-table :data="dataInfo" v-bind="$attrs" class="common-table" ref="elTableRef" v-if="showTable">
        <!-- 选择列 -->
        <el-table-column v-if="showSelection" type="selection" width="70" />
        <!-- 序号列 -->
        <el-table-column v-if="showIndex" type="index" width="70" label="序号" />
        <template v-for="(column, index) in config">
          <el-table-column :key="index" v-bind="column" v-if="!column.hidden"
            :show-overflow-tooltip="column.tooltip || true" :sortable="column.sortable">
            <!-- 插槽列 -->
            <template v-if="column.slotName && dataInfo.length > 0" #default="{ row, $index }">
              <slot v-if="$index > -1" :name="column.slotName" :row="row">
              </slot>
            </template>
            <!-- 枚举值标签列 -->
            <template v-if="column.type === 'enum'" #default="scope">
              {{ column.enumData.getDescFromValue(scope.row[column.prop]) }}
            </template>
            <!-- 字典标签列 -->
            <template v-if="column.type === 'dict'" #default="scope">
              <dict-tag :options="column.dictData" :value="scope.row[column.prop]" />
            </template>
            <!-- 时间列 -->
            <template v-if="column.type === 'time'" #default="scope">
              <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
            <!-- 按钮列 -->
            <template v-if="column.type === 'button'" #default="scope">
              <el-button :type="button.type" link @click="command(scope.row, button.command)"
                v-for="(button, index) in column.buttons" v-hasPermi="button.limits || ['']"
                :disabled="button.disabled?.(scope.row)" v-show="button.isShow?.(scope.row) ?? true">
                {{ button.name }}
              </el-button>
            </template>
          </el-table-column>
        </template>
      </el-table>
    </div>
    <!-- 分页组件 -->
    <pagination v-show="total > 10 && showPagination" :total="total" v-model:page="pageParams.pageNum"
      v-model:limit="pageParams.pageSize" @pagination="paginationChange" />
  </div>
</template>
<script setup>
const { proxy } = getCurrentInstance();
const props = defineProps({
  dataInfo: {
    type: Array,
    default: () => [],
  }, // 表格数据
  showSelection: {
    type: Boolean,
    default: () => false,
  }, // 是否显示表格选择框
  showTable: {
    type: Boolean,
    default: () => true,
  }, // 是否显示表格
  showPagination: {
    type: Boolean,
    default: () => false,
  }, // 是否显示分页组件
  showIndex: {
    type: Boolean,
    default: () => false,
  }, // 是否显示表格序号
  config: {
    type: Array,
    default: () => [],
  }, // 表格配置项
  total: {
    type: Number,
    default: () => 0,
  }, // 总数
  pageParams: {
    type: Object,
    default: () => {
      return { pageNum: 1, pageSize: 1 }
    },
  }, // 查询参数
});
const { dataInfo, showSelection, showIndex, config, total, pageParams, showPagination } = toRefs(props);
// 父组件提供的方法
const emit = defineEmits();
const elTableRef = ref(null);

// 操作列按钮触发
function command(row, command) {
  emit('buttonCommand', command, row);
}

// 分页更改
function paginationChange() {
  emit('paginationChange');
}
defineExpose({
  elTable: elTableRef
})
</script>