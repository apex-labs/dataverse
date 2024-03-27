<template>
  <div class="g-card h100 p10 ova">
    <div class="overview">
      <div class="flex list">
        <div class="card item flex fdc space m10">
          <div class="tit fxbw aic">
            <span>数据空间数</span>
            <router-link to="/planning?type=1">进入>></router-link>
          </div>
          <PieChart class="con flex1" :options="spaceChartData" />
        </div>
        <div class="card item flex fdc region m10">
          <div class="tit fxbw aic">
            <span>数据域数量</span>
            <router-link to="/planning?type=4">进入>></router-link>
          </div>
          <PieChart class="con flex1" :options="regionChartData" />
        </div>
        <div class="card item flex fdc source m10">
          <div class="tit fxbw aic">
            <span>数据源数量</span>
            <router-link to="/planning?type=3">进入>></router-link>
          </div>
          <div class="flex1 flex">
            <div class="flex1 source-con flex fdc ovh">
              <BarChart class="flex1" :options="sourceBarData" />
            </div>
            <PieChart class="con item" :options="sourceChartData" />
          </div>
        </div>
        <div class="card item flex fdc storage m10">
          <div class="tit flex fdc">
            <p>存储区数量</p>
            <p class="count">{{ statisticsData[5].num }}</p>
            <p><router-link to="/config?type=1">进入>></router-link></p>
          </div>
          <div class="con flex1 ova">
            <div class="storage-item" v-for="(storage, i) in storageData" :key="i">
              <p>存储区ID: {{ storage.storageId }}</p>
              <p>名称: {{ storage.storageName }}</p>
              <p>存储区类型: {{ storage.storageType }}</p>
            </div>
          </div>
        </div>
      </div>
      <div class="card flex fdc m10 mt10">
        <EtlChart :options="{ color: ['#112C1B', '#164D56', '#5A3EC8', '#9B82F3', '#EFCEF3', '#3C6DF0'] }">
          <PieChart class="w100 h100" :options="etlJobChartData" />
        </EtlChart>
      </div>
      <div class="card flex fdc m10 mt20">
        <BdmChart :options="{ color: ['#112C1B', '#164D56', '#5A3EC8', '#9B82F3', '#EFCEF3', '#3C6DF0'] }">
          <PieChart class="w100 h100" :options="bdmJobChartData" />
        </BdmChart>
      </div>
      <div class="card flex fdc m10 mt20">
        <CommandChart />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="overview">
import { ref, reactive, watch, onMounted } from 'vue'
import EtlChart from './components/EtlChart.vue'
import BdmChart from './components/BdmChart.vue'
import CommandChart from './components/CommandChart.vue'
import PieChart from '@/components/Echarts/PieChart.vue'
import BarChart from '@/components/Echarts/BarChart.vue'
import { getStatistics } from '@/api/modules/dashboard'
import { storageList } from '@/api/modules/config'
import { sourceList } from '@/api/modules/planning'

// 获取数据源
const sourceData = ref<any[]>([])
const getSourceList = () => {
  const params = {
    dataSourceReadAndWrite: null,
    dataSourceTypeId: null,
    keyword: '',
    pageQueryParam: {
      ascs: [],
      descs: [],
      pageNo: 1,
      size: 100
    }
  }
  sourceList(params).then((res: any) => {
    if (res.responseStatus === 200) {
      const data: any = res.data
      sourceData.value = data?.list || []
    }
  })
}
onMounted(() => {
  getSourceList()
})

// 获取存储区
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
onMounted(() => {
  getStorageList()
})

