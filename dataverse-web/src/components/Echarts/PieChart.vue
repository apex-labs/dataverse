<template>
  <div class="flex fdc">
    <div class="pie-chart flex1">
      <div :ref="(el: any) => setRef(el)" class="chart"></div>
      <div class="total">
        <b>{{ total }}</b><br>总数
      </div>
    </div>
    <div class="info flex aic">
      <div class="info-item" v-for="(data, i) in options.series[0].data" :key="i">
        <p class="value">{{ data.value || 0 }}</p>
        <p class="name">{{ data.name }}</p>
      </div>
    </div>
  </div>
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
const total = computed(() => props.options?.series?.[0]?.data?.reduce((prev: any, cur: any) => prev + cur.value, 0) || 0)

let myChart: any = {}

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
      trigger: "item",
      formatter: "{a} <br/>{b} : {c} ({d}%)"
    },
    legend: {
      left: "center",
      top: "bottom",
      data: ["rose 1", "rose 2", "rose 3", "rose 4", "rose 5", "rose 6", "rose 7", "rose 8"],
      textStyle: {
        color: "#a1a1a1"
      }
    },
    // toolbox: {
    //   show: true,
    //   feature: {
    //     mark: { show: true },
    //     dataView: { show: true, readOnly: false },
    //     restore: { show: true },
    //     saveAsImage: { show: true }
    //   }
    // },
    series: [
      {
        type: "pie",
        radius: ["70%", "88%"],  // [30,180] 饼图面积
        center: ["50%", "50%"],
        labelLine: {
          show: false
        },
        label: {
          show: false,
          position: 'center'
        },
        // roseType: "radius", // 玫瑰图
        itemStyle: {
          borderRadius: 0
        },
        emphasis: {
          label: {
            show: true
          }
        },
        data: [
          { value: 40, name: "rose 1" },
          { value: 33, name: "rose 2" },
          { value: 28, name: "rose 3" },
          { value: 22, name: "rose 4" },
          { value: 20, name: "rose 5" },
          { value: 15, name: "rose 6" },
          { value: 12, name: "rose 7" },
          { value: 10, name: "rose 8" }
        ]
      }
    ], ...props.options
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

<style scoped lang="less">
.pie-chart {
  position: relative;

  .chart {
    height: 100%;
    min-height: 200px;
  }

  .total {
    position: absolute;
    text-align: center;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    line-height: 1.2;
    font-size: 14px;

    b {
      font-size: 36px;
      font-weight: 400;
    }
  }
}

.info {
  margin-top: 10px;
  display: flex;
  align-items: center;
  justify-content: center;

  &-item {
    padding: 3px 20px;
    text-align: center;
    border-right: 1px solid var(--border-color-2);

    &:last-child {
      border: none
    }

    .value {
      font-size: 18px;
    }

    .name {
      margin-top: 10px;
      font-size: 12px;
      color: var(--text-color-4);
    }
  }
}
</style>
