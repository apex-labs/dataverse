<template>
  <div class="g-card h100 flex fdc ovh">
    <div class="flex flex1 ovh">
      <div class="menu-left flex fxbw fdc">
        <ul class="w100 flex fdc">
          <li class="menu-item" :class="{ active: currentTopMenu === 1 }" @click="clcikTopMenu(1)">
            <a-tooltip placement="right" title="集成任务">
              <SvgIcon name="integration" />
            </a-tooltip>
          </li>
          <li class="menu-item" :class="{ active: currentTopMenu === 2 }" @click="clcikTopMenu(2)">
            <a-tooltip placement="right" title="开发任务">
              <SvgIcon name="dev" />
            </a-tooltip>
          </li>
          <li class="menu-item" :class="{ active: currentTopMenu === 3 }" @click="clcikTopMenu(3)">
            <a-tooltip placement="right" title="调度任务">
              <SvgIcon name="etl" />
            </a-tooltip>
          </li>
        </ul>
        <ul class="w100 flex fdc">
          <li class="menu-item" :class="{ active: currentBottomMenu === 'output' }" @click="clcikBottomMenu('output')">
            <a-tooltip placement="right" title="运行过程">
              <CodeOutlined />
            </a-tooltip>
          </li>
          <li class="menu-item" :class="{ active: currentBottomMenu && currentBottomMenu !== 'output' }"
            @click="clcikBottomMenu(bottomTabs[1]?.value || 1)">
            <a-tooltip placement="right" title="运行结果">
              <TableOutlined />
            </a-tooltip>
          </li>
        </ul>
      </div>
      <div class="main flex1 flex fdc ovh">
        <div class="main-top flex flex1 ovh">
          <div class="group-list flex fdc" v-show="currentTopMenu">
            <div class="draggable draggable-right" ref="draggableRight"></div>
            <Tree class="flex1" showSearch showGroup name="开发任务" v-model:selected="selected" @select="selectNode"
              @add-task="addTask">
            </Tree>
          </div>
          <div class="work-space flex1 flex fdc ovh">
            <template v-if="tabs.length > 0">
              <Tabs v-model:tabs="tabs" v-model:activeKey="active" type="editable-card" hide-add show-icon
                @tabClick="tabClick" @edit="onEdit" @closeAll="closeAllTab" />
              <template v-if="active">
                <Toolbar @format="formatSql" @execute="executeSqlFn" @stop="stopSql" @save="saveFn"
                  @setConfig="setConfig" @viewConfig="viewConfig">
                  <!-- <a-dropdown class="mr5">
                <a-button type="text">
                  <SvgIcon name="flink" />
                  <span class="ml5">Flink</span>
                  <DownOutlined />
                </a-button>
                <template #overlay>
                  <a-menu @click="changeCommond">
                    <a-menu-item key="flink">Flink</a-menu-item>
                    <a-menu-item key="flink2">Flink2</a-menu-item>
                    <a-menu-item key="flink3">Flink3</a-menu-item>
                  </a-menu>
                </template>
