<template>
  <a-modal centered @ok="$emit('ok')" @cancel="onClose">
    <template #title>
      <slot name="title">
        <div ref="modalTitleRef" style="width: 100%; cursor: move">{{ title }}</div>
      </slot>
    </template>
    <template #modalRender="{ originVNode }">
      <div :style="transformStyle">
        <component :is="originVNode" />
      </div>
    </template>
    <template #footer>
      <slot name="footer">
        <div class="ar">
          <a-button type="text" class="mr10" :disabled="readonly" @click="onClose">取消</a-button>
          <a-button type="primary" :loading="loading" :disabled="readonly" @click="onOk">确定</a-button>
        </div>
      </slot>
    </template>
    <slot></slot>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, computed, type CSSProperties, watch, watchEffect } from 'vue';
import { useDraggable } from '@vueuse/core';
const emits = defineEmits(['close', 'ok'])
const props = defineProps({
  title: String,
  loading: { type: Boolean, default: false },
  readonly: { type: Boolean, default: false },
})
const onClose = () => {
  emits('close')
}
const onOk = () => {
  emits('ok')
}

const modalTitleRef = ref<HTMLElement | any>(null);
const { x, y, isDragging } = useDraggable(modalTitleRef);
const startX = ref<number>(0);
const startY = ref<number>(0);
const startedDrag = ref(false);
const transformX = ref(0);
const transformY = ref(0);
const preTransformX = ref(0);
const preTransformY = ref(0);
const dragRect = ref({ left: 0, right: 0, top: 0, bottom: 0 });
watch([x, y], () => {
  if (!startedDrag.value) {
    startX.value = x.value;
    startY.value = y.value;
    const bodyRect = document.body.getBoundingClientRect();
    const titleRect = modalTitleRef.value.getBoundingClientRect();
    dragRect.value.right = bodyRect.width - titleRect.width;
    dragRect.value.bottom = bodyRect.height - titleRect.height;
    preTransformX.value = transformX.value;
    preTransformY.value = transformY.value;
  }
  startedDrag.value = true;
});
watch(isDragging, () => {
  if (!isDragging) {
    startedDrag.value = false;
  }
});

watchEffect(() => {
  if (startedDrag.value) {
    transformX.value =
      preTransformX.value +
      Math.min(Math.max(dragRect.value.left, x.value), dragRect.value.right) -
      startX.value;
    transformY.value =
      preTransformY.value +
      Math.min(Math.max(dragRect.value.top, y.value), dragRect.value.bottom) -
      startY.value;
  }
});
const transformStyle = computed<CSSProperties>(() => {
  return {
    transform: `translate(${transformX.value}px, ${transformY.value}px)`,
  };
});
</script>

<style lang="less" scoped></style>