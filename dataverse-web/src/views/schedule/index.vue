<template>
  <div class="g-card h100 flex fdc">
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
        <!-- <div class="page-title">调度任务</div> -->
        <div class="filter fxbw">
          <a-form :model="query" name="horizontal_login" layout="inline" autocomplete="off">
            <a-form-item label="任务状态">
              <a-select v-model:value="query.scheduleLifecycle" placeholder="请选择" style="width:150px" allowClear>
                <a-select-option :value="k" v-for="(v, k) in lifecycleEnum" :key="k">
                  {{ v }}
                </a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="发布状态">
              <a-select v-model:value="query.env" placeholder="请选择" style="width:100px" allowClear>
                <a-select-option :value="k" v-for="(v, k) in envEnum" :key="k">
                  {{ v }}
                </a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="搜索关键字">
              <a-input v-model:value="query.scheduleName" v-trim placeholder="请输入"> </a-input>
            </a-form-item>

            <a-form-item>
              <a-button :disabled="loading" type="primary" @click="search">查询</a-button>
              <a-button class="ml10" @click="reset">重置</a-button>
            </a-form-item>
          </a-form>
          <div class="btns">
            <a-button :disabled="loading" type="primary" @click="addFn">创建调度任务</a-button>
          </div>
        </div>

        <a-spin :spinning="loading" tip="Loading..." wrapperClassName="list flex1 m10 ovh">
          <a-table :dataSource="list" :scroll="tableScroll" :pagination="false">
            <a-table-column key="scheduleId" title="任务ID" data-index="scheduleId" :width="80" :min-width="80"
              fixed="left" />
            <a-table-column key="scheduleName" title="任务名称" data-index="scheduleName" :width="100" :min-width="100" />
            <a-table-column key="scheduleLifecycle" title="任务状态" data-index="scheduleLifecycle" :width="100"
              :min-width="100">
              <template #default="{ text: scheduleLifecycle }">
                {{ lifecycleEnum[scheduleLifecycle] }}
              </template>
            </a-table-column>
            <!-- <a-table-column key="published_status" title="发布状态" data-index="published_status" :width="100"
              :min-width="100" />
            <a-table-column key="node_count" title="节点数量" data-index="node_count" :width="100" :min-width="100" />
            <a-table-column key="work_count" title="作业数量" data-index="work_count" :width="100" :min-width="100" /> -->
            <a-table-column key="userName" title="创建人" data-index="userName" :width="150" />
            <a-table-column key="createTime" title="创建时间" data-index="createTime" :width="150" />
            <a-table-column key="updateTime" title="更新时间" data-index="updateTime" :width="150" />
            <!-- <a-table-column key="start_time" title="开始时间" data-index="start_time" :width="150" />
            <a-table-column key="end_time" title="结束时间" data-index="end_time" :width="150" /> -->
            <a-table-column key="action" title="操作" fixed="right" :width="100">
              <template #default="{ record }">
                <a-space>
                  <a class="btn" @click="viewFn(record)">查看</a>
                  <a class="btn" @click="editFn(record)">编辑</a>
                  <a class="btn" @click="deleteFn(record)">删除</a>
                </a-space>
              </template>
            </a-table-column>
          </a-table>
        </a-spin>
        <div class="ar p10">
          <a-pagination v-model:current="current" v-model:pageSize="pageSize" show-quick-jumper :total="total"
            @change="onChange" />
        </div>
      </div>
    </div>

    <Drawer :width="500" :title="isEdit ? '编辑调度任务' : '创建调度任务'" placement="right" :open="open" :loading="saving"
      @close="cancelSave" @ok="saveJob">
      <a-form ref="formRef" :model="form" :rules="rules" v-bind="layout" autocomplete="off">
        <a-collapse v-model:activeKey="activeKey" :bordered="false">
          <a-collapse-panel key="1" header="任务信息">
            <a-form-item label="任务名称" name="scheduleName">
              <a-input v-model:value="form.scheduleName" v-trim placeholder="请输入" />
            </a-form-item>
            <a-form-item label="描述" name="description">
              <a-textarea v-model:value="form.description" placeholder="请输入" :auto-size="{ minRows: 2, maxRows: 5 }" />
            </a-form-item>
          </a-collapse-panel>
          <a-collapse-panel key="2" header="设置定时规则">
            <a-form-item label="刷新频率" name="dayNum">
              <a-input-number v-model:value="form.dayNum" addon-before="每" addon-after="天" :min="1" />
            </a-form-item>
            <a-form-item label="刷新时间" name="time">
              <TimePicker v-model:value="form.time" format="HH:mm" value-format="HH:mm" />
            </a-form-item>
            <a-form-item label="开始时间" name="startTimeValue">
              <a-date-picker v-model:value="form.startTimeValue"
                :show-time="{ defaultValue: dayjs('00:00:00', 'HH:mm:ss') }" format="YYYY-MM-DD HH:mm:ss"
                valueFormat="YYYY-MM-DD HH:mm:ss" placeholder="请选择" />
            </a-form-item>
            <a-form-item label="结束时间" name="endTimeValue">
              <a-date-picker v-model:value="form.endTimeValue"
                :show-time="{ defaultValue: dayjs('23:59:59', 'HH:mm:ss') }" format="YYYY-MM-DD HH:mm:ss"
                valueFormat="YYYY-MM-DD HH:mm:ss" placeholder="请选择" />
            </a-form-item>
          </a-collapse-panel>
        </a-collapse>
      </a-form>
    </Drawer>

    <a-modal v-model:open="isDelete" title="确定删除吗？" okType="danger" :confirm-loading="deleting" @ok="handleDeleteOk">
      <p>删除后无法恢复</p>
    </a-modal>
  </div>
