<!-- 横向布局 -->
<template>
  <a-layout class="layout">
    <a-layout-header :style="{ display: 'flex' }" v-if="!route.meta.hiddenHeader">
      <div class="flex1 flex aic ovh">
        <div class="logo">
          <img class="logo-img" :src="src" alt="logo" />
        </div>
        <Dataspace class="mr10 flex1 ovh" v-if="isShowSpace" />
        <Env class="flex1 ovh" v-if="isShowSpace && isShowEnv" />
      </div>
      <Menu class="flex1 ovh" />
      <ToolBarRight class="flex1 ar" />
    </a-layout-header>
    <!-- <Breadcrumb /> -->
    <Main />
    <!-- <Footer /> -->
  </a-layout>
</template>

<script setup lang="ts" name="layoutTransverse">
import { computed, toRefs } from 'vue'
import { useAppStore } from '@/stores/modules/app'
import { useRoute } from 'vue-router'
import Main from '@/layouts/components/Main/index.vue'
import ToolBarRight from '@/layouts/components/Header/ToolBarRight.vue'
import Menu from '@/layouts/components/Menu/index.vue'
// import Breadcrumb from '@/layouts/components/Breadcrumb/index.vue'
import Dataspace from '@/layouts/components/Header/components/Dataspace.vue'
import Env from '@/layouts/components/Header/components/Env.vue'
import logoDark from '@/assets/logo.svg'
import logoLight from '@/assets/logo_light.svg'
// import Footer from '@/layouts/components/Footer/index.vue'
import { useDvsStore } from '@/stores/modules/user'

const route = useRoute()

const dvsStore = useDvsStore()
const { currentSpace } = toRefs(dvsStore)
const isShowSpace = computed(() => ['development', 'operations'].some(e => route.path.indexOf(e) >= 0))
const isShowEnv = computed(() => currentSpace.value.envMode === 2)

const appStore = useAppStore()
const { theme } = toRefs(appStore)
const src = computed(() => (theme.value === 'dark' ? logoLight : logoDark))
</script>

<style scoped lang="less">
.layout {
  width: 100%;
  height: 100%;

  :deep(.ant-layout-header) {
    box-sizing: border-box;
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: auto;
    line-height: unset;
    padding: 0 15px;
    background-color: var(--bg-color-1);
    color: var(--text-color-1);

    .logo {
      margin-right: 20px;
        min-width: 169px;
        height: 34px;

      .logo-img {
        display: block;
      }
    }

    .tool-bar-ri {

      .toolBar-icon,
      .username,
      .fullscreen {
        color: var(--text-color-1);
      }
    }
  }
}
</style>
