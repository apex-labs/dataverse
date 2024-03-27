<template>
  <a-drawer v-bind="$attrs" :closable="false">
    <template #extra>
      <a-button class="ant-drawer-close" style="margin:0" @click="onClose" ghost>
        <CloseOutlined />
      </a-button>
    </template>
    <slot></slot>
    <template #footer v-if="footer">
      <slot name="footer">
        <div class="ar">
          <a-button type="text" class="mr10" :disabled="readonly" @click="onClose">取消</a-button>
          <a-button type="primary" :loading="loading" :disabled="readonly" @click="onOk">确定</a-button>
        </div>
      </slot>
    </template>
  </a-drawer>
</template>

<script setup lang="ts">
import { ref } from 'vue';
const emits = defineEmits(['close', 'ok'])
defineProps({
  loading: { type: Boolean, default: false },
  readonly: { type: Boolean, default: false },
  footer: { type: Boolean, default: true }
})
const onClose = () => {
  emits('close')
}
const onOk = () => {
  emits('ok')
}
</script>

<style lang="less" scoped></style>