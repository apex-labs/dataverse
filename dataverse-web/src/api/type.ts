export interface Result<U> {
  data: U | null | undefined
  result?: U | null | undefined
  message: null | undefined | string
  errorCode: null | undefined | string
  errorData: null | undefined | string
  status: null | undefined | string
  ex: null | undefined | string
  success: Boolean
}
