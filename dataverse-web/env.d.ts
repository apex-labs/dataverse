/// <reference types="vite/client" />
declare module 'lodash-es'
declare module 'ant-design-vue'
declare module '*.vue' {
  import { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}
