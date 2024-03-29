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
      </div>
      <div class="main flex1 flex fdc ovh">
        <div class="main-top flex flex1 ovh">
          <div class="group-list flex fdc" v-show="currentTopMenu">
            <div class="draggable draggable-right" ref="draggableRight"></div>
            <Tree ref="treeRef" class="flex1" name="集成任务" showSearch showGroup v-model:selected="selected"
              @select="selectNode" @add-task="addTask">
            </Tree>
          </div>
          <div class="work-space flex1 flex fdc ovh">
            <template v-if="tabs.length > 0">
              <Tabs v-model:tabs="tabs" v-model:activeKey="active" type="editable-card" hide-add show-icon
                @tabClick="tabClick" @edit="onEdit" @closeAll="closeAllTab" />
              <template v-if="active">
                <Toolbar :is-lock="currentTab.isLock" @edit="editFn" @lock="lockFn" @save="saveFn"
                  @setConfig="setConfig" @viewConfig="viewConfig" />
                <a-spin :spinning="loading" tip="Loading..." wrapperClassName="flex1 ova">
                  <div class="code h100 flex1 ovh">
                    <Config v-show="isLowCode" ref="configRef" v-model="configValue" :is-add="currentTab.isAdd"
                      :is-lock="currentTab.isLock" />
                    <Editor v-show="!isLowCode" v-model="configJson" lang="json" @format="formatFn" />
                  </div>
                </a-spin>
              </template>
            </template>
          </div>
          <div class="data-source flex fdc" v-if="currentRightMenu">
            <div class="draggable draggable-left" ref="draggableLeft"></div>
            <TreeDatasource v-show="currentRightMenu === 1" class="flex1" showSearch name="数据源" @drop="changeSource">
            </TreeDatasource>
            <Chat class="flex1" v-show="currentRightMenu === 2" />
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
import { ref, toRefs, reactive, computed, watch, provide } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue';
import SvgIcon from '@/components/SvgIcon/index.vue'
import Editor from "@/components/Editor/index.vue"
import Tabs from "@/components/Tabs.vue"
import Chat from "@/components/Chat/Chat.vue"
import Tree from "./components/Tree.vue"
import TreeDatasource from "./components/TreeDatasource.vue"
import Toolbar from "./components/ToolbarForEtl.vue"
import Config from "./components/Config.vue"
import SetConfig from './components/SetConfig.vue'
import { cloneDeep } from 'lodash-es'
import { useDvsStore } from '@/stores/modules/user'
// api
import { detailEtlJobVO, addEtlJob, editEtlJob, jobLifecycleEnum } from '@/api/modules/develoment'
import { sourceList, regionList } from '@/api/modules/planning'
// drag
import { useDraggableDireaction } from '@/hooks/useDraggable'

interface Item {
  label: string
  value: string | number
  isAdd?: boolean
  isLock?: boolean
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

const treeRef = ref()

// 生命周期枚举
const lifecycleEnum = ref<any[]>([])
const getLifecycleEnum = () => {
  jobLifecycleEnum().then((res: any) => {
    if (res.responseStatus === 200) {
      lifecycleEnum.value = res.data || []
    }
  })
}
getLifecycleEnum()
provide('lifecycleEnum', lifecycleEnum)

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

// datasource
const sourceData = ref<any[]>([])
const getSourceList = () => {
  const params = {
    dataSourceReadAndWrite: null,
    dataSourceTypeId: null,
    keyword: '',
    pageQueryParam: {
      ascs: [],
      descs: [],
      pageNo: 1,
      size: 100
    }
  }
  sourceList(params).then((res: any) => {
    if (res.responseStatus === 200) {
      const data: any = res.data
      const list = data?.list || []
      sourceData.value = list
    }
  })
}
getSourceList()
provide('sourceData', sourceData)

// 是否显示config组件
const isLowCode = ref(true)

// config
const configRef = ref()
let configValue = reactive({
  datasourceCode: '',
  dvsCode: dvsCode.value,
  env: env.value,
  dataRegionCode: '',
  etlGroupCode: '',
  description: '',
  etlJobId: null,
  etlJobName: '',
  etlJobCode: '',
  jobLifecycle: null,
  etlJobTableParamList: [
    {
      tableExtractParam: {
        env: null, incrType: 0, originTableName: null, incrFields: []
      },
      tableTransformParamList: [
      ],
      tableLoadParam: {
        addonBefore: 'ods_',
        env: null,
        pkField: '',
        tableName: '',
        partitionField: '',
        partitionMaxRows: null,
        visible: false
      },
      dvsTableParam: { createMode: 2, isStream: 0, dwLayer: 1, dwLayerDetail: 11, env: 0, tableName: '', tableAlias: '' },
    }
  ],
})
// 代码编辑模式下记录json格式是否错误，错误时提示并不能保存
const isJsonError = ref(false)
const configJson = computed({
  get() {
    return JSON.stringify(configValue, null, 2)
  },
  set(v) {
    try {
      configValue = Object.assign(configValue, JSON.parse(v))
      isJsonError.value = false
    } catch (error) {
      isJsonError.value = true
      message.error('JSON格式错误:' + error)
    }
  }
})

const loading = ref(false)
const editFn = () => {
  isLowCode.value = !isLowCode.value
  // console.log(configJson.value);
  // message.success('edit:' + configJson.value)
}
const saveFn = () => {
  configRef.value.validate().then(() => {
    saveEtl()
  })
}
const lockFn = (isLock: boolean) => {
  currentTab.isLock = isLock
}
const formatFn = () => {
  message.success('format')
}

const open = ref<boolean>(false)
const isEdit = ref<boolean>(false)
const setConfig = () => {
  if (currentNode.etlJobId) {
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
      jobCode: currentNode.etlJobCode
    }
  }).href
  window.open(href, "_blank")
}

