<template>
  <div class="not-container">
    <img src="@/assets/imgs/500.png" class="not-img" alt="500" />
    <div class="not-detail">
      <h2>500</h2>
      <h4>抱歉，您的网络不见了~🤦‍♂️🤦‍♀️</h4>
      <a-button type="primary" @click="router.push('/')">返回首页</a-button>
    </div>
  </div>
</template>

<script setup lang="ts" name="500">
import { useRouter } from 'vue-router'
const router = useRouter()
</script>

<style scoped lang="less">
@import './index.less';
</style>
