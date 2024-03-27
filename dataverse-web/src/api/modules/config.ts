import { get, post } from '@/utils/request'
import { type Result } from '../type'
const isProd = import.meta.env.PROD
const api = isProd ? import.meta.env?.VITE_MANAGE : '/api' + import.meta.env?.VITE_MANAGE

// 存储区
export const storageList = (data?: any): Promise<Result<any[]>> => post(`${api}/storage/page`, data)
export const editStorage = (data?: any): Promise<Result<any[]>> => post(`${api}/storage/edit`, data)
export const addStorage = (data?: any): Promise<Result<any[]>> => post(`${api}/storage/add`, data)
export const storageDetail = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/storage/detail`, data)
export const engineTypeList = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/storage/engineTypeList`, data)
export const storageTypeList = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/storage/storageTypeList`, data)
export const connTypeList = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/storage/connTypeList`, data)
export const tenants = (data?: any): Promise<Result<any[]>> => post(`${api}/storage/tenants`, data)

// 连接器port分组接口
export const listPortGroup = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/port-group/pageList`, data)
export const addPortGroup = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/port-group/add`, data)
export const editPortGroup = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/port-group/edit`, data)
export const deletePortGroup = (groupId: any): Promise<Result<any[]>> =>
  get(`${api}/port-group/delete/${groupId}`)
export const detailPortGroup = (groupId: any): Promise<Result<any[]>> =>
  get(`${api}/port-group/detail/${groupId}`)

// 连接器port相关接口
export const listPort = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/dvs-port/pageList`, data)
export const addStoragePort = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/dvs-port/addStoragePort`, data)
export const removeStoragePort = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/dvs-port/removeStoragePort`, data)
export const addPort = (data?: any): Promise<Result<any[]>> => post(`${api}/dvs-port/add`, data)
export const editPort = (data?: any): Promise<Result<any[]>> => post(`${api}/dvs-port/edit`, data)
export const deletePort = (portId?: any): Promise<Result<any[]>> =>
  get(`${api}/dvs-port/delete/${portId}`)
export const detailPort = (portId?: any): Promise<Result<any[]>> =>
  get(`${api}/dvs-port/detail/${portId}`)
