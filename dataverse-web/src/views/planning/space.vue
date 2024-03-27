<template>
  <div class="g-card flex fdc ovh">
    <div class="fxbw block-t aic">
      <h4 class="tit">数据空间</h4>
      <div class="btns">
        <a-button type="primary" @click="() => addSpaceFn()">新增数据空间</a-button>
      </div>
    </div>
    <div class="block-c flex1 flex fdc">
      <div class="search flex mb10 pl20 pr20">
        <a-form layout="inline" :model="searchForm" :colon="false">
          <a-form-item>
            <a-input v-model:value="searchForm.keyword" placeholder="空间名称或别名">
            </a-input>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="getSpaceList"> 查询 </a-button>
            <a-button class="ml10" @click="resetSearch"> 重置 </a-button>
          </a-form-item>
        </a-form>
      </div>

      <a-spin :spinning="searching" tip="Loading..." wrapperClassName="flex1 ovh pl20">
        <div class="space-list flex ffrw h100 ova pr20 pb20">
          <div class="space-item flex fdc" v-for="(item, i) in list" :key="i">
            <div class="space-item-header flex aic">
              <SvgIcon name="database" class="mr7" /><span class="flex1 toe">{{ item.parentAlias }}</span>
            </div>
            <div class="space-item-body flex1 flex fdc">
              <div class="env-list">
                <div class="env-item">
                  <div class="flex ffrw" v-if="item.envMode === 2">
                    <div class="flex ffrw w50 pt5" :class="{ 'border-right': j === 0 }"
                      v-for="(env, j) in item.dvsVOList" :key="'e_' + j">
                      <div class="item w100" :class="{ pl10: j > 0 }">别名：{{ env.dvsAlias }}</div>
                      <div class="item w100" :class="{ pl10: j > 0 }">名称：{{ env.dvsName }}</div>
                      <div class="item w100" :class="{ pl10: j > 0 }">简称：{{ env.dvsAbbr }}</div>
                      <div class="item w100 mode" :class="env.env === 0 ? 'basic' : env.env === 1 ? 'dev' : 'prod'">
                        <span class="text">{{ env.env === 0 ? 'BASIC' : env.env === 1 ? 'DEV' : 'PROD' }}</span>
                      </div>
                    </div>
                  </div>
                  <template v-else>
                    <div class="flex ffrw">
                      <div class="item w50">别名：{{ item.dvsVOList?.[0]?.dvsAlias || '' }}</div>
                      <div class="item w50">创建人：{{ item.userName }}</div>
                      <div class="item w50">名称：{{ item.dvsVOList?.[0].dvsName || '' }}</div>
                      <div class="item w50">创建时间：{{ dayjs(item.createTime).format('YYYY-MM-DD HH:mm') }}</div>
                      <div class="item w50">简称：{{ item.dvsVOList?.[0]?.dvsAbbr || '' }}</div>
                      <div class="item w50">更新时间：{{ dayjs(item.updateTime).format('YYYY-MM-DD HH:mm') }}</div>
                      <!-- <div class="item w100">成员列表：{{ item.dvsMemberVOList?.map((e: any) => e.nickName).join(',') }}
                      </div> -->
                      <div class="item w100">描述：{{ item.dvsVOList?.[0]?.description || '' }}</div>
                      <div class="item w100 mode basic"><span class="text">BASIC</span></div>
                    </div>
                  </template>
                  <div class="env-info flex ffrw mt10" v-if="item.envMode === 2">
                    <div class="item w50">创建人：{{ item.userName }}</div>
                    <div class="item w50">状态：草稿</div>
                    <div class="item w50">创建时间：{{ dayjs(item.createTime).format('YYYY-MM-DD HH:mm') }}</div>
                    <div class="item w50">更新时间：{{ dayjs(item.updateTime).format('YYYY-MM-DD HH:mm') }}</div>
                    <!-- <div class="item w100">成员列表：{{ item.dvsMemberVOList?.map((e: any) => e.nickName).join(',') }}</div> -->
                    <div class="item w100">描述：{{ item.dvsVOList?.[0]?.description || '' }}</div>
                  </div>
                </div>
              </div>
              <div class="chart flex1 ovh mt10">
                <BarChart class="w100 h100" :options="item.options" />
              </div>
            </div>
            <div class="space-item-footer flex">
              <!-- <span class="btn flex aic">
                <SvgIcon name="publish" class="mr5" /><span>运维发布</span>
              </span>
              <span class="btn flex aic" @click="showUsers">
                <SvgIcon name="users" class="mr5" /><span>成员列表</span>
              </span> -->
              <span class="btn flex aic" @click="editSpaceFn(item)">
                <SvgIcon name="setting" class="mr5" /><span>信息设置</span>
              </span>
              <span class="btn flex aic" @click="gotoWorkSpace(item)">
                <SvgIcon name="workspace" class="mr5" /><span>工作区</span>
              </span>
            </div>
          </div>
        </div>
      </a-spin>
    </div>

    <Drawer :width="500" :title="`${isEdit ? '编辑' : '新增'}数据空间`" placement="right" :open="open" :loading="loading"
      @close="onClose" @ok="onOk">
      <a-form ref="formRef" :model="form" :rules="rules" :labelCol="{ span: 6 }" autocomplete="off"
        :disabled="readonly">
        <a-form-item label="空间名称" name="parentName">
          <a-input v-model:value="form.parentName" v-trim placeholder="ex: english_name"> </a-input>
        </a-form-item>
        <a-form-item label="空间别名" name="parentAlias">
          <a-input v-model:value="form.parentAlias" v-trim placeholder="ex: 中文名称"> </a-input>
        </a-form-item>
        <a-form-item label="空间简称" name="parentAbbr">
          <a-input v-model:value="form.parentAbbr" v-trim placeholder="ex: en_for_short"> </a-input>
        </a-form-item>
        <a-form-item label="环境模式" name="envMode">
          <a-select v-model:value="form.envMode" placeholder="请选择" :disabled="isEdit">
            <a-select-option :value="item.value" v-for="(item, i) in modelEnum" :key="'m_' + i">{{ item.desc
              }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="储存类型" name="multStorage" required>
          <a-radio-group v-model:value="form.multStorage" id="form_item_multStorage" @change="changeMultStorage">
            <a-radio :value="0">单存储区</a-radio>
            <a-radio :value="1">多存储区</a-radio>
          </a-radio-group>
        </a-form-item>
        <template v-if="form.storageBoxParamList.length > 0">
          <a-form-item v-for="(s, s_i) in form.storageBoxParamList" :key="s_i"
            :label="`${form.multStorage === 0 ? '' : s_i === 0 ? 'ADS' : '非ADS'}存储区`"
            :name="['storageBoxParamList', s_i, 'storageId']" :rules="rules.storage">
            <a-select v-model:value="s.storageId" placeholder="请选择" @change="changeStorage">
              <a-select-option :value="item.storageId" v-for="(item, i) in storageData" :key="'s_' + i"
                :disabled="form.multStorage === 0 ? false : s_i === 0 ? form.storageBoxParamList[1].storageId === item.storageId : form.storageBoxParamList[0].storageId === item.storageId">({{
          item.storageType }}) {{ item.storageName }}</a-select-option>
            </a-select>
          </a-form-item>
        </template>
        <a-form-item label="空间描述" name="description">
          <a-textarea v-model:value="form.description" :maxlength="200" placeholder="请输入说明（200字以内）">
          </a-textarea>
        </a-form-item>
      </a-form>
    </Drawer>

    <Dialog :width="700" title="成员列表" :open="isShowUser" @close="onCloseUser" @ok="onOkUser">
      <div class="search ar flex jcfe mb10">
        <a-select v-model:value="selectedUsers" mode="multiple" placeholder="请选择成员" allowClear :options="allUsers"
          :max-tag-count="2" :field-names="{ label: 'nickName', value: 'userName' }" style="width:300px">
          <template #option="{ nickName, userName }">
            <a-checkbox :checked="selectedUsers.includes(userName)" @click.prevent></a-checkbox>
            &nbsp;&nbsp;{{ nickName }}
          </template>
        </a-select>
        <a-button class="ml5" type="primary" @click="addUser">添加成员</a-button>
      </div>
      <a-table class="result-table" :dataSource="users" :columns="userColumns" :pagination="false"
        :showSorterTooltip="false">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'roleId'">
            <a-dropdown :trigger="['click']">
              <a class="tlink" @click.prevent>
                {{ getRoleName(record[column.key]) }}
                <DownOutlined />
              </a>
              <template #overlay>
                <a-menu>
                  <a-menu-item v-for="(role, i) in roles" :key="i" @click="updateUser(record, role)">
                    <span>{{ role.roleName }}</span>
                  </a-menu-item>
                  <a-menu-divider />
                  <a-menu-item class="tbtn" key="remove" @click="removeUser(record)">移除该成员</a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </template>
        </template>
      </a-table>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, toRefs, onMounted, inject } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import type { Rule } from 'ant-design-vue/es/form'
