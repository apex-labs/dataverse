<template>
  <div class="flex1 job-item flex fdc">
    <div class="tit fxbw aic">
      <span>集成作业</span>
      <a-range-picker v-model:value="time" :disabled-date="disabledDate" :allow-clear="false" format="YYYY-MM-DD"
        valueFormat="YYYY-MM-DD" :placeholder="['开始时间', '结束时间']" @change="changeTime" />
    </div>
    <a-spin :spinning="loading" tip="Loading..." wrapperClassName="flex1 flex fdc">
      <div class="flex1 flex con ais">
        <div class="flex1">
          <BarChart class="w100 h100" :options="chartData" />
        </div>
        <div style="width:290px">
          <slot></slot>
        </div>
      </div>
    </a-spin>
  </div>
</template>

<script setup lang="ts" name="overview">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import BarChart from '@/components/Echarts/BarChart.vue'
import { getStatisticsEtlJob } from '@/api/modules/dashboard'
import dayjs, { type Dayjs } from 'dayjs'

const props = defineProps({
  options: { type: Object, default: () => ({}) }
})

const startTime = dayjs().subtract(6, 'day').format('YYYY-MM-DD')
const endTime = dayjs().format('YYYY-MM-DD')
// 集成作业
const chartData = reactive({
  legend: {
    show: true,
    orient: 'vertical',
    icon: 'rect',
    itemWidth: 10,
    itemHeight: 10,
    top: 60,
    right: 40,
    data: ['失败-BASIC', '失败-DEV', '失败-PROD', '成功-BASIC', '成功-DEV', '成功-PROD'],
    textStyle: {
      color: '#5A6872'
    }
  },
  xAxis: [
    {
      type: 'category',
      data: ['2024-03-07', '2024-03-08', '2024-03-09', '2024-03-10', '2024-03-11', '2024-03-12', '2024-03-13'],
      axisLabel: {
        color: '#5A6872',
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
  grid: {
    left: 20,
    right: 200,
    top: 20,
    bottom: 0,
    containLabel: true
  },
  // grid: {
  //   left: '3%',
  //   right: '3%',
  //   top: '3%',
  //   bottom: 60,
  //   containLabel: true
  // },
  // dataZoom: [
  //   {
  //     type: 'slider',
  //     maxValueSpan: 7,
  //     height: 10,
  //     bottom: 30,
  //     brushSelect: false,
  //     showDetail: false,
  //   },
  // ],
  series: [
    {
      name: '失败-BASIC',
      type: 'bar',
      barWidth: 10,
      emphasis: {
        focus: 'series'
      },
      data: [0, 0, 0, 0, 0, 0, 0]
    },
    {
      name: '失败-DEV',
      type: 'bar',
      barWidth: 10,
      // stack: 'Ad',
      emphasis: {
        focus: 'series'
      },
      data: [0, 0, 0, 0, 0, 0, 0]
    },
    {
      name: '失败-PROD',
      type: 'bar',
      barWidth: 10,
      // stack: 'Ad',
      emphasis: {
        focus: 'series'
      },
      data: [0, 0, 0, 0, 0, 0, 0]
    },
    {
      name: '成功-BASIC',
      type: 'bar',
      barWidth: 10,
      // stack: 'Ad',
      emphasis: {
        focus: 'series'
      },
      data: [0, 0, 0, 0, 0, 0, 0]
    },
    {
      name: '成功-DEV',
      type: 'bar',
      barWidth: 10,
      // stack: 'Ad',
      emphasis: {
        focus: 'series'
      },
      data: [0, 0, 0, 0, 0, 0, 0]
    },
    {
      name: '成功-PROD',
      type: 'bar',
      barWidth: 10,
      // stack: 'Ad',
      emphasis: {
        focus: 'series'
      },
      data: [0, 0, 0, 0, 0, 0, 0]
    },
  ],
  ...props.options
})
const loading = ref(false)
const time = ref<string[]>([startTime, endTime])
const disabledDate = (current: Dayjs) => {
  if (!time.value || (time.value as any).length === 0) {
    return false;
  }
  const tooLate = time.value[0] && current.diff(dayjs(time.value[0]), 'days') > 30;
  const tooEarly = time.value[1] && dayjs(time.value[1]).diff(current, 'days') > 30;
  return tooEarly || tooLate || (current && current > dayjs().endOf('day'))
}
const getXAxis = (startTime: string, endTime: string) => {
  const r = [startTime]
  let next = dayjs(startTime).add(1, 'day')
  while (next.isBefore(dayjs(endTime))) {
    r.push(next.format('YYYY-MM-DD'))
    next = next.add(1, 'day')
  }
  r.push(endTime)
  return r
}
const changeTime = () => {
  getDataFn()
}

const getDataFn = () => {
  if (time.value && time.value[0] && time.value[1]) {
    loading.value = true
    const xAxis: any = getXAxis(time.value[0], time.value[1])
    chartData.xAxis[0].data = xAxis
    const params = { startTime: time.value[0], endTime: time.value[1] }
    getStatisticsEtlJob(params).then((res: any) => {
      if (res.responseStatus === 200) {
        const list = res.data || []
        const series = chartData.series.map((e: any) => ({ ...e, data: xAxis.map(() => 0) }))
        list.forEach((e: any) => {
          const { date, basicFailJobCount, basicSuccessJobCount, devFailJobCount, devSuccessJobCount, prodFailJobCount, prodSuccessJobCount } = e
          const index = xAxis.indexOf(date)
          series[0].data[index] = basicFailJobCount
          series[1].data[index] = devFailJobCount
          series[2].data[index] = prodFailJobCount
          series[3].data[index] = basicSuccessJobCount
          series[4].data[index] = devSuccessJobCount
          series[5].data[index] = prodSuccessJobCount
        })
        chartData.series = series
      } else {
        message.error(res.errorMsg)
      }
    }).finally(() => {
      loading.value = false
    })
  }
}
getDataFn()
// onMounted(() => {
// })
</script>

<style scoped lang="less">
.tit {
  font-size: 14px;
  height: 61px;
  padding: 0 20px;
  border-bottom: 1px solid var(--border-color);
}

.con {
  padding: 20px 0;
}
</style>
