<script setup lang="ts">
import { ref, toRefs, reactive, provide, watch, nextTick } from 'vue'
import type { Elements } from '@vue-flow/core'
import { VueFlow, MarkerType, Position, Panel } from '@vue-flow/core'
import { Controls } from '@vue-flow/controls'
import { MiniMap } from '@vue-flow/minimap'
import { cloneDeep } from 'lodash-es'
import CustomNode from './CustomNode.vue'
import CustomEdge from './CustomEdge.vue'
import { useDvsStore } from '@/stores/modules/user'
const dvsStore = useDvsStore()
const { env, dvsCode } = toRefs(dvsStore)

const props = defineProps({
  // 节点间距
  gap: {
    type: Number,
    default: 100
  },
  columnCount: {
    type: Number,
    default: 4
  },
  // 节点尺寸
  width: {
    type: Number,
    default: 350
  },
  height: {
    type: Number,
    default: 400
  },
  // 初始位置
  x: {
    type: Number,
    default: 50
  },
  y: {
    type: Number,
    default: 50
  },
  // 一行4个
  limit: {
    type: Number,
    default: 4
  }
})
// 节点间距
provide('gap', props.gap)
// 一行N个节点
provide('limit', props.limit)
// 节点尺寸
provide('width', props.width)
provide('height', props.height)

const jobScheduleParam = reactive({
  scheduleId: null,
  scheduleCode: Date.now(),
  scheduleName: '',
  description: '',
  cron: '',
  env: env.value,
  dvsCode: dvsCode.value,
  scheduleLifecycle: 31,
  scheduleNodeParamList: [],
  scheduleEdgeParamList: [],
  startTime: null,
  endTime: null,
})
const nodeData = ref<any[]>([
  // nodes
  {
    id: 'start',
    type: 'custom',
    label: 'Node Start',
    data: { nodeType: 1 },
    // sourcePosition: Position.Left,
    // targetPosition: Position.Right
  },
  // {
  //   id: '1',
  //   type: 'custom',
  //   label: 'Node 1',
  //   data: { nodeType: 'normal' },
  //   dragging: false
  // },
  // {
  //   id: '2',
  //   type: 'custom',
  //   label: 'Node 2',
  //   data: { nodeType: 'normal' },
  //   dragging: false
  // },
  // {
  //   id: '3',
  //   type: 'custom',
  //   label: 'Node 3',
  //   data: { nodeType: 'normal' },
  //   dragging: false
  // },
  // {
  //   id: '4',
  //   type: 'custom',
  //   label: 'Node 4',
  //   data: { nodeType: 'normal' },
  //   dragging: false
  // },
  // {
  //   id: '5',
  //   type: 'custom',
  //   label: 'Node 5',
  //   data: { nodeType: 'normal' },
  //   dragging: false
  // },
  {
    id: 'end',
    type: 'custom',
    label: 'Node End',
    data: { nodeType: 3 },
    // sourcePosition: Position.Left,
    // targetPosition: Position.Right
  }
])
const nodes = ref<any[]>([])
const edges = ref<any[]>([
  // edges
  {
    id: 'start-end',
    type: 'custom',
    source: 'start',
    target: 'end',
    markerEnd: MarkerType.ArrowClosed,
    sourcePosition: Position.Left,
    targetPosition: Position.Right
  }
])
const elements = ref<Elements>([])

