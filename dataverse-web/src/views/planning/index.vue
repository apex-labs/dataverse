<template>
  <div class="g-card h100 flex ovh">
    <div class="menu-left flex fxbw fdc">
      <ul class="w100 flex fdc">
        <li class="menu-item" :class="{ active: currentTopMenu === 1 }" @click="clcikTopMenu(1)">
          <a-tooltip placement="right" title="数据空间">
            <SvgIcon name="integration" />
          </a-tooltip>
        </li>
        <li class="menu-item" :class="{ active: currentTopMenu === 2 }" @click="clcikTopMenu(2)">
          <a-tooltip placement="right" title="数据源地图">
            <SvgIcon name="map" />
          </a-tooltip>
        </li>
        <li class="menu-item" :class="{ active: currentTopMenu === 3 }" @click="clcikTopMenu(3)">
          <a-tooltip placement="right" title="数据源">
            <SvgIcon name="shujuyuan" />
          </a-tooltip>
        </li>
        <li class="menu-item" :class="{ active: currentTopMenu === 4 }" @click="clcikTopMenu(4)">
          <a-tooltip placement="right" title="数据域">
            <SvgIcon name="dev" />
          </a-tooltip>
        </li>
      </ul>
    </div>
    <div class="main flex1 flex fdc ovh">
      <Space class="flex1 block" v-show="currentTopMenu === 1" />
      <Map class="flex1 block" v-show="currentTopMenu === 2" @select="selectMap" />
      <Source class="flex1 block" ref="sourceRef" v-show="currentTopMenu === 3" />
      <Region class="flex1 block" v-show="currentTopMenu === 4" />
      <!-- <Task class="flex1 block" v-show="currentTopMenu === 5"/> -->
    </div>
  </div>
</template>

<script setup lang="ts" name="planning">
import { ref, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import Map from './map.vue'
import Source from './source.vue'
import Region from './region.vue'
import Space from './space.vue'
import { useDvsStore } from '@/stores/modules/user'
// import Task from '@/views/schedulingTasks/index.vue'

const dvsStore = useDvsStore()
dvsStore.getEnvList()
dvsStore.getSpaceList()
dvsStore.getModelList()

const route = useRoute()

// left menu
const currentTopMenu = ref<number>(route.query.type ? Number(route.query.type) : 1)
const clcikTopMenu = (e: number) => {
  currentTopMenu.value = e
}

const sourceRef = ref()
const selectMap = (map: any) => {
  clcikTopMenu(3)
  nextTick(() => {
    sourceRef.value.addSourceFn(map)
  })
}
</script>

<style scoped lang="less">
.menu-left,
.menu-right {
  width: 40px;
  text-align: center;
  border-left: 1px solid var(--border-color);
  border-right: 1px solid var(--border-color);

  .menu-item {
    border-radius: 4px;
    font-size: 16px;
    height: 28px;
    line-height: 28px;
    margin: 5px;
    cursor: pointer;

    &.active {
      background: var(--bg-color-3);
    }
  }
}

.block {
  color: var(--text-color-1-85);
}
</style>
