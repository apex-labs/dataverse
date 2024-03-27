import { get, post } from '@/utils/request'
import { type Result } from '../type'
const isProd = import.meta.env.PROD
const api = isProd ? import.meta.env?.VITE_ADMIN : '/api' + import.meta.env?.VITE_ADMIN

// 看板统计
export const getStatistics = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/dashboard/statistics`, data)
// 看板统计开发作业任务
export const getStatisticsBdmJob = (data: any): Promise<Result<any[]>> =>
  post(`${api}/dashboard/statisticsBdmJob`, data)
// 看板统计集成作业任务
export const getStatisticsEtlJob = (data: any): Promise<Result<any[]>> =>
  post(`${api}/dashboard/statisticsEtlJob`, data)
// 看板统计执行命令
export const getStatisticsExeCmd = (data: any): Promise<Result<any[]>> =>
  post(`${api}/dashboard/statisticsExeCmd`, data)
