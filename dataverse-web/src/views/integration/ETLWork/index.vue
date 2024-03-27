<template>
  <div class="g-card h100 flex fdc ovh">
    <div class="flex flex1 ovh">
      <div class="menu-left flex fxbw fdc">
        <ul class="w100 flex fdc">
          <li class="menu-item" :class="{ active: currentTopMenu === 1 }" @click="clcikTopMenu(1)">
            <a-tooltip placement="right" title="集成任务">
              <FolderOutlined />
            </a-tooltip>
          </li>
          <li class="menu-item" :class="{ active: currentTopMenu === 2 }" @click="clcikTopMenu(2)">
            <a-tooltip placement="right" title="开发任务">
              <FolderOutlined />
            </a-tooltip>
          </li>
        </ul>
        <ul class="w100 flex fdc">
          <li class="menu-item" :class="{ active: currentBottomMenu === 'output' }" @click="clcikBottomMenu('output')">
            <a-tooltip placement="right" title="日志">
              <CodeOutlined />
            </a-tooltip>
          </li>
          <li class="menu-item" :class="{ active: currentBottomMenu && currentBottomMenu !== 'output' }"
            @click="clcikBottomMenu(1)">
            <a-tooltip placement="right" title="执行器">
              <TableOutlined />
            </a-tooltip>
          </li>
        </ul>
      </div>
      <div class="main flex1 flex fdc ovh">
        <div class="main-top flex flex1 ovh">
          <div class="group-list flex fdc">
            <div class="flex1 flex fdc" v-if="currentTopMenu === 1">
              <h3 class="p10">集成任务</h3>
              <a-tree class="w100 flex1 ovh tree" ref="tree" v-model:expandedKeys="expandedKeys"
                v-model:selectedKeys="selectedKeys" :tree-data="treeData" :height="treeHeight" @select="selectNode">
                <template #switcherIcon="{ switcherCls }">
                  <DownOutlined :class="switcherCls" />
                </template>
                <template #title="{ title }">
                  <div class="flex aic">
                    <span class="mr5">
                      <SvgIcon name="xianxingdaoyu" color="#f00" />
                    </span>
                    <p class="toe flex1 mr20">{{ title }}</p>
                    <a-dropdown :trigger="['click']" placement="bottomRight">
                      <EllipsisOutlined class="mr10" />
                      <template #overlay>
                        <a-menu @click="actionFn">
                          <a-menu-item key="1">添加子分组</a-menu-item>
                          <a-menu-item key="2">编辑分组</a-menu-item>
                          <a-menu-item key="3">删除分组</a-menu-item>
                          <a-menu-divider />
                          <a-menu-item key="4">添加子作业</a-menu-item>
                        </a-menu>
                      </template>
                    </a-dropdown>
                  </div>
                </template>
              </a-tree>
            </div>
            <div class="flex1 flex fdc" v-if="currentTopMenu === 2">
              <h3 class="p10">开发任务</h3>
              <a-tree class="w100 flex1 ovh tree" ref="dev_tree" v-model:expandedKeys="expandedKeys"
                v-model:selectedKeys="selectedKeys" :tree-data="treeData" :height="devTreeHeight" @select="selectNode">
                <template #switcherIcon="{ switcherCls }">
                  <DownOutlined :class="switcherCls" />
                </template>
                <template #title="{ title }">
                  <div class="flex aic">
                    <span class="mr5">
                      <SvgIcon name="xianxingdaoyu" color="#f00" />
                    </span>
                    <p class="toe flex1 mr20">{{ title }}</p>
                    <a-dropdown :trigger="['click']" placement="bottomRight">
                      <EllipsisOutlined class="mr10" />
                      <template #overlay>
                        <a-menu @click="actionFn">
                          <a-menu-item key="1">添加子分组</a-menu-item>
                          <a-menu-item key="2">编辑分组</a-menu-item>
                          <a-menu-item key="3">删除分组</a-menu-item>
                          <a-menu-divider />
                          <a-menu-item key="4">添加子作业</a-menu-item>
                        </a-menu>
                      </template>
                    </a-dropdown>
                  </div>
                </template>
              </a-tree>
            </div>
          </div>
          <div class="work-space flex1 flex fdc ovh">
            <Tabs v-if="tabs.length > 0" v-model:tabs="tabs" v-model:activeKey="active" type="editable-card" hide-add
              @tabClick="tabClick" @edit="onEdit" @closeAll="closeAllTab" />
            <Toolbar @format="formatSql" @execute="executeSql" @stop="stopSql" />
            <a-spin :spinning="loading" tip="Loading..." wrapperClassName="flex1 ova">
              <div class="code h100 flex1 ovh">
                <Editor v-model="code" @blur="blurFn" />
              </div>
            </a-spin>

          </div>
          <div class="data-source flex fdc" v-if="currentRightMenu">
            <h3 class="p10">数据库</h3>
            <a-tree class="w100 flex1 ovh source_tree" ref="source_tree" v-model:expandedKeys="expandedKeys"
              v-model:selectedKeys="selectedKeys" :tree-data="treeData" :height="sourceTreeHeight" @select="selectNode">
              <template #switcherIcon="{ switcherCls }">
                <DownOutlined :class="switcherCls" />
              </template>
              <template #title="{ title }">
                <div class="flex aic">
                  <span class="mr5">
                    <SvgIcon name="xianxingdaoyu" color="#f00" />
                  </span>
                  <p class="toe flex1 mr20">{{ title }}</p>
                </div>
              </template>
            </a-tree>
          </div>
        </div>
        <div class="main-bottom flex fdc" v-if="currentBottomMenu">
          <Tabs v-model:tabs="bottomTabs" v-model:activeKey="bottomActive" type="editable-card" hide-add
            @tabClick="bottomTabClick" @edit="bottomTabEdit" @closeAll="closeBottomTab" />
          <div class="code flex1 ovh">
            <Editor v-model="outputCode" placeholder="" :disabled="true" v-if="currentBottomMenu === 'output'" />
            <a-table class="result-table" :dataSource="result" :columns="columns" :scroll="tableScroll"
              :pagination="false" v-else />
          </div>
        </div>
      </div>
      <div class="menu-right flex fxbw fdc">
        <ul class="w100 flex fdc">
          <li class="menu-item" :class="{ active: currentRightMenu === 1 }" @click="clcikRightMenu(1)">
            <a-tooltip placement="left" title="数据库">
              <DatabaseOutlined />
            </a-tooltip>
          </li>
        </ul>
      </div>
    </div>
    <div class="footer fxbw">
      <div class="footer-left">test.sql</div>
      <div class="footer-right">5:30 LF UTF-8 AWS:No ****************</div>
    </div>
  </div>
