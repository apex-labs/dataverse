<template>
  <div class="chat flex fdc ovh">
    <div class="list flex1 ova" id="chatlist" ref="chatref">
      <ChatItem v-for="(item, index) in list" :key="index" :item="item" />
    </div>
    <div class="search">
      <div class="search-wrap flex aic">
        <input class="search-text flex1" ref="input" type="text" v-model="searchValue" placeholder="请输入问题"
          :disabled="loading" @keypress.enter="sendFn" />
        <span class="search-btn" :class="{ loading: loading }" @click="sendFn">
          <loading-outlined v-if="loading" />
          <SvgIcon name="send" size="14" v-else />
        </span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import ChatItem from './ChatItem.vue';
import SvgIcon from '@/components/SvgIcon/index.vue'

interface Item {
  id: string | number
  name: string
  type: string | number
  question?: string
  answer?: string
  content?: string
}

const loading = ref(false)

const chatref = ref()
const input = ref()

const searchValue = ref<string>('')
const list = ref<Item[]>([
  {
    id: Date.now(),
    name: '文心一言',
    type: 0,
    content: `Welcome danny, I'm your intelligent code companion, here to be your partner-in -crime for getting things done in a flash.Together, we'll tackle tasks swiftly and efficiently, making your coding experience a joy.`
  }
])
const sendFn = () => {
  const item = {
    id: Date.now(),
    name: 'Danny',
    type: 1,
    content: searchValue.value,
  }
  list.value.push(item)
  getAnswer()
}
const getAnswer = async () => {
  loading.value = true
  setTimeout(async () => {
    const item = {
      id: Date.now(),
      name: '文心一言',
      type: 0,
      content: `sql
    SELECT user_id, SUM(amount) as total_amount
    FROM a_order
    WHERE purchase_date >= DATE_SUB(NOW(), INTERVAL 1 MONTH)
    GROUP BY user_id
    HAVING total_amount > 50000;`
    }
    list.value.push(item)
    await scrollToBottom()
    await focusInput()
    console.log(11);
    searchValue.value = ''
    loading.value = false
  }, 1000);
}

const focusInput = async () => {
  await nextTick(() => {
    setTimeout(() => {
      input.value.focus()
    }, 1000);
  });
}

const scrollToBottom = async () => {
  const h = chatref.value.scrollHeight
  await nextTick(() => {
    console.log(1);
    chatref.value.scrollTo({
      left: 0,
      top: h,
      behavior: 'auto'  // smooth或'auto'
    })
  });
}
</script>

<style scoped lang="less">
.chat {
  position: relative;
}

.list {
  padding: 0 10px 50px;
}

.search {
  overflow: hidden;
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 5px 10px 10px;
  background: var(--bg-color-1);

  &-wrap {
    border-radius: 6px;
    border: 1px solid #4F57B6;
    height: 32px;
  }

  &-text {
    border: none;
    background: none;
    height: 30px;
    line-height: 30px;
    padding: 0 10px;
    color: var(--text-color-3)
  }

  &-btn {
    cursor: pointer;
    width: 22px;
    height: 22px;
    line-height: 22px;
    text-align: center;
    background: var(--primary-color);
    color: #fff;
    border-radius: 6px;
    margin: 4px;

    &.loading {
      cursor: default;
    }
  }
}
</style>
