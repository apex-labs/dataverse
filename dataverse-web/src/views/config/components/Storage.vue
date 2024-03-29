<template>
  <div class="h100 flex flex1 fdc ovh">
    <div class="fxbw aic pt10 pl20 pr20 page-tit">
      <h4 class="tit">存储区</h4>
      <div class="btns">
        <a-button type="primary" @click="addStorageFn">新增存储区</a-button>
      </div>
    </div>
    <div class="list flex1 ova">
      <div class="storage-list flex ffrw">
        <div class="storage-item flex fdc" v-for="(item, i) in list" :key="i">
          <div class="storage-item-header fxbw aic">
            <div class="flex aic">
              <SvgIcon name="database" class="mr7" /><span>{{ item.storageAlias }}</span>
            </div>
            <a-button class="p0 m0 edit" type="link" @click="editStorageFn(item)">
              <EditOutlined />
            </a-button>
          </div>
          <div class="storage-item-body flex1 flex fdc">
            <div class="h100 flex ffrw p10">
              <div class="item w100">存储区ID：{{ item.storageId }}</div>
              <div class="item w50">别名：{{ item.storageAlias }}</div>
              <div class="item w50">创建人：{{ item.creatorName }}</div>
              <div class="item w50">名称：{{ item.storageName }}</div>
              <div class="item w50">创建时间：{{ item.createTime }}</div>
              <div class="item w50">简称：{{ item.storageAbbr }}</div>
              <div class="item w50">更新时间：{{ item.updateTime }}</div>
              <div class="item w50">存储区类型：{{ item.storageType }}</div>
              <div class="item w50">引擎类型：{{ item.engineType }}</div>
              <div class="item w100">存储区链接方式：{{ item.connType }}</div>
              <div class="item w100">描述：{{ item.description || '-' }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="ar p10">
      <a-pagination v-model:current="searchForm.pageQueryParam.pageNo" v-model:pageSize="searchForm.pageQueryParam.size"
        show-quick-jumper :total="total" @change="onChange" />
    </div>

    <Drawer :width="500" :title="`${readonly ? '查看' : isEdit ? '编辑' : '新增'}存储区`" placement="right" :open="open"
      :loading="loading" @close="onClose" @ok="onOk">
      <a-form ref="formRef" :model="form" :rules="rules" :labelCol="{ span: 7 }" autocomplete="off"
        :disabled="readonly">
        <a-form-item label="存储区名称" name="storageName">
          <a-input v-model:value="form.storageName" v-trim placeholder="ex: english_name"> </a-input>
        </a-form-item>
        <a-form-item label="存储区别名" name="storageAlias">
          <a-input v-model:value="form.storageAlias" v-trim placeholder="ex: 中文名称"> </a-input>
        </a-form-item>
        <a-form-item label="存储区简称" name="storageAbbr">
          <a-input v-model:value="form.storageAbbr" v-trim placeholder="ex: en_for_short"> </a-input>
        </a-form-item>
        <a-form-item label="存储区类型" name="storageType">
          <a-select v-model:value="form.storageType" placeholder="请选择" @change="changeStorageType">
            <a-select-option :value="item.desc" v-for="(item, i) in storageTypeEnum" :key="i">{{ item.desc
              }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="引擎类型" name="engineType" v-if="form.storageType === 'HDFS'">
          <a-radio-group v-model:value="form.engineType">
            <a-radio :value="item.desc" v-for="(item, i) in engineTypeEnum" :key="i">{{ item.desc }}</a-radio>
          </a-radio-group>
        </a-form-item>
        <!-- 根据存储区类型变化，不可选 -->
        <!-- <a-form-item label="链接方式" name="connType">
          <a-radio-group v-model:value="form.connType">
            <a-radio :value="item.desc" v-for="(item, i) in connTypeEnum" :key="i">{{ item.desc }}</a-radio>
          </a-radio-group>
        </a-form-item> -->
        <template v-if="form.connType === 'JDBC'">
          <a-form-item label="JDBC驱动类" :name="['saveJdbcStorageParam', 'driverClass']" :rules="rules.driverClass">
            <a-input v-model:value="form.saveJdbcStorageParam.driverClass" placeholder="请输入">
            </a-input>
          </a-form-item>
          <!-- <a-form-item label="JDBC存储类型" name="saveJdbcStorageParam.storageType" :rules="rules.jdbcStorageType">
            <a-select v-model:value="form.saveJdbcStorageParam.storageType" placeholder="请选择">
              <a-select-option :value="item.desc" v-for="(item, i) in jdbcStorageTypeEnum" :key="i">{{ item.desc
                }}</a-select-option>
            </a-select>
          </a-form-item> -->
          <a-form-item label="JDBC链接URL" :name="['saveJdbcStorageParam', 'jdbcUrl']" :rules="rules.jdbcUrl">
            <a-input v-model:value="form.saveJdbcStorageParam.jdbcUrl" placeholder="请输入">
            </a-input>
          </a-form-item>
          <a-form-item label="JDBC链接用户名" :name="['saveJdbcStorageParam', 'userName']" :rules="rules.userName">
            <a-input v-model:value="form.saveJdbcStorageParam.userName" placeholder="请输入">
            </a-input>
          </a-form-item>
          <a-form-item label="JDBC链接密码" :name="['saveJdbcStorageParam', 'password']" :rules="rules.password">
            <a-input type="password" v-model:value="form.saveJdbcStorageParam.password" placeholder="请输入">
            </a-input>
          </a-form-item>
          <a-form-item label="链接配置JSON">
            <a-textarea v-model:value="form.saveJdbcStorageParam.connConfig" auto-size placeholder="请输入">
            </a-textarea>
          </a-form-item>
        </template>
        <template v-if="form.connType === 'ODPC'">
          <!-- <a-form-item label="ODPC存储类型" :name="['saveOdpcStorageParam', 'storageType']" :rules="rules.odpcStorageType">
            <a-select v-model:value="form.saveOdpcStorageParam.storageType" placeholder="请选择">
              <a-select-option :value="item.desc" v-for="(item, i) in storageTypeEnum" :key="i">{{ item.desc
              }}</a-select-option>
            </a-select>
          </a-form-item> -->
          <a-form-item label="存储路径" :name="['saveOdpcStorageParam', 'storagePath']" :rules="rules.storagePath">
            <a-input v-model:value="form.saveOdpcStorageParam.storagePath" placeholder="请输入">
            </a-input>
          </a-form-item>
        </template>
        <a-form-item label="存储区描述" name="description">
          <a-textarea v-model:value="form.description" :maxlength="200" placeholder="请输入说明（200字以内）">
          </a-textarea>
        </a-form-item>
      </a-form>
    </Drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import type { Rule } from 'ant-design-vue/es/form'
import Drawer from '@/components/Drawer.vue'
import { storageList, addStorage, editStorage, storageTypeList, engineTypeList, connTypeList } from '@/api/modules/config'
import dayjs from 'dayjs'
import { cloneDeep } from 'lodash-es'
import { message } from 'ant-design-vue'

const jdbcStorageTypeEnum = ref<any[]>([
  { value: 1, desc: 'HDFS' },
  // { value: 2, desc: 'MYSQL' },
  // { value: 3, desc: 'PGSQL' },
  // { value: 4, desc: 'ORACLE' },
  // { value: 5, desc: 'DORIS' },
  // { value: 6, desc: 'STAR_ROCKS' },
  // { value: 7, desc: 'CLICKHOUSE' },
  // { value: 8, desc: 'ELASTICSEARCH' }
])
const storageTypeEnum = ref<any[]>([{ value: 0, desc: "HDFS" }])
const getStorageTypeList = () => {
  storageTypeList().then(res => {
    storageTypeEnum.value = res.data || []
  })
}
getStorageTypeList()

const engineTypeEnum = ref<any[]>([])
const getEngineTypeList = () => {
  engineTypeList().then(res => {
    engineTypeEnum.value = res.data || []
  })
}
getEngineTypeList()

const connTypeEnum = ref<any[]>([])
const getConnTypeList = () => {
  connTypeList().then(res => {
    connTypeEnum.value = res.data || []
  })
}
getConnTypeList()

const searchForm = reactive({
  storageName: '',
  storageType: null,
  connType: null,
  engineType: null,
  pageQueryParam: {
    ascs: [
    ],
    descs: [
    ],
    pageNo: 1,
    size: 10
  }
})
const scrollOptions = reactive({
  x: 'max-content',
  y: 1000
})
const total = ref(0)
const list = ref<any[]>([])
const getStorageList = () => {
  const params = { ...searchForm }
  storageList(params).then(res => {
    const data: any = res.data
    list.value = (data?.list || []).map((e: any) => ({ ...e, createTime: dayjs(e.createTime).format('YYYY-MM-DD HH:mm:ss'), updateTime: dayjs(e.updateTime).format('YYYY-MM-DD HH:mm:ss') }))
    total.value = data?.total || 0
  })
  updateTable()
}

const updateTable = () => {
  const obj: HTMLElement | null = document.querySelector('.list')
  scrollOptions.y = (obj && obj.offsetHeight) || 1000
}
onMounted(() => {
  getStorageList()
})

const checkName = async (_rule: Rule, value: string) => {
  if (!value) {
    return Promise.reject('请填写存储区名称')
  }
  const reg = /^[a-zA-Z]{1,20}$/
  if (reg.test(value)) {
    return Promise.resolve()
  } else {
    return Promise.reject('只能输入英文，且长度在20以内')
  }
}
const checkAlias = async (_rule: Rule, value: string) => {
  if (!value) {
    return Promise.reject('请填写存储区别名')
  }
  if (value.length <= 50) {
    return Promise.resolve()
  } else {
    return Promise.reject('请填写长度在50以内的字符')
  }
}
const checkAbbr = async (_rule: Rule, value: string) => {
  if (!value) {
    return Promise.reject('请填写存储区简称')
  }
  const reg = /^[a-zA-Z]{1,20}$/
  if (reg.test(value)) {
    return Promise.resolve()
  } else {
    return Promise.reject('只能输入英文，且长度在20以内')
  }
}
const rules: Record<string, Rule[]> = {
  storageName: [{ required: true, validator: checkName, trigger: 'blur' }],
  storageAlias: [{ required: true, validator: checkAlias, trigger: 'blur' }],
  storageAbbr: [{ required: true, validator: checkAbbr, trigger: 'blur' }],
  storageType: [{ required: true, message: '请选择存储区类型', trigger: 'blur' }],
  engineType: [{ required: true, message: '请选择引擎类型', trigger: 'blur' }],
  descripton: [{ max: 200, message: '请输入200长度以内的字符', trigger: 'blur' }],
  connType: [{ required: true, message: '请选择存储区链接方式', trigger: 'blur' }],
  driverClass: [{ required: true, message: '请填写JDBC驱动类', trigger: 'blur' }],
  jdbcStorageType: [{ required: true, message: '请选择JDBC存储类型', trigger: 'blur' }],
  jdbcUrl: [{ required: true, message: '请填写JDBC链接URL', trigger: 'blur' }],
  userName: [{ required: true, message: '请填写JDBC链接用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请填写JDBC链接密码', trigger: 'blur' }],
  odpcStorageType: [{ required: true, message: '请选择ODPC类型', trigger: 'blur' }],
  storagePath: [{ required: true, message: '请输入ODPC存储路径', trigger: 'blur' }],
}
const formRef = ref()
const form = reactive({
  storageId: null,
  storageName: '',
  storageAlias: '',
  storageAbbr: '',
  storageType: null,
  engineType: null,
  connType: 'JDBC',
  saveJdbcStorageParam: {
    storageId: null,
    storageType: 'HDFS',  // 存储区类型,1:HDFS,2:MYSQL,3:PGSQL,4:ORACLE,5:DORIS,6:STAR_ROCKS,7:CLICKHOUSE,8:ELASTICSEARCH
    jdbcUrl: '',
    userName: '',
    password: '',
    driverClass: '',
    connConfig: ''
  },
  saveOdpcStorageParam: {
    storageId: null,
    storageType: 'HDFS',   // 存储区类型，0：HDFS，1：MySQL，2：MariaDB，3：Doris，4：ClickHouse，5：Oracle
    storagePath: ''
  },
  description: '',
})
const open = ref<boolean>(false)
const isEdit = ref<boolean>(false)
const loading = ref<boolean>(false)
const readonly = ref<boolean>(false)
const changeStorageType = (v: string) => {
  form.saveJdbcStorageParam.storageType = v
  form.connType = v === 'HDFS' ? 'ODPC' : 'JDBC'
  form.engineType = null
}
const addStorageFn = () => {
  isEdit.value = false
  open.value = true
}
const viewStorageFn = (record: any) => {
  const { storageId, storageAlias, storageName, storageAbbr, storageType, engineType, connType, description, jdbcStorageVO, odpcStorageVO } = record
  Object.assign(form, { storageId, storageAlias, storageName, storageAbbr, storageType, engineType, connType, description, saveJdbcStorageParam: jdbcStorageVO, saveOdpcStorageParam: odpcStorageVO })
  readonly.value = true
  open.value = true
}
const editStorageFn = (record: any) => {
  const { storageId, storageAlias, storageName, storageAbbr, storageType, engineType, connType, description, jdbcStorageVO, odpcStorageVO } = record
  Object.assign(form, { storageId, storageAlias, storageName, storageAbbr, storageType, engineType, connType, description, saveJdbcStorageParam: jdbcStorageVO, saveOdpcStorageParam: odpcStorageVO })
  isEdit.value = true
  open.value = true
}
const removeStorageFn = (index: number) => {
  list.value.splice(index, 1)
}

const onClose = () => {
  open.value = false
  readonly.value = false
  isEdit.value = false
  Object.assign(form, {
    storageId: null,
    storageName: '',
    storageAlias: '',
    storageAbbr: '',
    storageType: null,
    engineType: null,
    connType: 'JDBC',
    saveJdbcStorageParam: {
      storageId: null,
      storageType: 'HDFS',  // 存储区类型,1:HDFS,2:MYSQL,3:PGSQL,4:ORACLE,5:DORIS,6:STAR_ROCKS,7:CLICKHOUSE,8:ELASTICSEARCH
      jdbcUrl: '',
      userName: '',
      password: '',
      driverClass: '',
      connConfig: ''
    },
    saveOdpcStorageParam: {
      storageId: null,
      storageType: 'HDFS',   // 存储区类型，0：HDFS，1：MySQL，2：MariaDB，3：Doris，4：ClickHouse，5：Oracle
      storagePath: ''
    },
    description: '',
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
const initParams = () => {
  const params: any = cloneDeep(form)
  if (params.connType === 'JDBC') {
    delete params.saveOdpcStorageParam
  }
  if (params.connType === 'ODPC') {
    delete params.saveJdbcStorageParam
  }
  return params
}
const submitFn = () => {
  const action = isEdit.value ? editStorage : addStorage
  const params = initParams()
  action(params)
    .then((res: any) => {
      if (res.responseStatus === 200) {
        open.value = false
        getStorageList()
      } else {
        message.error(res.errorMsg)
      }
    })
    .finally(() => {
      loading.value = false
    })
}

const onChange = (current: number, pageSize: number) => {
  console.log('Page: ', current, pageSize)
}


</script>

<style scoped lang="less">
.page-tit {
  padding: 10px 20px;
  line-height: 32px;
  border-bottom: 1px solid var(--border-color);

  .tit {
    font-size: 16px;
  }

  .btns {
    display: flex;
  }
}

.list {
  padding: 20px 10px;
}

.storage-list {

  .storage-item {
    width: 31.33333%;
    margin: 0 1% 2%;
    min-width: 400px;
    font-size: 12px;
    box-shadow: 0px 0px 4px 1px var(--bg-color-2);
    border-radius: 4px;
    border: 1px solid var(--border-color);
    background-color: var(--bg-color-2);

    &-header {
      height: 40px;
      line-height: 40px;
      font-size: 14px;
      padding: 0 10px;
      background: var(--bg-color-2-5);

      .edit {
        padding: 0;
        margin: 0;
        display: none;
        color: var(--text-color-1);
      }
    }

    &:hover {
      .edit {
        display: block;
      }
    }

    &-body {
      min-height: 100px;
      padding: 0 10px;

      .item {
        line-height: 1.6;
      }
    }

    &-footer {
      justify-content: space-around;
      border-top: 1px solid var(--border-color);
      line-height: 40px;
      margin: 0 10px;

      .btn {
        cursor: pointer;
      }
    }
  }
}
</style>
