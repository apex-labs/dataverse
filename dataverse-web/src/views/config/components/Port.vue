<template>
  <div class="h100 flex flex1 fdc ovh">
    <div class="fxbw aic pt10 pl20 pr20 page-tit">
      <h4 class="tit">PORT</h4>
      <div class="btns">
        <!-- <a-button type="primary" @click="addPortFn">新增PORT</a-button> -->
      </div>
    </div>
    <div class="list flex1 ova">
      <div class="storage-list flex ffrw">
        <div class="storage-item flex fdc" v-for="(item, i) in list" :key="i">
          <div class="storage-item-header fxbw aic">
            <div class="flex aic toe">
              <SvgIcon name="database" class="mr7" /><span class="toe">{{ item.portName }}</span>
            </div>
            <a-space class="" style="white-space: nowrap;">
              <a-tooltip title="添加授权的存储区" placement="top">
                <a class="tbtn" @click="addPortStorageFn(item)">添加</a>
              </a-tooltip>
              <a-tooltip title="移除授权的存储区" placement="top">
                <a class="tbtn" @click="removePortStorageFn(item)">移除</a>
              </a-tooltip>
            </a-space>
          </div>
          <div class="storage-item-body flex1 flex fdc">
            <div class="h100 flex ffrw p10">
              <div class="item w100">PortID：{{ item.portId }}</div>
              <div class="item w50">Port名称：{{ item.portName }}</div>
              <div class="item w50">Port状态：{{ item.state }}</div>
              <div class="item w50">主机名：{{ item.hostname }}</div>
              <div class="item w50">心跳频率(ms)：{{ item.heartbeatHz }}</div>
              <div class="item w50">最大链接：{{ item.maxDriverConns }}</div>
              <div class="item w50">已链接：{{ item.connectedDrivers }}</div>
              <div class="item w50">创建人：{{ item.creatorName }}</div>
              <div class="item w50">创建时间：{{ item.createTime }}</div>
              <div class="item w100">更新时间：{{ item.updateTime }}</div>
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

    <Drawer :width="500" :title="`${isAdd ? '添加' : '移除'}授权的存储区`" placement="right" :open="open" :loading="loading"
      @close="onClose" @ok="onOk">
      <a-form ref="formRef" :model="form" :rules="rules" :labelCol="{ span: 24 }" autocomplete="off">
        <div class="flex">
          <a-form-item label="最小链接数" name="minJdbcConns" class="flex1">
            <a-input-number v-model:value="form.minJdbcConns" placeholder="请填写" :min="1" :max="maxJdbcConns" />
          </a-form-item>
          <a-form-item label="最大链接数" name="maxJdbcConns" class="flex1">
            <a-input-number v-model:value="form.maxJdbcConns" placeholder="请填写" :min="minJdbcConns" />
          </a-form-item>
        </div>
        <div class="flex">
          <a-form-item label="最小引擎数" name="minOdpcEngines" class="flex1">
            <a-input-number v-model:value="form.minOdpcEngines" placeholder="请填写" :min="1" :max="maxOdpcEngines" />
          </a-form-item>
          <a-form-item label="最大引擎数" name="maxOdpcEngines" class="flex1">
            <a-input-number v-model:value="form.maxOdpcEngines" placeholder="请填写" :min="minOdpcEngines" />
          </a-form-item>
        </div>
        <a-form-item :label="isAdd ? '添加存储区' : '移除存储区'" name="targetKeys">
          <a-transfer v-model:target-keys="form.targetKeys" :data-source="canUseStorageData" show-search
            :titles="['', '已选']" :filter-option="filterOption" :render="(item: any) => item.title"
            @change="handleChange" @search="handleSearch" />
        </a-form-item>
      </a-form>
    </Drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import type { Rule } from 'ant-design-vue/es/form'
import Drawer from '@/components/Drawer.vue'
import { storageList, listPort, addStoragePort, removeStoragePort, detailPort } from '@/api/modules/config'
import dayjs from 'dayjs'
import { cloneDeep } from 'lodash-es'
import { message } from 'ant-design-vue'

const searchForm = reactive({
  groupName: null,
  groupCode: null,
  pageQueryParam: {
    ascs: [
    ],
    descs: [
    ],
    pageNo: 1,
    size: 20
  }
})
const total = ref(0)
const list = ref<any[]>([])
const currentPort = reactive<any>({})
const getPortList = () => {
  const params = { ...searchForm }
  listPort(params).then(res => {
    const data: any = res.data
    list.value = (data?.list || []).map((e: any) => ({ ...e, createTime: dayjs(e.createTime).format('YYYY-MM-DD HH:mm:ss'), updateTime: dayjs(e.updateTime).format('YYYY-MM-DD HH:mm:ss') }))
    total.value = data?.total || 0
  })
}
onMounted(() => {
  getPortList()
})
// pagination change
const onChange = () => {
  getPortList()
}