</template>

<script setup lang="ts" name="ETLWork">
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import SvgIcon from '@/components/SvgIcon/index.vue'
import Editor from "@/components/Editor/index.vue"
import Tabs from "@/components/Tabs.vue"
import Toolbar from "@/components/Toolbar.vue"
// format sql
import { format } from 'sql-formatter';
// api
import { query } from '@/api/modules/etl'

interface Item {
  label: string;
  value: string | number;
  closable?: boolean;
  locked?: boolean;
}

const code = ref(`SELECT * FROM a_user LIMIT 10;`)

const blurFn = (selected: string[], lines: any[]) => {
  console.log(selected.join(''), lines.join(''));
}

// WS
// import { useWsStore } from '@/stores/modules/ws';
// const wsStore = useWsStore();
// wsStore.initSocket({ url: 'ws://121.12.12.1' })
// const client = computed(() => wsStore.client)

// left menu
const currentTopMenu = ref(1)
const clcikTopMenu = (e: number) => {
  currentTopMenu.value = !currentTopMenu.value ? e : currentTopMenu.value === e ? 0 : e
}
// bootom menu
const currentBottomMenu = ref<string | number>('output')
const clcikBottomMenu = (e: string | number) => {
  currentBottomMenu.value = !currentBottomMenu.value ? e : currentBottomMenu.value === e ? 0 : e
  bottomActive.value = e
}
// right menu
const currentRightMenu = ref(1)
const clcikRightMenu = (e: number) => {
  currentRightMenu.value = !currentRightMenu.value ? e : currentRightMenu.value === e ? 0 : e
}

