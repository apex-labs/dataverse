import { defineStore } from 'pinia'
import piniaPersistConfig from '@/stores/piniaPersist'
import Socket from '@/utils/ws'

export const useWsStore = defineStore({
  id: 'apex-ws',
  state: () => ({
    // socket wrapper 实例
    client: null,
    // socket 连接状态
    status: null
  }),
  actions: {
    setClient(client: any) {
      this.client = client as any
    },
    setStatus(status: any) {
      if (this.status === status) {
        return
      }
      this.status = status
    },
    // 初始化Socket
    initSocket(options: any) {
      // console.log('init', this.client)
      // check is init
      if (this.client && this.client.ws && this.client.ws.readyState === 1) {
        return
      }
      const ws = new Socket({ ...options })
      ws.onmessage((data: any) => {
        const str = JSON.stringify(data)
        console.log('server data:', str)
      })
      this.setClient(ws)
    },

    // 关闭Socket连接
    closeSocket() {
      this.client?.close?.()
      this.setClient(null)
    }
  },
  persist: piniaPersistConfig('apex-ws')
})
