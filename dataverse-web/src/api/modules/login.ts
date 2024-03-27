import { get, post } from '@/utils/request'
import { type Result } from '../type'
const isProd = import.meta.env.PROD
const api = isProd ? import.meta.env?.VITE_ADMIN : '/api' + import.meta.env?.VITE_ADMIN

// login
export const userLogin = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/admin/login/userLogin`, data)
export const validateLoginToken = (data?: any): Promise<Result<any[]>> =>
  post(`${api}/admin/login/validateLoginToken/${data.token}`, data)