const tabs = ref<Item[]>([
  { label: 'tab1', value: 1 },
])
const active = ref<string | number>(1)
const tabClick = (key: string | number) => {
  console.log('tabClick', key);
  selectedKeys.value = [key]
}
const remove = (targetKey: string | number) => {
  let lastIndex = tabs.value.findIndex((pane) => pane.value === targetKey) - 1;
  tabs.value = tabs.value.filter(pane => pane.value !== targetKey);
  console.log('tabs.value', tabs.value);
  if (tabs.value.length && active.value === targetKey) {
    if (lastIndex >= 0) {
      active.value = tabs.value[lastIndex]?.value || '';
    } else {
      active.value = tabs.value[0]?.value || '';
    }
    selectedKeys.value = active.value ? [active.value] : []
  }
};
const onEdit = (targetKey: string | number) => {
  remove(targetKey);
};
const closeAllTab = () => {
  active.value = ''
  selectedKeys.value = []
}

const outputCode = ref()
const bottomTabs = ref<any[]>([
  {
    label: 'output', value: 'output', closable: false
  }
])
const bottomActive = ref<string | number>('output')
const bottomTabClick = (key: string | number) => {
  bottomActive.value = key
  currentBottomMenu.value = key
}
const bottomTabEdit = (targetKey: string | number) => {
  let lastIndex = bottomTabs.value.findIndex((pane) => pane.value === targetKey) - 1;
  bottomTabs.value = bottomTabs.value.filter(pane => pane.value !== targetKey);
  if (bottomTabs.value.length && bottomActive.value === targetKey) {
    if (lastIndex >= 0) {
      bottomActive.value = bottomTabs.value[lastIndex]?.value || '';
    } else {
      bottomActive.value = bottomTabs.value[0]?.value || '';
    }
    currentBottomMenu.value = bottomActive.value
  }
}
const closeBottomTab = () => {
  bottomTabs.value = [{
    label: 'output', value: 'output', closable: false
  }]
}

// left group
const expandedKeys = ref<(string | number)[]>(['0-0-0'])
const selectedKeys = ref<(string | number)[]>([])
const treeHeight = ref<number>(100)
const devTreeHeight = ref<number>(100)
const sourceTreeHeight = ref<number>(100)
const treeData = [
  {
    title: 'MySQL集成',
    icon: 'FolderOpenOutlined',
    key: '0-0',
    children: [
      {
        title: '会员组',
        icon: 'FolderOpenOutlined',
        key: '0-0-0',
        children: [
          {
            title: '会员中心数据接入',
            icon: 'FileOutlined',
            key: '0-0-0-0'
          }
        ]
      },
      {
        title: '订单组',
        icon: 'FolderOpenOutlined',
        key: '0-0-1',
        children: [
          {
            title: '订单中心数据接入',
            icon: 'FolderOpenOutlined',
            key: '0-0-1-0'
          }
        ]
      },
      {
        title: '优惠券数据接入',
        icon: 'FileOutlined',
        key: '0-0-2'
      }
    ]
  },
  {
    title: 'Oracle数据集成',
    icon: 'FolderOpenOutlined',
    key: '1-0',
    children: [
      {
        title: '积分中心数据接入',
        icon: 'FileOutlined',
        key: '1-0-0'
      }
    ]
  },
  {
    title: 'PostgreSQL数据集成',
    icon: 'FolderOpenOutlined',
    key: '2-0',
    children: [
      {
        title: '点评数据接入',
        icon: 'FileOutlined',
        key: '2-0-0'
      },
      {
        title: '游记数据接入',
        icon: 'FileOutlined',
        key: '2-0-1'
      }
    ]
  },
  {
    title: 'Oracle数据集成',
    icon: 'FolderOpenOutlined',
    key: '3-0',
    children: [
      {
        title: '积分中心数据接入',
        icon: 'FileOutlined',
        key: '3-0-0'
      }
    ]
  },
  {
    title: 'PostgreSQL数据集成',
    icon: 'FolderOpenOutlined',
    key: '4-0',
    children: [
      {
        title: '点评数据接入',
        icon: 'FileOutlined',
        key: '4-0-0'
      },
      {
        title: '游记数据接入',
        icon: 'FileOutlined',
        key: '4-0-1'
      }
    ]
  }
]

