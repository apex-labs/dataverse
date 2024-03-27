<template>
  <a-dropdown class="mr20">
    <span class="dropdown-link">
      <span class="mr5 toe">{{ parentName || '选择数据仓库' }}</span>
      <DownOutlined class="icon" :style="{fontSize: 9}"/>
    </span>
    <template #overlay>
      <a-menu @click="handleCommand">
        <a-menu-item v-for="(item) in spaceList" :key="item.dvsCode">{{ item.parentAlias }}</a-menu-item>
      </a-menu>
    </template>
  </a-dropdown>
</template>

<script lang="ts" setup name="DataSource">
import { computed, toRefs } from 'vue'
import { useDvsStore } from '@/stores/modules/user'
const dvsStore = useDvsStore()
dvsStore.getMapList()
dvsStore.getEnvList()
dvsStore.getModelList()
dvsStore.getSpaceList()
const { dvsCode, spaceList } = toRefs(dvsStore)

const parentName = computed(() => {
  const dvs = spaceList.value.find((e: any) => e.dvsCode === dvsCode.value)
  return dvs?.parentAlias || ''
})
const handleCommand = ({ key }: any) => {
  dvsStore.setDvsCode(key)
  const dvs = spaceList.value.find((e: any) => e.dvsCode === key)
  dvsStore.setCurrentSpace(dvs)
  // 若当前空间模式是BASIC，env设置为BASIC，若是DEV-PROD，env设置为DEV
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
