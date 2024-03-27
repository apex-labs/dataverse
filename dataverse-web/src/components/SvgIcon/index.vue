<template>
  <svg v-bind="$attrs" class="svg-icon" :style="getStyle" aria-hidden="true">
    <use :xlink:href="symbolId" :fill="color" />
  </svg>
</template>

<script lang="ts" setup name="SvgIcon">
import { computed, type CSSProperties } from 'vue'

const props = defineProps({
  prefix: {
    type: String,
    default: 'svg-icon'
  },
  name: {
    type: String,
    required: true
  },
  size: {
    type: [Number, String],
    default: 16
  },
  color: { type: String, default: '' }
})
const symbolId = computed(() => `#${props.prefix}-${props.name}`)
const getStyle = computed((): CSSProperties => {
  const { size } = props
  const s = `${size}`.replace('px', '').concat('px')
  return {
    width: s,
    height: s
  }
})
</script>

<style lang="less">
.svg-icon {
  overflow: hidden;
  fill: currentcolor;
  vertical-align: -0.15em;
  outline: unset;
}
</style>
