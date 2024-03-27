<template>
  <a-dropdown class="mr20">
    <span class="dropdown-link">
      <span class="mr5">{{ envName || '选择环境' }}</span>
      <DownOutlined class="icon" :style="{fontSize: 9}" />
    </span>
    <template #overlay>
      <a-menu @click="handleCommand">
        <a-menu-item v-for="(item) in envEnum" :key="item.value">{{ item.desc }}</a-menu-item>
      </a-menu>
    </template>
  </a-dropdown>
</template>

<script lang="ts" setup name="Env">
import { ref, computed, toRefs } from 'vue'
import { useDvsStore } from '@/stores/modules/user'
const dvsStore = useDvsStore()
const { env } = toRefs(dvsStore)

const envEnum = ref<any[]>([{ value: 1, desc: 'DEV' }, { value: 2, desc: 'PROD' }])
const envName = computed(() => {
  const item = envEnum.value.find((e: any) => e.value === env.value)
  return item?.desc || ''
})

const handleCommand = ({ key }: any) => {
  dvsStore.setEnv(key)
}
</script>

<style scoped lang="less">
.dropdown-link {
  display: flex;
  align-items: center;
  line-height: 24px;
  font-size: 12px;
  cursor: pointer;

  &:focus {
    outline: none;
  }
  .icon{
    transform: scale(0.75);
  }
}
</style>
