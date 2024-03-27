<template>
  <div class="tool-bar-ri">
    <div class="header-icon">
      <SwitchDark />
      <!-- <LockScreen id="lockscreen" /> -->
      <Fullscreen id="fullscreen" />
      <span class="custom">创络（上海）数据科技有限公司</span>
      <Avatar id="avatar" />
    </div>
    <span class="username">{{ username }}</span>
  </div>
</template>

<script setup lang="ts">
import { computed, toRefs } from 'vue'
import { useAppStore } from '@/stores/modules/app'
// import LockScreen from './components/LockScreen.vue'
import Fullscreen from './components/Fullscreen.vue'
import Avatar from './components/Avatar.vue'
import SwitchDark from '@/components/SwitchDark/index.vue'

const appStore = useAppStore()
const username = computed(() => appStore.userInfo.username)
</script>

<style scoped lang="less">
.tool-bar-ri {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 5px 0;

  .header-icon {
    display: flex;
    align-items: center;
    justify-content: center;

    &>* {
      margin-left: 15px;
      cursor: pointer;
    }
  }

  .custom {
    display: block;
    max-width: 134px;
    height: 24px;
    padding: 0 10px;
    overflow: hidden;
    font-size: 12px;
    line-height: 24px;
    color: var(--text-color-1);
    text-overflow: ellipsis;
    white-space: nowrap;
    background: rgb(255 255 255 / 20%);
    border-radius: 2px;
  }

  .username {
    margin-left: 15px;
    font-size: 15px;
  }

  #avatar {
    display: flex;
  }
}
</style>
