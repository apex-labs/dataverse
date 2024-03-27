<template>
  <div class="g-card flex fdc ovh">
    <div class="fxbw block-t aic">
      <h4 class="tit">数据域</h4>
      <div class="btns">
        <a-button type="primary" @click="addRegionFn">新增数据域</a-button>
      </div>
    </div>
    <div class="block-c flex1 flex fdc">
      <div class="search flex mb10">
        <a-form layout="inline" :model="searchForm" :colon="false">
          <a-form-item>
            <a-input v-model:value="searchForm.keyword" placeholder="数据域别名或英文名" allowClear> </a-input>
          </a-form-item>
          <a-form-item>
            <a-select v-model:value="searchForm.dvsCode" placeholder="数据空间" allowClear style="width:150px">
              <a-select-option :value="item.dvsCode" v-for="(item) in spaceList" :key="item.parentId">
                {{ item.parentAlias }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item>
            <a-select v-model:value="searchForm.dwLayerDetail" placeholder="分层明细" allowClear style="width:100px">
              <a-select-option :value="v" v-for="(k, v, i) in dwLayerDetailAllEnum" :key="i">{{ k }}</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="getRegionList"> 查询 </a-button>
            <a-button class="ml10" @click="resetSearch"> 重置 </a-button>
          </a-form-item>
        </a-form>
      </div>
      <a-spin :spinning="searching" tip="Loading..." wrapperClassName="region-list flex1 ovh">
        <a-table class="result-table" :dataSource="list" :columns="columns" :scroll="scrollOptions" :pagination="false"
          :showSorterTooltip="false">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'env'">
              <span>
                {{ envEnum[record.env] }}
              </span>
            </template>
            <template v-if="column.key === 'dwLayer'">
              <span>
                {{ dwLayerEnum[record.dwLayer] }}
              </span>
            </template>
            <!-- <template v-if="column.key === 'dwLayerDetail'">
              <span>
                {{ dwLayerDetailAllEnum[record.dwLayerDetail] }}
              </span>
            </template> -->
            <template v-if="column.key === 'action'">
              <a class="tbtn" @click="editRegionFn(record, true)">查看</a>
              <a class="tbtn ml10 mr10" @click="editRegionFn(record)">编辑</a>
              <a class="tbtn" @click="deleteRegionFn(record)">删除</a>
            </template>
          </template>
        </a-table>
      </a-spin>
      <div class="ar p10">
        <a-pagination v-model:current="searchForm.pageQueryParam.pageNo"
          v-model:pageSize="searchForm.pageQueryParam.size" show-size-changer :total="total" @change="changePage" />
      </div>
    </div>

    <Drawer :width="500" :title="`${isEdit ? '编辑' : '新增'}数据域`" placement="right" :open="open" :loading="loading"
      :readonly="readonly" @close="onClose" @ok="onOk">
      <a-form ref="formRef" :model="form" :rules="rules" :labelCol="{ span: 6 }" autocomplete="off"
        :disabled="readonly">
        <a-form-item label="数据域名称" name="regionName">
          <a-input v-model:value="form.regionName" v-trim placeholder="请输入"> </a-input>
        </a-form-item>
        <a-form-item label="数据域别名" name="regionAlias">
          <a-input v-model:value="form.regionAlias" v-trim placeholder="请输入"> </a-input>
        </a-form-item>
        <a-form-item label="数据域简称" name="regionAbbr">
          <a-input v-model:value="form.regionAbbr" v-trim placeholder="请输入"> </a-input>
        </a-form-item>
        <a-form-item label="数据空间" name="dvsCode">
          <a-select v-model:value="form.dvsCode" placeholder="请选择" :disabled="isEdit" @change="changeDvsCode">
            <a-select-option :value="item.dvsCode" v-for="(item) in spaceList" :key="item.parentId">
              <div class="fxbw">
                <span>{{ item.parentAlias }}</span>
                <span>{{ item.dvsCode }}</span>
              </div>
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="环境模式" name="envMode" v-show="form.dvsCode">
          <a-radio-group v-model:value="form.envMode" disabled>
            <a-radio :value="item.value" v-for="(item, i) in modelEnum" :key="'mode_' + i">{{ item.desc
              }}</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="所属分层" name="dwLayer">
          <a-select v-model:value="form.dwLayer" placeholder="请选择" @change="changeDwLayer">
            <a-select-option :value="Number(v)" v-for="(k, v, i) in dwLayerEnum" :key="i">{{ k }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="分层明细" name="dwLayerDetail">
          <a-select v-model:value="form.dwLayerDetail" placeholder="请选择">
            <a-select-option :value="dw.label" v-for="(dw, i) in dwLayerDetailEnum" :key="i">{{ dw.label
              }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="数据域描述" name="description">
          <!-- <a-input v-model:value="form.description" v-trim placeholder="请输入"> </a-input> -->
          <a-textarea v-model:value="form.description" :maxlength="200" placeholder="请输入说明（200字以内）">
          </a-textarea>
        </a-form-item>
      </a-form>
    </Drawer>

    <a-modal v-model:open="isDelete" title="确定删除吗？" okType="danger" :confirm-loading="deleting" @ok="handleDeleteOk">
      <p>删除后无法恢复</p>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onBeforeUnmount, toRefs } from 'vue'
import { message } from 'ant-design-vue'
import type { Rule } from 'ant-design-vue/es/form'
import { regionList, addRegion, editRegion, deleteRegion } from '@/api/modules/planning'
import Drawer from '@/components/Drawer.vue'
import { useDvsStore } from '@/stores/modules/user'
import dayjs from 'dayjs'
const dvsStore = useDvsStore()
const { modelEnum, spaceList } = toRefs(dvsStore)

const envEnum: any = { 0: 'BASIC', 1: 'DEV', 2: 'PROD' }
const dwLayerEnum: any = {
  1: 'ODS', 2: 'DW', 3: 'ADS'
}
const dwLayerDetailAllEnum: any = { '11': 'ODS', '21': 'DWD', '22': 'DWS', '23': 'DIM', '31': 'MASTER', '32': 'MODEL', '33': 'LABEL', '34': 'DM' }

const searchForm = reactive({
  dvsCode: null,
  dwLayerDetail: null,
  keyword: '',
  pageQueryParam: {
    ascs: [],
    descs: ['createTime'],
    pageNo: 1,
    size: 10
  }
})
const searching = ref(false)
const list = ref<any[]>([])
const total = ref<number>(0)
const columns = ref<any[]>([
  {
    title: 'ID',
    dataIndex: 'dataRegionId',
    key: 'dataRegionId',
    ellipsis: true
  },
  {
    title: '别名',
    dataIndex: 'regionAlias',
    key: 'regionAlias',
    ellipsis: true
  },
  {
    title: '英文名',
    dataIndex: 'regionName',
    key: 'regionName',
    ellipsis: true
  },
  {
    title: '简称',
    dataIndex: 'regionAbbr',
    key: 'regionAbbr',
    ellipsis: true
  },
  {
    title: '编码',
    dataIndex: 'dataRegionCode',
    key: 'dataRegionCode',
    ellipsis: true
  },
  {
    title: '空间名称',
    dataIndex: 'dvsName',
    key: 'dvsName',
    ellipsis: true
  },
  {
    title: '所属分层',
    key: 'dwLayer',
    dataIndex: 'dwLayer',
    ellipsis: true,
    width: 100,
  },
  {
    title: '环境',
    key: 'env',
    dataIndex: 'env',
    ellipsis: true
  },
  {
    title: '创建人',
    key: 'userName',
    dataIndex: 'userName',
    ellipsis: true
  },
  {
    title: '创建时间',
    key: 'createTime',
    dataIndex: 'createTime',
    ellipsis: true
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right'
  }
])

const scrollOptions = reactive({
  x: 'max-content',
  y: 1000
})
const updateTable = () => {
  const obj: HTMLElement | null = document.querySelector('.region-list')
  scrollOptions.y = (obj && obj.offsetHeight) ? (obj.offsetHeight - 50) : 1000
}

const getRegionList = () => {
  searching.value = true
  const params = { ...searchForm }
  regionList(params).then((res: any) => {
    if (res.responseStatus === 200) {
      const data: any = res.data
      list.value = (data?.list || []).map((e: any) => ({ ...e, createTime: dayjs(e.createTime).format('YYYY-MM-DD HH:mm:ss'), updateTime: dayjs(e.updateTime).format('YYYY-MM-DD HH:mm:ss') }))
      total.value = data?.total || 0
      updateTable()
    } else {
      message.error(res.errorMsg)
    }
  }).finally(() => {
    searching.value = false
  })
}
const resetSearch = () => {
  searchForm.dvsCode = null
  searchForm.dwLayerDetail = null
  searchForm.keyword = ''
  searchForm.pageQueryParam.pageNo = 1
  getRegionList()
}

const changePage = () => {
  getRegionList()
}

onMounted(() => {
  getRegionList()
  window.addEventListener('resize', updateTable)
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', updateTable)
})

const checkName = async (_rule: Rule, value: string) => {
  if (!value) {
    return Promise.reject('请填写数据域名称')
  }
  const reg = /^[a-zA-Z]\w{0,49}$/
  if (reg.test(value)) {
    return Promise.resolve()
  } else {
    return Promise.reject('以英文开头，由数字、英文或者下划线组成的字符串，且长度在50以内')
  }
}
const checkAlias = async (_rule: Rule, value: string) => {
  if (!value) {
    return Promise.reject('请填写数据域别名')
  }
  if (value.length <= 50) {
    return Promise.resolve()
  } else {
    return Promise.reject('请填写长度在50以内的字符')
  }
}
const checkAbbr = async (_rule: Rule, value: string) => {
  if (!value) {
    return Promise.reject('请填写数据域简称')
  }
  const reg = /^[a-zA-Z]\w{0,19}$/
  if (reg.test(value)) {
    return Promise.resolve()
  } else {
    return Promise.reject('以英文开头，由数字、英文或者下划线组成的字符串，且长度在20以内')
  }
}
const rules: Record<string, Rule[]> = {
  regionName: [{ required: true, validator: checkName, trigger: 'blur' }],
  regionAlias: [{ required: true, validator: checkAlias, trigger: 'blur' }],
  regionAbbr: [{ required: true, validator: checkAbbr, trigger: 'blur' }],
  dataRegionCode: [{ required: true, message: '请填写数据域编码', trigger: 'blur' }],
  dvsCode: [{ required: true, message: '请选择数据空间编码', trigger: 'blur' }],
  env: [{ required: true, message: '请选择数据域环境', trigger: 'blur' }],
  dwLayer: [{ required: true, message: '请选择所属分层', trigger: 'blur' }],
  dwLayerDetail: [{ required: false, message: '请选择分层明细', trigger: 'blur' }],
}
interface Domain {
  dataRegionCode: string
  dataRegionId: number | null
  dvsCode: string | null
  description: string
  dwLayer: number | null
  dwLayerDetail: string | number | null
  env: number | null
  regionAbbr: string
  regionAlias: string
  regionName: string
  envMode?: number | string | null
}
const formRef = ref()
const form = reactive<Domain>({
  dataRegionCode: '',
  dataRegionId: null,
  dvsCode: null,
  description: '',
  envMode: 1,
  dwLayer: 1,
  dwLayerDetail: 'ODS',
  env: 0,
  regionAbbr: '',
  regionAlias: '',
  regionName: ''
})
const dwLayerDetailEnum: any = computed(() => {
  let r: any[] = []
  switch (form.dwLayer) {
    case 1: // ODS
      r = [{ value: '11', label: 'ODS' }]
      break;
    case 2: // DW
      r = [{ value: '21', label: 'DWD' }, { value: '22', label: 'DWS' }, { value: '23', label: 'DIM' }]
      break;
    case 3: // ADS
      r = [{ value: '31', label: 'MASTER' }, { value: '32', label: 'MODEL' }, { value: '33', label: 'LABEL' }, { value: '34', label: 'DM' }]
      break;

    default:
      break;
  }
  return r
})
const open = ref<boolean>(false)
const isEdit = ref<boolean>(false)
const loading = ref<boolean>(false)
const readonly = ref<boolean>(false)
const addRegionFn = () => {
  isEdit.value = false
  open.value = true
}
const isDelete = ref(false)
const deleting = ref(false)
const currentRefionId = ref('')
const handleDeleteOk = () => {
  deleting.value = true
  deleteRegion(currentRefionId.value).then((res: any) => {
    if (res.responseStatus === 200) {
      message.success(res.msg)
      getRegionList()
    } else {
      message.error(res.errorMsg)
    }
  }).finally(() => {
    deleting.value = false
    isDelete.value = false
  })
}
const changeDvsCode = (v: any) => {
  const dvs = spaceList.value.find((e: any) => e.dvsCode === v)
  form.envMode = dvs.envMode
}
const changeDwLayer = () => {
  form.dwLayerDetail = dwLayerDetailEnum.value[0]?.label || null
}
const deleteRegionFn = (record: any) => {
  currentRefionId.value = record.dataRegionId
  isDelete.value = true
}
const viewRegionFn = (record: any) => {
  console.log(record);
}
const editRegionFn = (record: any, isReadonly: boolean = false) => {
  readonly.value = isReadonly
  const {
    regionName,
    regionAlias,
    regionAbbr,
    dataRegionCode,
    dataRegionId,
    dvsCode,
    description,
    dwLayer,
    dwLayerDetail,
    env,
    envMode
  } = record
  Object.assign(form, {
    regionName,
    regionAlias,
    regionAbbr,
    dataRegionCode,
    dataRegionId,
    dvsCode,
    description,
    dwLayer,
    dwLayerDetail,
    env,
    envMode
  })
  isEdit.value = true
  open.value = true
}

const onClose = () => {
  open.value = false
  Object.assign(form, {
    dataRegionCode: '',
    dataRegionId: null,
    dvsCode: null,
    description: '',
    envMode: 1,
    dwLayer: 1,
    dwLayerDetail: 'ODS',
    env: 0,
    regionAbbr: '',
    regionAlias: '',
    regionName: ''
  })
  formRef.value.clearValidate()
}
const onOk = () => {
  loading.value = true
  formRef.value
    .validate()
    .then(() => {
      submitFn()
    })
    .catch(() => {
      loading.value = false
    })
}
const submitFn = () => {
  const action = isEdit.value ? editRegion : addRegion
  const {
    regionName,
    regionAlias,
    regionAbbr,
    dataRegionCode,
    dataRegionId,
    dvsCode,
    description,
    dwLayer,
    dwLayerDetail,
    env,
    envMode
  } = form
  const editParams = {
    regionName,
    regionAlias,
    regionAbbr,
    dataRegionCode,
    dataRegionId,
    dvsCode,
    description,
    dwLayer,
    dwLayerDetail,
    env
  }
  const addParams = envMode === 1 ? [{ ...editParams }] : [{ ...editParams, env: 1 }, { ...editParams, env: 2 }]
  const params = isEdit.value ? [editParams] : addParams
  action(params)
    .then((res: any) => {
      if (res.responseStatus === 200) {
        onClose()
        getRegionList()
      } else {
        message.error(res.errorMsg)
      }
    })
    .finally(() => {
      loading.value = false
    })
}
</script>

<style scoped lang="less">
.block-t {
  padding: 10px 20px;
  line-height: 32px;
  border-bottom: 1px solid var(--border-color);

  .tit {
    font-size: 16px;
  }
  .btns{display: flex;}
}

.block-c {
  min-height: 200px;
  padding: 0 20px;
  margin-top: 10px;
}
</style>
