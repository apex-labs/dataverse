<template>
  <a-modal v-model:open="dialogVisible" title="个人信息" width="500px" centered @ok="handleOk">
    <p>This is userInfo</p>
    <template #footer>
      <a-button key="back" @click="handleCancel">取消</a-button>
      <a-button key="submit" type="primary" @click="handleOk">确认</a-button>
    </template>
  </a-modal>
</template>

<script setup lang="ts">
import { ref } from "vue";

const dialogVisible = ref(false);
const openDialog = () => {
  dialogVisible.value = true;
};

defineExpose({ openDialog });

const handleOk = () => {
  setTimeout(() => {
    dialogVisible.value = false;
  }, 16);
};

const handleCancel = () => {
  dialogVisible.value = false;
};
</script>