</a-dropdown> -->
                </Toolbar>
                <a-spin :spinning="loading" tip="Loading..." wrapperClassName="flex1 ova">
                  <div class="code h100 flex1 ovh" id="droptarget">
                    <Editor v-model="code" @focus="focusFn" @change="changeCode" @blur="blurFn" @save="saveFn"
                      ref="codeRef" />
                  </div>
                </a-spin>
              </template>
            </template>
          </div>
          <div class="data-source flex fdc" v-if="currentRightMenu">
            <div class="draggable draggable-left" ref="draggableLeft"></div>
            <TreeTable v-if="currentRightMenu === 1" class="flex1" showSearch name="数据表" @drop="changeTable">
            </TreeTable>
            <Chat class="flex1" v-if="currentRightMenu === 2" />
          </div>
        </div>
        <div class="main-bottom flex fdc" v-if="currentBottomMenu">
          <div class="draggable draggable-top" ref="draggableTop"></div>
          <Tabs v-model:tabs="bottomTabs" v-model:activeKey="bottomActive" type="editable-card" hide-add
            @tabClick="bottomTabClick" @edit="bottomTabEdit" @closeAll="closeBottomTab" />
          <div class="code flex1 ovh">
            <Editor v-model="outputCode" placeholder="" :disabled="true" v-if="currentBottomMenu === 'output'" />
            <Result v-if="bottomTabs.length > 1 && currentBottomMenu !== 'output'" :data="currentBottomItem"
              @lock="lock">
            </Result>
          </div>
        </div>
      </div>
      <div class="menu-right flex fxbw fdc">
        <ul class="w100 flex fdc">
          <li class="menu-item" :class="{ active: currentRightMenu === 1 }" @click="clcikRightMenu(1)">
            <a-tooltip placement="left" title="数据源">
              <SvgIcon name="datasource" />
            </a-tooltip>
          </li>
          <!-- <li class="menu-item" :class="{ active: currentRightMenu === 2 }" @click="clcikRightMenu(2)">
            <a-tooltip placement="left" title="AI">
              <SvgIcon name="ai" />
            </a-tooltip>
          </li> -->
        </ul>
      </div>
    </div>

    <SetConfig :width="500" :title="`${isEdit ? '编辑' : '配置'}调度计划`" placement="right" v-model:value="open"
      v-model:edit="isEdit" :current-node="currentNode">
    </SetConfig>
  </div>
</template>

