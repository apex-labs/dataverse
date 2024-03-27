import { defineStore } from 'pinia'
import piniaPersistConfig from '@/stores/piniaPersist'

type USERINFO = {
  username?: string
}

export const useAppStore = defineStore({
  id: 'apex-app',
  state: () => {
    return {
      loading: true,
      theme: 'dark',
      color: '#234297',
      avatar: '',
      bizWorkspace: {},
      device: '',
      expires_in: 0,
      flattenMenu: [],
      getMenu: true,
      interfaceConfig: {},
      isFullScreen: false,
      isLock: false,
      lockPasswd: '',
      menu: [],
      nickname: '',
      permissions: [],
      roles: [],
      token: '',
      userBizRole: [],
      userInfo: <USERINFO>{},
      lang: ''
    }
  },
  getters: {},
  actions: {
    // Set Token
    setToken(token: string) {
      this.token = token
    },
    resetToken() {
      this.token = ''
    },
    // Set setUserInfo
    setUserInfo(userInfo: USERINFO) {
      this.userInfo = userInfo
    },
    handleProps(getters: any): void {
      console.log(this.$state)
      Object.assign(this.$state, getters)
    },
    changeTheme(v: string | null): void {
      this.theme = v || this.theme === 'light' ? 'dark' : 'light'
      const html = document.documentElement as HTMLElement
      if (this.theme === 'dark') html.setAttribute('class', 'dark')
      else html.setAttribute('class', '')
    },
    changeLockStatus(isLock: boolean): void {
      this.isLock = isLock
    },
    changeLoading(loading: boolean): void {
      this.loading = loading
    },
    handleLogout() {
      return new Promise((resolve) => {
        resolve(true)
      })
    }
  },
  persist: piniaPersistConfig('apex-app')
})