import Drawer from '@/components/Drawer.vue'
import Dialog from '@/components/Dialog.vue'
import SvgIcon from '@/components/SvgIcon/index.vue'
import BarChart from '@/components/Echarts/BarChart.vue'
import { spaceList, addSpace, editSpace } from '@/api/modules/planning'
import { storageList } from '@/api/modules/config'
import dayjs from 'dayjs'
import { cloneDeep } from 'lodash-es'
import { useDvsStore } from '@/stores/modules/user'
const dvsStore = useDvsStore()
const { envEnum, modelEnum } = toRefs(dvsStore)
const router = useRouter()

const storageData = ref<any[]>([])
const getStorageList = () => {
  const params = {
    storageName: '',
    storageType: null,
    connType: null,
    engineType: null,
    pageQueryParam: {
      ascs: [
      ],
      descs: ['createTime'],
      pageNo: 1,
      size: 50
    }
  }
  storageList(params).then(res => {
    const data: any = res.data
    storageData.value = data.list || []
  })
}
getStorageList()

const storageTypeEnum: any = {
  1: 'HDFS', 2: 'MySQL', 3: 'ClickHouse', 4: 'Doris'
}

const pagination = reactive({
  position: ['bottomRight'], pageSize: 10, pageSizeOptions: ['10', '20', '50', '100', '500'], total: 0, current: 1
})
const searchForm = reactive({
  "keyword": "",
  "pageQueryParam": {
    "ascs": [
    ],
    "descs": ['createTime'],
    "pageNo": 1,
    "size": 50
  }
})
const searching = ref(false)
const list = ref<any[]>([])
const columns = ref<any[]>([])
const tableScroll = reactive({ x: 'max-content', })

