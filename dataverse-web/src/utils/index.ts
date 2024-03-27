import { validatenull } from './validate'
import * as CryptoJS from 'crypto-js'
import website from '@/const/website'
import { message } from 'ant-design-vue'

const keyName = website.key + '-'

type Params = {
  name: string
  debug?: boolean
}
type Obj = {
  dataType?: string
  content?: string | boolean | undefined
}
/**
 * 获取localStorage
 */

export const getStore = ({ name, debug }: Params) => {
  const targetName = keyName + name
  let obj: Obj = {}

  let stringObj: string = ''
  let content: any
  if (!validatenull(window.sessionStorage.getItem(targetName))) {
    stringObj = window.sessionStorage.getItem(targetName) || ''
  }
  if (!validatenull(window.localStorage.getItem(targetName))) {
    stringObj = window.localStorage.getItem(targetName) || ''
  }
  try {
    obj = JSON.parse(stringObj)
  } catch (e) {
    return obj
  }

  if (debug) {
    return obj
  }
  if (obj.dataType === 'string') {
    content = obj.content
  } else if (obj.dataType === 'number') {
    content = Number(obj.content)
  } else if (obj.dataType === 'object') {
    content = obj.content
  }
  return content
}

// 表单序列化
export const serialize = (data: { [x: string]: any }) => {
  const list: any = []
  Object.keys(data).forEach((ele) => {
    list.push(`${ele}=${data[ele]}`)
  })
  return list.join('&')
}

/**
 * @description 生成唯一 uuid
 * @returns {String}
 */
export function generateUUID() {
  let uuid = ''
  for (let i = 0; i < 32; i++) {
    const random = (Math.random() * 16) | 0
    if (i === 8 || i === 12 || i === 16 || i === 20) uuid += '-'
    uuid += (i === 12 ? 4 : i === 16 ? (random & 3) | 8 : random).toString(16)
  }
  return uuid
}

/**
 * 判断两个对象是否相同
 * @param {Object} a 要比较的对象一
 * @param {Object} b 要比较的对象二
 * @returns {Boolean} 相同返回 true，反之 false
 */
export function isObjectValueEqual(a: { [key: string]: any }, b: { [key: string]: any }) {
  if (!a || !b) return false
  const aProps = Object.getOwnPropertyNames(a)
  const bProps = Object.getOwnPropertyNames(b)
  if (aProps.length != bProps.length) return false
  for (let i = 0; i < aProps.length; i++) {
    const propName = aProps[i]
    const propA = a[propName]
    const propB = b[propName]
    // eslint-disable-next-line no-prototype-builtins
    if (!b.hasOwnProperty(propName)) return false
    if (propA instanceof Object) {
      if (!isObjectValueEqual(propA, propB)) return false
    } else if (propA !== propB) {
      return false
    }
  }
  return true
}

/**
 * @description 生成随机数
 * @param {Number} min 最小值
 * @param {Number} max 最大值
 * @returns {Number}
 */
export function randomNum(min: number, max: number): number {
  const num = Math.floor(Math.random() * (min - max) + max)
  return num
}

/**
 * @description 获取当前时间对应的提示语
 * @returns {String}
 */
export function getTimeState() {
  const timeNow = new Date()
  const hours = timeNow.getHours()
  if (hours >= 6 && hours <= 10) return `早上好 ⛅`
  if (hours >= 10 && hours <= 14) return `中午好 🌞`
  if (hours >= 14 && hours <= 18) return `下午好 🌞`
  if (hours >= 18 && hours <= 24) return `晚上好 🌛`
  if (hours >= 0 && hours <= 6) return `凌晨好 🌛`
}

/**
 * @description 获取浏览器默认语言
 * @returns {String}
 */
export function getBrowserLang() {
  const browserLang = navigator.language ? navigator.language : navigator.browserLanguage
  let defaultBrowserLang = ''
  if (['cn', 'zh', 'zh-cn'].includes(browserLang.toLowerCase())) {
    defaultBrowserLang = 'zh'
  } else {
    defaultBrowserLang = 'en'
  }
  return defaultBrowserLang
}

/**
 * @description 使用递归扁平化菜单，方便添加动态路由
 * @param {Array} menuList 菜单列表
 * @returns {Array}
 */
export function getFlatMenuList(menuList: any[]): any[] {
  const newMenuList: any[] = JSON.parse(JSON.stringify(menuList))
  return newMenuList.flatMap((item) => [
    item,
    ...(item.children ? getFlatMenuList(item.children) : [])
  ])
}

