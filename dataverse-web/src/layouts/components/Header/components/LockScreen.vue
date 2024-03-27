<template>
  <div class="setting flx-center toolBar-icon">
    <a-tooltip class="box-item" :title="isLock ? '解锁' : '锁屏'" placement="top">
      <lock-outlined v-if="isLock" :style="{ fontSize: '16px' }" @click="changeLockStatus(false)" />
      <unlock-outlined v-else :style="{ fontSize: '16px' }" @click="changeLockStatus(true)" />
    </a-tooltip>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAppStore } from '@/stores/modules/app'

const appStore = useAppStore()
const isLock = computed(() => appStore.isLock)
const changeLockStatus = (isLock: boolean) => appStore.changeLockStatus(isLock)
</script>
