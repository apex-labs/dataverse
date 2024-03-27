<template>
  <a-switch v-model:checked="isDark" class="theme" @change="changeTheme">
    <template #checkedChildren>
      <SvgIcon name="moon" color="#2234BE" class="mt3" />
    </template>
    <template #unCheckedChildren>
      <SvgIcon name="sun" color="#fff" class="mt1" />
    </template>
  </a-switch>
</template>

<script setup lang="ts" name="SwitchDark">
import { ref, toRefs } from 'vue'
import SvgIcon from '@/components/SvgIcon/index.vue'
import { useAppStore } from '@/stores/modules/app'

defineOptions({ name: 'switch-dark' })

const appStore = useAppStore()
const { theme } = toRefs(appStore)
const isDark = ref<boolean>(theme.value === 'dark')

const changeTheme = () => {
  appStore.changeTheme()
}
</script>
