<template>
  <div class="h100 config">
    <div class="h100 config-wrap">
      <a-form class="w100" ref="formRef" :model="configValue" :rules="rules" autocomplete="off" :disabled="isLock">
        <a-form-item label="" name="datasourceCode">
          <div class="w100 datasource" id="droptarget">
            <p v-if="configValue.datasourceCode">
              <SvgIcon v-if="datasourceTypeIcon" :name="datasourceTypeIcon" style="color:#2A94F4" /><span class="ml5">{{
        datasourceName }}</span>
            </p>
            <span v-else>选择数据源：拖拽右侧数据源到此处</span>
          </div>
        </a-form-item>
        <a-collapse v-model:activeKey="activeKey" :bordered="false">
          <a-collapse-panel key="1" header="任务信息">
            <div class="flex">
              <a-form-item label="任务名称" name="etlJobName" class="flex1">
                <a-input v-if="isAdd" v-trim v-model:value="configValue.etlJobName" />
                <span v-else>{{ configValue.etlJobName }}</span>
              </a-form-item>
              <a-form-item label="数据域" name="dataRegionCode" class="flex1 ml10 mr10">
                <!-- <a-select v-model:value="configValue.dataRegionCode" placeholder="请选择" @change="changeDataRegion">
                  <a-select-option :value="item.dataRegionCode" v-for="item in regionData" :key="item.dataRegionCode">
                    {{ item.regionAlias }}
                  </a-select-option>
                </a-select> -->
                <span>{{ getDataRegionName(configValue.dataRegionCode) }}</span>
              </a-form-item>
              <a-form-item label="任务描述" name="description" class="flex1">
                <a-input v-trim v-model:value="configValue.description" />
              </a-form-item>
            </div>
          </a-collapse-panel>
          <a-collapse-panel key="2" header="ETL设置">
            <div class="etl-list">
              <div class="etl-item flex" v-for="(item, i) in configValue.etlJobTableParamList" :key="i">
                <div class="input w35">
                  <div>表输入</div>
                  <a-form-item label="表源" :name="['etlJobTableParamList', i, 'tableExtractParam', 'originTableName']"
                    :rules="rules.originTableName">
                    <a-select v-model:value="item.tableExtractParam.originTableName" show-search placeholder="请选择"
                      @change="(v: any) => changeTableName(v, i)">
                      <a-select-option :value="item.tableName" v-for="item in tableData" :key="item.tableName">
                        {{ item.tableName }}
                      </a-select-option>
                    </a-select>
                  </a-form-item>
                  <a-form-item label="增量标记字段" :name="['etlJobTableParamList', i, 'tableExtractParam', 'incrFields']">
                    <a-select v-model:value="item.tableExtractParam.incrFields" mode="multiple" show-search
                      placeholder="请选择" allowClear :max-tag-count="2" @change="(v: any) => changeColumnName(v, i)"
                      @focus="() => changeTableName(item.tableExtractParam.originTableName, i)">
                      <a-select-option :value="item.columnName" v-for="item in markColumnData" :key="item.columnName"
                        :disabled="item.disabled">
                        {{ item.columnName }}
                      </a-select-option>
                    </a-select>
                  </a-form-item>
                </div>
                <div class="arrow flex aic">
                  <SvgIcon name="arrow_right" />
                </div>
                <div class="transform flex aic ac">
                  <a @click="showTransferModel(item)">转换<br />
                    <SvgIcon name="transformdt" />
                  </a>
                </div>
                <div class="arrow flex aic">
                  <SvgIcon name="arrow_right" />
                </div>
                <div class="output flex1">
                  <div>表输出</div>
                  <div class="flex">
                    <a-form-item class="flex1" label="表名"
                      :name="['etlJobTableParamList', i, 'tableLoadParam', 'tableName']" :rules="rules.tableName">
                      <a-input v-trim v-model:value="item.tableLoadParam.tableName"
                        :addon-before="item.tableLoadParam.addonBefore" />
                    </a-form-item>
                    <a-form-item class="flex1" label="别名"
                      :name="['etlJobTableParamList', i, 'tableLoadParam', 'tableAlias']" :rules="rules.tableAlias">
                      <a-input v-trim v-model:value="item.tableLoadParam.tableAlias" />
                    </a-form-item>
                  </div>
                  <div class="flex">
                    <a-form-item class="flex1" label="主键"
                      :name="['etlJobTableParamList', i, 'tableLoadParam', 'pkField']">
                      <a-select v-model:value="item.tableLoadParam.pkField" show-search placeholder="请选择" allowClear
                        @focus="() => changeTableName(item.tableExtractParam.originTableName, i)">
                        <a-select-option :value="it.columnName" v-for="(it) in pkColumnData" :key="it.columnName">
                          {{ it.columnName }}
                        </a-select-option>
                      </a-select>
                    </a-form-item>
                    <a-form-item class="flex1" label="备注"
                      :name="['etlJobTableParamList', i, 'tableLoadParam', 'pkFieldComment']">
                      <a-input v-trim v-model:value="item.tableLoadParam.pkFieldComment" allowClear />
                    </a-form-item>
                  </div>
                </div>
                <div class="set ml10 flex aic">
                  <a-popover v-model:open="item.tableLoadParam.visible" title="" trigger="click"
                    placement="bottomRight">
                    <template #content>
                      <a-form-item label="分区字段" :name="['etlJobTableParamList', i, 'tableLoadParam', 'partitionField']"
                        class="mb10">
                        <a-select v-model:value="item.tableLoadParam.partitionField" show-search placeholder="请选择"
                          allowClear>
                          <a-select-option :value="it.outColumn" v-for="(it) in item.tableTransformParamList"
                            :key="it.outColumn">
                            {{ it.outColumn }}
                          </a-select-option>
                        </a-select>
                      </a-form-item>
                      <a-form-item label="分区大小"
                        :name="['etlJobTableParamList', i, 'tableLoadParam', 'partitionMaxRows']" class="mb10">
                        <a-input-number v-model:value="item.tableLoadParam.partitionMaxRows" class="w100" allowClear />
                      </a-form-item>
                      <!-- <div class="ar">
                        <a-button type="text" class="mr10"
                          @click="item.tableLoadParam.visible = !item.tableLoadParam.visible">取消</a-button>
                        <a-button type="primary"
                          @click="item.tableLoadParam.visible = !item.tableLoadParam.visible">保存</a-button>
                      </div> -->
                    </template>
                    <a class="mr5">配置</a>
                  </a-popover>
                  <a @click="() => removeEtl(i)" v-if="configValue.etlJobTableParamList.length > 1">
                    <CloseOutlined />
                  </a>
                </div>
              </div>
              <div class="etl-action">
                <a-button type="text" @click="addEtl">
                  <PlusCircleFilled /><span>添加</span>
                </a-button>
              </div>
            </div>
          </a-collapse-panel>
        </a-collapse>
      </a-form>
    </div>

    <a-modal v-model:open="open" title="转换" centered :confirm-loading="confirmLoading" @ok="handleOk"
      @cancel="handleCancel">
      <a-table :data-source="tableTransformParamList" bordered :pagination="false">
        <a-table-column title="ID" data-index="index" width="25%">
          <template #default="{ index }">
            {{ index + 1 }}
          </template>
        </a-table-column>
        <a-table-column title="输入" data-index="inColumn" width="25%"></a-table-column>
        <a-table-column title="转换" width="25%">
          <template #default="{ record }">
            <a-input class="w100" v-trim v-model:value="record.transform"></a-input>
          </template>
        </a-table-column>
        <a-table-column title="输出" width="25%">
          <template #default="{ record }">
            <a-input class="w100" v-trim v-model:value="record.outColumn"></a-input>
          </template>
        </a-table-column>
      </a-table>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, toRefs, reactive, computed, inject, watch, onMounted, onBeforeUnmount } from 'vue'
