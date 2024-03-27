import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { staticRouter, loginRouter, errorRouter } from './modules/staticRouter'
import NProgress from '@/utils/nprogress'
// import pinia from '@/stores'
// import { useDvsStore } from '@/stores/modules/user'
// import { useAppStore } from '@/stores/modules/app'
import { getCookie, isCookieExpired } from '@/utils/cookie'

const BASE_URL = import.meta.env.BASE_URL

const LOGIN_URL = '/login'

const DEFAULT_URL = '/overview'

const routes = [...staticRouter, ...loginRouter, ...errorRouter]

const router = createRouter({
  history: createWebHistory(BASE_URL),
  routes: routes as unknown as RouteRecordRaw[]
})

// let appStore: any = null

/**
 * @description 路由拦截 beforeEach
 * */
router.beforeEach(async (to, from, next) => {
  // const dvsStore = useDvsStore(pinia)
  // appStore = useAppStore(pinia)
  const token = getCookie('token')

  // 1.Loading 开始
  NProgress.start()
  // appStore.changeLoading(true)

  // 2.动态设置标题
  const title = import.meta.env.VITE_GLOB_APP_TITLE
  document.title = to.meta.title ? `${to.meta.title} - ${title}` : title

  // 3.判断是访问登陆页，有 Token 就在当前页面，没有 Token 重置路由到登陆页
  if (to.path.toLocaleLowerCase() === LOGIN_URL) {
    if (token && !isCookieExpired('token')) return next({ path: DEFAULT_URL, replace: true })
  }

  // 4.判断访问页面是否在路由白名单地址(静态路由)中，如果存在直接放行
  // if (ROUTER_WHITE_LIST.includes(to.path)) return next();

  // 5.判断是否有 Token，没有重定向到 login 页面

  if (!to.meta.skipLogin) {
    if (!token) {
      localStorage.clear()
      return next({ path: LOGIN_URL, replace: true })
    }
  }

  // 6.如果没有菜单列表，就重新请求菜单列表并添加动态路由
  // if (!authStore.authMenuListGet.length) {
  //   await initDynamicRouter();
  //   return next({ ...to, replace: true });
  // }

  // 7.存储 routerName 做按钮权限筛选
  // authStore.setRouteName(to.name as string);

  // 8.正常访问页面
  next()
})

/**
 * @description 重置路由
 * */
// export const resetRouter = () => {
//   const authStore = useAuthStore();
//   authStore.flatMenuListGet.forEach(route => {
//     const { name } = route;
//     if (name && router.hasRoute(name)) router.removeRoute(name);
//   });
// };

/**
 * @description 路由跳转错误
 * */
router.onError((error) => {
  NProgress.done()
  // appStore.changeLoading(false)
  console.warn('路由错误', error.message)
})

/**
 * @description 路由跳转结束
 * */
router.afterEach(() => {
  NProgress.done()
  // appStore.changeLoading(false)
  // setTimeout(() => {
  //   appStore.changeLoading(false)
  // }, 1000)
})

export default router