const getSpaceList = () => {
  searching.value = true
  const params = { ...searchForm }
  params.pageQueryParam.pageNo = pagination.current
  params.pageQueryParam.size = pagination.pageSize
  spaceList(params).then((res: any) => {
    if (res.responseStatus === 200) {
      const data: any = res.data
      list.value = (data?.list || []).map((e: any) => {
        const options = {
          color: ['#3C6DF0', '#473793', '#00A68F'],
          xAxis: [
            {
              type: 'category',
              data: ['集成作业数', '建模作业数', '失败作-集成', '失败作-建模', '表数量'],
              axisLabel: {
                color: '#5A6872',
                interval: 0,
                width: 60,
                overflow: 'breakAll',
                ellipsis: '...'
              },
              axisLine: { lineStyle: { color: '#5A6872' } }
            }
          ],
          grid: {
            left: 5,
            right: 5,
            top: 10,
            bottom: 5,
            containLabel: true
          },
          series: [
            {
              name: 'DEV',
              type: 'bar',
              barWidth: 30,
              emphasis: {
                focus: 'series'
              },
              data: [320, 332, 301, 334, 390]
            }
          ]
        }
        const { basicEtlJobCount, basicBdmJobCount, basicEtlJobFailCount, basicBdmJobFailCount, basicTableCount, devEtlJobCount, devBdmJobCount, devEtlJobFailCount, devBdmJobFailCount, devTableCount, prodEtlJobCount, prodBdmJobCount, prodEtlJobFailCount, prodBdmJobFailCount, prodTableCount } = e.dvsDetailCountVO
        if (e.envMode === 1) {
          // basic
          options.color = ['#3C6DF0']
          options.series = [
            {
              name: 'BASIC',
              type: 'bar',
              barWidth: 13,
              emphasis: {
                focus: 'series'
              },
              data: [basicEtlJobCount || 0, basicBdmJobCount || 0, basicEtlJobFailCount || 0, basicBdmJobFailCount || 0, basicTableCount || 0]
            }
          ]
        } else {
          // dev-prod
          options.color = ['#473793', '#00A68F']
          options.series = [
            {
              name: 'DEV',
              type: 'bar',
              barWidth: 13,
              emphasis: {
                focus: 'series'
              },
              data: [devEtlJobCount || 0, devBdmJobCount || 0, devEtlJobFailCount || 0, devBdmJobFailCount || 0, devTableCount || 0]
            },
            {
              name: 'PROD',
              type: 'bar',
              barWidth: 13,
              emphasis: {
                focus: 'series'
              },
              data: [prodEtlJobCount || 0, prodBdmJobCount || 0, prodEtlJobFailCount || 0, prodBdmJobFailCount || 0, prodTableCount || 0]
            }
          ]
        }
        e.options = options
        return e
      })
      pagination.total = data?.total || 0
      // 缓存空间列表
      dvsStore.setSpaceList(list.value)
    } else {
      message.error(res.errorMsg)
    }
  }).finally(() => {
    searching.value = false
  })
}
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.pageQueryParam.pageNo = 1
  getSpaceList()
}
onMounted(() => {
  getSpaceList()
})