import type { Rule } from 'ant-design-vue/es/form'
import SvgIcon from '@/components/SvgIcon/index.vue'
import { useDvsStore } from '@/stores/modules/user'
import { tableListByType, columnListByType } from '@/api/modules/planning'
import { cloneDeep } from 'lodash-es'
import { getIconNameByDatasourceType } from '@/utils/constant'

const dvsStore = useDvsStore()
const { env } = toRefs(dvsStore)

const lifecycleEnum = inject('lifecycleEnum')
const regionData: any = inject('regionData')
const sourceData: any = inject('sourceData')
const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({})
  },
  isAdd: {
    type: Boolean,
    default: false
  },
  isLock: {
    type: Boolean,
    default: false
  },
})

const emit = defineEmits(['update:modelValue'])

const configValue = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emit('update:modelValue', v)
  }
})

const changeDataRegion = (v: any) => {
  const region = regionData.value.find((e: any) => e.dataRegionCode === v)
  if (region) {
    const { dwLayer, dwLayerDetail, env } = region
    configValue.value.env = env
    configValue.value.etlJobTableParamList = configValue.value.etlJobTableParamList.map((e: any) => {
      e.dvsTableParam = { ...e.dvsTableParam, dwLayer, dwLayerDetail, env, createMode: 2, isStream: 0 }
      return e
    })
  }
}
const getDataRegionName = (v: string) => {
  const region = regionData.value.find((e: any) => e.dataRegionCode === v)
  return region?.regionName || ''
}

