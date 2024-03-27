import { get, post } from '@/utils/request'
import type { DialogData } from '@/interface/dialog'

interface Params {
  id: string | number | null | undefined
}

type AskParams = {
  appId: number
  dialogueId: number
  serviceId: number
  message: string
}

type AskData = {
  completionTokens: number
  created: number
  errorCode: null
  errorMsg: null
  isTruncated: null
  needClearHistory: null
  object: string
  promptTokens: number
  requestId: number
  responseId: number
  responseTime: string
  result: string
  totalTokens: number
}

interface Result<U> {
  data: U | null | undefined
  message: null | undefined | string
  errorCode: null | undefined | string
  errorData: null | undefined | string
  status: null | undefined | string
  ex: null | undefined | string
  success: Boolean
}

type DialogueParams = {
  dialogueId: number
}

type DetailResData = {
  createTime: string
  deptId: number
  deptName: string
  dialogueId: number
  dialogueName: string
  questionAnswers: { requestBody: string; result: any }[]
  tenantId: number
  tenantName: string
  updateTime: string | null
  userId: number
  userName: string
}

type AppList = {
  appId: number
  appName: string
  clientId: string
  clientSecret: string
  createTime: string
  deptId: number
  deptName: string
  method: string
  tenantId: number
  tenantName: string
  tokenUrl: string
  userId: number
  userName: string
}

type ServiceList = {
  apiUrl: string
  appId: number
  createTime: string
  method: string
  price: number
  qps: string
  serviceId: number
  serviceName: string
  serviceType: string
}

export const getAppList = (): Promise<Result<AppList[]>> => get('/cdp-aigc/bce-app/list')

export const getServiceList = (params: Params): Promise<Result<ServiceList[]>> =>
  get(`/cdp-aigc/bce-service/list/${params.id}`)

export const getDialogList = (): Promise<Result<DialogData[]>> => get(`/cdp-aigc/dialogue/list`)

export const postSaveDialog = (data: any): Promise<Result<DialogData>> =>
  post(`/cdp-aigc/dialogue/save`, data)

export const postAskService = (data: AskParams): Promise<Result<AskData>> =>
  post(`/cdp-aigc/bce-service/ask`, data)

export const getDialogueDetail = (params: DialogueParams): Promise<Result<DetailResData>> =>
  get(`/cdp-aigc/dialogue/getDetail/${params.dialogueId}`)