const checkName = async (_rule: Rule, value: string) => {
  if (!value) {
    return Promise.reject('请填写数据空间名称')
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
    return Promise.reject('请填写数据空间别名')
  }
  if (value.length <= 50) {
    return Promise.resolve()
  } else {
    return Promise.reject('请填写长度在50以内的字符')
  }
}
const checkAbbr = async (_rule: Rule, value: string) => {
  if (!value) {
    return Promise.reject('请填写数据空间简称')
  }
  const reg = /^[a-zA-Z]\w{0,19}$/
  if (reg.test(value)) {
    return Promise.resolve()
  } else {
    return Promise.reject('以英文开头，由数字、英文或者下划线组成的字符串，且长度在20以内')
  }
}
const rules: Record<string, Rule[]> = {
  parentName: [{ required: true, validator: checkName, trigger: 'blur' }],
  parentAlias: [{ required: true, validator: checkAlias, trigger: 'blur' }],
  parentAbbr: [{ required: true, validator: checkAbbr, trigger: 'blur' }],
  dvsCode: [{ required: true, message: '请填写数据空间编码', trigger: 'blur' }],
  envMode: [{ required: true, message: '请选择数据空间环境', trigger: 'blur' }],
  descripton: [{ max: 200, message: '请输入200长度以内的字符', trigger: 'blur' }],
  storage: [{ required: true, message: '请选择存储区', trigger: 'blur' }],
}
const formRef = ref()
const form = reactive({
  parentId: null,
  parentAlias: '',
  parentName: '',
  parentAbbr: '',
  dvsCode: null,
  envMode: 1,
  multStorage: 0,
  dvsParamList: [] as any,
  storageBoxParamList: [{
    storageId: null,
    storageName: '',
    storageType: '',
    storagePath: ''
  }] as any,
  description: ''
})
const open = ref<boolean>(false)
const isEdit = ref<boolean>(false)
const loading = ref<boolean>(false)
const readonly = ref<boolean>(false)

const changeMultStorage = (e: any) => {
  const v = e.target.value
  if (v === 0) {
    form.storageBoxParamList = [{
      storageId: null,
      storageName: '',
      storageType: '',
      storagePath: ''
    }]
  } else {
    form.storageBoxParamList = [{
      storageId: null,
      storageName: '',
      storageType: '',
      storagePath: ''
    }, {
      storageId: null,
      storageName: '',
      storageType: '',
      storagePath: ''
    }]
  }
}
const changeStorage = (v: any) => {
  const s = storageData.value.find((e: any) => e.storageId === v)
  const i = form.storageBoxParamList.findIndex((e: any) => e.storageId === v)
  if (s && i >= 0) {
    const { connType, odpcStorageVO, storageName, storageType } = s
    form.storageBoxParamList[i].storageName = storageName
    form.storageBoxParamList[i].storageType = storageType
    if (connType === 'ODPC') {
      form.storageBoxParamList[i].storagePath = odpcStorageVO?.storagePath || ''
    }
  }
}

