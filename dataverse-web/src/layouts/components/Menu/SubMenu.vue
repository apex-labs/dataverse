<template>
  <template v-for="subItem in menuList">
    <template v-if="!subItem.meta.hidden">
      <a-sub-menu v-if="subItem.children?.length" :key="subItem.path">
        <template #title>
          <i v-if="subItem.meta.icon" class="icon iconfont" :class="subItem.meta.icon"></i>
          <span class="ac sle">{{ subItem.meta.title }}</span>
        </template>
        <SubMenu :menu-list="subItem.children" />
      </a-sub-menu>
      <a-menu-item v-else :key="subItem.path" @click="handleClickMenu(subItem)">
        <i v-if="subItem.meta.icon" class="icon iconfont" :class="subItem.meta.icon"></i>
        <span class="ac sle">{{ subItem.meta.title }}</span>
      </a-menu-item>
    </template>
  </template>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { MenuOptions } from 'ant-design-vue'

defineProps<{ menuList: MenuOptions[] }>()

const router = useRouter()
const handleClickMenu = (subItem: MenuOptions) => {
  if (subItem.meta.isLink) return window.open(subItem.meta.isLink, '_blank')
  router.push(subItem.path)
}
</script>

<style lang="less"></style>