</template>

<script setup lang="ts" name="tasks">
import { ref, toRefs, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { message, TimePicker } from 'ant-design-vue';
import type { Rule } from 'ant-design-vue/es/form';
import { useDvsStore } from '@/stores/modules/user'
import { schedulePage, saveJobSchedule, deleteSchedule } from '@/api/modules/develoment'
import Drawer from '@/components/Drawer.vue'
import dayjs from 'dayjs';
const router = useRouter()
const dvsStore = useDvsStore()
const { env, dvsCode } = toRefs(dvsStore)

const envEnum = {
  0: 'BASIC', 1: 'DEV', 2: 'PROD'
}
const lifecycleEnum: any = {
  31: '调度编排中', 32: '调度编排完成', 41: '调度测试中', 42: '调度测试完成(通过）', 51: '已发布生产', 61: '生产测试中', 62: '生产测试完成(通过）', 71: '已上线', 72: '已下线'
}

interface PageQueryParam {
  ascs: string[]
  descs: string[]
  pageNo: number
  size: number
}
interface FormState {
  dvsCode: string | null
  env: number | string | null
  scheduleLifecycle: number | null
  scheduleName: string | null
  pageQueryParam: PageQueryParam
}
interface Item {
  scheduleId: string
  scheduleName: string
  scheduleLifecycle: number
  scheduleCode: string
  rawData: string
  dvsCode: string
  description: string
  env: number
  jobId: number
  createTime: string
  updateTime: string
  published_status: number
  node_count: number
  work_count: number
  userName: string
  start_time: string
  end_time: string
}

// left menu
const currentTopMenu = ref(3)
const clcikTopMenu = (e: number) => {
  if (e === 1) { router.push({ name: 'developmentEtl' }) }
  if (e === 2) { router.push({ name: 'developmentBdm' }) }
  currentTopMenu.value = !currentTopMenu.value ? e : currentTopMenu.value === e ? 0 : e
}

const tableScroll = reactive({
  x: 'max-content',
  y: 1000
})
const updateTable = () => {
  const obj: HTMLElement | null = document.querySelector('.list')
  tableScroll.y = (obj && obj.offsetHeight) ? (obj.offsetHeight - 50) : 1000
}
onMounted(() => {
  getList()
})

const loading = ref<boolean>(false)
const list = ref<Item[]>([])
const current = ref<number>(1)
const pageSize = ref<number>(10)
const total = ref<number>(0)
const query = reactive<FormState>({
  dvsCode: dvsCode.value,
  env: null,
  scheduleLifecycle: null,
  scheduleName: '',
  pageQueryParam: {
    ascs: [],
    descs: [],
    pageNo: 1,
    size: 10
  }
})
watch(
  () => dvsCode,
  () => {
    reset()
  },
  { deep: true }
)

const getList = () => {
  loading.value = true
  schedulePage(query).then((res: any) => {
    if (res.responseStatus === 200) {
      const data: any = res.data
      list.value = (data?.list || []).map((e: any) => {
        e.createTime = e.createTime ? dayjs(e.createTime, 'YYYY-MM-DD HH:mm:ss').format('YYYY-MM-DD HH:mm:ss') : '-'
        e.updateTime = e.updateTime ? dayjs(e.updateTime, 'YYYY-MM-DD HH:mm:ss').format('YYYY-MM-DD HH:mm:ss') : '-'
        return e
      })
      updateTable()
    }
  }).finally(() => {
    loading.value = false
  })
}
const search = () => {
  current.value = 1
  getList()
}
const reset = () => {
  loading.value = false
  current.value = 1
  query.dvsCode = dvsCode.value
  query.env = null
  query.scheduleName = ''
  query.scheduleLifecycle = null
  getList()
}

const onChange = (current: number, pageSize: number) => {
  console.log('Page: ', current, pageSize)
  query.pageQueryParam.pageNo = current
  query.pageQueryParam.size = pageSize
  getList()
}

// save
const checkName = async (_rule: Rule, value: string) => {
  if (!value) {
    return Promise.reject('请填写调度任务名称')
  }
  if (value.length <= 20) {
    return Promise.resolve()
  } else {
    return Promise.reject('请填写长度在20以内的字符')
  }
}
const rules: Record<string, Rule[]> = {
  scheduleName: [{ required: true, validator: checkName, trigger: ['blur', 'change'] }],
  cron: [{ required: true, message: '请输入cron表达式', trigger: ['blur', 'change'] }],
  dayNum: [{ required: true, message: '请选择填写刷新频率', trigger: ['blur', 'change'] }],
  time: [{ required: true, message: '请选择刷新时间', trigger: ['blur', 'change'] }],
  startTimeValue: [{ required: true, message: '请选择开始时间', trigger: ['blur', 'change'] }],
  endTimeValue: [{ required: true, message: '请选择结束时间', trigger: ['blur', 'change'] }],
}
const layout = {
  labelCol: { span: 5 },
}
const formRef = ref()
const open = ref(false)
const isEdit = ref(false)
const saving = ref(false)
const form = reactive({
  scheduleId: null,
  scheduleCode: null,
  scheduleName: '',
  description: '',
  env: env.value,
  dvsCode: dvsCode.value,
  scheduleLifecycle: 31,
  dayNum: 1,
  time: null,
  startTimeValue: null,
  endTimeValue: null,
})
const activeKey = ref(['1', '2'])
const resetInfo = () => {
  Object.assign(form, {
    scheduleId: null,
    scheduleCode: null,
    scheduleName: '',
    description: '',
    env: env.value,
    dvsCode: dvsCode.value,
    scheduleLifecycle: 31,
    dayNum: 1,
    time: null,
    startTimeValue: null,
    endTimeValue: null,
  })
}
const cancelSave = () => {
  isEdit.value = false
  open.value = false
  saving.value = false
  resetInfo()
}
const saveJob = () => {
  formRef.value.validate().then(() => {
    saving.value = true
    const params = Object.assign({}, form)
    saveJobSchedule(params).then((res: any) => {
      if (res.responseStatus === 200) {
        message.success(res.msg)
        open.value = false
        getList()
      } else {
        message.error(res.errorMsg)
      }
    }).finally(() => {
      saving.value = false
    })
  })
}

const viewFn = (record: Item) => {
  const href = router.resolve({
    path: '/development/schedule/detail',
    query: {
      scheduleCode: record.scheduleCode
    }
  }).href
  window.open(href, "_blank")
  // router.push({
  //   path: '/development/schedule/detail',
  //   query: {
  //     scheduleCode: record.scheduleCode
  //   }
  // })
}
const addFn = () => {
  resetInfo()
  isEdit.value = false
  open.value = true
}
const editFn = (record: any) => {
  resetInfo()
  const { scheduleName, scheduleId, scheduleCode, scheduleLifecycle, dvsCode, env, description, dayNum, time, startTime, endTime } = record
  Object.assign(form, {
    scheduleName, scheduleId, scheduleCode, scheduleLifecycle, dvsCode, env, description, dayNum, time: dayjs(time, 'HH:mm').format('HH:mm'), startTimeValue: dayjs(startTime).format('YYYY-MM-DD HH:mm:ss'), endTimeValue: dayjs(endTime).format('YYYY-MM-DD HH:mm:ss')
  })
  isEdit.value = true
  open.value = true
}

const isDelete = ref(false)
const deleting = ref(false)
const currentScheduleId = ref<string | number>('')
const handleDeleteOk = () => {
  deleting.value = true
  deleteSchedule({ scheduleId: currentScheduleId.value }).then((res: any) => {
    if (res.responseStatus === 200) {
      message.success(res.msg)
      isDelete.value = false
      getList()
    } else {
      message.error(res.errorMsg)
    }
  }).finally(() => {
    deleting.value = false
  })
}
const deleteFn = (record: Item) => {
  currentScheduleId.value = record.scheduleId
  isDelete.value = true
}
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

.filter {
  padding: 10px 20px;
  line-height: 32px;
  border-bottom: 1px solid var(--border-color);
}

.btn,
.btn:hover {
  color: #ff6b00;
}
</style>
