<template>
  <div class="tabs" :class="'direction-' + direction">
    <a-tabs v-bind="$attrs">
      <a-tab-pane v-for="(item) in tabs" :key="item[defaultProps.value]" :closable="item.closable">
        <template #tab>
          <div class="flex aic">
            <SvgIcon name="job-mysql" class="mr5" style="color:#2A94F4" v-if="showIcon" />
            <span>{{ item[defaultProps.label] }}</span>
          </div>
        </template>
      </a-tab-pane>
      <template #rightExtra v-if="tabs.length > 5">
        <a-button>关闭所有</a-button>
      </template>
    </a-tabs>
  </div>
</template>

<script setup lang="ts" name="Tabs">
interface Item {
  [propName: string]: any;
}

const props = defineProps({
  tabs: {
    type: Array<Item>, default: () => []
  },
  direction: {
    type: String,
    default: 'bottom' // top,bottom,left,right
  },
  defaultProps: {
    type: Object,
    default: () => ({
      label: 'label',
      value: 'value',
      closable: false,
    })
  },
  showIcon: {
    type: Boolean,
    default: false
  }
})

</script>

<style scoped lang="less">
.tabs {
  font-size: 12px;

  :deep(.ant-tabs, .ant-tabs-card) {
    height: 40px;
    line-height: 40px;

    .ant-tabs-nav {
      margin: 0 !important;

      .ant-tabs-tab {
        border: none;
        padding: 0 16px;
        height: 40px;
        line-height: 40px;

        &+& {
          margin-left: 0;
        }

        &-btn {
          color: var(--text-color-1);

          &:hover,
          &:active {
            color: var(--text-color-1);
          }
        }

        &-active {
          background-color: var(--bg-color-3);

          &:after {
            content: '';
            position: absolute;
          }

          .ant-tabs-tab-btn {
            color: var(--text-color-1);
          }
        }
      }
    }
  }

  &.direction-top {
    :deep(.ant-tabs, .ant-tabs-card) {
      .ant-tabs-nav {
        .ant-tabs-tab {
          &-active {
            &:after {
              left: 0;
              top: 0;
              width: 100%;
              border-top: 2px solid var(--light-primary-color);
            }
          }
        }
      }
    }
  }

  &.direction-bottom {
    :deep(.ant-tabs, .ant-tabs-card) {
      .ant-tabs-nav {
        .ant-tabs-tab {
          &-active {
            &:after {
              left: 0;
              bottom: 0;
              width: 100%;
              border-bottom: 2px solid var(--light-primary-color);
            }
          }
        }
      }
    }
  }

  &.direction-left {
    :deep(.ant-tabs, .ant-tabs-card) {
      .ant-tabs-nav {
        .ant-tabs-tab {
          &-active {
            &:after {
              left: 0;
              top: 0;
              height: 100%;
              border-left: 2px solid var(--light-primary-color);
            }
          }
        }
      }
    }
  }

  &.direction-right {
    :deep(.ant-tabs, .ant-tabs-card) {
      .ant-tabs-nav {
        .ant-tabs-tab {
          &-active {
            &:after {
              right: 0;
              top: 0;
              height: 100%;
              border-right: 2px solid var(--light-primary-color);
            }
          }
        }
      }
    }
  }
}
</style>