/**
 * @description 使用递归过滤出需要渲染在左侧菜单的列表 (需剔除 isHide == true 的菜单)
 * @param {Array} menuList 菜单列表
 * @returns {Array}
 * */
export function getShowMenuList(menuList: any[]) {
  const newMenuList: any[] = JSON.parse(JSON.stringify(menuList))
  return newMenuList.filter((item) => {
    item.children?.length && (item.children = getShowMenuList(item.children))
    return !item.meta?.isHide
  })
}

/**
 * @description 使用递归找出所有面包屑存储到 pinia/vuex 中
 * @param {Array} menuList 菜单列表
 * @param {Array} parent 父级菜单
 * @param {Object} result 处理后的结果
 * @returns {Object}
 */
export const getAllBreadcrumbList = (
  menuList: any[],
  parent = [],
  result: { [key: string]: any } = {}
) => {
  for (const item of menuList) {
    result[item.path] = [...parent, item]
    if (item.children) getAllBreadcrumbList(item.children, result[item.path], result)
  }
  return result
}

/**
 * @description 使用递归处理路由菜单 path，生成一维数组 (第一版本地路由鉴权会用到，该函数暂未使用)
 * @param {Array} menuList 所有菜单列表
 * @param {Array} menuPathArr 菜单地址的一维数组 ['**','**']
 * @returns {Array}
 */
export function getMenuListPath(menuList: any[], menuPathArr: string[] = []): string[] {
  for (const item of menuList) {
    if (typeof item === 'object' && item.path) menuPathArr.push(item.path)
    if (item.children?.length) getMenuListPath(item.children, menuPathArr)
  }
  return menuPathArr
}

/**
 * @description 递归查询当前 path 所对应的菜单对象 (该函数暂未使用)
 * @param {Array} menuList 菜单列表
 * @param {String} path 当前访问地址
 * @returns {Object | null}
 */
export function findMenuByPath(menuList: any[], path: string): any | null {
  for (const item of menuList) {
    if (item.path === path) return item
    if (item.children) {
      const res = findMenuByPath(item.children, path)
      if (res) return res
    }
  }
  return null
}

/**
 * @description 使用递归过滤需要缓存的菜单 name (该函数暂未使用)
 * @param {Array} menuList 所有菜单列表
 * @param {Array} keepAliveNameArr 缓存的菜单 name ['**','**']
 * @returns {Array}
 * */
export function getKeepAliveRouterName(menuList: any[], keepAliveNameArr: string[] = []) {
  menuList.forEach((item) => {
    item.meta.isKeepAlive && item.name && keepAliveNameArr.push(item.name)
    item.children?.length && getKeepAliveRouterName(item.children, keepAliveNameArr)
  })
  return keepAliveNameArr
}

export function copy(content: string) {
  // 创建输入框元素
  const textarea = document.createElement('textarea') //不会保留文本格式
  //如果要保留文本格式，比如保留换行符，或者多行文本，可以使用  textarea 标签，再配和模板字符串 ` `
  //const input = document.createElement('input')
  // 将想要复制的值
  textarea.value = content
  // 页面底部追加输入框
  document.body.appendChild(textarea)
  // 选中输入框
  textarea.select()
  // 执行浏览器复制命令
  document.execCommand('Copy')
  // 弹出复制成功信息
  message.success('复制成功')
  // 复制后移除输入框
  textarea.remove()
}

/**
 *加密处理
 */
export const encryption = (
  data,
  type: string = 'Aes',
  param: string[] = ['password'],
  key: string = website.signKey
) => {
  const result = JSON.parse(JSON.stringify(data))
  if (type === 'Base64') {
    param.forEach((ele) => {
      result[ele] = btoa(result[ele])
    })
  } else {
    param.forEach((ele) => {
      const data = result[ele]
      key = CryptoJS.enc.Latin1.parse(key)
      const iv = key
      // 加密
      const encrypted = CryptoJS.AES.encrypt(data, key, {
        iv: iv,
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.ZeroPadding
      })
      result[ele] = encrypted.toString()
    })
  }
  return result
}
/**
 * @word 要加密的内容
 * @keyWord String  服务器随机返回的关键字
 *  */
export function aesEncrypt(word, keyWord = 'apexnexusdatavs1') {
  const key = CryptoJS.enc.Utf8.parse(keyWord)
  const srcs = CryptoJS.enc.Utf8.parse(word)
  const encrypted = CryptoJS.AES.encrypt(srcs, key, {
    mode: CryptoJS.mode.ECB,
    padding: CryptoJS.pad.Pkcs7
  })
  return encrypted.toString()
}
