<template>
  <div class="g-card flex fdc ovh">
    <div class="fxbw block-t">
      <h4 class="tit">数据源地图</h4>
      <div class="btns"></div>
    </div>
    <div class="block-c">
      <div class="sql-type">关系型数据库</div>
      <div class="sql-list flex ffrw">
        <div class="sql-item" v-for="item in sql_list" :key="item.datasourceTypeId" :title="item.comment"
          @click="$emit('select', item)">
          <SvgIcon :size="80" :name="item.datasourceTypeName.toLocaleLowerCase()" />
        </div>
      </div>
      <div class="sql-type">NoSQL数据库</div>
      <div class="sql-list flex ffrw">
        <div class="sql-item" v-for="item in nosql_list" :key="item.datasourceTypeId" :title="item.comment"
          @click="$emit('select', item)">
          <SvgIcon :size="80" :name="item.datasourceTypeName.toLocaleLowerCase()" />
        </div>
      </div>
      <div class="sql-type">消息中间件</div>
      <div class="sql-list flex ffrw">
        <div class="sql-item" v-for="item in middleware_list" :key="item.datasourceTypeId" :title="item.comment"
          @click="$emit('select', item)">
          <SvgIcon :size="80" :name="item.datasourceTypeName.toLocaleLowerCase()" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, toRefs } from 'vue'
import SvgIcon from '@/components/SvgIcon/index.vue'
import { useDvsStore } from '@/stores/modules/user'
const dvsStore = useDvsStore()
dvsStore.getMapList()
const { mapList } = toRefs(dvsStore)

defineEmits(['select'])

const sql_list = computed(() => mapList.value.filter((e: any) => e.datasourceCategoryId === 1))
const nosql_list = computed(() => mapList.value.filter((e: any) => e.datasourceCategoryId === 2))
const middleware_list = computed(() => mapList.value.filter((e: any) => e.datasourceCategoryId === 3))
</script>

<style scoped lang="less">
.block-t {
  padding: 10px 20px;
  line-height: 32px;
  border-bottom: 1px solid var(--border-color);

  .tit {
    font-size: 16px;
  }
}

.block-c {
  margin-top: 10px;
  min-height: 200px;
  padding: 0 20px 20px;

  .sql-type {
    line-height: 32px;
  }

  .sql-list {
    margin: 5px 0 10px;

    .sql-item {
      background-color: #fff;
      border: 1px solid var(--border-color-2);
      padding: 10px 20px;
      margin: 0 10px 10px 0;
      cursor: pointer;
      color: #000;
    }
  }
}
</style>
