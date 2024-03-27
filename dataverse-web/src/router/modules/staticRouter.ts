import type { RouteMeta, RouteRecordRaw } from 'vue-router'
import { defineComponent } from 'vue'

type Component<T = any> =
  | ReturnType<typeof defineComponent>
  | (() => Promise<typeof import('*.vue')>)
  | (() => Promise<T>)

export interface AppRouteRecordRaw extends Omit<RouteRecordRaw, 'meta' | 'children'> {
  path: string
  name: string
  meta?: RouteMeta
  redirect?: string
  component?: Component | string
  children?: AppRouteRecordRaw[]
  fullPath?: string
}

/**
 * staticRouter (静态路由)
 */
const BasicLayout = () => import('@/layouts/BasicLayout.vue')
const RouterView = () => import('@/layouts/RouterView.vue')
export const staticRouter: AppRouteRecordRaw[] = [
  {
    path: '/',
    name: 'dataverse',
    component: BasicLayout,
    meta: {
      title: '数据仓库',
      permissionCode: 'A28'
    },
    redirect: '/overview',
    children: [
      {
        path: '/overview',
        name: 'overview',
        component: () => import('@/views/overview/index.vue'),
        meta: {
          title: '概览',
          icon: 'gailan',
          type: 'svg',
          hidden: false
        }
      },
      // 规划
      {
        path: '/planning',
        name: 'planning',
        meta: {
          icon: 'guihua',
          type: 'svg',
          title: '规划',
          hidden: false
        },
        component: () => import('@/views/planning/index.vue')
      },
      {
        path: '/map',
        name: 'map',
        component: () => import('@/views/planning/map.vue'),
        meta: {
          icon: '',
          title: '数据地图',
          hidden: true,
          activeMenu: '/planning'
        }
      },
      {
        path: '/source',
        name: 'source',
        component: () => import('@/views/planning/source.vue'),
        meta: {
          icon: '',
          title: '数据源',
          hidden: true,
          activeMenu: '/planning'
        }
      },
      {
        path: '/region',
        name: 'region',
        component: () => import('@/views/planning/region.vue'),
        meta: {
          icon: '',
          title: '数据域',
          hidden: true,
          activeMenu: '/planning'
        }
      },
      {
        path: '/space',
        name: 'space',
        component: () => import('@/views/planning/space.vue'),
        meta: {
          icon: '',
          title: '数据空间',
          hidden: true,
          activeMenu: '/planning'
        }
      },
      // 研发
      {
        path: '/development/etl',
        name: 'developmentEtl',
        meta: {
          icon: 'yanfa',
          type: 'svg',
          title: '研发',
          hidden: false
        },
        component: () => import('@/views/development/etl.vue')
      },
      {
        path: '/development/bdm',
        name: 'developmentBdm',
        component: () => import('@/views/development/bdm.vue'),
        meta: {
          icon: '',
          title: '开发任务',
          hidden: true,
          activeMenu: '/development/etl'
        }
      },
      {
        path: '/development/schedule',
        name: 'developmentTask',
        component: () => import('@/views/schedule/index.vue'),
        meta: {
          icon: '',
          title: '调度任务',
          hidden: true,
          activeMenu: '/development/etl'
        }
      },
      {
        path: '/development/schedule/detail',
        name: 'developmentTaskDetail',
        component: () => import('@/views/schedule/detail.vue'),
        meta: {
          icon: '',
          title: '调度任务详情',
          hidden: true,
          activeMenu: '/development/etl',
          hiddenHeader: true
        }
      },
      {
        path: '/development/log',
        name: 'developmentLog',
        component: () => import('@/views/development/log.vue'),
        meta: {
          icon: 'Menu',
          title: '运行日志',
          hidden: true,
          activeMenu: '/development/etl'
        }
      },
      // 资产
      // {
      //   path: '/property',
      //   name: 'property',
      //   meta: {
      //     icon: 'icon-shujujicheng',
      //     title: '资产',
      //     hidden: false
      //   },
      //   component: () => import('@/views/property/index.vue')
      // },
      // 运维
      // {
      //   path: '/operations',
      //   name: 'operations',
      //   component: () => import('@/views/operations/index.vue'),
      //   meta: {
      //     icon: 'yunwei',
      //     type: 'svg',
      //     title: '运维',
      //     hidden: false
      //   }
      // },
      // 配置
      {
        path: '/config',
        name: 'config',
        component: () => import('@/views/config/index.vue'),
        meta: {
          icon: 'setting',
          type: 'svg',
          title: '配置',
          hidden: false
        }
      }
    ]
  }
]

/**
 * login/register
 */
export const loginRouter = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/login.vue'),
    meta: {
      title: '登录',
      skipLogin: true
    }
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/register.vue'),
    meta: {
      title: '注册',
      skipLogin: true
    }
  },
  {
    path: '/resetPassword',
    name: 'resetPassword',
    component: () => import('@/views/resetPassword.vue'),
    meta: {
      title: '重置密码',
      skipLogin: true
    }
  }
]

/**
 * errorRouter (错误页面路由)
 */
export const errorRouter = [
  {
    path: '/403',
    name: '403',
    component: () => import('@/components/ErrorMessage/403.vue'),
    meta: {
      title: '403页面',
      skipLogin: true
    }
  },
  {
    path: '/404',
    name: '404',
    component: () => import('@/components/ErrorMessage/404.vue'),
    meta: {
      title: '404页面',
      skipLogin: true
    }
  },
  {
    path: '/500',
    name: '500',
    component: () => import('@/components/ErrorMessage/500.vue'),
    meta: {
      title: '500页面',
      skipLogin: true
    }
  },
  // Resolve refresh page, route warnings
  {
    path: '/:pathMatch(.*)*',
    component: () => import('@/components/ErrorMessage/404.vue')
  }
]