const tree = ref()
const dev_tree = ref()
const source_tree = ref()
const updateTreeHeight = () => {
  treeHeight.value = tree.value?.$el.nextSibling.offsetHeight || 100
  devTreeHeight.value = dev_tree.value?.$el.nextSibling.offsetHeight || 100
  sourceTreeHeight.value = source_tree.value?.$el.nextSibling.offsetHeight || 100
}
onMounted(() => {
  window.addEventListener('resize', updateTreeHeight)
  updateTreeHeight()
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', updateTreeHeight)
})

// 文件树操作
const actionFn = (e: any) => {
  console.log(e.key)
}
const selectNode = (keys: any, e: any) => {
  const { key, title } = e.node
  if (!tabs.value.some(e => e.value === key)) {
    const item = { label: title, value: key }
    tabs.value.push(item)
  }
  active.value = key
}

// 执行结果
const tableScroll = reactive({ x: 1500, y: 226 })
const result = ref<any[]>([])
const columns = ref<any[]>([])
// 根据执行结果获取columns和datasource
const initResult = (data: any[]) => {
  const r: any[] = Object.keys(data[0]).map((e: string) => ({ key: e, dataIndex: e, title: e, ellipsis: true, width: 100, sorter: (a: any, b: any) => typeof a[e] === 'number' ? (a[e] - b[e]) : (a[e].length - b[e].length) }))
  r.unshift({ title: '', key: 'index', dataIndex: 'index', ellipsis: true, width: 50, sorter: false })
  columns.value = [...r]
  result.value = data.map((e, i) => ({ ...e, index: i + 1 }))
}
// 渲染结果表格
const updateTable = () => {
  const obj: any = document?.querySelector('.main-bottom')
  const w = obj?.offsetWidth || 1000
  tableScroll.x = w - 6
}

// sql编辑器操作
const loading = ref(false)
const formatSql = () => {
  code.value = format(code.value, { language: 'postgresql', keywordCase: 'upper' })
}
const executeSql = () => {
  if (!code.value) return
  loading.value = true
  query({ sql: code.value, storeInfos: [{ "tableName": "a_user", "storePath": "/tmp/dataverse/a_user" }] }).then((res: any) => {
    if (res.result) {
      const index = bottomTabs.value.findIndex(e => e.value === bottomActive.value)
      const result: any[] = res.result.map((e: string) => JSON.parse(e))
      if (result.length > 0) { initResult(result) }
      if (index >= 0 && bottomActive.value !== 'output') bottomTabs.value[index].result = result
      else {
        const obj: any = tabs.value.find(e => e.value === active.value)
        const bottomIndex: number = bottomTabs.value.findIndex(e => e.value === active.value)
        if (bottomIndex > 0) {
          bottomTabs.value[bottomIndex].result = result
        } else {
          bottomTabs.value.push({
            label: obj.label,
            value: obj.value,
            result
          })
        }
        bottomActive.value = obj.value
        currentBottomMenu.value = obj.value
      }
      updateTable()
    }
    else {
      outputCode.value = res.message
      bottomActive.value = 'output'
    }
  }).catch((error: any) => {
    if (error && error.message)
      outputCode.value = error.message
  }).finally(() => {
    loading.value = false
  })
}
const stopSql = () => { }

</script>

<style scoped lang="less">
.g-card {
  border-top: 1px solid var(--border-color);
}

.menu-left,
.menu-right {
  width: 40px;
  text-align: center;
  border-left: 1px solid var(--border-color);
  border-right: 1px solid var(--border-color);

  .menu-item {
    border-radius: 4px;
    font-size: 16px;
    height: 28px;
    line-height: 28px;
    margin: 5px;
    cursor: pointer;

    &.active {
      background: var(--bg-color-3);
    }
  }
}

.main {
  &-top {}

  &-bottom {
    height: 300px;
    border-top: 1px solid var(--border-color);
  }

}

.group-list {
  width: 270px;
  border-right: 1px solid var(--border-color);
}

.work-space {}

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

.data-source {
  width: 250px;
  border-left: 1px solid var(--border-color);
}

.footer {
  height: 20px;
  line-height: 20px;
  font-size: 10px;
  padding: 0 10px;
  border-top: 1px solid var(--border-color);
}
</style>
