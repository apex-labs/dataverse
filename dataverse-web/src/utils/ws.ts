import { message } from 'ant-design-vue'

export type Callback = (e: Event) => void
export type MessageCallback<RT> = (e: RT) => void

export interface Ioptions<RT> {
  url: string | null // 链接的通道的地址
  heartTime?: number // 心跳时间间隔
  heartMsg?: string // 心跳信息,默认为'ping'
  isReconnect?: boolean // 是否自动重连
  isRestory?: boolean // 是否销毁
  reconnectTime?: number // 重连时间间隔
  reconnectCount?: number // 重连次数 -1 则不限制
  openCb?: Callback // 连接成功的回调
  closeCb?: Callback // 关闭的回调
  messageCb?: MessageCallback<RT> // 消息的回调
  errorCb?: Callback // 错误的回调
}

/**
 * 心跳基类
 */

export class Heart {
  heartTimeOut!: number // 心跳计时器
  ServerHeartTimeOut!: number // 心跳计时器
  timeout = 5000
  // 重置
  reset(): void {
    clearTimeout(this.heartTimeOut)
    clearTimeout(this.ServerHeartTimeOut)
  }

  /**
   * 启动心跳
   * @param {Function} cb 回调函数
   */
  start(cb: Callback): void {
    this.heartTimeOut = setTimeout((e: Event) => {
      cb(e)
      this.ServerHeartTimeOut = setTimeout((e: Event) => {
        cb(e)
        // 重新开始检测
        this.reset()
        this.start(cb)
      }, this.timeout)
    }, this.timeout)
  }
}

export default class Socket<T, RT> extends Heart {
  ws!: WebSocket

  reconnectTimer = 0 // 重连计时器
  reconnectCount = 10 // 变量保存，防止丢失

  options: Ioptions<RT> = {
    url: null, // 链接的通道的地址
    heartTime: 5000, // 心跳时间间隔
    heartMsg: 'ping', // 心跳信息,默认为'ping'
    isReconnect: true, // 是否自动重连
    isRestory: false, // 是否销毁
    reconnectTime: 5000, // 重连时间间隔
    reconnectCount: 5, // 重连次数 -1 则不限制
    openCb: (e: Event) => {
      console.log('连接成功的默认回调::::', e)
    }, // 连接成功的回调
    closeCb: (e: Event) => {
      console.log('关闭的默认回调::::', e)
    }, // 关闭的回调
    messageCb: (e: RT) => {
      console.log('连接成功的默认回调::::', e)
    }, // 消息的回调
    errorCb: (e: Event) => {
      console.log('错误的默认回调::::', e)
    } // 错误的回调
  }

  constructor(ops: Ioptions<RT>) {
    super()
    Object.assign(this.options, ops)
    this.create()
  }

  /**
   * 建立连接
   */
  create(): void {
    if (!('WebSocket' in window)) {
      throw new Error('当前浏览器不支持，无法使用')
    }
    if (!this.options.url) {
      throw new Error('地址不存在，无法建立通道')
    }
    // this.ws = null
    this.ws = new WebSocket(this.options.url)
    this.onopen(this.options.openCb as Callback)
    this.onclose(this.options.closeCb as Callback)
    this.onmessage(this.options.messageCb as MessageCallback<RT>)
  }

  /**
   * 自定义连接成功事件
   * 如果callback存在，调用callback，不存在调用OPTIONS中的回调
   * @param {Function} callback 回调函数
   */
  onopen(callback: Callback): void {
    this.ws.onopen = (event) => {
      clearTimeout(this.reconnectTimer) // 清除重连定时器
      this.options.reconnectCount = this.reconnectCount // 计数器重置
      // 建立心跳机制
      super.reset()
      super.start(() => {
        this.send(this.options.heartMsg as string)
      })
      if (typeof callback === 'function') {
        callback(event)
      } else {
        typeof this.options.openCb === 'function' && this.options.openCb(event)
      }
    }
  }

  /**
   * 自定义关闭事件
   * 如果callback存在，调用callback，不存在调用OPTIONS中的回调
   * @param {Function} callback 回调函数
   */
  onclose(callback: Callback): void {
    this.ws.onclose = (event) => {
      super.reset()
      !this.options.isRestory && this.onreconnect()
      if (typeof callback === 'function') {
        callback(event)
      } else {
        typeof this.options.closeCb === 'function' && this.options.closeCb(event)
      }
    }
  }

  /**
   * 自定义错误事件
   * 如果callback存在，调用callback，不存在调用OPTIONS中的回调
   * @param {Function} callback 回调函数
   */
  onerror(callback: Callback): void {
    this.ws.onerror = (event) => {
      if (typeof callback === 'function') {
        callback(event)
      } else {
        typeof this.options.errorCb === 'function' && this.options.errorCb(event)
      }
    }
  }

  /**
   * 自定义消息监听事件
   * 如果callback存在，调用callback，不存在调用OPTIONS中的回调
   * @param {Function} callback 回调函数
   */
  onmessage(callback: MessageCallback<RT>): void {
    this.ws.onmessage = (event: MessageEvent<string>) => {
      const strMessage = event.data
      const { code, data, msg }: any = JSON.parse(strMessage)
      if (code === 200) {
        // 收到任何消息，重新开始倒计时心跳检测
        super.reset()
        super.start(() => {
          this.send(this.options.heartMsg as string)
        })
        console.log(data, 'onmessage')
        if (typeof callback === 'function') {
          callback(data)
        } else {
          typeof this.options.messageCb === 'function' && this.options.messageCb(data)
        }
      } else {
        message.error(msg || '收到失败的数据！')
      }
    }
  }

  /**
   * 自定义发送消息事件
   * @param {String} data 发送的文本
   */
  send(data: T | string): void {
    if (this.ws.readyState !== this.ws.OPEN) {
      throw new Error('没有连接到服务器，无法推送')
    }
    data = JSON.stringify(data)
    this.ws.send(data)
  }

  /**
   * 连接事件
   */
  onreconnect(): void {
    if ((this.options.reconnectCount as number) > 0 || this.options.reconnectCount === -1) {
      this.reconnectTimer = setTimeout(() => {
        this.create()
        if (this.options.reconnectCount !== -1) (this.options.reconnectCount as number)--
      }, this.options.reconnectTime)
    } else {
      clearTimeout(this.reconnectTimer)
      this.options.reconnectCount = this.reconnectCount
    }
  }

  /**
   * 销毁
   */
  destroy(): void {
    super.reset()
    clearTimeout(this.reconnectTimer) // 清除重连定时器
    this.options.isRestory = true
    this.ws.close()
  }
}
