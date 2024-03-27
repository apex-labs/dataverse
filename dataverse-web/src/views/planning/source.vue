<template>
  <div class="g-card flex fdc ovh">
    <div class="fxbw block-t aic">
      <h4 class="tit">数据源</h4>
      <div class="btns">
        <a-button type="primary" @click="addSourceFn()">新增数据源</a-button>
      </div>
    </div>
    <div class="block-c flex1 flex fdc">
      <div class="search flex mb10">
        <a-form layout="inline" :model="searchForm" :colon="false">
          <a-form-item>
            <a-input v-model:value="searchForm.keyword" v-trim placeholder="数据源名称或简称" allowClear> </a-input>
          </a-form-item>
          <a-form-item>
            <a-select v-model:value="searchForm.dataSourceTypeId" placeholder="数据源类型" allowClear style="width:150px">
              <a-select-option :value="t.datasourceTypeId" v-for="(t, i) in mapList" :key="i">{{ t.datasourceTypeName
                }}</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item>
            <a-select v-model:value="searchForm.dataSourceReadAndWrite" placeholder="读写权限" allowClear
              style="width:100px">
              <a-select-option :value="1">只读</a-select-option>
              <a-select-option :value="2">读写</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="getSourceList"> 查询 </a-button>
            <a-button class="ml10" @click="resetSearch"> 重置 </a-button>
          </a-form-item>
        </a-form>
      </div>
      <a-spin :spinning="searching" tip="Loading..." wrapperClassName="source-list flex1 ovh">
        <a-table class="result-table" :dataSource="list" :columns="columns" :scroll="scrollOptions" :pagination="false"
          :showSorterTooltip="false">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'envMode'">
              <span>
                {{ envModes[record.envMode] }}
              </span>
            </template>
            <template v-if="column.key === 'datasourceReadWrite'">
              <span>
                {{ readWrite[record.datasourceReadWrite] }}
              </span>
            </template>
            <template v-if="column.key === 'action'">
              <span>
                <a class="tbtn" @click="editSourceFn(record)">编辑</a>
              </span>
            </template>
          </template>
        </a-table>
      </a-spin>
      <div class="ar p10">
        <a-pagination v-model:current="searchForm.pageQueryParam.pageNo"
          v-model:pageSize="searchForm.pageQueryParam.size" show-size-changer :total="total" @change="changePage" />
      </div>
    </div>

    <Drawer :width="500" :title="`${isEdit ? '编辑' : '新增'}数据源`" placement="right" :open="open" :loading="loading"
      @close="onClose" @ok="onOk">
      <a-form ref="formRef" :model="form" :rules="rules" :labelCol="{ span: 7 }" autocomplete="off">
        <a-form-item label="数据源名称" name="datasourceName">
          <a-input v-model:value="form.datasourceName" v-trim placeholder="请输入"> </a-input>
        </a-form-item>
        <a-form-item label="数据源简称" name="datasourceAbbr">
          <a-input v-model:value="form.datasourceAbbr" placeholder="请输入"> </a-input>
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
        <a-form-item label="环境模式" name="envMode">
          <a-radio-group v-model:value="form.envMode" disabled>
            <a-radio :value="item.value" v-for="(item, i) in modelEnum" :key="'mode_' + i">{{ item.desc
              }}</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="读写权限" name="datasourceReadWrite">
          <a-radio-group v-model:value="form.datasourceReadWrite" @change="changeReadWrite(form.datasourceReadWrite)">
            <a-radio :value="1">只读</a-radio>
            <a-radio :value="2">读写</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="数据源类型" name="datasourceTypeId">
          <a-select v-model:value="form.datasourceTypeId" placeholder="请选择" @change="changeDatasourceType">
            <a-select-option :value="t.datasourceTypeId" v-for="(t, i) in mapList" :key="i">{{ t.datasourceTypeName
              }}</a-select-option>
          </a-select>
        </a-form-item>

        <template v-if="form.dvsCode">
          <div v-for="(param, i) in form.datasourceParamList" :key="i">
            <a-divider />
            <p class="pl10 mb10"><b>{{ param.env === 0 ? 'BASIC' : param.env === 1 ? 'DEV' : 'PROD' }}</b></p>
            <a-form-item :label="`数据源实例请求`" class="mb10">
              {{ envs[param.env] }}
            </a-form-item>
            <!-- <a-form-item label="数据源链接类型" :name="['datasourceParamList', i, 'connType']" required>
              <a-radio-group v-model:value="param.connType" @change="changeConnType">
                <a-radio :value="1">JDBC数据源</a-radio>
                <a-radio :value="2">kafka数据源</a-radio>
              </a-radio-group>
            </a-form-item> -->
            <template v-if="param.connType === 1">
              <a-form-item label="JDBC链接URL" :name="['datasourceParamList', i, 'jdbcSourceParam', 'jdbcUrl']"
                :rules="[{ required: true, message: '请填写JDBC链接URL', trigger: 'blur' }]">
                <a-input v-model:value="param.jdbcSourceParam.jdbcUrl" placeholder="请输入">
                </a-input>
              </a-form-item>
              <a-form-item label="JDBC链接用户名" :name="['datasourceParamList', i, 'jdbcSourceParam', 'userName']"
                :rules="[{ required: true, message: '请填写JDBC链接用户名', trigger: 'blur' }]">
                <a-input v-model:value="param.jdbcSourceParam.userName" placeholder="请输入">
                </a-input>
              </a-form-item>
              <a-form-item label="JDBC链接密码" :name="['datasourceParamList', i, 'jdbcSourceParam', 'password']"
                :rules="[{ required: true, message: '请填写JDBC链接密码', trigger: 'blur' }]">
                <a-input type="password" v-model:value="param.jdbcSourceParam.password" placeholder="请输入">
                </a-input>
              </a-form-item>
              <a-form-item label="链接配置JSON">
                <a-textarea v-model:value="param.jdbcSourceParam.connConfig" auto-size placeholder="请输入">
                </a-textarea>
              </a-form-item>
              <a-form-item label="JDB实例描述">
                <a-textarea v-model:value="param.jdbcSourceParam.description" :maxlength="200"
                  placeholder="请输入说明（200字以内）">
                </a-textarea>
              </a-form-item>
            </template>
          </div>
        </template>

        <a-divider />

        <a-form-item :wrapper-col="{ offset: 7 }">
          <a-button :loading="testing" @click="testSourceLink">{{ testing ? '测试中' : '测试链接' }}</a-button>
        </a-form-item>
      </a-form>
    </Drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount, toRefs } from 'vue'
