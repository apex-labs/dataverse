<template>
  <icon class="collapse-icon" @click="changeCollapse">
    <component :is="globalStore.isCollapse ? 'expand' : 'fold'"></component>
  </icon>
</template>

<script setup lang="ts">
import { useGlobalStore } from "@/stores/modules/global";

const globalStore = useGlobalStore();
const changeCollapse = () => globalStore.setGlobalState("isCollapse", !globalStore.isCollapse);
</script>

<style scoped lang="less">
.collapse-icon {
  margin-right: 20px;
  font-size: 22px;
  cursor: pointer;
}
</style>
