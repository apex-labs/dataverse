<template>
  <div :ref="(el: any) => setRef(el)"></div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import * as echarts from 'echarts'
import { useEcharts } from '@/hooks/useEcharts'
import { useAppStore } from '@/stores/modules/app'

const props = defineProps({
  options: {
    type: Object,
    default: () => ({
    })
  }
})

let myChart: echarts.ECharts | null = null

const appStore = useAppStore()
const currentTheme = computed(() => appStore.theme)
watch(
  () => currentTheme.value,
  () => {
    myChart?.dispose()
    drawChart()
  }
)
watch(
  () => props.options,
  () => {
    updateChart()
  },
  { deep: true }
)

const echartsRef = ref<HTMLElement>()
const setRef = (el: HTMLElement) => {
  if (el) {
    echartsRef.value = el
  }
}

const updateChart = () => {
  let option: echarts.EChartsOption = {
    backgroundColor: '',
    color: ['#4374F5', '#3E9C9B', '#9C703F', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#9a60b4', '#ea7ccc'],
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      show: false,
      textStyle: {
        color: '#5A6872'
      }
    },
    grid: {
      left: '20',
      right: '20',
      top: '20',
      bottom: '20',
      containLabel: true
    },
    xAxis: [
      {
        type: 'category',
        data: ['集成作业数', '建模作业数', '失败作-数据集成', '失败作-建模', '表数量'],
        axisLabel: {
          color: '#5A6872',
          interval: 'auto',
          width: 60,
          overflow: 'breakAll',
          // ellipsis: '...'
        },
        axisLine: { lineStyle: { color: '#5A6872' } }
      }
    ],
    yAxis: [
      {
        type: 'value',
        splitLine: { lineStyle: { type: 'dashed' } },
        axisLabel: {
          color: '#5A6872'
        },
        axisLine: { lineStyle: { color: '#5A6872' } }
      }
    ],
    series: [
      {
        name: 'DEV',
        type: 'bar',
        barWidth: 20,
        emphasis: {
          focus: 'series'
        },
        data: [320, 332, 301, 334, 390]
      },
      {
        name: 'PROD',
        type: 'bar',
        barWidth: 20,
        stack: 'Ad',
        emphasis: {
          focus: 'series'
        },
        data: [120, 132, 101, 134, 90]
      },
      // {
      //   name: 'Search Engine',
      //   type: 'bar',
      //   data: [862, 1018, 964, 1026, 1679],
      //   emphasis: {
      //     focus: 'series'
      //   },
      //   markLine: {
      //     lineStyle: {
      //       type: 'dashed'
      //     },
      //     data: [[{ type: 'min' }, { type: 'max' }]]
      //   }
      // }
    ],
    ...props.options
  }
  if (myChart) myChart.setOption(option)
  else useEcharts(myChart, option)
}
const drawChart = () => {
  myChart = currentTheme.value === 'dark' ? echarts.init(echartsRef.value as HTMLElement, 'dark', { renderer: "svg" }) : echarts.init(echartsRef.value as HTMLElement, { renderer: "svg" })
  updateChart()
}
onMounted(() => {
  drawChart()
})
</script>

<style scoped lang="less"></style>
