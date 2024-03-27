export default {
  title: 'nexus3.0',
  copyright: 'Copyright © 2019 chinapex.com. All rights reserved.',
  isFirstPage: true, // 配置首页不可关闭
  key: 'nexus3.0', // 配置主键,目前用于存储
  signKey: 'apexnexusdatavs1', // 校验签名
  menuKeys: ['/labelManage', '/group'], // 应用菜单管理
  whiteList: ['login', '404'], // 配置无权限可以访问的页面
  fistPage: {
    label: '首页',
    value: '/wel/index',
    params: {},
    query: {},
    group: [],
    close: false
  },
  // 配置菜单的属性
  menu: {
    props: {
      label: 'label',
      path: 'path',
      icon: 'icon',
      children: 'children'
    }
  },
  // 本地图片地址
  // staticImages: {
  //   logo_login_main: require('@/assets/logo_cn.svg'), // 登录页-pc端左侧logo （用于layouts-userLayout组件）
  //   logo_login_sub: require('@/assets/apexnexus.svg'), // 登录页-输入框上方文字logo （用于views-login-index）
  //   logo_index_w: require('@/assets/logo_w.svg'), // 首页白字logo（用于components-tools-logo组件）
  //   logo_index_b: require('@/assets/logo_b.svg') // 首页黑字logo（用于components-tools-logo组件）
  // },
  // 系统默认文本
  defalutText: {
    company_name: '创络科技'
  }
}
