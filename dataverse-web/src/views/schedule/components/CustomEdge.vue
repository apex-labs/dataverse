<template>
  <!-- 原始线 -->
  <!-- <BaseEdge :id="id" :style="style" :path="path[0]" :marker-end="markerEnd" /> -->
  <!-- 自定义线 -->
  <EdgeLabelRenderer>
    <div class="nodrag nopan fxc" :class="[data && data.direction]" :style="{
      width: gap + 'px',
      height: gap + 'px',
      pointerEvents: 'all',
      position: 'absolute',
      transform: `translate(-50%, -50%) translate(${path[1]}px,${path[2]}px)`,
      '--flow-gap': gap / 4 + 'px'
    }">
      <i class="line line-start"></i>
      <i class="line line-end"></i>
      <i class="arrow"></i>
      <div>
        <a-button type="text" style="font-size: 20px; height: auto" @click="() => $emit('addNode', id)">
          <FolderAddOutlined />
        </a-button>
      </div>
    </div>
  </EdgeLabelRenderer>
</template>

<script setup lang="ts">
import { computed, inject } from 'vue'
import { BaseEdge, EdgeLabelRenderer, getBezierPath } from '@vue-flow/core'

const emit = defineEmits(['addNode'])
const props = defineProps({
  id: {
    type: String,
    required: true
  },
  sourceX: {
    type: Number,
    required: true
  },
  sourceY: {
    type: Number,
    required: true
  },
  targetX: {
    type: Number,
    required: true
  },
  targetY: {
    type: Number,
    required: true
  },
  sourcePosition: {
    type: String,
    required: true
  },
  targetPosition: {
    type: String,
    required: true
  },
  data: {
    type: Object,
    required: false
  },
  markerEnd: {
    type: String,
    required: false
  },
  style: {
    type: Object,
    required: false
  }
})
const path = computed(() => getBezierPath(props))
// console.log('path', props.sourcePosition, props.targetPosition);
const gap: number = inject('gap') || 100
</script>

<style lang="less">
.line {
  position: absolute;
  width: var(--flow-gap);
  height: 1px;
  background: var(--text-color-2);
  top: 50%;

  &-start {
    left: 0%;
    margin-left: 5px;
  }

  &-end {
    right: 0%;
    margin-right: 5px;
  }
}

.bottom-top {
  .line {
    position: absolute;
    width: 1px;
    height: var(--flow-gap);
    background: var(--text-color-2);
    left: 50%;
    top: auto;

    &-start {
      top: 0%;
      margin-left: 0;
      margin-top: 5px;
    }

    &-end {
      bottom: 0%;
      margin-right: 0;
      margin-bottom: 5px;
    }
  }
}

.arrow {
  position: absolute;
  background: var(--text-color-2);
  width: 12px;
  height: 1px;
}

.left-right {
  .arrow {
    right: 0%;
    top: 50%;
    margin-right: 8px;
    transform: rotate(45deg) translateY(-6px);
  }
}

.right-left {
  .arrow {
    left: 0%;
    top: 50%;
    margin-left: 8px;
    transform: rotate(-45deg) translateY(-6px);
  }
}

.bottom-top {
  .arrow {
    width: 1px;
    height: 12px;
    bottom: 0%;
    margin-bottom: 8px;
    left: 50%;
    transform: rotate(-45deg) translateX(-6px);
  }
}
</style>
