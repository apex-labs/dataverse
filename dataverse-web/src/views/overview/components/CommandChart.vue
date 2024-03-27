<template>
  <div class="flex1 command flex fdc">
    <div class="tit fxbw aic">
      <span>执行命令</span>
      <a-range-picker v-model:value="time" :disabled-date="disabledDate" :allow-clear="false" format="YYYY-MM-DD"
        valueFormat="YYYY-MM-DD" :placeholder="['开始时间', '结束时间']" @change="changeTime" />
    </div>
    <a-spin :spinning="loading" tip="Loading..." wrapperClassName="flex1 flex fdc">
      <div class="flex1 flex con ais">
        <div class="flex1">
          <LineChart class="w100 h100" :options="chartData" />
        </div>
      </div>
    </a-spin>
  </div>
</template>

<script setup lang="ts" name="overview">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import LineChart from '@/components/Echarts/LineChart.vue'
import { getStatisticsExeCmd } from '@/api/modules/dashboard'
import dayjs, { type Dayjs } from 'dayjs'

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
    top: 100,
    right: 50,
    data: ['成功', '失败'],
    textStyle: {
      color: '#5A6872'
    }
  },
  grid: {
    left: 20,
    right: 150,
    top: 20,
    bottom: 0,
    containLabel: true
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
  series: [
    {
      name: '成功',
      type: 'line',
      barWidth: 10,
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [{
            offset: 0, color: 'rgba(60,109,240,0.36)' // 0% 处的颜色
          }, {
            offset: 1, color: 'rgba(111,166,249,0)' // 100% 处的颜色
          }],
          global: false // 缺省为 false
        }
      },  // 范围面积
      emphasis: {
        focus: 'series'
      },
      data: [32, 33, 30, 33, 39, 30, 40]
    },
    {
      name: '失败',
      type: 'line',
      barWidth: 10,
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
            offset: 1, color: 'rgba(60,109,240,0.16)' // 100% 处的颜色
          }],
          global: false // 缺省为 false
        }
      },  // 范围面积
      emphasis: {
        focus: 'series'
      },
      data: [12, 13, 10, 13, 19, 10, 29]
    },
  ]
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

const getDataFn = async () => {
  if (time.value && time.value[0] && time.value[1]) {
    loading.value = true
    const xAxis = getXAxis(time.value[0], time.value[1])
    const series = chartData.series.map((e: any) => ({ ...e, data: xAxis.map(() => 0) }))
    const params = { startTime: time.value[0], endTime: time.value[1] }
    getStatisticsExeCmd(params).then((res: any) => {
      if (res.responseStatus === 200) {
        const list = res.data || []
        list.forEach((e: any) => {
          const { date, failCount, successCount } = e
          const index = xAxis.indexOf(date)
          series[0].data[index] = successCount
          series[1].data[index] = failCount
        })
      } else {
        message.error(res.errorMsg)
      }
    }).finally(() => {
      chartData.xAxis[0].data = xAxis
      chartData.series = series
      loading.value = false
    })
  }
}
onMounted(() => {
  getDataFn()
})
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
