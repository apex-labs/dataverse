import PubSub from 'pubsub-js'
import { useAppStore } from '@/stores/modules/app'
/**
 * @name 声明一个常量准备将props内的部分内容储存起来
 */
const STORE = {
  setGlobalState: Function(),
  name: ''
}

/**
 * @name 启动qiankun应用间通信机制
 * @param {Object} props 官方通信函数
 * @description 注意：主应用是从qiankun中导出的initGlobalState方法，
 * @description 注意：子应用是附加在props上的onGlobalStateChange, setGlobalState方法（只用主应用注册了通信才会有）
 */
let propI18n: any = null
const storeProps = (props: {
  onGlobalStateChange: (arg0: (value: any, prev: any) => void, arg1: boolean) => void
  name: any
  i18n: any
  store: {
    getters: object
  }
  setGlobalState: (arg0: { ignore: any; msg: string }) => void
}) => {
  const appStore = useAppStore()
  const {
    store: { getters }
  } = props
  appStore.handleProps(getters)
  propI18n = props.i18n
  /**
   * @name 监听应用间通信，并存入store
   */
  props.onGlobalStateChange((value, prev) => {
    if (value.requestCode && value.val !== prev.val) {
      switch (value.requestCode) {
        case 50001: // 切换语言包
          // store.dispatch('SetLang', value.val)
          break
        case 50002: // 切换业务体系
          PubSub.publish('changeBizWorkSpace', value.val)
          break
        default:
          console.log(value.requestCode, value.msg)
      }
    }
  }, true)
  /**
   * @name 改变并全局广播新消息
   */
  props.setGlobalState({
    ignore: props.name,
    msg: `来自${props.name}动态设定的消息`
  })

  /**
   * @name 将你需要的数据存起来，供下面setState方法使用
   */
  STORE.setGlobalState = props.setGlobalState
  STORE.name = props.name
}

/**
 * @name 全局setState方法，修改的内容将通知所有微应用
 * @param {Object} data 按照你设定的内容格式数据
 */
const setState = (data: any) => {
  STORE.setGlobalState({
    ignore: STORE.name,
    ...data
  })
}

export { setState, propI18n }
export default storeProps