const color = ['#473793', '#3C6DF0', '#48D4BB']
const tooltip = {
  trigger: "item",
}
const legend = {
  show: false,
  left: "center",
  top: "bottom",
  data: ["Basic", "Dev", "Prod"],
  textStyle: {
    color: "#a1a1a1"
  }
}
const statisticsData = ref<any[]>([
  { title: '数据空间数', num: 0, unit: '个' },
  { title: '数据域个数', num: 0, unit: '个' },
  { title: '数据源个数', num: 0, unit: '个' },
  { title: '集成作业数', num: 0, unit: '个' },
  { title: '开发作业数', num: 0, unit: '个' },
  { title: '存储区个数', num: 0, unit: '个' }
])
const spaceChartData = reactive({
  title: {
    show: false
  },
  color, tooltip, legend,
  series: [
    {
      type: 'pie',
      radius: ["70%", "88%"],  // [30,180] 饼图面积
      center: ["50%", "50%"],
      labelLine: {
        show: false
      },
      label: {
        show: false,
        position: 'center'
      },
      data: [{ value: 0, name: 'Basic' }, { value: 0, name: 'Dev' }, { value: 0, name: 'Prod' }]
    }
  ]
})
const regionChartData = reactive({
  title: {
    show: false
  },
  color, tooltip, legend,
  series: [
    {
      type: 'pie',
      radius: ["70%", "88%"],  // [30,180] 饼图面积
      center: ["50%", "50%"],
      labelLine: {
        show: false
      },
      label: {
        show: false,
        position: 'center'
      },
      data: [{ value: 0, name: 'Basic' }, { value: 0, name: 'Dev' }, { value: 0, name: 'Prod' }]
    }
  ]
})
// 数据源饼图
const sourceChartData = reactive({
  title: {
    show: false
  },
  color, tooltip, legend,
  series: [
    {
      type: 'pie',
      radius: ["70%", "88%"],  // [30,180] 饼图面积
      center: ["50%", "50%"],
      labelLine: {
        show: false
      },
      label: {
        show: false,
        position: 'center'
      },
      data: [{ value: 0, name: 'Basic' }, { value: 0, name: 'Dev' }, { value: 0, name: 'Prod' }]
    }
  ]
})
// 数据源柱状图
const sourceBarData = reactive({
  title: {
    show: false
  },
  color: ['#44216A', '#473793', '#3C6DF0', '#9B82F3', '#EFCEF3', '#5A3EC8'], tooltip, legend,
  xAxis: [
    {
      type: 'category',
      data: ['MySQL', 'PostgreSQL', 'Oracle', 'Doris', 'Clickhouse', 'Others'],
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
      type: 'bar',
      barWidth: 10,
      data: [
        { value: 0, name: 'MySQL', itemStyle: { color: '#44216A' } },
        { value: 0, name: 'PostgreSQL', itemStyle: { color: '#473793' } },
        { value: 0, name: 'Oracle', itemStyle: { color: '#3C6DF0' } },
        { value: 0, name: 'Doris', itemStyle: { color: '#9B82F3' } },
        { value: 0, name: 'Clickhouse', itemStyle: { color: '#EFCEF3' } },
        { value: 0, name: 'Others', itemStyle: { color: '#5A3EC8' } },
      ]
    },
  ]
})
watch(
  () => sourceData,
  () => {
    const data = [
      { value: 0, name: 'MySQL', itemStyle: { color: '#44216A' } },
      { value: 0, name: 'PostgreSQL', itemStyle: { color: '#473793' } },
      { value: 0, name: 'Oracle', itemStyle: { color: '#3C6DF0' } },
      { value: 0, name: 'Doris', itemStyle: { color: '#9B82F3' } },
      { value: 0, name: 'Clickhouse', itemStyle: { color: '#EFCEF3' } },
      { value: 0, name: 'Others', itemStyle: { color: '#5A3EC8' } }
    ]
    sourceData.value.forEach((e: any) => {
      switch (e.datasourceTypeName.toLocaleLowerCase()) {
        case 'mysql':
          data[0].value = data[0].value + 1
          break;
        case 'postgresql':
          data[1].value = data[1].value + 1
          break;
        case 'oracle':
          data[2].value = data[2].value + 1
          break;
        case 'doris':
          data[3].value = data[3].value + 1
          break;
        case 'clickhouse':
          data[4].value = data[4].value + 1
          break;

        default:
          data[5].value = data[5].value + 1
          break;
      }
    })
    sourceBarData.series[0].data = data
  },
  { deep: true }
)
const etlJobChartData = reactive({
  title: {
    show: false
  },
  color, tooltip, legend,
  series: [
    {
      type: 'pie',
      radius: ["70%", "88%"],  // [30,180] 饼图面积
      center: ["50%", "50%"],
      labelLine: {
        show: false
      },
      label: {
        show: false,
        position: 'center'
      },
      data: [{ value: 0, name: 'Basic' }, { value: 0, name: 'Dev' }, { value: 0, name: 'Prod' }]
    }
  ]
})
const bdmJobChartData = reactive({
  title: {
    show: false
  },
  color, tooltip, legend,
  series: [
    {
      type: 'pie',
      radius: ["70%", "88%"],  // [30,180] 饼图面积
      center: ["50%", "50%"],
      labelLine: {
        show: false
      },
      label: {
        show: false,
        position: 'center'
      },
      data: [{ value: 0, name: 'Basic' }, { value: 0, name: 'Dev' }, { value: 0, name: 'Prod' }]
    }
  ]
})

