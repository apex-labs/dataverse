import { get, post } from '@/utils/request'
import { type Result } from '../type'
const isProd = import.meta.env.PROD
const api = isProd ? import.meta.env?.VITE_ADMIN : '/api' + import.meta.env?.VITE_ADMIN

// 数据集成分组
export const etlGroupList = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/etl-job-group/list`, data)
export const addEtlJobGroup = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/etl-job-group/saveEtlJobGroup`, data)
export const editEtlJobGroup = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/etl-job-group/editEtlJobGroup`, data)
export const deleteEtlJobGroup = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/etl-job-group/deleteEtlJobGroup?etlJobGroupId=${data.etlJobGroupId}`, data)
export const listEtlByDatasourceTypeId = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/etl-job-group/listByDatasourceTypeId/${data.datasourceTypeId}`, data)
export const treeEtlJob = (data: any): Promise<Result<any[]>> =>
  post(`${api}/etl-job-group/dataRegion/etlJobGroupTree`, data)
// 集成任务
export const etlList = (data?: any): Promise<Result<any[]>> => post(`${api}/etl-job/page`, data)
export const addEtlJob = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/etl-job/saveEtlJob`, data)
export const editEtlJob = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/etl-job/editEtlJob`, data)
export const updateEtlJobName = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/etl-job/updateEtlJobName`, data)
// 删除etl job
export const deleteEtlJob = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/etl-job-group/deleteEtlJob?etlJobId=${data.etlJobId}`, data)
// 通过数据集成ID查询数据集成详情
export const detailEtlJobVO = (etlJobId: any): Promise<Result<any[]>> =>
  get(`${api}/etl-job/detailEtlJobVO/${etlJobId}`)
// 设置数据集成状态为开发
export const setStatusDevelop = (etlJobId: any): Promise<Result<any[]>> =>
  get(`${api}/etl-job/setStatusDevelop/${etlJobId}`)
// 设置数据集成状态为下线
export const setStatusDownLine = (etlJobId: any): Promise<Result<any[]>> =>
  get(`${api}/etl-job/setStatusDownLine/${etlJobId}`)
// 设置数据集成状态为上线
export const setStatusOnline = (etlJobId: any): Promise<Result<any[]>> =>
  get(`${api}/etl-job/setStatusOnline/${etlJobId}`)
// 设置数据集成状态为加入调度
export const setStatusSchedule = (etlJobId: any): Promise<Result<any[]>> =>
  get(`${api}/etl-job/setStatusSchedule/${etlJobId}`)
// 设置数据集成状态为测试
export const setStatusTest = (etlJobId: any): Promise<Result<any[]>> =>
  get(`${api}/etl-job/setStatusTest/${etlJobId}`)
// 表创建模式枚举
export const createModeEnum = (): Promise<Result<any[]>> => get(`${api}/etl-job/createModeEnum`)
// 流式创建枚举
export const isStreamEnum = (): Promise<Result<any[]>> => get(`${api}/etl-job/isStreamEnum`)
// 任务生命周期状态枚举
export const jobLifecycleEnum = (): Promise<Result<any[]>> => get(`${api}/etl-job/jobLifecycleEnum`)

// 数据开发分组
export const bdmGroupList = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/bdm-job-group/list`, data)
export const addBdmJobGroup = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/bdm-job-group/saveBdmJobGroup`, data)
export const editBdmJobGroup = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/bdm-job-group/editBdmJobGroup`, data)
export const deleteBdmJobGroup = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/bdm-job-group/deleteBdmJobGroup?bdmJobGroupId=${data.bdmJobGroupId}`, data)
export const listBdmByDataRegionCode = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/bdm-job-group/getBdmJobGroupList?dataRegionCode=${data.dataRegionCode}`, data)
// 开发任务
export const bdmList = (data?: any): Promise<Result<any[]>> => post(`${api}/bdm-job/page`, data)
export const addBdmJob = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/bdm-job/saveBdmJob`, data)
export const editBdmJob = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/bdm-job/editBdmJob`, data)
export const updateBdmJobName = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/bdm-job/updateBdmJobName`, data)
// 删除bdm Job
export const deleteBdmJob = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/bdm-job/deleteBdmJob?bdmJobId=${data.bdmJobId}`, data)
// 通过数据集成ID查询建模作业详情
export const detailBdmJobVO = (bdmJobId: any): Promise<Result<any[]>> =>
  get(`${api}/bdm-job/detailBdmJobVO/${bdmJobId}`)
// 设置数据建模作业状态
export const setBdmJobStatus = (data: any): Promise<Result<any[]>> =>
  get(`${api}/bdm-job/setBdmJobStatus/${data.bdmJobId}/${data.status}`)
// 单次执行SQL语句
export const executeSql = (data: any): Promise<Result<any[]>> =>
  post(`${api}/bdm-job/executeSql`, data)
// 查询表结构
export const displayTableStructure = (data: any): Promise<Result<any[]>> =>
  post(`${api}/bdm-job/displayTableStructure`, data)
// 展示数据集成列表树
export const treeBdmJob = (data: any): Promise<Result<any[]>> =>
  post(`${api}/bdm-job/treeBdmJob?dataRegionCode=${data.dataRegionCode}`, data)

// 调度任务列表
export const schedulePage = (data: any): Promise<Result<any[]>> =>
  post(`${api}/job-schedule/page`, data)
export const saveJobSchedule = (data: any): Promise<Result<any[]>> =>
  post(`${api}/job-schedule/saveJobSchedule`, data)
export const deleteSchedule = (data: any): Promise<Result<any[]>> =>
  post(`${api}/job-schedule/deleteSchedule/${data.scheduleId}`, data)
// 根据jobcode获取调度code
export const scheduleCodeByJobCode = (data: any): Promise<Result<any[]>> =>
  get(`${api}/job-schedule/scheduleCodeByJobCode/${data.env}/${data.jobCode}`)
// 检查作业是否加入调度
export const checkJobAddSchedule = (data: any): Promise<Result<any[]>> =>
  get(`${api}/job-schedule/checkJobAddSchedule/${data.env}/${data.jobCode}`)
// 添加调度节点
export const saveOrUpdateScheduleNode = (data: any): Promise<Result<any[]>> =>
  post(`${api}/job-schedule/saveOrUpdateScheduleNode`, data)
// 移除调度节点
export const removeScheduleNode = (data: any): Promise<Result<any[]>> =>
  post(`${api}/job-schedule/removeScheduleNode`, data)
// 移除调度依赖
export const removeDependence = (data: any): Promise<Result<any[]>> =>
  post(`${api}/job-schedule/removeDependence`, data)
// 查看调度任务节点及依赖列表
export const scheduleInfo = (data: any): Promise<Result<any[]>> =>
  post(`${api}/job-schedule/scheduleInfo`, data)
// 更新调度依赖生效/失效
export const updateNodeDependence = (data: any): Promise<Result<any[]>> =>
  post(`${api}/job-schedule/updateNodeDependence`, data)
// 生成调度树结构数据查询
export const jobScheduleInfo = (data: any): Promise<Result<any[]>> =>
  post(`${api}/job-schedule/jobScheduleInfo`, data)
// 设置调度任务状态
export const setJobStatus = (data: any): Promise<Result<any[]>> =>
  get(`${api}/job-schedule/setJobStatus/${data.scheduleId}/${data.status}`)
// 调度发布列表展示
export const deploySchedulePage = (data: any): Promise<Result<any[]>> =>
  get(`${api}/job-schedule/publishSchedule?scheduleCode=${data.scheduleCode}`)