// 数据表
const tableData = ref<any[]>([])
const getTableList = () => {
  const data = sourceData.value.find((e: any) => e.datasourceCode === configValue.value.datasourceCode)
  if (data) {
    const { datasourceCode, datasourceTypeId } = data
    const params = {
      datasourceCode,
      datasourceTypeId,
      env: env.value
    }
    tableListByType(params).then((res: any) => {
      if (res.responseStatus === 200) {
        tableData.value = res.data || []
      }
    })
  }
}
watch(
  () => configValue.value.datasourceCode,
  () => {
    getTableList()
  },
  { immediate: true }
)

// 数据列
const currentTableIndex = ref(0)
const columnData = ref<any[]>([])
// 增量标记筛选项
const markColumnData = computed(() => columnData.value.filter((e: any) => e.isIncrement))
// 主键筛选项
const pkColumnData = computed(() => columnData.value.filter((e: any) => e.isPk === 1))
const getColumnList = async (tableName: any) => {
  const data = sourceData.value.find((e: any) => e.datasourceCode === configValue.value.datasourceCode)
  if (!data) return
  const { datasourceCode, datasourceTypeId } = data
  const params = {
    datasourceCode,
    datasourceTypeId,
    env: env.value,
    tableName
  }
  await columnListByType(params).then((res: any) => {
    if (res.responseStatus === 200) {
      columnData.value = (res.data || []).map((e: any) => ({ ...e, disabled: false }))
    }
  })
}
const changeTableName = async (v: any, i: number) => {
  await getColumnList(v)
  currentTableIndex.value = i
  configValue.value.etlJobTableParamList[i].tableTransformParamList = columnData.value.map(({ columnName, dateTypeId, dataTypeName, isPk, shortDataTypeId, tableName }: any) => ({ env: env.value, inColumn: columnName, inDataTypeId: dateTypeId, inDataTypeName: dataTypeName, transform: '', outColumn: columnName, outDataTypeId: dateTypeId, outDataTypeName: dataTypeName, isPrimaryKey: isPk, shortDataTypeId, tableName }))
}
const changeColumnName = (v: any, i: number) => {
  const column: any = columnData.value.filter((e: any) => v.includes(e.columnName))
  columnData.value = columnData.value.map((e: any) => ({ ...e, disabled: e.dateTypeId !== column[0].dateTypeId }))
  // incrType：0：非增量抽取，1：日期增量，2：数值增量
  // dataTypeId：1:字符串 2:整形 3:浮点 4:日期 5:日期时间 6:长整形 7:时间戳 8:小整数 9:布尔
  configValue.value.etlJobTableParamList[i].tableExtractParam.incrType = [2, 3].includes(column[0].shortDataTypeId) ? 2 : [4, 5].includes(column[0].shortDataTypeId) ? 1 : 0
}

const datasourceName = computed(() => {
  const data = sourceData.value.find((e: any) => e.datasourceCode === configValue.value.datasourceCode)
  return data?.datasourceName || '-'
})

const datasourceTypeIcon = computed(() => {
  const data: any = sourceData.value.find((e: any) => e.datasourceCode === configValue.value.datasourceCode)
  return data ? getIconNameByDatasourceType(data.datasourceTypeId) : null
})

const activeKey = ref(['1', '2', '3'])

const formRef = ref()
const checkFrequency = async (_rule: Rule, value: string) => {
  if (!value) {
    return Promise.reject('请填写刷新频率')
  }
  const reg = /^\+?[1-9][0-9]*$/
  if (!reg.test(value)) {
    return Promise.reject('请填写数字')
  } else {
    Promise.resolve()
  }
}
const rules: Record<string, Rule[]> = {
  datasourceCode: [{ required: true, message: '请添加数据源', trigger: ['blur', 'change'] }],
  etlJobName: [
    { required: true, message: '请填写任务名称', trigger: ['blur', 'change'] },
  ],
  dataRegionCode: [{ required: true, message: '请选择数据域', trigger: 'blur' }],
  description: [{ required: false, message: '请填写任务描述', trigger: 'blur' }],
  originTableName: [{ required: true, message: '请选择表源', trigger: 'blur' }],
  incrFields: [{ required: true, message: '请选择增量标记字段', trigger: 'blur' }],
  tableName: [{ required: true, message: '请填写表名', trigger: 'blur' }],
  tableAlias: [{ required: true, message: '请填写别名', trigger: 'blur' }],
  frequency: [{ required: true, validator: checkFrequency, trigger: ['blur', 'change'] }],
  time: [{ required: true, message: '请填写刷新时间', trigger: 'blur' }],
  startTime: [{ required: true, message: '请填写开始时间', trigger: 'blur' }],
  endTime: [{ required: true, message: '请填写结束时间', trigger: 'blur' }]
}

