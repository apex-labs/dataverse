<template>
  <div class="result h100">
    <div class="pagination flex aic tools">
      <div class="flex1">
        <a-tooltip placement="top" title="第一页">
          <a-button type="text" class="btn" :disabled="pageInfo.current === 1" @click="goPage('first')">
            <VerticalRightOutlined />
          </a-button>
        </a-tooltip>
        <a-tooltip placement="top" title="上一页">
          <a-button type="text" class="btn" :disabled="pageInfo.current === 1" @click="goPage('prev')">
            <LeftOutlined />
          </a-button>
        </a-tooltip>
        <a-dropdown class="mr5">
          <a-button type="text" class="btn" @click="goPage('current')">
            <span>{{ start + '-' + end }}</span>
            <DownOutlined />
          </a-button>
          <template #overlay>
            <a-menu @click="changeCommond">
              <a-menu-item :key="10">10</a-menu-item>
              <a-menu-item :key="20">20</a-menu-item>
              <a-menu-item :key="50">50</a-menu-item>
              <a-menu-item :key="100">100</a-menu-item>
              <a-menu-item :key="500">500</a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
        <a-button type="text" class="btn" @click="goPage('all')">
          <span>{{ other }}</span>
        </a-button>
        <a-tooltip placement="top" title="下一页">
          <a-button type="text" class="btn" :disabled="pageInfo.current === pageInfo.pageCount" @click="goPage('next')">
            <RightOutlined />
          </a-button>
        </a-tooltip>
        <a-tooltip placement="top" title="最后一页">
          <a-button type="text" class="btn" :disabled="pageInfo.current === pageInfo.pageCount" @click="goPage('last')">
            <VerticalLeftOutlined />
          </a-button>
        </a-tooltip>
        <a-tooltip placement="top" :title="data.locked ? '解除固定' : '固定'">
          <a-button type="text" class="btn" @click="lock">
            <PushpinFilled v-if="data.locked" />
            <PushpinOutlined v-else />
          </a-button>
        </a-tooltip>
      </div>
      <!-- <a-button type="text" class="btn" @click="downloadFn">
        <DownloadOutlined />
      </a-button> -->
    </div>
    <!-- { position: ['topLeft'], pageSize: 500, pageSizeOptions: ['10', '20', '50', '100', '500'] } -->
    <a-table class="result-table" :dataSource="list" :columns="columns" :scroll="tableScroll" :pagination="false"
      :showSorterTooltip="false" @resizeColumn="handleResizeColumn" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted, onBeforeUnmount } from 'vue'
import { copy } from '@/utils'

const props = defineProps({
  data: {
    type: Object,
    default: () => ({
      label: '',
      value: '',
      closable: false,
      locked: false,
      result: []
    })
  }
})

const emit = defineEmits(['lock'])

const newData = computed(() => props.data)
const showAll = ref(false)
const tableScroll = reactive({ x: 1500, y: 183 })
const pageInfo = reactive<any>({ current: 1, size: 10, total: 10, pageCount: 1 })
const start = computed(() => (pageInfo.current - 1) * pageInfo.size + 1)
const end = computed(() => pageInfo.current * pageInfo.size)
const other = computed(() => showAll.value ? `${(end.value + 1) + '-' + pageInfo.total}` : `of ${end.value + 1}+`)
const result = ref<any[]>([])
const list = computed(() => showAll.value ? result.value.slice(end.value) : result.value.slice(start.value - 1, end.value))
const columns = ref<any[]>([])

const customCell = (r: any, i: number, c: any) => ({ onClick: () => { copy(r[c.key]) } })

const initResult = (data: any[]) => {
  if (!data[0]) return
  const r: any[] = Object.keys(data[0]).map((e: string) => ({ key: e, dataIndex: e, title: e, ellipsis: true, resizable: true, customCell, width: 100, minWidth: 100, sorter: (a: any, b: any) => typeof a[e] === 'number' ? (a[e] - b[e]) : (a[e].length - b[e].length) }))
  r.unshift({ title: '', align: 'center', key: 'index', dataIndex: 'index', ellipsis: true, resizable: true, width: 50, sorter: false })
  columns.value = [...r]
  result.value = data.map((e, i) => ({ ...e, index: i + 1 }))
  pageInfo.total = result.value.length
  pageInfo.pageCount = Math.ceil(result.value.length / pageInfo.size)
}

// 监听元素的尺寸变化，重新渲染table
const updateTable = () => {
  const obj: any = document?.querySelector('.result')
  const w = obj?.offsetWidth || 1000
  const h = obj.offsetHeight || 183
  tableScroll.x = w - 6
  tableScroll.y = h - 64 - 6
}
const resizeObserver: any = new ResizeObserver(function (entries) {
  entries.forEach((item) => {
    updateTable()
  })
})

onMounted(() => {
  window.addEventListener('resize', updateTable)
  updateTable()
  // 监听dom
  resizeObserver.observe(document.querySelector('.result'))
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', updateTable)
  resizeObserver.disconnect()
})

watch(
  newData,
  (n: any, o: any) => {
    // reset data
    showAll.value = false
    pageInfo.current = 1
    initResult(n.result)
  }, { immediate: true, deep: true }
)

const goPage = (current: number | string) => {
  showAll.value = false
  switch (current) {
    case 'first':
      pageInfo.current = 1
      break;
    case 'prev':
      pageInfo.current = pageInfo.current - 1
      break;
    case 'next':
      pageInfo.current = pageInfo.current + 1
      break;
    case 'last':
      pageInfo.current = pageInfo.pageCount
      break;
    case 'all':
      showAll.value = true
      break;
    case 'current':
      break;

    default:
      pageInfo.current = current
      break;
  }
}

// pageInfo
const changeCommond = ({ key }: any) => {
  pageInfo.size = key
  goPage(1)
}

const lock = () => {
  emit('lock', props.data.value, !props.data.locked)
}

const downloadFn = () => {
  console.log(result.value);
}

const handleResizeColumn = (w: number, col: any) => {
  col.width = w;
}

</script>

<style scoped lang="less">
.pagination {
  padding: 5px;
  border-bottom: 1px dashed var(--border-color);
}

:deep(.result-table) {

  .ant-table {
    .ant-table-thead {

      &>tr>th,
      &>tr>td {
        padding: 5px !important;
      }
    }

    .ant-table-tbody {
      tr.ant-table-measure-row+tr>td {
        padding: 5px !important;
      }

      tr+tr>td {
        padding: 5px !important;
      }
    }
  }
}
</style>
