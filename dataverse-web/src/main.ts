import 'ant-design-vue/dist/reset.css'
import '@/assets/css/index.less'

import { type Component, createApp } from 'vue'

import App from './App.vue'
import pinia from '@/stores'
import router from './router'
import directives from '@/directives/index'
import * as Icons from '@ant-design/icons-vue'
import { Button, message } from 'ant-design-vue'
// svg icons
import 'virtual:svg-icons-register'

const app: any = createApp(App)

const initApp = () => {
  app.use(directives).use(pinia).use(router)

  // 全局引用icon
  const IconData: Record<string, Component> = Icons
  Object.keys(IconData).forEach((key: string) => {
    app.component(key, IconData[key])
  })
  /* 自动注册 Button 下的子组件, 例如 Button.Group */
  app.use(Button)
  app.config.globalProperties.$message = message
}

initApp()
app.mount(document.querySelector('#app'))
// app.mount('#app')
