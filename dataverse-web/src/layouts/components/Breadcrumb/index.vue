<template>
  <div :class="['breadcrumb-box']">
    <a-breadcrumb>
      <template #separator>
        <RightOutlined />
      </template>
      <a-breadcrumb-item class="ant-breadcrumb-item" v-for="(item, index) in breadcrumbList" :key="item.path">
        <span class="a-breadcrumb__inner is-link" @click="onBreadcrumbClick(item, index)">
          <HomeOutlined v-if="index < 1" class="breadcrumb-icon" />
          <router-link class="breadcrumb-title" :to="item.path" v-if="index < breadcrumbList.length - 1">
            {{ item.meta.title }}
          </router-link>
          <span v-else class="breadcrumb-title">{{ item.meta.title }}</span>
        </span>
        <template #overlay v-if="item.children && item.children.length > 0">
          <a-menu>
            <a-menu-item v-for="child in item.children" :key="child.path">
              <router-link class="breadcrumb-title" :to="child.path">
                {{ child.meta.title }}
              </router-link>
            </a-menu-item>
          </a-menu></template>
      </a-breadcrumb-item>
      <!-- <transition-group name="breadcrumb">
      </transition-group> -->
    </a-breadcrumb>
  </div>
</template>

<script setup lang="ts" name="Breadcrumb">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

defineOptions({
  name: 'bread-crumb'
})

const route = useRoute()
const router = useRouter()

const breadcrumbList = computed(() => {
  let breadcrumbData = route.matched ?? []
  breadcrumbData = [...breadcrumbData].map((e) => {
    if (!(e.children && e.children.length > 0)) {
      delete e.children
    }
    return e
  })
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
  padding: 10px 20px;
  overflow: hidden;
  border-top: 1px solid var(--border-color);

  :deep(.ant-breadcrumb) {
    display: flex;
    white-space: nowrap;
    color: var(--text-color-1);

    .ant-breadcrumb-item {
      position: relative;
      display: flex;
      align-items: center;
      float: none;

      .a-breadcrumb__inner {
        display: flex;
        align-items: center;

        .breadcrumb-icon {
          margin-right: 6px;
          font-size: 16px;
        }

        .breadcrumb-title {
          color: var(--text-color-1);
        }
      }

      .ant-breadcrumb__separator {
        position: relative;
      }

      .ant-breadcrumb-overlay-link {
        display: flex;
      }
    }

    .ant-breadcrumb-separator {
      color: var(--text-color-1) !important;
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
