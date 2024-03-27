<template>
  <Codemirror v-model="value" placeholder="Code goes here..." :style="{ height: '100%', width: '100%', fontSize }"
    :extensions="extensions" @ready="handleReady" @focus="focusFn" @change="changeFn" @blur="blurFn"/>
</template>

<script setup lang="ts" name="Editor">
defineOptions({ name: 'sql-editor' })
import { ref, toRefs, shallowRef, computed, watch, onMounted, onBeforeUnmount } from 'vue'
import { useAppStore } from '@/stores/modules/app'
// codemirror
import { keymap, dropCursor } from "@codemirror/view"
import { defaultKeymap, history, historyKeymap } from "@codemirror/commands"
import { autocompletion } from "@codemirror/autocomplete"
import { Codemirror } from '@/components/CodeMirror'
import { sql } from '@codemirror/lang-sql'
import { json } from '@codemirror/lang-json'
import { oneDark } from '@codemirror/theme-one-dark'
import { syntaxTree } from "@codemirror/language"
import { linter, type Diagnostic } from "@codemirror/lint"

const regexpLinter = linter(view => {
  let diagnostics: Diagnostic[] = []
  console.log(syntaxTree(view.state).cursor());
  syntaxTree(view.state).cursor().iterate(node => {
    // console.log('node', node);
    if (node.name == "JsonText") diagnostics.push({
      from: node.from,
      to: node.to,
      severity: "error",
      message: "Regular expressions are FORBIDDEN",
      actions: [{
        name: "Remove",
        apply(view, from, to) { view.dispatch({ changes: { from, to } }) }
      }]
    })
  })
  return diagnostics
})

const languages: any = {
  json: json(),
  sql: sql({ upperCaseKeywords: true })
}

const appStore = useAppStore()
const { theme } = toRefs(appStore)

// Our list of completions (can be static, since the editor
/// will do filtering based on context).
const completions = [
  { label: "panic", type: "keyword" },
  { label: "park", type: "constant", info: "Test completion" },
  { label: "password", type: "variable" },
]

const myCompletions = (context: any) => {
  let before = context.matchBefore(/\w+/)
  console.log(before);
  // If completion wasn't explicitly started and there
  // is no word before the cursor, don't open completions.
  if (!context.explicit && !before) return null
  return {
    from: before ? before.from : context.pos,
    options: completions,
    validFor: /^\w*$/
  }
}

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  fontSize: {
    type: String,
    default: '14px'
  },
  lang: {
    type: String,
    default: 'sql'
  },
})

const emit = defineEmits(['update:modelValue', 'focus', 'blur', 'change', 'save'])

const value = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emit('update:modelValue', v)
  }
})
watch(
  () => props.modelValue,
  () => {
    getCodemirrorStates()
  },
  { deep: true }
)

const extensions = computed(() => {
  const r: any[] = [
    history(),
    dropCursor(),
    keymap.of([...defaultKeymap, ...historyKeymap, { key: "Mod-s", run: saveFn }]),
    // regexpLinter,
    // autocompletion({ override: [myCompletions] }),
  ]
  r.push(languages[props.lang])
  if (theme.value === 'dark') r.push(oneDark)
  return [...r]
})

// Codemirror EditorView instance ref
const currentPos = ref(1)
const view = shallowRef()
const handleReady = (payload: any) => {
  view.value = payload.view
      // 监听编辑器的 drop 事件
      view.value?.dom.addEventListener('drop', (event: any) => {
        event.preventDefault() // 阻止浏览器默认行为
        // 获取拖拽的文本内容
        // const droppedText = event.dataTransfer.getData('text/plain')

        // 获取光标所在位置的文档位置
        const pos = view.value?.posAtCoords({ x: event.clientX, y: event.clientY })

        // 如果存在文档位置，则在此位置插入拖拽的文本
        if (pos) {
          currentPos.value = pos
        }
      })
}

// Status is available at all times via Codemirror EditorView
const selected = ref<string[]>([])
const lines = ref<any[]>([])
const selectedLines = ref<any[]>([])
const getCodemirrorStates = () => {
  selected.value = []
  lines.value = []
  selectedLines.value = []
  const state = view.value.state
  // console.log(state);
  const doc = state.doc
  const ranges = state.selection.ranges
  ranges.forEach((r: any) => {
    const fromLine = doc.lineAt(r.from)
    const toLine = doc.lineAt(r.to)
    if (r.from !== r.to) {
      const text = doc.slice(r.from, r.to).text
      selected.value.push(...text)
    }
    if (fromLine.number === toLine.number) {
      if (!lines.value.includes(fromLine.number)) {
        lines.value.push(fromLine.number)
        selectedLines.value.push(fromLine.text)
      }
    } else {
      if (!lines.value.includes(fromLine.number)) {
        lines.value.push(fromLine.number)
        selectedLines.value.push(fromLine.text)
      }
      if (!lines.value.includes(toLine.number)) {
        lines.value.push(toLine.number)
        selectedLines.value.push(fromLine.text)
      }
    }
  })
}

const saveFn = () => {
  emit('save')
}

const changeFn = () => {
  getCodemirrorStates()
  emit('change', selected.value, lines.value, selectedLines.value)
}
const focusFn = () => {
  getCodemirrorStates()
  emit('focus', selected.value, lines.value, selectedLines.value)
}
const blurFn = () => {
  getCodemirrorStates()
  emit('blur', selected.value, lines.value, selectedLines.value)
}
const focus = () => {
  view.value.focus()
}
const drop = (droppedText:string)=>{
  view.value.dispatch({
    changes: { from: currentPos.value, insert: droppedText }
  })
  // 计算插入文本后的位置
  const finalPos = currentPos.value + droppedText.length
  // 设置编辑器的焦点到插入文本后的位置
  view.value.dispatch({
    selection: { anchor: finalPos, head: finalPos }
  })
  // 聚焦光标
  view.value.focus()
}

defineExpose({ focus,drop }) // 暴露给父组件调用

const onDragOver = (event: any) => {
  // 阻止默认行为（会作为某些元素的链接打开）
  event.preventDefault()
}
onMounted(() => {
  /* 在放置目标上触发的事件 */
  const target: HTMLElement | null = document.querySelector("#droptarget")
  target?.addEventListener('dragover', onDragOver, false)
})
onBeforeUnmount(() => {
  /* 移除目标上的事件 */
  const target: HTMLElement | null = document.querySelector("#droptarget")
  target?.removeEventListener('dragover', onDragOver, false)
})
</script>

<style scoped lang="less"></style>
