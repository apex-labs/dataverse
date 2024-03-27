import { type RouteMeta as VRouteMeta } from 'vue-router'

declare module 'vue-router' {
  interface RouteMeta extends VRouteMeta {
    title: string
    i18n?: string
    permissionCode?: string
    // role info
    roles?: RoleEnum[]
    // icon on tab
    icon?: string
    // Used internally to mark single-level menus
    single?: boolean
    // Never show in menu
    hidden?: boolean
    // Whether to ignore permissions
    ignoreAuth?: boolean
    // Whether not to cache
    ignoreKeepAlive?: boolean
    /** 设置当前路由高亮的菜单项，值为route fullPath或route name,一般用于详情页 */
    activeMenu?: string
    /** 菜单排序号 */
    orderNum?: number
    /** 是否外链 */
    isLink?: boolean
  }
}
