<template>
  <div class="menu">
    <a-menu mode="horizontal" v-model:selected-keys="state.selectedKeys" :open-keys="state.openKeys"
      @click="clickMenuItem">
      <MenuItem :menus="menuList" />
    </a-menu>
  </div>
</template>

<script setup lang="ts">
import { reactive, computed, watch } from 'vue'
import { useRoute, useRouter, type RouteMeta } from 'vue-router';
import { staticRouter } from '@/router/modules/staticRouter'
import MenuItem from './MenuItem.vue'

defineOptions({
  name: 'my-menu'
})

// 当前路由
const currentRoute = useRoute();
const router = useRouter();
const state = reactive({
  openKeys: [] as string[],
  selectedKeys: [currentRoute.path] as string[],
});

const menuList = computed(() => staticRouter[0].children || [])
const getRouteByName = (name: string) => router.getRoutes().find((n) => n.name === name);
// 根据activeMenu获取指定的menu
const getTargetMenuByActiveMenuName = (activeMenu: string) => {
  return router.getRoutes().find((n) => [n.name, n.path].includes(activeMenu));
};

// 获取当前打开的子菜单
const getOpenKeys = (): string[] => {
  const meta: RouteMeta = currentRoute.meta;
  if (meta?.activeMenu) {
    const targetMenu = getTargetMenuByActiveMenuName(meta.activeMenu);
    return targetMenu?.meta?.path ?? [meta?.activeMenu];
  }

  return (
    meta?.hidden
      ? state?.openKeys || []
      : currentRoute.meta?.namePath ?? currentRoute.matched.slice(1).map((n) => n.name)
  ) as string[];
};

// 跟随页面路由变化，切换菜单选中状态
watch(
  () => currentRoute.fullPath,
  () => {
    if (currentRoute.name === 'login') return;
    state.openKeys = getOpenKeys();
    const meta: any = currentRoute.meta;
    if (meta && meta.activeMenu) {
      const targetMenu = getTargetMenuByActiveMenuName(meta.activeMenu);
      state.selectedKeys = [targetMenu?.path ?? meta?.activeMenu] as string[];
    } else {
      state.selectedKeys = [currentRoute.meta?.activeMenu ?? currentRoute.path] as string[];
    }
  },
  {
    immediate: true,
  },
);

// 点击菜单
const clickMenuItem = ({ key }) => {
  if (key === currentRoute.path) return;
  router.push({ path: key });
  // 检测是新开页面还是当前页面
  // const targetRoute = getRouteByName(key);
  // const { isExt, openMode } = targetRoute?.meta || {};
  // if (isExt && openMode !== 2) {
  //   window.open(key);
  // } else {
  //   router.push({ name: key });
  // }
};
</script>

<style lang="less" scoped>
.menu {
  height: 55px;
  display: flex;
  justify-content: center;

  :deep(.ant-menu) {
    height: 100%;
    overflow: hidden;
    background: none;
    border-bottom: none;

    .ant-menu-item,
    .ant-menu-submenu {
      padding: 0;

      &-title {
        line-height: 1;

        [class^='icon'] {
          margin-right: 0;
        }
      }

      .ant-menu-title-content {
        min-width: 60px;
        display: block;
        padding: 0 10px;
      }
    }

    .level_1 {
      line-height: 1;
      display: block;
      padding: 8px 0 4px;
      text-align: center;

      .icon {
        font-size: 20px;
      }

      &+p {
        line-height: 1;
        font-size: 12px;
      }
    }

    .ant-menu-submenu-title {
      padding-right: 0px;
    }

    .ant-menu-item-selected,
    .ant-menu-submenu-selected {
      color: #fff;
      background: linear-gradient(180deg, #3637ff 0%, #2231c8 72%, #234297 100%);
      border-bottom: none !important;

      &::before {
        width: 0;
      }

      .ant-menu-submenu-title {
        color: #fff;
        background: linear-gradient(180deg, #3637ff 0%, #2231c8 72%, #234297 100%);
        border-bottom: none !important;

        &:hover {
          color: #fff;
        }
      }
    }

    .ant-menu-overflow-item-rest {
      text-align: center;
      background: none !important;

      .ant-menu-submenu-title {
        line-height: 56px;
        border-radius: 0;
        background: none !important;
        color: var(--text-color-1);

        &:hover {
          color: var(--text-color-1);
        }

        .anticon {
          font-size: 20px;
        }
      }
    }
  }
}
</style>
