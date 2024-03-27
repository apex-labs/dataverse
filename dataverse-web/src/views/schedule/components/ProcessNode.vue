<script lang="ts" setup>
import { Handle } from '@vue-flow/core'

defineProps({
  currentNodeCode: {
    type: [String, Object]
  },
  data: {
    type: Object,
    required: true,
  },
  sourcePosition: {
    type: String,
  },
  targetPosition: {
    type: String,
  },
})
</script>

<template>
  <div class="process-node flex aic" :class="{ active: currentNodeCode === data.nodeCode }">
    <Handle type="target" :position="targetPosition" class="handle-target" />
    <Handle type="source" :position="sourcePosition" class="handle-source" />

    <SettingOutlined class="icon set mr10" />
    <span class="flex1 wba">
      {{ data.jobName || data.nodeName }}
    </span>
    <span class="icon status ml10">
      <CheckCircleOutlined />
    </span>

  </div>
</template>

<style lang="less" scoped>
.process-node {
  padding: 5px 10px;
  border-radius: 3px;
  width: 150px;
  font-size: 12px;
  line-height: 1.3;
  border-width: 1px;
  border-style: solid;
  color: var(--text-color-1);
  background: var(--bg-color-2);
  border-color: var(--border-color);
  border-left: 3px solid #52c41a;

  .icon {
    color: var(--text-color-3)
  }

  .status {
    color: #52c41a;
  }

  &.active {
    border-left: 3px solid var(--light-primary-color);
    color: var(--light-primary-color);

    .status {
      color: var(--light-primary-color);
    }
  }
}

.handle-target,
.handle-source {
  background: #fff; //var(--text-color-1);
  border: 1px solid var(--border-color);
}

// .process-node .vue-flow__handle {
//   border: none;
//   height: unset;
//   width: unset;
//   background: transparent;
//   font-size: 12px;
// }
</style>