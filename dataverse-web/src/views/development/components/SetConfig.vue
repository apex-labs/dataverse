<template>
  <Drawer :width="500" :title="`${isEdit ? '编辑' : '配置'}调度计划`" placement="right" :open="open" :footer="false"
    @close="onClose">
    <a-spin :spinning="spinning" tip="Loading...">
      <a-form ref="formRef" :model="form" :rules="rules" :labelCol="{ span: 6 }" autocomplete="off">
        <a-collapse v-model:activeKey="activeKey" :bordered="false">
          <a-collapse-panel key="1" header="调度信息">
            <a-form-item label="调度任务" name="scheduleCode" :rules="rules.scheduleCode">
              <a-select v-model:value="form.scheduleCode" placeholder="请选择" @change="changeScheduleCode">
                <a-select-option :value="item.scheduleCode" v-for="item in scheduleList" :key="item.scheduleCode">
                  {{ item.scheduleName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-collapse-panel>
          <a-collapse-panel key="2" header="作业节点">
            <a-table sticky rowKey="nodeCode"
              :row-selection="{ selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange, getCheckboxProps: getCheckboxProps }"
              :data-source="scheduleNodeList">
              <a-table-column title="节点名称" :width="150">
                <template #default="{ record }">
                  {{ record.nodeJobsDTO?.jobName || '-' }}
                </template>
              </a-table-column>
              <a-table-column title="节点类型" :width="150">
                <template #default="{ record }">
                  {{ record.nodeJobsDTO?.jobType === 1 ? 'ETL job' : 'BDM job' }}
                </template>
              </a-table-column>
              <a-table-column title="操作" :width="80">
                <template #default="{ record }">
                  <a-button v-if="record.disabled" type="link" danger class="btn-text"
                    @click="removeDependenceOrScheduleNode(record)">
                    <a-tooltip placement="top" title="移除调度节点">
                      <DeleteOutlined />
                    </a-tooltip>
                  </a-button>
                </template>
              </a-table-column>
            </a-table>
          </a-collapse-panel>
          <a-collapse-panel key="3">
            <template #header>
              <div class="fxbw">
                <span>上游依赖</span>
                <!-- <a-button type="link" class="btn-text">自动解析</a-button> -->
              </div>
            </template>
            <div class="fxbw">
              <a-input v-model:value="searchValue" placeholder="请输入作业名">
                <template #prefix>
                  <SearchOutlined />
                </template>
              </a-input>
              <a-button type="primary" class="ml10 mr10" :disabled="hasAdd || !form.scheduleCode" :loading="saving"
                @click="onOk()">添加根节点</a-button>
              <a-button :disabled="!form.scheduleCode || !hasSelected" :loading="saving2"
                @click="onOk(false)">添加作业节点</a-button>
            </div>
            <a-table :data-source="dependenceNodeListSearch">
              <a-table-column key="jobName" title="依赖作业名" data-index="jobName" :width="100" />
              <a-table-column key="userName" title="责任人" data-index="userName" :width="100" />
              <a-table-column key="isValid" title="生效" data-index="isValid" :width="100">
                <template #default="{ record }">
                  <a-switch :checked="record.isValid" :checkedValue="1" :unCheckedValue="0"
                    @change="(v: any) => changeValid(v, record)" />
                </template>
              </a-table-column>
              <a-table-column key="action" title="操作" :width="80">
                <template #default="{ record }">
                  <a-button type="link" class="btn-text" @click="removeDependenceOrScheduleNode(record, false)">
                    <a-tooltip placement="top" title="移除依赖关系">
                      <DeleteOutlined />
                    </a-tooltip>
                  </a-button>
                </template>
              </a-table-column>
            </a-table>
          </a-collapse-panel>
        </a-collapse>
      </a-form>
    </a-spin>
  </Drawer>
</template>

<script setup lang="ts">
import { ref, toRefs, reactive, computed, watch } from 'vue'
import type { Rule } from 'ant-design-vue/es/form'
import Drawer from '@/components/Drawer.vue'
import { useDvsStore } from '@/stores/modules/user'
import { schedulePage, scheduleInfo, saveOrUpdateScheduleNode, removeScheduleNode, removeDependence, updateNodeDependence, scheduleCodeByJobCode } from '@/api/modules/develoment'
import { cloneDeep } from 'lodash-es'
import { message } from 'ant-design-vue'

const dvsStore = useDvsStore()
const { dvsCode, env } = toRefs(dvsStore)

const props = defineProps({
  value: {
    type: Boolean,
    default: false
  },
  edit: {
    type: Boolean,
    default: false
  },
  isLock: {
    type: Boolean,
    default: false
  },
  currentNode: {
    type: Object,
    default: () => ({})
  },
})

const emit = defineEmits(['update:value', 'update:edit'])

const open = computed({
  get() {
    return props.value
  },
  set(v) {
    emit('update:value', v)
  }
})
const isEdit = computed({
  get() {
    return props.edit
  },
  set(v) {
    emit('update:edit', v)
  }
})
watch(
  () => open,
  () => {
    if (open.value) {
      getScheduleList()
      getScheduleCodeByJobCode()
    }
  },
  { deep: true }
)

const spinning = ref(false)
const activeKey = ref(['1', '2', '3'])
const formRef = ref()
const rules: Record<string, Rule[]> = {
  scheduleCode: [{ required: true, message: '请选择调度任务', trigger: ['blur', 'change'] }],
}
const form = reactive({
  scheduleId: null,
  scheduleCode: null,
  env: env.value,
  nodeJobsParam: [],
  scheduleEdgeParam: [],
})
const saving = ref<boolean>(false)
const saving2 = ref<boolean>(false)

// 数据表
const scheduleNodeList = ref<any[]>([]) // 作业节点列表
const dependenceNodeList = ref<any[]>([])  // 依赖列表
const searchValue = ref('')
const dependenceNodeListSearch = computed(() => dependenceNodeList.value?.filter((e: any) => e.jobName.indexOf(searchValue.value) >= 0))
type Key = string | number;
const state = reactive<{
  selectedRowKeys: Key[];
  loading: boolean;
}>({
  selectedRowKeys: [], // Check here to configure the default column
  loading: false,
})
const hasAdd = computed(() => scheduleNodeList.value?.some((e) => e.nodeJobsDTO.jobCode === (props.currentNode.etlJobCode || props.currentNode.bdmJobCode)))
const hasSelected = computed(() => state.selectedRowKeys.length > 0)
const onSelectChange = (selectedRowKeys: Key[]) => {
  state.selectedRowKeys = selectedRowKeys;
};
const getCheckboxProps = (record: any) => ({ disabled: record.disabled, width: 60 })

// 调度任务列表
const scheduleList = ref<any[]>([])
const getScheduleList = () => {
  const params = {
    dvsCode: dvsCode.value,
    env: env.value,
    scheduleLifecycle: null,
    scheduleName: '',
    pageQueryParam: {
      ascs: [],
      descs: [],
      pageNo: 1,
      size: 100
    }
  }
  schedulePage(params).then((res: any) => {
    if (res.responseStatus === 200) {
      const data: any = res.data
      scheduleList.value = data?.list || []
    }
  })
}

// 根据jobCode获取scheduleId
const getScheduleCodeByJobCode = () => {
  const { etlJobCode, bdmJobCode, env } = props.currentNode
  const params: any = { env, jobCode: etlJobCode || bdmJobCode }
  scheduleCodeByJobCode(params).then((res: any) => {
    if (res.responseStatus === 200) {
      const { scheduleId, scheduleCode } = res.data
      Object.assign(form, { scheduleId, scheduleCode })
      getScheduleInfo()
    } else {
      message.error(res.errorMsg)
    }
  })
}

// 根据选择的调度任务查询作业节点和依赖列表
const getScheduleInfo = () => {
  state.selectedRowKeys = []
  spinning.value = true
  const { etlJobCode, bdmJobCode, env } = props.currentNode
  const params = {
    env,
    jobCode: etlJobCode || bdmJobCode,
    jobType: etlJobCode ? 1 : 2,
    scheduleCode: form.scheduleCode
  }
  scheduleInfo(params).then((res: any) => {
    if (res.responseStatus === 200) {
      const { scheduleNodeDTOList, dependenceNodeDTOList }: any = res.data
      scheduleNodeList.value = scheduleNodeDTOList?.map((e: any) => ({ ...e, disabled: e.nodeJobsDTO?.jobCode === params.jobCode || dependenceNodeDTOList?.some((o: any) => e.nodeCode === o.nodeCode) })) || []
      dependenceNodeList.value = dependenceNodeDTOList || []
    }
  }).finally(() => {
    spinning.value = false
  })
}
const changeScheduleCode = (scheduleCode: string | number) => {
  const schedule = scheduleList.value.find((e: any) => e.scheduleCode === scheduleCode)
  Object.assign(form, { scheduleCode, scheduleId: schedule.scheduleId, env: schedule.env })
  state.selectedRowKeys = []
  getScheduleInfo()
}
const changeValid = (v: any, record: any) => {
  const node = dependenceNodeList.value.find((e: any) => e.nodeCode === record.nodeCode)
  node.isValid = v
  const { env, nodeCode } = record
  const { etlJobCode, bdmJobCode } = props.currentNode
  const params = {
    dependenceCode: nodeCode,
    env,
    isValid: v,
    jobCode: etlJobCode || bdmJobCode,
    scheduleCode: form.scheduleCode
  }
  updateNodeDependence(params).then((res: any) => {
    if (res.responseStatus === 200) {
      message.success(res.msg)
      getScheduleInfo()
    } else {
      message.error(res.errorMsg)
    }
  })
}

const onClose = () => {
  Object.assign(form, {
    scheduleId: null,
    scheduleCode: null,
    nodeJobsParam: [],
    scheduleEdgeParam: [],
  })
  formRef.value.clearValidate()
  scheduleNodeList.value = []
  dependenceNodeList.value = []
  state.selectedRowKeys = []
  open.value = false
}
const onOk = (isRoot: boolean = true) => {
  if (isRoot)
    saving.value = true
  else saving2.value = true
  formRef.value
    .validate()
    .then(() => {
      if (!isRoot && !hasSelected.value) {
        message.error('请选择依赖的作业节点')
        return
      }
      submitFn(isRoot)
    })
    .catch(() => {
      if (isRoot)
        saving.value = false
      else saving2.value = false
    })
}
const submitFn = (isRoot: boolean = true) => {
  const params = Object.assign(cloneDeep(form), { nodeType: isRoot ? 1 : 2 })
  const { env, etlJobCode, etlJobName, bdmJobCode, bdmJobName, userId, userName } = props.currentNode
  params.jobCode = etlJobCode || bdmJobCode
  params.scheduleEdgeParam = []
  params.isAddNode = hasAdd.value ? 1 : 0
  if (!hasAdd.value)
    params.nodeJobsParam = [{
      env,
      jobCode: etlJobCode || bdmJobCode,
      jobName: etlJobName || bdmJobName,
      jobType: etlJobCode ? 1 : 2,
      nodeJobId: null,  // etlJobId || bdmJobId,
      nodeCode: null,
      userId,
      userName
    }]
  if (!isRoot) {
    const nodes = scheduleNodeList.value.filter((e: any) => state.selectedRowKeys.includes(e.nodeCode))
    nodes.forEach((e: any) => {
      const { env, nodeCode, scheduleCode } = e
      const node = { ...e.nodeJobsDTO, userId, userName }
      const edge = { env, fromNode: nodeCode, scheduleCode }
      params.nodeJobsParam.push(node)
      params.scheduleEdgeParam.push(edge)
    })
  }
  saveOrUpdateScheduleNode(params)
    .then((res: any) => {
      if (res.responseStatus === 200) {
        message.success(res.msg)
        getScheduleInfo()
      } else {
        message.error(res.errorMsg)
      }
    })
    .finally(() => {
      if (isRoot)
        saving.value = false
      else saving2.value = false
    })
}

const removeDependenceOrScheduleNode = (record: any, isRoot: boolean = true) => {
  const { etlJobCode, bdmJobCode, env } = props.currentNode
  const params: any = {
    env,
    jobCode: etlJobCode || bdmJobCode,
    scheduleCode: form.scheduleCode,
    fromCode: null
  }
  const action = isRoot ? removeScheduleNode : removeDependence
  if (isRoot) {
    params.fromCode = dependenceNodeListSearch.value.map((e: any) => e.nodeCode)
  } else {
    params.fromCode = record.nodeCode
  }
  action(params).then((res: any) => {
    if (res.responseStatus === 200) {
      message.success(res.msg)
      getScheduleInfo()
    } else {
      message.error(res.errorMsg)
    }
  })
}
</script>

<style scoped lang="less">
.config {
  padding: 10px;
  overflow: auto;

  &-wrap {
    content: '';
    min-width: 800px;
    display: block;
  }
}
</style>
