/**
 * @description:解决input前后空格问题
 * \s：空格
 * \uFEFF：字节次序标记字符（Byte Order Mark），也就是BOM,它是es5新增的空白符
 * \xA0：禁止自动换行空白符，相当于html中的&nbsp;
 * @return {*}
 */
import type { Directive, DirectiveBinding } from 'vue'
interface ElType extends HTMLElement {
  __handleClick__: any
}
const trim: Directive = {
  mounted(el: ElType, binding: DirectiveBinding) {
    setTimeout(() => {
      const suffix = el.getElementsByClassName('ant-input-suffix')[0]
      if (suffix) {
        const input = el.getElementsByClassName('ant-input')[0]
        Trim(input)
      } else {
        // 没有清除图标
        Trim(el)
      }
    }, 0)
  }
}
const Trim = (el: any) => {
  // 当按下按键时运行脚本
  el.onkeydown = () => {
    // 去除前后空格:
    el.value = el.value?.replace(/^[\s\uFEFF]+|[\s\uFEFF]+$/g, '')
  }
  // 当松开按键时运行脚本
  el.onkeyup = () => {
    // 去除前后空格:
    el.value = el.value?.replace(/^\s+|\s+$/g, '')
  }
  // 当松开按键时运行脚本
  el.onkeypress = () => {
    // 去除前后空格:
    // el.value = el.value?.replace(/^\s+|\s+$/g, '')
  }
}
export default trim
