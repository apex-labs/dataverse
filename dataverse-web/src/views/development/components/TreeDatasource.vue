<template>
  <div class="flex fdc">
    <div class="fxbw aic name">
      <h3 class="toe">{{ name }}</h3>
    </div>
    <div style="margin:0 8px 8px">
      <a-input-search v-if="showSearch" v-model:value="searchValue" placeholder="Search" allowClear />
    </div>
    <a-tree class="w100 flex1 ovh tree" :ref="(el: any) => setRef(el)" v-model:expandedKeys="expandedKeys"
      v-if="treeData && treeData.length > 0" v-model:selectedKeys="selectedKeys" :tree-data="treeData" :height="height"
      defaultExpandAll :auto-expand-parent="autoExpandParent" @expand="onExpand" v-bind="$attrs">
      <template #switcherIcon="{ switcherCls }">
        <DownOutlined :class="switcherCls" />
      </template>
      <template #title="{ title, expanded, root, key, isLeaf, datasourceTypeId }">
        <div class="flex aic draggable" :draggable="!root" :data-key="key">
          <span class="mr5">
            <SvgIcon :name="getSourceType(datasourceTypeId)" style="color:#2A94F4" v-if="root" />
            <template v-else>
              <template v-if="!isLeaf">
                <SvgIcon :name="expanded ? 'folder-open' : 'folder'" />
                <!-- <FolderOpenFilled v-if="expanded" style="color:#2A94F4" />
              <FolderFilled v-else style="color:#2A94F4" /> -->
              </template>
              <SvgIcon name="database" v-else />
            </template>
          </span>
          <p class="toe flex1 mr20">
            <span v-if="title.indexOf(searchValue) > -1">
              {{ title.substr(0, title.indexOf(searchValue)) }}
              <span class="strong">{{ searchValue }}</span>
              {{ title.substr(title.indexOf(searchValue) + searchValue.length) }}
            </span>
            <span v-else>{{ title }}</span>
          </p>
        </div>
      </template>
    </a-tree>
    <Empty v-else />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, toRefs, inject, computed, watch, onMounted, onBeforeUnmount, nextTick } from 'vue'
import SvgIcon from '@/components/SvgIcon/index.vue'
import Empty from '@/components/Empty.vue'
import type { Tree } from 'ant-design-vue';
import { useDvsStore } from '@/stores/modules/user'
import { getIconNameByDatasourceType } from '@/utils/constant'

const dvsStore = useDvsStore()
const { dvsCode, mapList } = toRefs(dvsStore)
type TreeNode = typeof Tree.TreeNode

const sourceData: any = inject('sourceData')
const getSourceType = (datasourceTypeId: number | string | null) => {
  return getIconNameByDatasourceType(datasourceTypeId)
}

const props = defineProps({
  name: {
    type: String,
    default: '数据源'
  },
  showName: {
    type: Boolean,
    default: true
  },
  showSearch: {
    type: Boolean,
    default: false
  },
  showGroup: {
    type: Boolean,
    default: false
  },
  selected: {
    type: Array,
    default: () => ([])
  }
})

const emit = defineEmits(['update:selected', 'drop'])