const addSpaceFn = () => {
  isEdit.value = false
  open.value = true
}
const editSpaceFn = (record: any) => {
  const { dvsCode, parentId, parentAlias, parentName, parentAbbr, envMode, dvsVOList, storageBoxVOList } = record
  // 获取存储区类型、存储区ID
  const multStorage = dvsVOList[0].multStorage
  const description = dvsVOList[0].description
  const ADS = storageBoxVOList?.find((e: any) => e.dwLayer === 3) || {}
  const unADS = storageBoxVOList?.find((e: any) => e.dwLayer !== 3) || {}
  if (multStorage === 1) {
    // 多存储
    const storageBoxParamList = [
      {
        storageId: ADS.storageId,
        storageName: ADS.storageName,
        storageType: ADS.storageType,
        storagePath: ADS.storagePath
      }, {
        storageId: unADS.storageId,
        storageName: unADS.storageName,
        storageType: unADS.storageType,
        storagePath: unADS.storagePath
      }
    ]
    Object.assign(form, { dvsCode, parentId, parentAlias, parentName, parentAbbr, envMode, description, multStorage, storageBoxParamList })
    changeStorage(ADS.storageId)
    changeStorage(unADS.storageId)
  } else {
    // 单存储
    const storageBoxParamList = [
      {
        storageId: ADS.storageId,
        storageName: ADS.storageName,
        storageType: ADS.storageType,
        storagePath: ADS.storagePath
      }
    ]
    Object.assign(form, { parentId, parentAlias, parentName, parentAbbr, envMode, description, multStorage, storageBoxParamList })
    changeStorage(ADS.storageId)
  }
  isEdit.value = true
  open.value = true
}
const gotoWorkSpace = (record: any) => {
  dvsStore.setCurrentSpace(cloneDeep(record))
  router.push({ path: '/development/etl' })
}

const onClose = () => {
  formRef.value.clearValidate()
  open.value = false
  Object.assign(form, {
    parentId: null,
    parentAlias: '',
    parentName: '',
    parentAbbr: '',
    dvsCode: null,
    envMode: 1,
    multStorage: 0,
    dvsParamList: [] as any,
    storageBoxParamList: [{
      storageId: null,
      storageName: '',
      storageType: '',
      storagePath: ''
    }] as any,
    description: ''
  })
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
  const params = initParams()
  const action = isEdit.value ? editSpace : addSpace
  action(params)
    .then((res: any) => {
      if (res.responseStatus === 200) {
        open.value = false
        getSpaceList()
      } else {
        message.error(res.errorMsg)
      }
    })
    .finally(() => {
      loading.value = false
    })
}

const initParams = () => {
  const { parentId, parentName, parentAlias, parentAbbr, dvsCode, envMode, multStorage, storageBoxParamList, description } = form
  const dvsParamList: any[] = []
  // envMode--环境模式，1：BASIC、2：DEV-PROD
  const envList = envMode === 1 ? [0] : [1, 2]   // 0：BASIC、1:DEV、2：PROD
  envList.forEach((env) => {
    const item = {
      description, dvsAbbr: parentAbbr, dvsAlias: parentAlias, dvsCode, dvsName: parentName, dvsId: parentId, env, multStorage
    }
    dvsParamList.push(item)
  })
  return { parentId, parentName, parentAlias, parentAbbr, dvsCode, envMode, multStorage, dvsParamList, description, storageBoxParamList }
}

