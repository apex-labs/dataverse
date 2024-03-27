<template>
  <div :class="['breadcrumb-box']">
    <el-breadcrumb :separator-icon="RightOutlined">
      <transition-group name="breadcrumb">
        <el-breadcrumb-item v-for="(item, index) in breadcrumbList" :key="item.path">
          <div class="el-breadcrumb__inner is-link" @click="onBreadcrumbClick(item, index)">
            <icon v-if="item.meta.icon && index < 1" class="breadcrumb-icon">
              <component :is="item.meta.icon"></component>
            </icon>
            <span class="breadcrumb-title">{{ item.meta.title }}</span>
          </div>
        </el-breadcrumb-item>
      </transition-group>
    </el-breadcrumb>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { RightOutlined } from '@ant-design/icons-vue'
import { useAuthStore } from '@/stores/modules/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const breadcrumbList = computed(() => {
  let breadcrumbData =
    authStore.breadcrumbListGet[route.matched[route.matched.length - 1].path] ?? []
  // 🙅‍♀️不需要首页面包屑可删除以下判断
  if (breadcrumbData[0].path !== '/') {
    breadcrumbData = [{ path: '/', meta: { icon: 'HomeFilled', title: '首页' } }, ...breadcrumbData]
  }
  return breadcrumbData
})

// Click Breadcrumb
const onBreadcrumbClick = (item: any, index: number) => {
  if (index !== breadcrumbList.value.length - 1) router.push(item.path)
}
</script>

<style scoped lang="less">
.breadcrumb-box {
  display: flex;
  align-items: center;
  padding: 10px 20px 0;
  overflow: hidden;
  background-color: var(--bg-color-1) !important;
  .el-breadcrumb {
    display: flex;
    white-space: nowrap;
    .el-breadcrumb__item {
      position: relative;
      display: flex;
      align-items: center;
      float: none;
      .el-breadcrumb__inner {
        display: flex;
        align-items: center;
        .breadcrumb-icon {
          margin-right: 6px;
          font-size: 16px;
        }
      }
      :deep(.el-breadcrumb__separator) {
        position: relative;
      }
    }
  }
}
.no-icon {
  .el-breadcrumb {
    .el-breadcrumb__item {
      top: -2px;
      :deep(.el-breadcrumb__separator) {
        top: 2px;
      }
    }
  }
}
</style>