// WS
// import { useWsStore } from '@/stores/modules/ws';
// const wsStore = useWsStore();
// wsStore.initSocket({ url: 'ws://121.12.12.1' })
// const client = computed(() => wsStore.client)

// left menu
const currentTopMenu = ref(1)
const clcikTopMenu = (e: number) => {
  // if (e === 1) { goTo({ name: 'developmentEtl' }) }
  if (e === 2) { goTo({ name: 'developmentBdm' }) }
  if (e === 3) { goTo({ name: 'developmentTask' }) }
  currentTopMenu.value = !currentTopMenu.value ? e : currentTopMenu.value === e ? 0 : e
}
// right menu
const currentRightMenu = ref(1)
const clcikRightMenu = (e: number) => {
  currentRightMenu.value = !currentRightMenu.value ? e : currentRightMenu.value === e ? 0 : e
}

const tabs = ref<any[]>([
])
const currentTab = reactive<Item>({
  label: '', value: '', isAdd: false, isLock: false
})
const active = ref<string | number | null>(null)
const tabClick = (key: string | number) => {
  const tab = tabs.value.find((e: any) => e.value === key)
  if (tab) {
    selected.value = [tab.key]
    Object.assign(currentNode, tab.data)
    const {
      datasourceCode,
      dvsCode,
      env,
      dataRegionCode,
      etlGroupCode,
      description,
      etlJobId,
      etlJobName,
      etlJobCode,
      jobLifecycle, etlJobTableParamList } = tab
    configValue = Object.assign(configValue, {
      datasourceCode,
      dvsCode,
      env,
      dataRegionCode,
      etlGroupCode,
      description,
      etlJobId,
      etlJobName,
      etlJobCode,
      jobLifecycle, etlJobTableParamList
    })
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

// left group
const selected = ref<(string | number)[]>([])

// 文件树操作
const currentNode = reactive<any>({})
const selectNode = (node: any) => {
  if (!node) return
  const { key, isLeaf } = node
  selected.value = [key]
  Object.assign(currentNode, node, { etlJobId: node.etlJobId })
  if (!isLeaf) {
    active.value = null
    return
  }
  // 获取作业信息
  getEtlDetail(node)
}
const addTask = (node: any) => {
  const { key, etlJobName, dataRegionCode, etlGroupCode } = node
  const item = { label: etlJobName, value: key, isAdd: true, ...node }
  if (!tabs.value.some(e => e.value == key)) {
    tabs.value.push(item)
  }
  Object.assign(currentTab, { label: etlJobName + '作业', value: key, isAdd: true, isLock: false })
  resetInfo()
  configValue = Object.assign(configValue, { etlJobName, dataRegionCode, etlGroupCode })
  active.value = key
}

// drag
const draggableLeft = useDraggableDireaction('left')
const draggableRight = useDraggableDireaction('right')

const getEtlDetail = (node: any) => {
  const { etlJobId, key } = node
  if (!etlJobId) return
  detailEtlJobVO(etlJobId).then((res: any) => {
    if (res.responseStatus === 200) {
      const {
        datasourceCode,
        dvsCode,
        env,
        dataRegionCode,
        etlGroupCode,
        description,
        etlJobId,
        etlJobName,
        etlJobCode,
        jobLifecycle,
        etlJobTableVOList } = res.data
      const etlJobTableParamList = etlJobTableVOList?.map((e: any) => {
        const obj =
        {
          tableExtractParam: {
            ...e.tableExtractVO, incrFields: e.tableExtractVO.incrFields.split(',')
          },
          tableTransformParamList: [...e.tableTransformVOList
          ],
          tableLoadParam: {
            ...e.tableLoadVO,
            addonBefore: 'ods_',
            tableName: e.tableLoadVO.tableName.replace('ods_', ''),
            tableAlias: e.dvsTableVO.tableAlias,
            visible: false
          },
          dvsTableParam: { ...e.dvsTableVO, ...e.dvsTableDdlVO },
        }
        return obj
      }) || []
      configValue = Object.assign(configValue, {
        datasourceCode,
        dvsCode,
        env,
        dataRegionCode,
        etlGroupCode,
        description,
        etlJobId,
        etlJobName,
        etlJobCode,
        jobLifecycle, etlJobTableParamList
      })
      const item = { key, label: etlJobName, value: etlJobId, isAdd: false, ...configValue, data: node }
      const tabIndex = tabs.value.findIndex(e => e.value === etlJobId)
      if (tabIndex >= 0) {
        tabs.value[tabIndex].label = etlJobName
      } else {
        tabs.value.push(item)
      }
      Object.assign(currentTab, { ...item, isLock: true })
      active.value = etlJobId
    }
  })
}

// 更换数据源
const changeSource = (data: any) => {
  if (currentTab.isLock) return
  if (configValue.datasourceCode !== data.datasourceCode) {
    configValue.datasourceCode = data.datasourceCode
    configValue.etlJobTableParamList = [
      {
        tableExtractParam: {
          env: null, incrType: 0, originTableName: null, incrFields: []
        },
        tableTransformParamList: [
        ],
        tableLoadParam: {
          addonBefore: 'ods_',
          env: null,
          pkField: '',
          tableName: '',
          partitionField: '',
          partitionMaxRows: null,
          visible: false
        },
        dvsTableParam: { createMode: 2, isStream: 0, dwLayer: 1, dwLayerDetail: 11, env: 0, tableName: '', tableAlias: '' },
      }
    ]
  }
}

// save
const saveEtl = () => {
  const params = cloneDeep(configValue)
  const action = currentTab.isAdd ? addEtlJob : editEtlJob
  const { etlJobCode, dataRegionCode, env, dvsCode, description } = params
  params.etlJobTableParamList = params.etlJobTableParamList.map((e: any) => {
    e.tableExtractParam = { ...e.tableExtractParam, env, incrFields: e.tableExtractParam.incrFields.join(',') }
    e.tableTransformParamList = e.tableTransformParamList.map((o: any) => ({ ...o, env, etlJobCode }))
    e.tableLoadParam = { ...e.tableLoadParam, env, etlJobCode, tableName: 'ods_' + e.tableLoadParam.tableName }
    const dvsTableColumnParamList = e.tableTransformParamList.map(({ outColumn, isPrimaryKey, shortDataTypeId, outDataTypeId, outDataTypeName }: any) => ({ env, columnAlias: outColumn, columnName: outColumn, isPrimaryKey, shortDataTypeId, dataTypeId: outDataTypeId, dataTypeName: outDataTypeName }))
    e.dvsTableParam = { ...e.dvsTableParam, env, dvsCode, dataRegionCode, description, dvsTableColumnParamList, tableName: e.tableLoadParam.tableName, tableAlias: e.tableLoadParam.tableAlias }
    return e
  })
  action(params)
    .then((res: any) => {
      if (res.responseStatus === 200) {
        message.success(res.msg)
        if (currentTab.isAdd) {
          // deleteCurrentTab
          deleteCurrentTab()
          resetInfo()
          treeRef.value.getEtlGroupList(dataRegionCode, env)
        }
      } else {
        message.error(res.errorMsg)
      }
    })
}
const deleteCurrentTab = () => {
  const index = tabs.value.findIndex(e => e.value === currentTab.value)
  if (index >= 0) tabs.value.splice(index, 1)
}
const resetInfo = () => {
  configValue = Object.assign(configValue, {
    datasourceCode: '',
    dvsCode: dvsCode.value,
    env: env.value,
    dataRegionCode: '',
    etlGroupCode: '',
    description: '',
    etlJobId: null,
    etlJobName: '',
    etlJobCode: '',
    jobLifecycle: null,
    etlJobTableParamList: [
      {
        tableExtractParam: {
          env: null, incrType: 0, originTableName: null, incrFields: []
        },
        tableTransformParamList: [
        ],
        tableLoadParam: {
          addonBefore: 'ods_',
          env: null,
          pkField: '',
          tableName: '',
          partitionField: '',
          partitionMaxRows: null,
          visible: false
        },
        dvsTableParam: { createMode: 2, isStream: 0, dwLayer: 1, dwLayerDetail: 11, env: 0, tableName: '', tableAlias: '' },
      }
    ],
  })
}

const resetTabs = () => {
  tabs.value = []
  Object.assign(currentTab, { label: '', value: '', isAdd: false, isLock: false })
  active.value = null
}
watch(
  dvsCode,
  () => {
    if (dvsCode.value) {
      resetTabs()
      getRegionList()
      getRegionList(1)
    }
  },
  { deep: true, immediate: true }
)
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
</style>