const init = () => {
  console.log('inint');
  const newEdges: any[] = [],
    newNodes: any[] = []
  nodeData.value.forEach((n: any, i: number) => {
    // 当前节点属于第*行，从0开始
    const row = Math.ceil((i + 1) / props.limit) - 1
    // 当前行第*个，从0开始
    const index = (i + 1) % props.limit ? ((i + 1) % props.limit - 1) : (props.limit - 1)
    // 奇数行从左至右，偶数行从右至左
    const isOdd = (row + 1) % 2 > 0

    // 复制节点信息
    const node = cloneDeep(n)
    // 设置节点走向
    // 从左至右，最后节点连接线起点在底部
    const position = {
      x: props.x + props.width * index + props.gap * index,
      y: props.y + props.height * row + props.gap * row
    }
    // 从右至左，最后节点连接线起点在底部
    if (!isOdd) {
      position.x =
        props.x + props.width * (props.limit - index - 1) + props.gap * (props.limit - index - 1)
    }
    // 节点
    node.position = position
    node.data.index = index
    node.data.row = row
    node.sourcePosition = index + 1 === props.limit ? Position.Bottom : isOdd ? Position.Right : Position.Left
    node.targetPosition = index === 0 ? Position.Top : isOdd ? Position.Left : Position.Right
    console.log('node', node.label, node.targetPosition, node.sourcePosition);
    node.direction = ''
    if (isOdd) {
      node.data.direction =
        index === 0 ? 'bottom-top' : index + 1 === props.limit ? 'left-bottom' : 'left-right'
    } else {
      node.data.direction =
        index === 0 ? 'bottom-top' : index + 1 === props.limit ? 'left-bottom' : 'right-left'
    }
    newNodes.push(node)
    // 边
    if (nodeData.value[i + 1]) {
      const edge = {
        id: nodeData.value[i].id + '-' + nodeData.value[i + 1].id,
        type: 'custom',
        label: nodeData.value[i].label + '-' + nodeData.value[i + 1].label,
        source: nodeData.value[i].id,
        target: nodeData.value[i + 1].id,
        markerEnd: MarkerType.ArrowClosed,
        data: { direction: '' },
        sourceHandle: 'source',
        sourcePosition: Position.Left,
        targetPosition: Position.Right
      }
      edge.sourcePosition = index + 1 === props.limit ? Position.Bottom : isOdd ? Position.Left : Position.Right
      edge.targetPosition = index + 1 === props.limit ? Position.Top : isOdd ? Position.Right : Position.Left
      edge.data.direction = index + 1 === props.limit ? 'bottom-top' : isOdd ? 'left-right' : 'right-left'
      newEdges.push(edge)
    }
  })
  nodes.value = newNodes
  edges.value = newEdges
  elements.value = [...newNodes, ...newEdges]
  nextTick(() => {
    console.log(edges.value)
  })
}
watch(
  nodeData,
  () => {
    init()
  },
  { immediate: true }
)

const addNode = (id: string) => {
  const time = Date.now()
  const sourceId = id.split('-')[0]
  const index = nodeData.value.findIndex((e: any) => e.id === sourceId)
  const newNodes = cloneDeep(nodeData.value)
  const label = 'Custom Node ' + (newNodes.length - 1)
  const node = {
    id: String(time),
    type: 'custom',
    label,
    data: { nodeType: 2, nodeJobsParamList: [], nodeId: null, nodeCode: time, env: env.value, nodeName: label, scheduleCode: jobScheduleParam.scheduleCode }
  }
  newNodes.splice(index + 1, 0, node)
  nodeData.value = [...newNodes]
}
const removeNode = (id: string) => {
  const index = nodeData.value.findIndex((e: any) => e.id === id)
  const newNodes = cloneDeep(nodeData.value)
  newNodes.splice(index, 1)
  nodeData.value = [...newNodes]
}

const loading = ref(false)
const saveFn = () => {
  loading.value = true

  setTimeout(() => {
    loading.value = false
  }, 1000);
}
</script>

<template>
  <a-spin :spinning="loading" tip="Loading..." wrapperClassName="flex1 ovh flex fdc">
    <VueFlow class="flex1" :nodes="nodes" :edges="edges" :zoom-on-scroll="false" :nodes-draggable="false"
      :nodes-connectable="false" :apply-default="false">
      <MiniMap />
      <Controls />
      <Panel position="top-right">
        <div>
          <a-button type="primary" :loading="loading" @click="saveFn">保存</a-button>
        </div>
      </Panel>

      <!-- bind your custom node type to a component by using slots, slot names are always `node-<type>` -->
      <template #node-custom="customNodeProps">
        <CustomNode v-bind="customNodeProps" @removeNode="removeNode" />
      </template>

      <!-- bind your custom edge type to a component by using slots, slot names are always `edge-<type>` -->
      <template #edge-custom="customEdgeProps">
        <CustomEdge v-bind="customEdgeProps" @addNode="addNode" />
      </template>
    </VueFlow>
  </a-spin>
</template>

<style>
/* import the necessary styles for Vue Flow to work */
@import '@vue-flow/core/dist/style.css';

/* import the default theme, this is optional but generally recommended */
@import '@vue-flow/core/dist/theme-default.css';
@import '@vue-flow/controls/dist/style.css';

.vue-flow__handle {
  opacity: 0;
}
</style>
