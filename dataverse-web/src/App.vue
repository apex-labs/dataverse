<template>
  <ConfigProvider :locale="zhCN" component-size="middle" :autoInsertSpaceInButton="false" :theme="themeConfig">
    <StyleProvider hash-priority="high" :transformers="[legacyLogicalPropertiesTransformer]">
      <RouterView />
    </StyleProvider>
  </ConfigProvider>
  <Loading v-if="loading" />
</template>

<script setup lang="ts">
import { reactive, computed, watch, onBeforeMount, onMounted } from 'vue'
import { RouterView } from 'vue-router'
import {
  ConfigProvider,
  StyleProvider,
  legacyLogicalPropertiesTransformer,
  theme
} from 'ant-design-vue'
import { useAppStore } from '@/stores/modules/app'
import Loading from "@/components/Loading.vue"
import themeToken from '@/const/themeJson.json'

const { darkAlgorithm, defaultAlgorithm, compactAlgorithm } = theme
const darkTheme = [darkAlgorithm]
const lightTheme = [defaultAlgorithm]

const appStore = useAppStore()
const currentTheme = computed(() => appStore.theme)
const loading = computed(() => appStore.loading)
onBeforeMount(() => {
  appStore.changeLoading(true)
})
onMounted(async () => {
  setTimeout(() => {
    appStore.changeLoading(false)
  }, 500);
})

const html = document.documentElement as HTMLElement
if (currentTheme.value === 'dark') html.setAttribute('class', 'dark')
else html.setAttribute('class', '')

const themeConfig = reactive({
  token: themeToken.token,
  algorithm: currentTheme.value === 'dark' ? darkTheme : lightTheme
})
watch(
  () => currentTheme.value,
  (v) => {
    themeConfig.algorithm = v === 'dark' ? darkTheme : lightTheme
  }
)

import enUS from 'ant-design-vue/es/locale/en_US'
import zhCN from 'ant-design-vue/es/locale/zh_CN'

import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
dayjs.locale('en')
</script>