// 角色列表
const roles = ref<any[]>([
  { roleId: '1', roleName: '项目经理' },
  { roleId: '2', roleName: '架构师' },
  { roleId: '3', roleName: '数据开发' },
  { roleId: '4', roleName: '业务负责人' },
  { roleId: '5', roleName: '数据分析师' },
])
const getRoleName = (roleId: any) => {
  const role = roles.value.find(e => e.roleId === roleId)
  return role?.roleName || ''
}
// 成员列表
const isShowUser = ref(false)
const userColumns = ref([
  {
    title: '用户名',
    dataIndex: 'userName',
    key: 'userName'
  },
  {
    title: '昵称',
    dataIndex: 'nickName',
    key: 'nickName'
  },
  {
    title: '加入时间',
    dataIndex: 'createTime',
    key: 'createTime'
  },
  {
    title: '角色',
    dataIndex: 'roleId',
    key: 'roleId',
    width: 120
  },
])
const users = ref<any[]>([])
const addUser = () => {
  const data = allUsers.value.filter(e => selectedUsers.value.includes(e.userName)).map(e => ({ ...e, roleId: '3', createTime: dayjs().format('YYYY-MM-DD HH:mm:ss') }))
  users.value.push(...data)
  selectedUsers.value = []
}
const removeUser = (record: any) => {
  const index = users.value.findIndex(e => e.roleId === record.roleId)
  users.value.splice(index, 1)
}
const updateUser = (record: any, role: any) => {
  record.roleId = role.roleId
}
const selectedUsers = ref<any[]>([])
const allUsers = ref<any[]>([
  { userName: 'Danny_huo', nickName: '霍强', roleId: '2' },
  { userName: 'wending', nickName: '王稳定', roleId: '2' },
  { userName: 'wangqiang', nickName: '王强', roleId: '3' },
  { userName: 'chenzhan', nickName: '陈占', roleId: '3' },
])
const showUsers = (data: any) => {
  // users.value = data.datavsMemberVOList
  users.value = [
    {
      userName: 'Danny_huo',
      nickName: '霍强',
      createTime: '2024-01-16 10:10:00',
      roleId: '2'
    },
    {
      userName: 'wending',
      nickName: '王稳定',
      createTime: '2024-01-16 10:10:00',
      roleId: '2'
    },
    {
      userName: 'wangqiang',
      nickName: '王强',
      createTime: '2024-01-16 10:10:00',
      roleId: '3'
    },
  ]
  isShowUser.value = true
}
const onCloseUser = () => {
  isShowUser.value = false
  users.value = []
}
const onOkUser = () => {
  isShowUser.value = false
  users.value = []
  // loading.value = true
  // formRef.value
  //   .validate()
  //   .then(() => {
  //     submitFn()
  //   })
  //   .catch(() => {
  //     loading.value = false
  //   })
}
</script>

<style scoped lang="less">
@color-basic: #3C6DF0;
@color-dev: #473793;
@color-prod: #00A68F;

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
  margin-top: 10px;
}

.space-list {
  // min-width: 1440px;

  .space-item {
    width: 32%;
    margin: 0 0 2%;
    font-size: 12px;
    height: 562px;
    box-shadow: 0px 0px 4px 1px var(--bg-color-2);
    border-radius: 4px;
    border: 1px solid var(--border-color);
    background-color: var(--bg-color-2);

    &:nth-child(3n+2) {
      margin: 0 2% 2%;
    }

    &-header {
      height: 40px;
      line-height: 40px;
      font-size: 14px;
      padding: 0 10px;
      background: var(--bg-color-2-5);
    }

    &-body {
      min-height: 400px;
      padding: 10px;

      .env-list {
        .env-item {

          .border-right {
            border-right: 1px solid var(--border-color);
          }

          .item {
            line-height: 1.6;
          }

          .mode {
            margin-top: 10px;
            text-align: left;

            .text {
              display: inline-block;
              height: 34px;
              line-height: 32px;
              width: 100px;
              text-align: center;
              border-bottom: 2px solid @color-basic;
              background: var(--bg-color-2-5);
            }
          }

          .dev {
            border-bottom: 1px solid @color-dev;

            .text {
              border-bottom: 2px solid @color-dev;
            }
          }

          .prod {
            border-bottom: 1px solid @color-prod;

            .text {
              border-bottom: 1px solid @color-prod;
            }
          }
        }
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
