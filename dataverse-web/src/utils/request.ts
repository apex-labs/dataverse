import axios, {
  type AxiosInstance,
  type AxiosRequestConfig,
  type InternalAxiosRequestConfig,
  type AxiosResponse,
  type AxiosError
} from 'axios'
import qs from 'qs'
import { notification } from 'ant-design-vue'
import router from '@/router'
import { setState } from '@/utils/storeProps'
import { getStore, serialize } from '@/utils/index'
import pinia from '@/stores'
import { useAppStore } from '@/stores/modules/app'
import { getCookie, isCookieExpired } from '@/utils/cookie'
const token = getCookie('token')
const queue: number[] = []

// const white_apis = ['/code', '/code/check', '/auth/oauth/token']
interface ResultFormat<T = unknown> {
  data: null | T
  err: AxiosError | null
  response: AxiosResponse
  message?: string
  msg?: string
  code?: number
}

interface RequestConfig extends AxiosRequestConfig {
  args?: Record<string, unknown>
}

interface RequestInterface {
  <Payload = unknown>(
    config: RequestConfig
  ): (requestConfig?: Partial<RequestConfig>) => Promise<ResultFormat<Payload>>

  <Payload, Data>(
    config: RequestConfig
  ): (
    requestConfig: Partial<Omit<RequestConfig, 'data'>> & { data: Data }
  ) => Promise<ResultFormat<Payload>>

  <Payload, Data, Params>(
    config: RequestConfig
  ): (
    requestConfig: Partial<Omit<RequestConfig, 'data' | 'params'>> &
      (Data extends undefined ? { data?: undefined } : { data: Data }) & { params: Params }
  ) => Promise<ResultFormat<Payload>>

  <Payload, Data, Params, Args>(
    config: RequestConfig
  ): (
    requestConfig: Partial<Omit<RequestConfig, 'data' | 'params' | 'args'>> &
      (Data extends undefined ? { data?: undefined } : { data: Data }) &
      (Params extends undefined ? { params?: undefined } : { params: Params }) & {
        args: Args
      }
  ) => Promise<ResultFormat<Payload>>
}

const service: AxiosInstance = axios.create({
  baseURL: '/', //import.meta.env.VITE_API,
  timeout: 1000 * 300, // 请求超时时间30s
  withCredentials: true
})

const onRejectedError = (error: AxiosError) => {
  if (error.response) {
    const status = error.response.status
    switch (status) {
      case 401: {
        if (token && !isCookieExpired('token')) {
          notification.error({
            message: '401',
            description: 'token失效，请重新登录' // 'toekn失效，请重新登录'
          })
        } else {
          setState({
            requestCode: 401,
            msg: 'token失效，请重新登录' // msg: 'token失效'
          })
        }
        break
      }
      case 403:
        notification.error({
          message: 'Forbidden',
          description: ''
        })
        break
      case 503:
        if (!queue.length) {
          queue.push(+new Date())
          notification.error({
            message: '系统提示',
            description: '服务端错误 - 503',
            onClose: () => queue.pop()
          })
        }
        break

      default:
        break
    }
  }
  return Promise.reject(error)
}

service.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  // const appStore = useAppStore(pinia)
  // if (appStore.token) {
  //   config.headers['Authorization'] = 'Bearer ' + appStore.token // token
  // }
  // const TENANT_ID = getStore({ name: 'tenantId' })
  // if (TENANT_ID) {
  //   config.headers['TENANT-ID'] = TENANT_ID // 租户ID
  // }
  // 获取业务体系
  // const bizWorks = getStore({ name: 'biz_works' })
  // if (bizWorks?.bussionType) {
  //   config.headers['bizWorkspaceId'] = bizWorks.bussionType // 业务体系ID
  //   config.headers['bizWorkspaceName'] = encodeURIComponent(bizWorks.bussionName) // 业务体系名称
  // } else {
  //   config.headers['bizWorkspaceId'] = 0 // 体系id
  //   config.headers['bizWorkspaceName'] = 'test' // 体系名称
  // }

  // headers中配置serialize为true开启序列化
  if (config.method === 'post' && config.headers.serialize) {
    config.data = serialize(config.data)
    delete config.data.serialize
  }

  if (config.method === 'get') {
    config.paramsSerializer = function (params) {
      return qs.stringify(params, { arrayFormat: 'repeat' })
    }
  }
  return config
}, onRejectedError)

service.interceptors.response.use((response: AxiosResponse) => {
  const { status, data } = response
  const message = data.msg || data.message || data.errorMsg || ''
  if (status === 200 || data.responseStatus === 200) {
    return data
  } else {
    notification.error({
      description: message,
      message: undefined
    })
    return Promise.reject(new Error(message))
  }
}, onRejectedError)

const get = <T = any, R = AxiosResponse<T>, D = any>(
  url: string,
  config?: AxiosRequestConfig<D>
): Promise<R> => service.get(url, config)

const post = <T = any, R = AxiosResponse<T>, D = any>(
  url: string,
  data?: D,
  config?: AxiosRequestConfig<D>
): Promise<R> => service.post(url, data, config)

const axiosHead = (url: string, config?: AxiosRequestConfig) => service.head(url, config)

const axiosDelete = (url: string, config?: AxiosRequestConfig) => service.delete(url, config)

const axiosPut = (url: string, config?: AxiosRequestConfig) => service.put(url, config)

const request: RequestInterface = <T>(config: RequestConfig) => {
  return async (requestConfig?: Partial<RequestConfig>) => {
    const mergedConfig: RequestConfig = {
      ...config,
      ...requestConfig,
      headers: {
        ...config.headers,
        ...requestConfig?.headers
      }
    }
    // 统一处理返回类型
    try {
      const response: AxiosResponse<T, RequestConfig> = await service.request<T>(mergedConfig)
      const { data } = response
      return <ResultFormat>{ err: null, data, response }
    } catch (err: unknown) {
      return <ResultFormat>(<unknown>{ err, data: null, response: null })
    }
  }
}

export { service as axios, get, post, axiosHead, axiosDelete, axiosPut, request }