const configItem = reactive({
  tableExtractParam: {
    env: null,
    incrType: null,
    originTableName: null,
    incrFields: []
  },
  tableTransformParamList: [],
  tableLoadParam: {
    env: null,
    pkField: '',
    tableName: '',
    partitionField: '',
    partitionMaxRows: null,
    visible: false
  },
  dvsTableParam: { createMode: 2, isStream: 0, dwLayer: 1, dwLayerDetail: 11, env: 0, tableName: '', tableAlias: '' },
})

const addEtl = () => {
  const item = cloneDeep(configItem)
  item.tableLoadParam.addonBefore = 'ods_'
  configValue.value.etlJobTableParamList.push(item)
}
const removeEtl = (index: number) => {
  configValue.value.etlJobTableParamList.splice(index, 1)
}
const validate = () => {
  return formRef.value
    .validate()
}
const resetForm = () => {
  formRef.value.resetFields()
}
defineExpose({ validate, resetForm }) // 暴露给父组件调用

// transform
const tableTransformParamList = ref<any[]>([
  {
    env: 0,
    etlJobCode: '',
    inColumn: '',
    inDataTypeId: 0,
    inDataTypeName: '',
    outColumn: '',
    outDataTypeId: 0,
    outDataTypeName: '',
    tableCode: '',
    tableTransformId: 0,
    transform: ''
  }
])
const open = ref(false)
const showTransferModel = (item: any) => {
  tableTransformParamList.value = cloneDeep(item.tableTransformParamList)
  open.value = true
}
const confirmLoading = ref(false)
const handleOk = () => {
  configValue.value.etlJobTableParamList[currentTableIndex.value].tableTransformParamList = [...tableTransformParamList.value]
  open.value = false
}
const handleCancel = () => {
  tableTransformParamList.value = [...configValue.value.etlJobTableParamList[currentTableIndex.value].tableTransformParamList]
}

const onDragOver = (event: any) => {
  // 阻止默认行为（会作为某些元素的链接打开）
  event.preventDefault()
}
watch(
  () => props.isLock,
  (v) => {
    /* 在放置目标上触发的事件 */
    const target: HTMLElement | null = document.getElementById("droptarget")
    if (v) target?.removeEventListener('dragover', onDragOver, false)
    else target?.addEventListener('dragover', onDragOver, false)
  }, { immediate: true }
)
onMounted(() => {
  /* 在放置目标上触发的事件 */
  const target: HTMLElement | null = document.getElementById("droptarget")
  if (props.isLock) {
    target?.removeEventListener('dragover', onDragOver, false)
  } else {
    target?.addEventListener('dragover', onDragOver, false)
  }
})
onBeforeUnmount(() => {
  /* 在放置目标上触发的事件 */
  const target: HTMLElement | null = document.getElementById("droptarget")
  target?.removeEventListener('dragover', onDragOver, false)
})
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

  .datasource {
    height: 60px;
    border: 1px dashed var(--border-color-light);
    border-radius: var(--border-radius-base);
    text-align: center;
    line-height: 58px;
    cursor: pointer;
    font-size: 14px;
  }
}

:deep(.ant-collapse) {
  background: none;
  margin-top: 10px;

  &-item {
    border-bottom: none !important;
    margin-bottom: 10px;
  }

  &-header {
    background: var(--bg-color-3);
  }

  &-content {
    font-size: 12px;

    &-box {
      padding: 10px 10px 1px !important;
    }
  }
}

:deep(.ant-form-item) {
  margin-bottom: 10px;

  .ant-form-item {
    margin-bottom: 0;
  }

  &-label {
    label {
      font-size: 12px;
      justify-content: flex-end;
    }
  }
}

.etl-list {
  .etl-item {
    margin-bottom: 10px;

    :deep(.ant-form-item) {
      margin-bottom: 10px;

      .ant-form-item {
        margin-bottom: 0;
      }

      &-label {
        label {
          font-size: 12px;
          min-width: 100px;
          justify-content: flex-end;
        }
      }
    }

    .input,
    .output,
    .transform {
      padding: 10px 10px 0px;
    }

    .output {
      :deep(.ant-form-item) {
        &-label {
          label {
            min-width: 50px !important;
          }
        }
      }
    }

    .arrow {
      margin: 0 10px;
    }

    .set {
      white-space: nowrap;

      a {
        color: var(--text-color-1);
      }
    }
  }
}
</style>
