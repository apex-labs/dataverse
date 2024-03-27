<template>
  <a-layout-content>
    <div class="main h100">
      <router-view v-slot="{ Component, route }">
        <transition appear name="fade-transform" mode="out-in">
          <keep-alive :include="keepAliveName">
            <component :is="Component" v-if="isRouterShow" :key="route.fullPath" />
          </keep-alive>
        </transition>
      </router-view>
      <!-- <router-view></router-view> -->
    </div>
  </a-layout-content>
</template>

<script setup lang="ts">
import { ref, onBeforeUnmount, provide, watch } from 'vue'
import { storeToRefs } from 'pinia'
import { useDebounceFn } from '@vueuse/core'
import { useKeepAliveStore } from '@/stores/modules/keepAlive'

defineOptions({ name: 'my-main' })

const keepAliveStore = useKeepAliveStore()
const { keepAliveName } = storeToRefs(keepAliveStore)

// 注入刷新页面方法
const isRouterShow = ref(true)
const refreshCurrentPage = (val: boolean) => (isRouterShow.value = val)
provide('refresh', refreshCurrentPage)

// 监听布局变化，在 body 上添加相对应的 layout class
// watch(
//   () => layout.value,
//   () => {
//     const body = document.body as HTMLElement
//     body.setAttribute('class', layout.value)
//   },
//   { immediate: true }
// )

// 监听窗口大小变化，折叠侧边栏
const screenWidth = ref(0)
const listeningWindow = useDebounceFn(() => {
  screenWidth.value = document.body.clientWidth
}, 100)
window.addEventListener('resize', listeningWindow, false)
onBeforeUnmount(() => {
  window.removeEventListener('resize', listeningWindow)
})
</script>

<style scoped lang="less">
.ant-layout-content {
  box-sizing: border-box;
  overflow-x: hidden;
  background-color: var(--bg-color-1);
}

.ant-layout-footer {
  height: auto;
  padding: 0;
}

.main {
  border-top: 1px solid var(--border-color);
  color: var(--text-color-1);
}
</style>
