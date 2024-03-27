<template>
  <div class="toolbar flex aic">
    <div class="flex aic flex1">
      <span class="btn" @click="() => execute(true, true)">
        <a-tooltip placement="bottom" title="显示表结构">
          <TableOutlined :style="{ color: '#5272C1' }" />
        </a-tooltip>
      </span>
      <span class="btn" @click="() => execute()">
        <a-tooltip placement="bottom" title="执行SQL">
          <SvgIcon name="start" color="#6AA48D" />
        </a-tooltip>
      </span>
      <span class="btn" @click="() => execute(false, true)">
        <a-tooltip placement="bottom" title="执行选中的SQL">
          <SvgIcon name="start-column" color="#6AA48D" />
        </a-tooltip>
      </span>
      <!-- <span class="btn" @click="() => stop()">
        <a-tooltip placement="bottom" title="停止执行SQL">
          <SvgIcon name="stop" color="#838486" />
        </a-tooltip>
      </span> -->
      <span class="btn" @click="() => formatSql()">
        <a-tooltip placement="bottom" title="格式化">
          <SvgIcon name="format" color="#5272C1" />
        </a-tooltip>
      </span>
      <!-- <span class="btn" @click="() => validate()">
        <a-tooltip placement="bottom" title="验证">
          <SvgIcon name="validate" color="#6AA48D" />
        </a-tooltip>
      </span> -->
      <span class="btn" @click="() => save()">
        <a-tooltip placement="bottom" title="保存">
          <SvgIcon name="save" color="#5272C1" />
        </a-tooltip>
      </span>
      <!-- <span class="btn" @click="() => dev()">
        <a-tooltip placement="bottom" title="开发">
          <SvgIcon name="dev" color="#EF754A" />
        </a-tooltip>
      </span> -->
      <!-- <span class="btn" @click="() => translate()">
        <a-tooltip placement="bottom" title="翻译">
          <SvgIcon name="translate" color="#6AA48D" />
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
import SvgIcon from '@/components/SvgIcon/index.vue'

const emit = defineEmits(['format', 'execute', 'stop', 'validate', 'save', 'dev', 'translate', 'setConfig', 'viewConfig'])

const formatSql = () => {
  emit('format')
}
const execute = (isTable: boolean = false, isColumn: boolean = false) => {
  emit('execute', isTable, isColumn)
}
const stop = () => {
  emit('stop')
}
const validate = () => {
  emit('validate')
}
const save = () => {
  emit('save')
}
const dev = () => {
  emit('dev')
}
const translate = () => {
  emit('translate')
}
const setConfig = () => {
  emit('setConfig')
}
const viewConfig = () => {
  emit('viewConfig')
}
const handleCommand = ({ key }: any) => {
  console.log(key);
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
