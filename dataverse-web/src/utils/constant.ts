import { ref } from 'vue'

// 分页选项
export const pageSizeOptions = ref<string[]>(['10', '20', '50', '100'])
// 根据数据源类型获取对应图标
export const getIconNameByDatasourceType = (datasourceType: number | string | null) => {
  let r = 'job-mysql'
  switch (datasourceType) {
    case 1:
      r = 'job-mysql'
      break
    case 2:
      r = 'job-mariadb'
      break
    case 3:
      r = 'job-postgresql'
      break
    case 4:
      r = 'job-sql-server'
      break
    case 5:
      r = 'job-oracle'
      break
    case 6:
      r = 'job-db2'
      break
    case 7:
      r = 'job-oceanbase'
      break
    case 8:
      r = 'job-mongodb'
      break
    case 9:
      r = 'job-hive'
      break
    case 10:
      r = 'job-hbase'
      break
    case 11:
      r = 'job-doris'
      break
    case 12:
      r = 'job-elasticsearch'
      break
    case 13:
      r = 'job-clickhouse'
      break
    case 14:
      r = 'job-kafka'
      break

    default:
      r = 'job-pulsar'
      break
  }
  return r
}