<script setup lang="ts">
import { ref, toRefs, reactive, computed, provide, watch, nextTick, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue';
import SvgIcon from '@/components/SvgIcon/index.vue'
import Editor from "@/components/Editor/index.vue"
import Tabs from "@/components/Tabs.vue"
import Chat from "@/components/Chat/Chat.vue"
import Tree from "./components/TreeBdm.vue"
import TreeTable from "./components/TreeTable.vue"
import Result from "./components/Result.vue"
import Toolbar from "./components/ToolbarForBdm.vue"
import SetConfig from './components/SetConfig.vue'
import { useDvsStore } from '@/stores/modules/user'
// format sql
import { format } from 'sql-formatter';
// api
import { executeSql, displayTableStructure, addBdmJob, editBdmJob, detailBdmJobVO } from '@/api/modules/develoment'
import { sourceList, regionList } from '@/api/modules/planning'
// drag
import { useDraggableDireaction } from '@/hooks/useDraggable'

interface Item {
  key: string | number
  label: string
  value: string | number
  code: any
  isAdd?: boolean
  closable?: boolean
  locked?: boolean
  result?: any
  data?: any
}

const router = useRouter()
const goTo = (data: any) => {
  router.push(data)
}

const dvsStore = useDvsStore()
const { env, dvsCode } = toRefs(dvsStore)

// 获取数据空间下数据域列表作为根目录
const regionData = ref<any[]>([])
const regionDataJob = ref<any[]>([])
const getRegionList = (jobType: number | null = null) => {
  const params = {
    jobType,
    dvsCode: dvsCode.value,
    pageQueryParam: {
      ascs: [],
      descs: [],
      pageNo: 1,
      size: 100
    }
  }
  regionList(params).then((res: any) => {
    if (res.responseStatus === 200) {
      const data: any = res.data
      if (jobType) regionDataJob.value = data?.list || []
      else regionData.value = data?.list || []
    }
  })
}
provide('regionData', regionData)
provide('regionDataJob', regionDataJob)

// WS
// import { useWsStore } from '@/stores/modules/ws';
// const wsStore = useWsStore();
// wsStore.initSocket({ url: 'ws://121.12.12.1' })
// const client = computed(() => wsStore.client)

// left menu
const currentTopMenu = ref(2)
const clcikTopMenu = (e: number) => {
  if (e === 1) { goTo({ name: 'developmentEtl' }) }
  // if (e === 2) { goTo({ name: 'developmentBdm' }) }
  if (e === 3) { goTo({ name: 'developmentTask' }) }
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

const codeRef = ref()
const code = ref<any>(`SELECT * FROM a_user LIMIT 10;`)
const selectedCode = ref('')
const tabs = ref<Item[]>([])
const currentTab = reactive<Item>({
  key: '', label: '', value: '', code: '', isAdd: false
})
const active = ref<string | number | null>(null)
const tabClick = (key: string | number) => {
  const tab = tabs.value.find((e: any) => e.value === key)
  if (tab) {
    code.value = tab.code
    selected.value = [tab.key]
    Object.assign(currentNode, tab.data)
    // selectNode(tab.data)
  }
}
const remove = (targetKey: string | number) => {
  let lastIndex = tabs.value.findIndex((pane) => pane.value === targetKey) - 1;
  tabs.value = tabs.value.filter(pane => pane.value !== targetKey);
  if (tabs.value.length && active.value === targetKey) {
    let tab: any = null
    if (lastIndex >= 0) {
      tab = tabs.value[lastIndex]
    } else {
      tab = tabs.value[0]
    }
    active.value = tab?.value || ''
    code.value = tab.code
    selected.value = [tab.key]
    Object.assign(currentNode, tab.data)
  }
};
const onEdit = (targetKey: string | number) => {
  remove(targetKey);
};
const closeAllTab = () => {
  active.value = ''
  selected.value = []
}
const changeCode = (selected: string[], lines: any[], selectedLines: any[]) => {
  // update tab code
  setTimeout(() => {
    const index = tabs.value.findIndex((e: any) => e.value === currentTab.value)
    if (index >= 0) {
      tabs.value[index] = { ...tabs.value[index], code: code.value }
    }
  }, 10);
}
const focusFn = (selected: string[], lines: any[], selectedLines: any[]) => {
  selectedCode.value = selected.length > 0 ? selected.join('') : selectedLines.join('')
}
const blurFn = (selected: string[], lines: any[], selectedLines: any[]) => {
  selectedCode.value = selected.length > 0 ? selected.join('') : selectedLines.join('')
}
// 更换数据源
const bdmInTableList = ref<any[]>([])
const changeTable = (data: any) => {
  const { dataRegionCode, description, dwLayer, dwLayerDetail, env, tableAlias, tableCode, tableName, createTime, updateTime } = data
  if (!bdmInTableList.value.some((e: any) => e.tableCode === tableCode)) {
    const item = { bdmJobCode: currentNode.bdmJobCode, dataRegionCode, description, dwLayer, dwLayerDetail, env, tableAlias, tableCode, tableName, createTime, updateTime }
    bdmInTableList.value.push(item)
  }
  // code.value = code.value + data.tableName
  const index = tabs.value.findIndex((e: any) => e.value === currentTab.value)
  if (index >= 0) {
    tabs.value[index] = { ...tabs.value[index], code: code.value }
  }
  setTimeout(() => {
    codeRef.value.drop(data.tableName)
  }, 20);
}

const outputCode = ref()
const bottomTabs = ref<any[]>([
  {
    label: '运行过程', value: 'output', closable: false
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

const currentBottomItem = computed(() => {
  const obj = bottomTabs.value.find(e => e.value === bottomActive.value)
  if (obj)
    return obj
  else
    return {
      label: '',
      value: '',
      closable: false,
      locked: false,
      result: []
    }
})

const lock = (value: string | number, locked: boolean) => {
  const index = bottomTabs.value.findIndex(tab => tab.value === value)
  bottomTabs.value[index].locked = locked
  message.success(locked ? '已固定' : '解除固定');
}

// left group
const selected = ref<(string | number)[]>([])

// 文件树操作
const currentNode = reactive<any>({})
const selectNode = (node: any) => {
  if (!node) return
  const { key, isLeaf } = node
  selected.value = [key]
  Object.assign(currentNode, node, { bdmJobId: node.bdmJobId })
  if (!isLeaf) {
    active.value = null
    return
  }
  getBdmJobDetail(node)
}
const getBdmJobDetail = (node: any) => {
  const { bdmJobId, key } = node
  if (!bdmJobId) return
  detailBdmJobVO(bdmJobId).then((res: any) => {
    if (res.responseStatus === 200) {
      const {
        env,
        bdmJobId,
        bdmJobName,
        bdmScriptVO,
        bdmInTableVOList } = res.data
      // 存储引用的数据表
      bdmInTableList.value = bdmInTableVOList || []
      const item = { key, label: bdmJobName, value: bdmJobId, isAdd: false, code: bdmScriptVO?.bdmScript || '', data: node }
      const tabIndex = tabs.value.findIndex(e => e.value === bdmJobId)
      if (tabIndex >= 0) {
        tabs.value[tabIndex].label = bdmJobName
      } else {
        tabs.value.push(item)
      }
      code.value = bdmScriptVO?.bdmScript || ''
      active.value = bdmJobId
      Object.assign(currentNode, bdmScriptVO || { env: env.value, engineType: 1, version: 1 }, res.data)
    }
  })
}

const addTask = (node: any) => {
  const { key, bdmJobName } = node
  const item = { label: bdmJobName, value: key, isAdd: true, ...node, code: '' }
  active.value = key
  code.value = ''
  tabs.value.push({ ...item })
  Object.assign(currentTab, { ...item })
  Object.assign(currentNode, node)
}

const saveBdmJob = () => {
  const { bdmGroupCode, bdmJobCode, bdmJobId, bdmJobName, dataRegionCode, jobLifecycle, createTime, updateTime, version, engineType } = currentNode
  const params: any = {
    bdmGroupCode, bdmJobCode, bdmJobId, bdmJobName, dvsCode: dvsCode.value, dataRegionCode, env: env.value, jobLifecycle, createTime, updateTime
  }
  if (!currentTab.isAdd) {
    const bdmInTableParamList: any = []
    bdmInTableList.value.forEach((e: any) => {
      if (code.value.toLocaleLowerCase().indexOf(e.tableName.toLocaleLowerCase()) >= 0) bdmInTableParamList.push(e)
    })
    params.bdmScriptParam = { bdmScript: code.value, bdmJobCode, createTime, updateTime, env: env.value, engineType: engineType || 1, version }
    params.bdmInTableParamList = bdmInTableParamList
  }
  const action = currentTab.isAdd ? addBdmJob : editBdmJob
  action(params)
    .then((res: any) => {
      if (res.responseStatus === 200) {
        message.success(res.msg)
        selectNode(currentNode)
      } else {
        message.error(res.errorMsg)
      }
    })
}

// sql编辑器操作
const loading = ref(false)
const formatSql = () => {
  code.value = format(code.value, { language: 'postgresql', keywordCase: 'upper' })
}
const executeSqlFn = (isTable: boolean, isColumn: boolean = false) => {
  nextTick(() => {
    const action = isTable ? displayTableStructure : executeSql
    if (!isTable && !code.value) return
    loading.value = true
    const params: any = { sql: isColumn ? selectedCode.value : code.value, bdmJobId: currentNode.bdmJobId, engineType: 1 }
    const bdmInTableParamList: any = []
    bdmInTableList.value.forEach((e: any) => {
      if (code.value.toLocaleLowerCase().indexOf(e.tableName.toLocaleLowerCase()) >= 0) bdmInTableParamList.push(e)
    })
    params.bdmInTableParamList = bdmInTableParamList
    action(params).then((res: any) => {
      if (res.responseStatus === 200) {
        if (res.data.result) {
          const result: any[] = res.data.result.map((e: string) => JSON.parse(e))
          // 查询是否存在未被锁定的结果标签页
          const index = bottomTabs.value.findIndex(e => e.bdmJobId === currentNode.bdmJobId && !e.locked)
          // 获取顶部标签页信息
          const obj: any = tabs.value.find(e => e.value === active.value)
          // 设置标签页的唯一值
          const value = Date.now()
          if (index >= 0) {
            // exist
            if (!bottomTabs.value[index].locked) {
              // unlock
              bottomTabs.value[index].result = result
              bottomActive.value = bottomTabs.value[index].value
              currentBottomMenu.value = bottomTabs.value[index].value
            } else {
              // locked
              bottomTabs.value.push({
                label: '运行结果-' + obj.label,
                value,
                locked: false,
                closable: true,
                result,
                bdmJobId: currentNode.bdmJobId
              })
              bottomActive.value = value
              currentBottomMenu.value = value
            }
          }
          else {
            // unexist
            bottomTabs.value.push({
              label: '运行结果-' + obj.label,
              value,
              locked: false,
              closable: true,
              result,
              bdmJobId: currentNode.bdmJobId
            })
            bottomActive.value = value
            currentBottomMenu.value = value
          }
        }
        else {
          outputCode.value = res.message
          bottomActive.value = 'output'
          currentBottomMenu.value = 'output'
        }
      } else {
        outputCode.value = res.errorMsg
        bottomActive.value = 'output'
        currentBottomMenu.value = 'output'
      }
    }).catch((error: any) => {
      if (error && error.message)
        outputCode.value = error.message
      bottomActive.value = 'output'
      currentBottomMenu.value = 'output'
    }).finally(() => {
      loading.value = false
    })
  });
}
const stopSql = () => { }
const saveFn = () => {
  saveBdmJob()
}
const changeCommond = ({ key }: any) => {
  console.log(key);
}

const open = ref<boolean>(false)
const isEdit = ref<boolean>(false)
const setConfig = () => {
  if (currentNode.bdmJobId) {
    open.value = true
    isEdit.value = true
  } else {
    message.error('请选择作业')
  }
}
const viewConfig = () => {
  const href = router.resolve({
    path: '/development/schedule/detail',
    query: {
      jobCode: currentNode.bdmJobCode
    }
  }).href
  window.open(href, "_blank")
}

// drag
const draggableTop = useDraggableDireaction('top')
const draggableLeft = useDraggableDireaction('left')
const draggableRight = useDraggableDireaction('right')

const resetTabs = () => {
  tabs.value = []
  Object.assign(currentTab, { key: '', label: '', value: '', code: '', isAdd: false })
  active.value = null
}
watch(
  dvsCode,
  () => {
    if (dvsCode.value) {
      resetTabs()
      getRegionList()
      getRegionList(2)
    }
  },
  { deep: true, immediate: true }
)

const ctrls = (event: any) => {
  // 检查按键是否为S，并且Ctrl键是否被按下
  if (event.keyCode === 83 && (navigator.platform.match("Mac") ? event.metaKey : event.ctrlKey)) {
    event.preventDefault(); // 阻止默认行为
  }
}
onMounted(() => {
  window.addEventListener('keydown', ctrls)
})
onBeforeUnmount(() => {
  window.removeEventListener('keydown', ctrls)
})
</script>

<style scoped lang="less">
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
    position: relative;
    height: 300px;
    border-top: 1px solid var(--border-color);

    .code {
      // height: 260px;
    }

  }


}

.group-list {
  position: relative;
  width: 270px;
  border-right: 1px solid var(--border-color);
  padding-right: 5px;
}

.work-space {}

.data-source {
  position: relative;
  width: 270px;
  border-left: 1px solid var(--border-color);
  padding-left: 5px;
}

.draggable {
  position: absolute;
  z-index: 10;
  opacity: .01;
  background-color: var(--primary-color);
  transition: all .3s;
  cursor: col-resize;

  &-top {
    left: 0;
    right: 0;
    top: 0;
    height: 5px;
    cursor: row-resize;
  }

  &-left {
    width: 5px;
    left: 0;
    top: 0;
    bottom: 0;
  }

  &-right {
    width: 5px;
    right: 0;
    top: 0;
    bottom: 0;
  }

  &:hover {
    opacity: .05;
  }
}

.footer {
  height: 20px;
  line-height: 20px;
  font-size: 10px;
  padding: 0 10px;
  border-top: 1px solid var(--border-color);
}
</style>
