<template>
  <div class="level_1">
    <template v-if="item?.meta?.icon">
      <SvgIcon v-if="item?.meta?.type === 'svg'" class="icon" :size="20" :name="item?.meta?.icon" />
      <i v-if="item?.meta?.type === 'icon'" class="icon iconfont" :class="item?.meta?.icon"></i>
    </template>
  </div>
  <p class="ac">{{ item?.meta?.title }}</p>
</template>

<script lang="ts" setup>
import { type PropType } from 'vue'
import type { RouteRecordRaw } from 'vue-router';
import SvgIcon from '@/components/SvgIcon/index.vue'

defineProps({
  item: {
    type: Object as PropType<RouteRecordRaw>,
    default: () => ({}),
  },
});
</script>
