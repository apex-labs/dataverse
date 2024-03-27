<template>
  <a-dropdown trigger="click" :teleported="false">
    <a-button size="small" type="primary" @click.prevent>
      <span>更多</span>
      <DownOutlined />
    </a-button>
    <template #overlay>
      <a-menu>
        <a-menu-item @click="refresh">
          <RedoOutlined />刷新
        </a-menu-item>
        <a-menu-item divided @click="closeCurrentTab">
          <DeleteOutlined />关闭当前
        </a-menu-item>
        <a-menu-item @click="closeOtherTab">
          <CloseCircleOutlined />关闭其他
        </a-menu-item>
        <a-menu-item @click="closeAllTab">
          <DeleteOutlined />关闭所有
        </a-menu-item>
      </a-menu>
    </template>
  </a-dropdown>
</template>

<script setup lang="ts">
import { inject, nextTick } from 'vue'
import { useTabsStore } from '@/stores/modules/tabs'
import { useKeepAliveStore } from '@/stores/modules/keepAlive'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const tabStore = useTabsStore()
const keepAliveStore = useKeepAliveStore()

// refresh current page
const refreshCurrentPage: Function = inject('refresh') as Function
const refresh = () => {
  setTimeout(() => {
    keepAliveStore.removeKeepAliveName(route.name as string)
    refreshCurrentPage(false)
    nextTick(() => {
      keepAliveStore.addKeepAliveName(route.name as string)
      refreshCurrentPage(true)
    })
  }, 0)
}

// Close Current
const closeCurrentTab = () => {
  if (route.meta.isAffix) return
  tabStore.removeTabs(route.fullPath)
  keepAliveStore.removeKeepAliveName(route.name as string)
}

// Close Other
const closeOtherTab = () => {
  tabStore.closeMultipleTab(route.fullPath)
  keepAliveStore.setKeepAliveName([route.name] as string[])
}

// Close All
const closeAllTab = () => {
  tabStore.closeMultipleTab()
  keepAliveStore.setKeepAliveName()
  router.push('/')
}
</script>

<style scoped lang="less">
@import '../index.less';
</style>
