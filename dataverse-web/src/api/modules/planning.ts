import { get, post } from '@/utils/request'
import { type Result } from '../type'
const isProd = import.meta.env.PROD
const api = isProd ? import.meta.env?.VITE_ADMIN : '/api' + import.meta.env?.VITE_ADMIN

// 数据地图
export const mapList = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/datasource-type-map/listAll`, data)

// 数据源
export const sourceList = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/datasource-parent/page`, data)
export const editSource = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/datasource-parent/editDatasourceParent`, data)
export const addSource = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/datasource-parent/addDatasourceParent`, data)
export const deleteSource = (parentId: string | number): Promise<Result<any[]>> =>
  get(`${api}/datasource-parent/delete/${parentId}`)
export const sourceListByType = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/datasource-parent/listDatasourceParentByType`, data)
export const tableListByType = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/datasource-parent/getDatasourceTable`, data)
export const columnListByType = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/datasource-parent/getDatasourceTableAndColumn`, data)
export const sourceLink = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/datasource-parent/testDatasourceParentLink`, data)

// 数据域
export const regionList = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/data-region/page`, data)
export const editRegion = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/data-region/editDataRegion`, data)
export const addRegion = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/data-region/addDataRegion`, data)
export const deleteRegion = (dataRegionId: string | number): Promise<Result<any[]>> =>
  get(`${api}/data-region/delete/${dataRegionId}`)
export const dwLayer = (): Promise<Result<any[]>> => get(`${api}/data-region/dwLayer`)
export const dwLayerDetail = (): Promise<Result<any[]>> => get(`${api}/data-region/dwLayerDetail`)

// 数据空间
export const spaceList = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/dvs-parent/page`, data)
export const editSpace = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/dvs-parent/editDvsParent`, data)
export const addSpace = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/dvs-parent/addDvsParent`, data)
export const deleteSpace = (parentId: string | number): Promise<Result<any[]>> =>
  get(`${api}/dvs-parent/delete/${parentId}`)
export const spaceDetail = (parentId: string | number): Promise<Result<any[]>> =>
  get(`${api}/dvs-parent/detail/${parentId}`)
export const listStorage = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/dvs-parent/listStorage`, data)

// 根据数据域查询数据表列表
export const getDvsTableList = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/dvs-table/getDvsTableList?dataRegionCode=${data.dataRegionCode}`, data)

// 数据类型
export const typeList = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/data-type-map/list`, data)

// 环境模式
export const listEnv = (): Promise<Result<any[]>> => get(`${api}/env/listEnv`)
export const listModel = (): Promise<Result<any[]>> => get(`${api}/env/listModel`)

// 数据类型列表
export const dataTypeList = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/data-type-map/list`, data)

export {}
