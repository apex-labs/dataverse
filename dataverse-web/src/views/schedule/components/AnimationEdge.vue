<script lang="ts" setup>
import { computed } from 'vue'
import { BaseEdge, Position, getBezierPath } from '@vue-flow/core'

const props = defineProps({
  id: {
    type: String,
    required: true,
  },
  source: {
    type: String,
    required: true,
  },
  target: {
    type: String,
    required: true,
  },
  sourceX: {
    type: Number,
    required: true,
  },
  sourceY: {
    type: Number,
    required: true,
  },
  targetX: {
    type: Number,
    required: true,
  },
  targetY: {
    type: Number,
    required: true,
  },
  sourcePosition: {
    type: String,
    default: Position.Right,
  },
  targetPosition: {
    type: String,
    default: Position.Left,
  },
})

const path = computed(() => getBezierPath(props))

</script>

<template>
  <BaseEdge :id="id" ref="edgeRef" :path="path[0]" />
</template>

<style lang="less">
.vue-flow__edge-path,
.vue-flow__connection-path {
  stroke: var(--border-color-2);
}
</style>
