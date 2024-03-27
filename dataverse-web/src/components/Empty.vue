<script setup lang="ts">
import { Empty } from 'ant-design-vue'
const simpleImage = Empty.PRESENTED_IMAGE_SIMPLE;
</script>

<template>
  <a-empty :image="simpleImage" v-bind="$attrs" />
</template>

<style></style>