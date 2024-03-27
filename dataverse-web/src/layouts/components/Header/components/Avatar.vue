<template>
  <div>
    <a-dropdown arrow :trigger="['click']" placement="bottomRight">
      <div class="avatar">
        <!-- <img src="@/assets/imgs/avatar.gif" alt="avatar" /> -->
        <SvgIcon name="user" :size="26" />
      </div>
      <template #overlay>
        <a-menu>
          <!-- <a-menu-item @click="openDialog('infoRef')">
            <UserOutlined class="mr5" />个人信息
          </a-menu-item>
          <a-menu-item @click="openDialog('passwordRef')">
            <FormOutlined class="mr5" />修改密码
          </a-menu-item>
          <a-menu-divider /> -->
          <a-menu-item @click="openDialog('logout')">
            <PoweroffOutlined class="mr5" />退出登录
          </a-menu-item>
        </a-menu>
      </template>
    </a-dropdown>
    <InfoDialog ref="infoRef"></InfoDialog>
    <PasswordDialog ref="passwordRef"></PasswordDialog>
    <a-modal v-model:open="open" title="温馨提示" @ok="logout">
      <p>您是否确认退出登录</p>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAppStore } from '@/stores/modules/app'
import { message } from 'ant-design-vue'
import InfoDialog from './InfoDialog.vue'
import PasswordDialog from './PasswordDialog.vue'
import SvgIcon from '@/components/SvgIcon/index.vue'
import { deleteCookie } from '@/utils/cookie'

const router = useRouter()
const appStore = useAppStore()

// logout
const open = ref(false)
const logout = () => {
  // 1.执行退出登录接口
  // await logoutApi();

  // 2.清除 Token
  appStore.changeTheme('dark')
  appStore.resetToken()
  deleteCookie('token')
  // localStorage.clear()

  // 3.重定向到登陆页
  message.success('退出登录成功！')
  router.replace('/login')

  open.value = false
}

// 打开修改密码和个人信息弹窗
const infoRef = ref<InstanceType<typeof InfoDialog> | null>(null)
const passwordRef = ref<InstanceType<typeof PasswordDialog> | null>(null)
const openDialog = (ref: string) => {
  if (ref == 'infoRef') infoRef.value?.openDialog()
  if (ref == 'passwordRef') passwordRef.value?.openDialog()
  if (ref == 'logout') open.value = true
}
</script>

<style scoped lang="less">
.avatar {
  width: 26px;
  height: 26px;
  overflow: hidden;
  cursor: pointer;
  border-radius: 50%;
  display: flex;
  background: #d5d7e8;
  color: #e8e9f5;

  img {
    width: 100%;
    height: 100%;
  }
}
</style>
