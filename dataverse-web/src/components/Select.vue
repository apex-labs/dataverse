<template>
  <a-select v-bind="$attrs">
    <slot></slot>
  </a-select>
</template>

<script setup lang="ts">
const props = defineProps({
  values: {
    type: Array, default: () => []
  },
})

</script>

<style scoped lang="less"></style>