// transfer
const form = reactive({
  maxJdbcConns: 2, maxOdpcEngines: 2, minJdbcConns: 1, minOdpcEngines: 1, targetKeys: [] as any
})
const maxJdbcConns = computed(() => form.maxJdbcConns - 1)
const minJdbcConns = computed(() => form.minJdbcConns + 1)
const maxOdpcEngines = computed(() => form.maxOdpcEngines - 1)
const minOdpcEngines = computed(() => form.minOdpcEngines + 1)
const rules: Record<string, Rule[]> = {
  maxJdbcConns: [{ required: true, message: '请填写最大链接数', trigger: 'blur' }],
  minJdbcConns: [{ required: true, message: '请填写最小链接数', trigger: 'blur' }],
  maxOdpcEngines: [{ required: true, message: '请填写最大引擎数', trigger: 'blur' }],
  minOdpcEngines: [{ required: true, message: '请填写最小引擎数', trigger: 'blur' }],
  targetKeys: [{ required: true, message: '请选择存储区', trigger: 'change' }],
}
const formRef = ref()
const filterOption = (inputValue: string, option: any) => {
  return option.title.indexOf(inputValue) > -1;
};
const handleChange = (keys: string[], direction: string, moveKeys: string[]) => {
  console.log(keys, direction, moveKeys);
};
const handleSearch = (dir: string, value: string) => {
  console.log('search:', dir, value);
};

// 获取存储区列表，供prot添加移除使用
const storageData = ref<any[]>([])
const canUseStorageData = computed(() => {
  if (currentPort.storagePortVOList && currentPort.storagePortVOList.length > 0) {
    const r: any[] = []
    storageData.value.forEach((e: any) => {
      const item = currentPort.storagePortVOList.find((o: any) => o.storageId === e.storageId)
      if (item) r.push({ ...e, storagePortId: item.storagePortId })
    })
    return r
  }
  else return cloneDeep(storageData.value)
})
const getStorageList = async () => {
  const params = {
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
      size: 100
    }
  }
  await storageList(params).then((res: any) => {
    if (res.responseStatus === 200) {
      const data: any = res.data
      storageData.value = (data?.list || []).map((e: any) => ({ ...e, key: String(e.storageId), title: e.storageName }))
    }
  })
}
const getPortDetail = (portId: string) => {
  detailPort(portId).then((res: any) => {
    if (res.responseStatus === 200) {
      const data: any = res.data
      Object.assign(currentPort, { ...data })
      if (data.storagePortVOList && data.storagePortVOList.length > 0) {
        const keys = data.storagePortVOList?.map((e: any) => e.storageId)
        const { maxJdbcConns, maxOdpcEngines, minJdbcConns, minOdpcEngines } = data.storagePortVOList[0]
        Object.assign(form, { maxJdbcConns, maxOdpcEngines, minJdbcConns, minOdpcEngines, targetKeys: keys })
      }
    }
  })
}

const isAdd = ref<boolean>(false)
const open = ref<boolean>(false)
const loading = ref<boolean>(false)
const addPortFn = () => {
  // open.value = true
}
const addPortStorageFn = async (record: any) => {
  isAdd.value = true
  const { portId } = record
  await getStorageList()
  getPortDetail(portId)
  open.value = true
}
const removePortStorageFn = async (record: any) => {
  isAdd.value = false
  const { portId } = record
  await getStorageList()
  getPortDetail(portId)
  open.value = true
}

// submit
const onClose = () => {
  formRef.value.clearValidate()
  isAdd.value = false
  open.value = false
  Object.assign(form, {
    maxJdbcConns: 2, maxOdpcEngines: 2, minJdbcConns: 1, minOdpcEngines: 1, targetKeys: []
  })
}
const onOk = () => {
  formRef.value
    .validate()
    .then(() => {
      submitFn()
    })
}
const initParams = () => {
  const { maxJdbcConns, maxOdpcEngines, minJdbcConns, minOdpcEngines, targetKeys } = form
  const params: any = canUseStorageData.value.filter((e: any) => targetKeys.includes(e.key)).map((e: any) => {
    const { storageId, connType, description, engineType, storagePortId } = e, { portCode, portId, portName } = currentPort
    return { storageId, connType, description, engineType, portCode, portId, portName, maxJdbcConns, maxOdpcEngines, minJdbcConns, minOdpcEngines, storagePortId }
  })
  return params
}
const submitFn = () => {
  loading.value = true
  const action = isAdd.value ? addStoragePort : removeStoragePort
  const params = initParams()
  action(params)
    .then((res: any) => {
      if (res.responseStatus === 200) {
        isAdd.value = false
        open.value = false
        message.success(res.msg)
        getPortList()
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
.page-tit{
  padding: 10px 20px;
  line-height: 32px;
  border-bottom: 1px solid var(--border-color);
  .tit {
    font-size: 16px;
  }
  .btns{display: flex;}
}
.list{
  padding: 20px 10px;
}
.storage-list {

  .storage-item {
    width: 31.33333%;
    margin: 0 1% 2%;
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