<template>
  <div class="toolbar flex aic">
    <div class="flex aic flex1">
      <span class="btn" @click="editFn">
        <a-tooltip :title="mode === 1 ? '代码模式' : '页面模式'" placement="bottom">
          <CodeOutlined :style="{ color: '#5272C1' }" v-if="mode === 1" />
          <DesktopOutlined :style="{ color: '#5272C1' }" v-if="mode === 2" />
        </a-tooltip>
      </span>
      <span class="btn" @click="lockFn">
        <a-tooltip :title="isLock ? '解锁' : '锁定'" placement="bottom">
          <LockOutlined v-if="isLock" :style="{ color: '#8C8C8C' }" />
          <UnlockOutlined v-else :style="{ color: '#8C8C8C' }" />
        </a-tooltip>
      </span>
      <span class="btn" @click="formatFn">
        <a-tooltip placement="bottom" title="格式化">
          <SvgIcon name="format" color="#5272C1" />
        </a-tooltip>
      </span>
      <!-- <span class="btn" @click="validateFn">
        <a-tooltip placement="bottom" title="验证">
          <SvgIcon name="validate" color="#6AA48D" />
        </a-tooltip>
      </span> -->
      <span class="btn" @click="saveFn">
        <a-tooltip placement="bottom" title="保存">
          <SvgIcon name="save" color="#5272C1" />
        </a-tooltip>
      </span>
      <!-- <span class="btn" @click="importFn">
        <a-tooltip placement="bottom" title="导入">
          <ImportOutlined :style="{ color: '#6AA48D' }" />
        </a-tooltip>
      </span>
      <span class="btn" @click="exportFn">
        <a-tooltip placement="bottom" title="导出">
          <ExportOutlined :style="{ color: '#5272C1' }" />
        </a-tooltip>
      </span> -->
      <span class="btn" @click="() => setConfig()">
        <a-tooltip placement="bottom" title="配置调度计划">
          <SvgIcon name="setConfig" color="#5272C1" />
        </a-tooltip>
      </span>
      <span class="btn" @click="() => viewConfig()">
        <a-tooltip placement="bottom" title="查看任务依赖">
          <SvgIcon name="viewConfig" color="#5272C1" />
        </a-tooltip>
      </span>
    </div>
    <slot></slot>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import SvgIcon from '@/components/SvgIcon/index.vue'

const props = defineProps({
  isLock: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['edit', 'lock', 'format', 'clear', 'validate', 'save', 'import', 'export', 'setConfig', 'viewConfig'])

const mode = ref(1) // 1:页面模式 2:代码模式
const editFn = () => {
  mode.value = mode.value === 1 ? 2 : 1
  emit('edit', mode.value)
}
const lockFn = () => {
  emit('lock', !props.isLock)
}
const formatFn = () => {
  emit('format')
}
const clearFn = () => {
  emit('clear')
}
const validateFn = () => {
  emit('validate')
}
const saveFn = () => {
  emit('save')
}
const importFn = () => {
  emit('import')
}
const exportFn = () => {
  emit('export')
}
const setConfig = () => {
  emit('setConfig')
}
const viewConfig = () => {
  emit('viewConfig')
}
</script>

<style scoped lang="less">
.toolbar {
  height: 40px;
  padding: 12px 6px;

  .btn {
    margin: 0 6px;
    cursor: pointer;
  }
}
</style>
