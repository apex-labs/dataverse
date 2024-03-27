<template>
  <div class="item">
    <div class="title flex aic">
      <img class="avatar" :src="url" />
      <span class="name flex1">{{ item?.name }}</span>
      <!-- <a-tooltip placement="top" title="复制">
      </a-tooltip> -->
      <SvgIcon class="btn" name="copy" @click="copyFn" v-if="item?.type === 0" />
    </div>
    <div class="content">{{ item?.content }}</div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import type { PropType } from "vue";
import SvgIcon from '@/components/SvgIcon/index.vue'
import robin from '@/assets/imgs/robin.png'
import avatar from '@/assets/imgs/avatar.gif'
import { copy } from '@/utils'

interface Item {
  id: string | number
  name: string
  type: string | number
  question?: string
  answer?: string
  content?: string
}

const emit = defineEmits([])

const props = defineProps({
  item: {
    type: Object as PropType<Item>
  },
})

// const url = ref<string>(robin)
const url = computed(() => props.item?.type ? avatar : robin)

const copyFn = () => {
  props.item && props.item.content && copy(props.item.content)
}
</script>

<style scoped lang="less">
.item {
  padding: 15px 0;
  border-bottom: 1px solid var(--border-color);

  // &:last-child {
  //   border-bottom: none;
  // }

  .title {
    .avatar {
      width: 20px;
      height: 20px;
      border-radius: 50%;
    }

    .name {
      margin: 0 10px;
      font-size: 12px;
      color: var(--text-color-2);
    }

    .btn {
      cursor: pointer;
    }
  }

  .content {
    margin-top: 10px;
    word-break: break-all;
    text-align: justify;
    line-height: 1.3;
    font-size: 12px;
  }
}
</style>
