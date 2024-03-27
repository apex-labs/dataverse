<template>
  <div class="fullscreen flx-center">
    <a-tooltip class="box-item" :title="isFullscreen ? '缩小' : '全屏'" placement="top">
      <FullscreenExitOutlined v-if="isFullscreen" :style="{ fontSize: '16px' }" @click="toggle" />
      <FullscreenOutlined v-else :style="{ fontSize: '16px' }" @click="toggle" />
    </a-tooltip>
  </div>
</template>

<script setup lang="ts">
import { useFullscreen } from '@vueuse/core'
const { toggle, isFullscreen } = useFullscreen()
</script>
