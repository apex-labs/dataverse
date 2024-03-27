<template>
  <div class="g-card h100 flex ovh">
    <div class="menu-left flex fxbw fdc">
      <ul class="w100 flex fdc">
        <li class="menu-item" :class="{ active: currentTopMenu === 1 }" @click="clcikTopMenu(1)">
          <a-tooltip placement="right" title="存储区">
            <SvgIcon name="storage" />
          </a-tooltip>
        </li>
        <li class="menu-item" :class="{ active: currentTopMenu === 2 }" @click="clcikTopMenu(2)">
          <a-tooltip placement="right" title="PORT">
            <SvgIcon name="dev" />
          </a-tooltip>
        </li>
      </ul>
    </div>
    <div class="main flex1 flex fdc ovh">
      <Storage v-if="currentTopMenu === 1" />
      <Port v-if="currentTopMenu === 2" />
    </div>
  </div>
</template>

<script setup lang="ts" name="config">
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import Storage from './components/Storage.vue'
import Port from './components/Port.vue'

const route = useRoute()

// left menu
const currentTopMenu = ref<number>(route.query.type ? Number(route.query.type) : 1)
const clcikTopMenu = (e: number) => {
  currentTopMenu.value = e
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
</style>
