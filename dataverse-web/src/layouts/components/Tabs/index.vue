<template>
  <div class="tabs-box">
    <div class="tabs-menu">
      <a-tabs
        v-model:activeKey="tabsMenuValue"
        type="card"
        @tab-click="tabClick"
        @tab-remove="tabRemove"
      >
        <a-tab-pane
          v-for="item in tabsMenuList"
          :key="item.path"
          :label="item.title"
          :name="item.path"
          :closable="item.close"
        >
          <template #label>
            <icon v-show="item.icon" class="tabs-icon">
              <component :is="item.icon"></component>
            </icon>
            {{ item.title }}
          </template>
        </a-tab-pane>
      </a-tabs>
      <MoreButton />
    </div>
  </div>
</template>

<script setup lang="ts">
// import Sortable from "sortablejs";
import { ref, computed, watch, onMounted } from 'vue'
import Icon from '@ant-design/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { useTabsStore } from '@/stores/modules/tabs'
import { useAuthStore } from '@/stores/modules/auth'
import { useKeepAliveStore } from '@/stores/modules/keepAlive'
import { TabsPaneContext, TabPaneName } from 'ant-design-vue'
import MoreButton from './components/MoreButton.vue'

const route = useRoute()
const router = useRouter()
const tabStore = useTabsStore()
const authStore = useAuthStore()
const keepAliveStore = useKeepAliveStore()

const tabsMenuValue = ref(route.fullPath)
const tabsMenuList = computed(() => tabStore.tabsMenuList)

onMounted(() => {
  // tabsDrop();
  initTabs()
})

// 监听路由的变化（防止浏览器后退/前进不变化 tabsMenuValue）
watch(
  () => route.fullPath,
  () => {
    if (route.meta.isFull) return
    tabsMenuValue.value = route.fullPath
    const tabsParams = {
      icon: route.meta.icon as string,
      title: route.meta.title as string,
      path: route.fullPath,
      name: route.name as string,
      close: !route.meta.isAffix
    }
    tabStore.addTabs(tabsParams)
    route.meta.isKeepAlive && keepAliveStore.addKeepAliveName(route.name as string)
  },
  { immediate: true }
)

// tabs 拖拽排序
// const tabsDrop = () => {
//   Sortable.create(document.querySelector(".a-tabs__nav") as HTMLElement, {
//     draggable: ".a-tabs__item",
//     animation: 300,
//     onEnd({ newIndex, oldIndex }) {
//       const tabsList = [...tabStore.tabsMenuList];
//       const currRow = tabsList.splice(oldIndex as number, 1)[0];
//       tabsList.splice(newIndex as number, 0, currRow);
//       tabStore.setTabs(tabsList);
//     }
//   });
// };

// 初始化需要固定的 tabs
const initTabs = () => {
  authStore.flatMenuListGet.forEach((item) => {
    if (item.meta.isAffix && !item.meta.isHide && !item.meta.isFull) {
      const tabsParams = {
        icon: item.meta.icon,
        title: item.meta.title,
        path: item.path,
        name: item.name,
        close: !item.meta.isAffix
      }
      tabStore.addTabs(tabsParams)
    }
  })
}

// Tab Click
const tabClick = (tabItem: TabsPaneContext) => {
  const fullPath = tabItem.props.name as string
  router.push(fullPath)
}

// Remove Tab
const tabRemove = (fullPath: TabPaneName) => {
  const name = tabStore.tabsMenuList.filter((item) => item.path == fullPath)[0].name || ''
  keepAliveStore.removeKeepAliveName(name)
  tabStore.removeTabs(fullPath as string, fullPath == route.fullPath)
}
</script>

<style scoped lang="less">
@import './index.less';
</style>
