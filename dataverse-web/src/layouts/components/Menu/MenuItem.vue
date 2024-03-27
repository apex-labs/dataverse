
<template>
  <template v-for="item in filterMenus" :key="item.path || item.name || item.fullPath">
    <!-- 目录 -->
    <template v-if="isShowSubMenu(item)">
      <a-sub-menu :key="item?.path" v-bind="$attrs">
        <template #title>
          <MenuItemContent :item="item" />
        </template>
        <template v-if="item.children">
          <!-- 递归生成菜单 -->
          <MenuItem :menus="item.children" />
        </template>
      </a-sub-menu>
    </template>
    <!-- 菜单 -->
    <template v-else>
      <a-menu-item :key="item?.path">
        <MenuItemContent :item="item" />
      </a-menu-item>
    </template>
  </template>
</template>

<script setup lang="ts">
import { type PropType, computed } from 'vue'
import { type RouteRecordRaw } from 'vue-router'
import MenuItemContent from './MenuItemContent.vue';

defineOptions({
  name: 'menu-item'
})

const props = defineProps({
  menus: {
    type: Array as PropType<RouteRecordRaw[]>,
    default: () => [],
  },
});

const filterMenus = computed(() => {
  return [...props.menus]
    .filter((n) => !n.meta?.hidden)
    .sort((a: any, b: any) => (a?.meta?.orderNum || 0) - (b?.meta?.orderNum || 0));
});

const isShowSubMenu = (menuItem: RouteRecordRaw) => {
  return (
    menuItem?.meta?.type === 0 ||
    (!Object.is(menuItem?.meta?.hideChildrenInMenu, true) && menuItem?.children?.length)
  );
};

</script>

<style lang="less" scoped></style>
