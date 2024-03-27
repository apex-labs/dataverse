import { defineStore } from 'pinia'
import piniaPersistConfig from '@/stores/piniaPersist'
import { mapList, spaceList, listEnv, listModel } from '@/api/modules/planning'

export const useDvsStore = defineStore({
  id: 'apex-dvs',
  state: () => ({
    dvsCode: '',
    env: 1,
    envEnum: [] as any,
    model: 1,
    modelEnum: [] as any,
    spaceList: [] as any,
    currentSpace: { envMode: null },
    mapList: [] as any
  }),
  getters: {},
  actions: {
    getEnvList() {
      listEnv().then((res) => {
        const data = res.data || []
        this.envEnum = data
      })
    },
    setEnv(env: number) {
      this.env = env
    },
    setEnvList(envEnum: any[]) {
      this.envEnum = envEnum
    },
    getModelList() {
      listModel().then((res) => {
        const data = res.data || []
        this.modelEnum = data
      })
    },
    setModelList(modelEnum: any[]) {
      this.modelEnum = modelEnum
    },
    getSpaceList() {
      const params = {
        keyword: '',
        pageQueryParam: {
          ascs: [],
          descs: [],
          pageNo: 1,
          size: 50
        }
      }
      spaceList(params).then((res: any) => {
        const data = res.data.list || []
        this.spaceList = data
        if (this.dvsCode) {
          const dvs = data.find((e: any) => e.dvsCode === this.dvsCode)
          this.currentSpace = dvs
          this.dvsCode = dvs.dvsCode
          this.env = dvs.envMode === 1 ? 0 : 1
        } else {
          if (data[0]) {
            this.currentSpace = data[0]
            this.dvsCode = data[0].dvsCode
            this.env = data[0].envMode === 1 ? 0 : 1
          }
        }
      })
    },
    setSpaceList(spaceList: any[]) {
      this.spaceList = spaceList
    },
    setDvsCode(dvsCode: string) {
      this.dvsCode = dvsCode
    },
    setCurrentSpace(currentSpace: any) {
      this.currentSpace = currentSpace
      this.env = currentSpace.envMode === 1 ? 0 : 1
      this.dvsCode = currentSpace.dvsCode
    },
    getMapList() {
      mapList().then((res: any) => {
        this.mapList = res.data || []
      })
    }
  },
  persist: piniaPersistConfig('apex-user')
})
