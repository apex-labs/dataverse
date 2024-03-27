<script setup lang="ts">
import { ref, computed, inject } from 'vue'
import { Handle, Position } from '@vue-flow/core'
import { TimePicker } from 'ant-design-vue';
const ATimePicker = TimePicker;

// import type { NodeProps } from '@vue-flow/core'
// import { StringDecoder } from 'string_decoder'
// interface CustomData {
//   type: StringDecoder
// }
// interface CustomEvents {
//   onCustomEvent: (event: MouseEvent) => void
// }
// props were passed from the slot using `v-bind="customNodeProps"`
// const props = defineProps<NodeProps<CustomData, CustomEvents>>()

const props = defineProps({
  id: [String, Number],
  index: Number,
  label: String,
  data: { type: Object, default: () => ({}) },
})

const emits = defineEmits(['removeNode', 'update:data'])

// 节点类型 start/end/normal
const nodeType = computed(() => props.data.nodeType)
// 节点尺寸
const width = inject('width')
const height = inject('height')
const limit: number = inject('limit') || 4

const columns = ref<any[]>([
  {
    title: '作业ID',
    dataIndex: 'id',
    key: 'id',
    width: 80,
    ellipsis: true
  },
  {
    title: '作业名称',
    dataIndex: 'name',
    key: 'name',
    ellipsis: true
  },
  {
    title: '操作',
    key: 'action',
    width: 80
  }
])
const removeItem = (record: any, index: number) => {
  const newData = { ...props.data }
  newData.list.splice(index, 1)
  emits('update:data', newData)
}
const addItem = () => {
  const newData = { ...props.data }
  newData.list.push({ id: Date.now() + '', name: '新增作业' })
  emits('update:data', newData)
}
</script>

<template>
  <div class="custom-node flex fdc nodrag nopan" :style="{ width: width + 'px', height: height + 'px' }">
    <div class="node-title">
      <span>{{ nodeType === 1 ? '开始节点' : nodeType === 3 ? '结束节点' : '作业节点' }}</span>
      <a-button class="btn" type="text" v-if="nodeType === 2" @click="() => $emit('removeNode', props.id)">
        <DeleteOutlined />
      </a-button>
    </div>
    <div class="flex1 node-content nowheel ovh">
      <div class="h100 ova">
        <!-- start -->
        <a-form v-if="nodeType === 1" class="p10" :labelCol="{ span: 24 }">
          <a-form-item label="刷新频率" required>
            <div class="flex aic">
              <span>每</span>
              <a-input style="width:100px;margin:0 5px"></a-input>
              <a-select style="width:100px" placeholder="请选择">
                <a-select-option value="天">天</a-select-option>
              </a-select>
            </div>
          </a-form-item>
          <a-form-item label="刷新时间" required>
            <a-time-picker class="w100" format="HH:mm" />
          </a-form-item>
          <a-form-item label="开始时间" required>
            <a-date-picker class="w100" format="YYYY-MM-DD" />
          </a-form-item>
          <a-form-item label="结束时间" required>
            <a-date-picker class="w100" format="YYYY-MM-DD" />
          </a-form-item>
        </a-form>
        <!-- end -->
        <a-form v-if="nodeType === 3" class="p10" :labelCol="{ span: 24 }">
          <a-form-item label="数据空间名称" required>
            <a-input class="w100" placeholder="请填写名称"></a-input>
          </a-form-item>
          <a-form-item label="描述">
            <a-textarea class="w100" placeholder="请填200以内的字符" />
          </a-form-item>
        </a-form>
        <!-- normal -->
        <a-table v-if="nodeType === 2" class="result-table h100" :dataSource="data.list" :columns="columns"
          :pagination="false" :showSorterTooltip="false">
          <template #bodyCell="{ column, record, index }">
            <template v-if="column.key === 'action'">
              <a class="tbtn" @click="removeItem(record, index)">删除</a>
            </template>
          </template>
        </a-table>
      </div>
      <!-- <Handle v-if="type !== 3" type="source" :position="Position.Right" /> -->
    </div>
    <div class="node-footer flex" v-if="nodeType === 2">
      <a-select class="flex1 mr5" placement="topLeft" placeholder="请选择作业">
        <a-select-option value="lucy">lucy</a-select-option>
      </a-select>
      <a-button type="primary" @click="addItem">添加作业</a-button>
    </div>

    <!-- 节点连接点 -->
    <!-- 奇数行 -->
    <template v-if="(data.row + 1) % 2 > 0">
      <!-- 起点：与下一个节点的连接点 -->
      <Handle v-if="nodeType !== 3" id="source" type="source"
        :position="data.index % limit + 1 === limit ? Position.Bottom : Position.Right" />
      <!-- 终点：与上一个节点的连接点 -->
      <Handle v-if="nodeType !== 1" id="target" type="target"
        :position="data.index % limit === 0 ? Position.Top : Position.Left" />
    </template>
    <!-- 偶数行 -->
    <template v-else>
      <!-- 起点：与下一个节点的连接点 -->
      <Handle v-if="nodeType !== 3" id="source" type="source"
        :position="data.index % limit + 1 === limit ? Position.Bottom : Position.Left" />
      <!-- 终点：与上一个节点的连接点 -->
      <Handle id="target" type="target" :position="data.index % limit === 0 ? Position.Top : Position.Right" />
    </template>
  </div>
</template>

<style lang="less" scoped>
.custom-node {
  // width: 350px;
  // height: 400px;
  background: var(--bg-color-2);
  box-shadow: 0px 0px 4px 1px var(--bg-color-3);
  border-radius: 4px;
  overflow: hidden;
  cursor: default;

  .node-title {
    height: 42px;
    background: var(--bg-color-3);
    line-height: 42px;
    font-size: 16px;
    text-align: center;
    position: relative;

    .btn {
      position: absolute;
      right: 5px;
      top: 50%;
      transform: translateY(-50%);
    }
  }

  .node-content {
    padding: 5px;

    :deep(.ant-form-item) {
      margin-bottom: 10px;

      &-label {
        padding-bottom: 4px;

        &>label {
          height: auto;
        }
      }
    }
  }

  .node-footer {
    padding: 5px;
  }
}
</style>