const gData = ref<TreeNode[]>([]);
const treeData = computed(() => gData.value.filter((e: any) => e.children && e.children.length > 0))
const autoExpandParent = ref<boolean>(true);
const tree = ref<any>(null)
const setRef = (el: HTMLElement) => {
  if (el) {
    tree.value = el
  }
}
const height = ref<number>(100)
const updateTreeHeight = () => {
  height.value = tree.value?.$el.nextSibling?.offsetHeight || 100
}
const initData = (nodes: any[], parentKey: any) => {
  return nodes.map(e => {
    e.key = parentKey + '-' + e.datasourceCode
    e.title = e.datasourceName
    e.isLeaf = true
    return e
  })
}
const getSourceList = () => {
  const nData = mapList.value.map((e: any) => ({ datasourceTypeId: e.datasourceTypeId, datasourceTypeName: e.datasourceTypeName, title: e.datasourceTypeName + '数据源', key: 'root_' + e.datasourceTypeId, root: true, isLeaf: false }))
  gData.value = nData.map((e: any) => {
    const nodes = sourceData.value.filter((o: any) => o.datasourceTypeId === e.datasourceTypeId && o.dvsCode === dvsCode.value)
    if (nodes.length > 0) {
      e.children = initData(nodes, e.key)
    }
    return e
  })
}
watch(
  () => dvsCode,
  () => {
    getSourceList()
  },
  { deep: true }
)
watch(
  mapList,
  () => {
    gData.value = mapList.value.map((e: any) => ({ datasourceTypeId: e.datasourceTypeId, datasourceTypeName: e.datasourceTypeName, title: e.datasourceTypeName + '数据源', key: 'root_' + e.datasourceTypeId, root: true, isLeaf: false }))
  },
  { deep: true, immediate: true }
)
watch(
  () => sourceData,
  () => {
    const nData = mapList.value.map((e: any) => ({ datasourceTypeId: e.datasourceTypeId, title: e.datasourceTypeName + '数据源', key: 'root_' + e.datasourceTypeId, root: true, isLeaf: false }))
    gData.value = nData.map((e: any) => {
      const nodes = sourceData.value.filter((o: any) => o.datasourceTypeId === e.datasourceTypeId && o.dvsCode === dvsCode.value)
      if (nodes.length > 0) {
        e.children = initData(nodes, e.key)
      }
      return e
    })
  },
  { deep: true }
)
watch(
  () => treeData,
  () => {
    nextTick(() => {
      updateTreeHeight()
    })
  },
  { deep: true }
)
onMounted(() => {
  getSourceList()
  window.addEventListener('resize', updateTreeHeight)
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', updateTreeHeight)
})
const expandedKeys = ref<any[]>([])
const selectedKeys = computed({
  get: () => props.selected,
  set: (val) => {
    emit('update:selected', val)
  }
})
const onExpand = (keys: string[]) => {
  expandedKeys.value = keys;
  autoExpandParent.value = false;
};

// search
const getParentKey = (
  key: string | number,
  tree: TreeNode[],
): string | number | undefined => {
  let parentKey;
  for (let i = 0; i < tree.length; i++) {
    const node = tree[i];
    if (node.children) {
      if (node.children.some((item: TreeNode) => item.key === key)) {
        parentKey = node.key;
      } else if (getParentKey(key, node.children)) {
        parentKey = getParentKey(key, node.children);
      }
    }
  }
  return parentKey;
};
const getAllNode = (
  tree: TreeNode[],
): any[] => {
  let r = [];
  for (let i = 0; i < tree.length; i++) {
    const node = tree[i];
    r.push(node)
    if (node.children) {
      r.push(...getAllNode(node.children))
    }
  }
  return r;
};
const allNode = computed(() => getAllNode(gData.value))
const searchValue = ref<string>('')
watch(searchValue, value => {
  if (value && value.length > 0) {
    const expandedList = allNode.value.filter((item: TreeNode) => item.title.toLocaleLowerCase().indexOf(value.toLocaleLowerCase()) > -1)
      .map((item: TreeNode) => {
        return getParentKey(item.key, gData.value)
      })
      .filter((item, i, self) => item && self.indexOf(item) === i)
    expandedKeys.value = expandedList
    autoExpandParent.value = true
  }
})

// drag
const source = reactive<any>({})
const onDragStart = (event: any) => {
  // make it half transparent
  const key = event.target.dataset.key
  const node = allNode.value.find(e => e.key === key)
  Object.assign(source, node)
  event.target.classList.add("dragging")
}
const onDragEnd = (event: any) => {
  // reset the transparency
  event.target.classList.remove("dragging")
}
const onDrop = (event: any) => {
  // 阻止默认行为（会作为某些元素的链接打开）
  event.preventDefault()
  // 将被拖动的元素移动到选定的放置目标
  const target: any = event?.target
  if (target.id === "droptarget" || target.parentElement.id === "droptarget") {
    emit('drop', source)
  }
}
onMounted(() => {
  window.addEventListener('dragstart', onDragStart)
  window.addEventListener('dragend', onDragEnd)

  /* 在放置目标上触发的事件 */
  window.addEventListener('drop', onDrop, { capture: true })
})
onBeforeUnmount(() => {
  window.removeEventListener('dragstart', onDragStart)
  window.removeEventListener('dragend', onDragEnd)

  /* 在放置目标上触发的事件 */
  window.removeEventListener('drop', onDrop)
})

</script>

<style scoped lang="less">
.name {
  padding: 4px 10px;

  .btn {
    height: 32px;
  }
}

.toolbar {
  height: 40px;
  padding: 12px 6px;

  .btn {
    margin: 0 6px;
    cursor: pointer;
  }
}

:deep(.ant-tree-title) {

  cursor: pointer !important;
}

.dragging {
  opacity: 0.5;
  cursor: pointer !important;
}
</style>
