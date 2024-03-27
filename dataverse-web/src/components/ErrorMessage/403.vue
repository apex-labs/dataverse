<template>
  <div class="not-container">
    <img src="@/assets/imgs/403.png" class="not-img" alt="403" />
    <div class="not-detail">
      <h2>403</h2>
      <h4>抱歉，您无权访问该页面~🙅‍♂️🙅‍♀️</h4>
      <a-button type="primary" @click="router.push('/')">返回首页</a-button>
    </div>
  </div>
</template>

<script setup lang="ts" name="403">
import { useRouter } from 'vue-router'
const router = useRouter()
</script>

<style scoped lang="less">
@import './index.less';
</style>
