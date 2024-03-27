<template>
  <div :ref="(el: any) => setRef(el)" class="card content-box"></div>
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
        type: "cross",
        label: {
          backgroundColor: "#6a7985"
        }
      }
    },
    legend: {
      icon: 'roundRect',
      data: ["Email", "Union Ads", "Video Ads", "Direct", "Search Engine"],
      textStyle: {
        color: "#5A6872"
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: [
      {
        type: 'category',
        data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
        axisLabel: {
          color: '#5A6872'
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
        name: "Email",
        type: "line",
        stack: "Total",
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [{
              offset: 0, color: 'rgba(111,166,249,0)' // 0% 处的颜色
            }, {
              offset: 1, color: 'rgba(60,109,240,0.36)' // 100% 处的颜色
            }],
            global: false // 缺省为 false
          }
        },  // 范围面积
        emphasis: {
          focus: "series"
        },
        data: [120, 132, 101, 134, 90, 230, 210]
      },
      {
        name: "Union Ads",
        type: "line",
        stack: "Total",
        // areaStyle: {},  // 范围面积
        emphasis: {
          focus: "series"
        },
        data: [220, 182, 191, 234, 290, 330, 310]
      },
      {
        name: "Video Ads",
        type: "line",
        stack: "Total",
        // areaStyle: {},  // 范围面积
        emphasis: {
          focus: "series"
        },
        data: [150, 232, 201, 154, 190, 330, 410]
      },
      {
        name: "Direct",
        type: "line",
        stack: "Total",
        // areaStyle: {},  // 范围面积
        emphasis: {
          focus: "series"
        },
        data: [320, 332, 301, 334, 390, 330, 320]
      },
      {
        name: "Search Engine",
        type: "line",
        stack: "Total",
        label: {
          show: true,
          position: "top"
        },
        // areaStyle: {},  // 范围面积
        emphasis: {
          focus: "series"
        },
        data: [820, 932, 901, 934, 1290, 1330, 1320]
      }
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
