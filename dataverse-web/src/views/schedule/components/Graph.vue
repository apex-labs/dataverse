<script lang="ts" setup>
import { ref, toRefs, watch, nextTick, onMounted } from 'vue'
import { Panel, VueFlow, useVueFlow } from '@vue-flow/core'
import Empty from '@/components/Empty.vue'
import { useLayout } from './useLayout'
import { useRoute } from 'vue-router'
import { jobScheduleInfo } from '@/api/modules/develoment'
import AnimationEdge from './AnimationEdge.vue'
import ProcessNode from './ProcessNode.vue'
import { message } from 'ant-design-vue';
import { useDvsStore } from '@/stores/modules/user'
import { MiniMap } from '@vue-flow/minimap'
const route = useRoute()
const dvsStore = useDvsStore()
const { env } = toRefs(dvsStore)
const { layout, previousDirection } = useLayout()
const { fitView } = useVueFlow()

const position = { x: 0, y: 0 }
const level = ref(0)
const changeLvel = () => {
  initData()
  nextTick(() => {
    layoutGraph(previousDirection.value)
  })
}

const scheduleNodeList = ref<any[]>([])
const scheduleEdgeList = ref<any[]>([])
const nodes = ref<any[]>([])
const edges = ref<any[]>([])

// getUpAndDownstreamData
const getUpstreamData = (nodeCode: string, deep: number = 0) => {
  const nodeList: any[] = []
  const edgeList: any[] = []
  if (deep > 0) {
    const upEdges = scheduleEdgeList.value.filter((e: any) => e.target === nodeCode)
    if (upEdges.length > 0) {
      upEdges.forEach((e: any) => {
        const node = scheduleNodeList.value.find((n: any) => n.id === e.source)
        const r: any = getUpstreamData(e.source, deep - 1)
        edgeList.push(e, ...r.edgeList)
        nodeList.push(node, ...r.nodeList)
      })
    }
  }
  return { nodeList, edgeList }
}
const getDownstreamData = (nodeCode: string, deep: number = 0) => {
  const nodeList: any[] = []
  const edgeList: any[] = []
  if (deep > 0) {
    const downEdges = scheduleEdgeList.value.filter((e: any) => e.source === nodeCode)
    if (downEdges.length > 0) {
      downEdges.forEach((e: any) => {
        const node = scheduleNodeList.value.find((n: any) => n.id === e.target)
        const r: any = getDownstreamData(e.target, deep - 1)
        edgeList.push(e, ...r.edgeList)
        nodeList.push(node, ...r.nodeList)
      })
    }
  }
  return { nodeList, edgeList }
}
const initData = () => {
  nodes.value = []
  edges.value = []
  if (currentNodeCode.value) {
    const node = scheduleNodeList.value.find((n: any) => n.id === currentNodeCode.value)
    // 根据层级筛选node
    const upStream = getUpstreamData(currentNodeCode.value, level.value)
    const downStream = getDownstreamData(currentNodeCode.value, level.value)
    nodes.value = [node, ...upStream.nodeList, ...downStream.nodeList]
    edges.value = [...upStream.edgeList, ...downStream.edgeList]
  } else {
    nodes.value = [...scheduleNodeList.value]
    edges.value = [...scheduleEdgeList.value]
  }
}

async function layoutGraph(direction: string) {
  nodes.value = layout(nodes.value, edges.value, direction)
  nextTick(() => {
    if (currentNodeCode.value)
      fitView({ nodes: [currentNodeCode.value], duration: 200, })
    else fitView()
  })
}

const currentNodeCode = ref<any>(null)
const getJobScheduleInfo = () => {
  const { scheduleCode, jobCode }: any = route.query
  const params: any = { env: env.value }
  if (scheduleCode) params.scheduleCode = scheduleCode
  if (jobCode) params.jobCode = jobCode
  jobScheduleInfo(params).then((res: any) => {
    if (res.responseStatus === 200) {
      const { scheduleNodeDTOList, scheduleEdgeDTOList } = res.data
      scheduleNodeList.value = scheduleNodeDTOList.map((e: any) => {
        Object.assign(e, e.nodeJobsDTO)
        const { nodeCode, nodeName, jobName, } = e
        if (jobCode === e.jobCode && e.jobCode) {
          currentNodeCode.value = nodeCode
          level.value = 1
        }
        const node = { id: nodeCode, label: nodeName || jobName, position, data: e, type: 'process' }
        return node
      })
      scheduleEdgeList.value = scheduleEdgeDTOList.map((e: any) => {
        const { fromNode, toNode } = e
        const edge = {
          id: `e${fromNode}-${toNode}`, source: fromNode, target: toNode, animated: true, type: 'animation'
        }
        return edge
      })
      initData()
      nextTick(() => {
        layoutGraph(previousDirection.value)
      })
    } else {
      message.error(res.errorMsg)
    }
  })
}
watch(
  () => env,
  () => {
    getJobScheduleInfo()
  },
  { deep: true }
)
onMounted(() => {
  getJobScheduleInfo()
})
</script>

<template>
  <div class="flex1 ovh flex fdc">
    <VueFlow :nodes="nodes" :edges="edges" @nodes-initialized="layoutGraph('TB')" v-if="nodes.length > 0">
      <MiniMap />
      <template #node-process="props">
        <ProcessNode :data="props.data" :currentNodeCode="currentNodeCode" :source-position="props.sourcePosition"
          :target-position="props.targetPosition" />
      </template>
      <template #edge-animation="edgeProps">
        <AnimationEdge :id="edgeProps.id" :source="edgeProps.source" :target="edgeProps.target"
          :source-x="edgeProps.sourceX" :source-y="edgeProps.sourceY" :targetX="edgeProps.targetX"
          :targetY="edgeProps.targetY" :source-position="edgeProps.sourcePosition"
          :target-position="edgeProps.targetPosition" />
      </template>
      <Panel position="top-right" v-if="currentNodeCode">
        <div class="flex" style="width:180px">
          <a-input-number v-model:value="level" addon-before="上下游依赖" addon-after="层" :min="0" @change="changeLvel" />
        </div>
      </Panel>
    </VueFlow>

    <div class="flex1 fxc" v-else>
      <Empty description="不存在节点和依赖关系" />
    </div>
  </div>
</template>

<style>
/* import the necessary styles for Vue Flow to work */
@import '@vue-flow/core/dist/style.css';

/* import the default theme, this is optional but generally recommended */
@import '@vue-flow/core/dist/theme-default.css';
@import '@vue-flow/controls/dist/style.css';
</style>