import { message } from 'ant-design-vue'
import type { Rule } from 'ant-design-vue/es/form';
import { sourceList, addSource, editSource, sourceLink } from '@/api/modules/planning'
import Drawer from '@/components/Drawer.vue'
import { useDvsStore } from '@/stores/modules/user'
import { cloneDeep } from 'lodash-es'
const dvsStore = useDvsStore()
const { mapList, spaceList, modelEnum } = toRefs(dvsStore)

const envModes: any = {
  1: 'BASIC', 2: 'DEV-PROD'
}
const envs: any = {
  0: 'BASIC', 1: 'DEV', 2: 'PROD'
}
const readWrite: any = {
  1: '只读', 2: '读写'
}

const searchForm = reactive({
  dataSourceReadAndWrite: null,
  dataSourceTypeId: null,
  keyword: '',
  pageQueryParam: {
    ascs: [],
    descs: ['createTime'],
    pageNo: 1,
    size: 10
  }
})
const scrollOptions = reactive({
  x: 'max-content',
  y: 1000
})
const updateTable = () => {
  const obj: HTMLElement | null = document.querySelector('.source-list')
  scrollOptions.y = (obj && obj.offsetHeight) ? (obj.offsetHeight - 50) : 1000
}
const total = ref(0)
const searching = ref(false)
const list = ref<any[]>([])
const columns = ref<any[]>([
  {
    title: '名称',
    dataIndex: 'datasourceName',
    key: 'datasourceName',
    ellipsis: true,
  },
  {
    title: '简称',
    dataIndex: 'datasourceAbbr',
    key: 'datasourceAbbr',
    ellipsis: true,
  },
  {
    title: '编码',
    dataIndex: 'datasourceCode',
    key: 'datasourceCode',
    ellipsis: true,
  },
  {
    title: '类型',
    key: 'datasourceTypeName',
    dataIndex: 'datasourceTypeName',
    ellipsis: true,
  },
  {
    title: '环境',
    key: 'envMode',
    dataIndex: 'envMode',
    ellipsis: true,
  },
  {
    title: '权限',
    key: 'datasourceReadWrite',
    dataIndex: 'datasourceReadWrite',
    ellipsis: true,
  },
  {
    title: '操作',
    key: 'action',
  },
])
const getSourceList = () => {
  searching.value = true
  const params = { ...searchForm }
  sourceList(params).then((res: any) => {
    if (res.responseStatus === 200) {
      const data: any = res.data
      list.value = data?.list || []
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
  searchForm.dataSourceReadAndWrite = null
  searchForm.dataSourceTypeId = null
  searchForm.keyword = ''
  searchForm.pageQueryParam.pageNo = 1
  getSourceList()
}
const changePage = (page: number, pageSize: number) => {
  getSourceList()
}
onMounted(() => {
  getSourceList()
  window.addEventListener('resize', updateTable)
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', updateTable)
})

const checkName = async (_rule: Rule, value: string) => {
  if (!value) {
    return Promise.reject('请填写数据源名称')
  }
  const reg = /^[a-zA-Z]\w{0,49}$/
  if (reg.test(value)) {
    return Promise.resolve()
  } else {
    return Promise.reject('以英文开头，由数字、英文或者下划线组成的字符串，且长度在50以内')
  }
}
const checkAbbr = async (_rule: Rule, value: string) => {
  if (!value) {
    return Promise.reject('请填写数据源简称');
  }
  const reg = /^[a-zA-Z]\w{0,19}$/
  if (reg.test(value)) {
    return Promise.resolve()
  } else {
    return Promise.reject('以英文开头，由数字、英文或者下划线组成的字符串，且长度在20以内')
  }
}
const rules: Record<string, Rule[]> = {
  datasourceName: [{ required: true, validator: checkName, trigger: 'blur' }],
  datasourceAbbr: [{ required: true, validator: checkAbbr, trigger: 'blur' }],
  dvsCode: [{ required: true, message: '请选择数据空间编码', trigger: 'blur' }],
  datasourceTypeId: [{ required: true, message: '请选择数据源类型', trigger: 'blur' }],
  datasourceReadWrite: [{ required: true, message: '请选择数据源读写权限', trigger: 'change' }],
  envMode: [{ required: true, message: '请选择数据源环境', trigger: 'blur' }],
  datasourceParamList: [{ required: true, message: '请增加实例', trigger: 'blur' }]
};
const formRef = ref()
const form = reactive({
  datasourceAbbr: '',
  datasourceCode: '',
  datasourceName: '',
  datasourceParamList: [
    {
      connType: 1,
      datasourceAbbr: '',
      datasourceCode: '',
      datasourceId: null,
      datasourceName: '',
      datasourceReadWrite: 1,
      datasourceTypeId: null,
      datasourceTypeName: '',
      dvsCode: '',
      env: 0,
      jdbcSourceParam: {
        connConfig: '',
        datasourceCode: '',
        datasourceName: '',
        description: '',
        env: 0,
        jdbcSourceId: null,
        jdbcUrl: '',
        password: '',
        userName: ''
      },
      kafkaSourceParam: {
        datasourceCode: '',
        datasourceName: '',
        env: 0,
        kafkaSourceId: null
      }
    }
  ] as any,
  datasourceReadWrite: 1,
  datasourceTypeId: null,
  datasourceTypeName: '',
  dvsCode: null,
  envMode: 1,
  parentId: null
})
const open = ref<boolean>(false)
const isEdit = ref<boolean>(false)
const loading = ref<boolean>(false)
const addSourceFn = (map?: any) => {
  if (map) {
    form.datasourceTypeId = map.datasourceTypeId
    form.datasourceTypeName = map.datasourceTypeName
  }
  isEdit.value = false
  open.value = true
}
defineExpose({ addSourceFn }) // 暴露给父组件调用
const editSourceFn = (record: any) => {
  const { datasourceAbbr, datasourceCode, datasourceName, datasourceVOList, datasourceReadWrite, datasourceTypeId, datasourceTypeName, dvsCode, envMode, parentId } = record
  form.datasourceParamList = datasourceVOList.map(({ connType, env, jdbcSourceVO, kafkaSourceVO }: any) => ({ datasourceAbbr, datasourceCode, datasourceName, datasourceReadWrite, datasourceTypeId, datasourceTypeName, dvsCode, connType, env, jdbcSourceParam: jdbcSourceVO, kafkaSourceParam: kafkaSourceVO }))
  Object.assign(form, { datasourceAbbr, datasourceCode, datasourceName, datasourceReadWrite, datasourceTypeId, datasourceTypeName, dvsCode, envMode, parentId })
  isEdit.value = true
  open.value = true
}

const changeDvsCode = (v: any) => {
  const dvs = spaceList.value.find((e: any) => e.dvsCode === v)
  form.envMode = dvs.envMode
  changeModel(dvs.envMode)
}
const changeModel = (v: any) => {
  const { datasourceAbbr, datasourceCode, datasourceName, datasourceReadWrite, datasourceTypeId, datasourceTypeName, dvsCode } = form
  if (v === 1) {
    form.datasourceParamList = [
      {
        datasourceAbbr, datasourceCode, datasourceName, datasourceReadWrite, datasourceTypeId, datasourceTypeName, dvsCode,
        datasourceId: null,
        connType: 1,
        env: 0,
        jdbcSourceParam: {
          datasourceCode, datasourceName,
          connConfig: '',
          description: '',
          env: 0,
          jdbcSourceId: null,
          jdbcUrl: '',
          password: '',
          userName: ''
        },
        kafkaSourceParam: {
          datasourceCode, datasourceName,
          env: 0,
          kafkaSourceId: null
        }
      }
    ]
  } else {
    form.datasourceParamList = [
      {
        datasourceAbbr, datasourceCode, datasourceName, datasourceReadWrite, datasourceTypeId, datasourceTypeName, dvsCode,
        datasourceId: null,
        connType: 1,
        env: 1,
        jdbcSourceParam: {
          datasourceCode, datasourceName,
          connConfig: '',
          description: '',
          env: 1,
          jdbcSourceId: null,
          jdbcUrl: '',
          password: '',
          userName: ''
        },
        kafkaSourceParam: {
          datasourceCode, datasourceName,
          env: 1,
          kafkaSourceId: null
        }
      },
      {
        datasourceAbbr, datasourceCode, datasourceName, datasourceReadWrite, datasourceTypeId, datasourceTypeName, dvsCode,
        datasourceId: null,
        connType: 1,
        env: 2,
        jdbcSourceParam: {
          datasourceCode, datasourceName,
          connConfig: '',
          description: '',
          env: 2,
          jdbcSourceId: null,
          jdbcUrl: '',
          password: '',
          userName: ''
        },
        kafkaSourceParam: {
          datasourceCode, datasourceName,
          env: 2,
          kafkaSourceId: null
        }
      }
    ]
  }
}
const changeReadWrite = (v: any) => {
  form.datasourceParamList = form.datasourceParamList.map((e: any) => ({ ...e, datasourceReadWrite: v }))
}
const changeDatasourceType = (v: any) => {
  const map = mapList.value.find((e: any) => e.datasourceTypeId === v)
  // 14:kafka 15:pulsar 
  form.datasourceParamList.forEach((e: any) => {
    e.connType = [14].includes(v) ? 2 : 1
  })
  form.datasourceTypeName = map.datasourceTypeName
}
const onClose = () => {
  open.value = false
  Object.assign(form, {
    datasourceAbbr: '',
    datasourceCode: '',
    datasourceName: '',
    datasourceParamList: [
      {
        connType: 1,
        datasourceAbbr: '',
        datasourceCode: '',
        datasourceId: null,
        datasourceName: '',
        datasourceReadWrite: 1,
        datasourceTypeId: null,
        datasourceTypeName: '',
        dvsCode: '',
        env: 0,
        jdbcSourceParam: {
          connConfig: '',
          datasourceCode: '',
          datasourceName: '',
          description: '',
          env: 0,
          jdbcSourceId: null,
          jdbcUrl: '',
          password: '',
          userName: ''
        },
        kafkaSourceParam: {
          datasourceCode: '',
          datasourceName: '',
          env: 0,
          kafkaSourceId: null
        }
      }
    ] as any,
    datasourceReadWrite: 1,
    datasourceTypeId: null,
    datasourceTypeName: '',
    dvsCode: null,
    envMode: 1,
    parentId: null
  })
  formRef.value.clearValidate()
}
const onOk = () => {
  loading.value = true
  formRef.value.validate().then(() => {
    submitFn()
  }).catch(() => {
    loading.value = false
  })
}
const submitFn = () => {
  const params = initParams()
  const action = isEdit.value ? editSource : addSource
  action(params)
    .then((res: any) => {
      if (res.responseStatus === 200) {
        console.log(res)
        open.value = false
        getSourceList()
      } else {
        message.error(res.errorMsg)
      }
    })
    .finally(() => {
      loading.value = false
    })
}
const initParams = () => {
  const params = cloneDeep(form)
  const { datasourceParamList, datasourceTypeId, datasourceTypeName, datasourceCode, datasourceName, datasourceReadWrite, datasourceAbbr } = params
  params.datasourceParamList = datasourceParamList.map((e: any) => {
    if (e.connType === 1) {
      e.jdbcSourceParam = { ...e.jdbcSourceParam, datasourceCode, datasourceName, env: e.env }
      delete e.kafkaSourceParam
    }
    if (e.connType === 2) {
      e.kafkaSourceParam = { ...e.jdbcSourceParam, datasourceCode, datasourceName, env: e.env }
      delete e.jdbcSourceParam
    }
    e = { ...e, datasourceTypeId, datasourceTypeName, datasourceCode, datasourceName, datasourceReadWrite, datasourceAbbr }
    return e
  })
  return params
}
const changeConnType = (v: any) => {
  if (v === 1) {
    // JDBC
    form.datasourceParamList[0].jdbcSourceParam = {
      connConfig: '',
      datasourceCode: '',
      datasourceName: '',
      description: '',
      env: 0,
      jdbcSourceId: null,
      jdbcUrl: '',
      password: '',
      userName: ''
    }
  } else {
    // ODPC
    form.datasourceParamList[0].kafkaSourceParam = {
      datasourceCode: '',
      datasourceName: '',
      env: 0,
      kafkaSourceId: null
    }
  }
}

const testing = ref<boolean>(false)
const testSourceLink = () => {
  const params = initParams()
  testing.value = true
  formRef.value.validate().then(() => {
    sourceLink(params).then((res: any) => {
      if (res.responseStatus === 200) {
        const data = res.data || []
        let msg = '链接成功'
        const fail = data.filter((e: any) => e.result === 2)
        if (fail.length > 0) {
          msg = fail.map((e: any) => e.instanceName).join('、') + '链接失败'
          message.error(msg)
        } else {
          message.success(msg)
        }
      } else {
        message.error('链接失败: ' + res.errorMsg)
      }
    }).catch((err) => {
      console.log('err', err);
    }).finally(() => {
      testing.value = false
    })
  }).catch(() => {
    testing.value = false
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