const getStatisticsFn = () => {
  getStatistics().then((res: any) => {
    if (res.responseStatus === 200) {
      const { bdmJobStatisticsVO, dataRegionStatisticsVO, datasourceStatisticsVO, dvsStatisticsVO, etlJobStatisticsVO, storageStatisticsVO } = res.data
      // 数据空间
      const { dvsCount, basicDvsCount, devDvsCount, prodDvsCount } = dvsStatisticsVO
      statisticsData.value[0].num = dvsCount
      spaceChartData.series[0].data = [{ value: basicDvsCount || 0, name: 'Basic' }, { value: devDvsCount || 0, name: 'Dev' }, { value: prodDvsCount || 0, name: 'Prod' }]
      // 数据域
      const { dataRegionCount, basicDataRegionCount, devDataRegionCount, prodDataRegionCount } = dataRegionStatisticsVO
      statisticsData.value[1].num = dataRegionCount
      regionChartData.series[0].data = [{ value: basicDataRegionCount || 0, name: 'Basic' }, { value: devDataRegionCount || 0, name: 'Dev' }, { value: prodDataRegionCount || 0, name: 'Prod' }]
      // 数据源
      const { dataSourceCount, basicDataSourceCount, devDataSourceCount, prodDataSourceCount } = datasourceStatisticsVO
      statisticsData.value[2].num = dataSourceCount
      sourceChartData.series[0].data = [{ value: basicDataSourceCount || 0, name: 'Basic' }, { value: devDataSourceCount || 0, name: 'Dev' }, { value: prodDataSourceCount || 0, name: 'Prod' }]
      // 集成作业
      const { etlJobCount, basicEtlJobCount, devEtlJobCount, prodEtlJobCount } = etlJobStatisticsVO
      statisticsData.value[3].num = etlJobCount
      etlJobChartData.series[0].data = [{ value: basicEtlJobCount || 0, name: 'Basic' }, { value: devEtlJobCount || 0, name: 'Dev' }, { value: prodEtlJobCount || 0, name: 'Prod' }]
      // 开发作业
      const { bdmJobCount, basicBdmJobCount, devBdmJobCount, prodBdmJobCount } = bdmJobStatisticsVO
      statisticsData.value[4].num = bdmJobCount
      bdmJobChartData.series[0].data = [{ value: basicBdmJobCount || 0, name: 'Basic' }, { value: devBdmJobCount || 0, name: 'Dev' }, { value: prodBdmJobCount || 0, name: 'Prod' }]
      // 存储区
      statisticsData.value[5].num = storageStatisticsVO.storageCount
    }
  })
}
onMounted(() => {
  getStatisticsFn()
})
</script>

<style scoped lang="less">
.overview {
  min-width: 1400px;
}

.card {
  height: 360px;
  border-radius: 2px;
  background: var(--bg-color-2);
  border: 1px solid var(--border-color-3);
}

.statistic-list {
  .statistic-item {
    border-radius: 5px;
    background: var(--bg-color-2);
    padding: 20px;
    margin: 10px;
  }
}

.list {
  flex-wrap: nowrap;

  .item {
    width: 240px;

    &.source {
      flex: 1;
    }

    &.storage {
      color: #fff;
      background: url('@/assets/imgs/storage_bg.png');
      background-size: cover;

      .tit {
        height: 164px;
        padding: 20px;
        border-bottom: none;

        a {
          color: #fff;

          &:hover {
            color: #fff;
          }
        }

        .count {
          font-size: 36px;
          line-height: 1;
          margin: 24px 0 20px;
        }
      }

      .con {
        padding: 20px;

        .storage-item {
          line-height: 1.3;
          margin: 10px 0;
        }
      }
    }

    .tit {
      font-size: 14px;
      height: 61px;
      padding: 0 20px;
      border-bottom: 1px solid var(--border-color);

      a {
        color: var(--text-color-4);
        font-size: 12px;

        &:hover {
          color: var(--text-color-hover);
        }
      }
    }

    .con {
      padding: 20px 0;
    }

  }
}
</style>
