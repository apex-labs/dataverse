<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import type { Rule } from 'ant-design-vue/es/form';
import { encryption } from '@/utils'
import { userLogin } from '@/api/modules/login'
import { setCookie } from '@/utils/cookie'
import { useAppStore } from '@/stores/modules/app'
const appStore = useAppStore()
const router = useRouter()

const DEFAULT_USERNAME = 'nexus_datavs_vip'
const DEFAULT_PASSWORD = 'Apexdataverse0320!'

interface FormState {
  username: string
  password: string
  remember: boolean
  loginType: number
}

const loading = ref(false)
const loginForm = ref()
const isOnProbation = ref(true)
const formState = reactive<FormState>({
  username: DEFAULT_USERNAME,
  password: DEFAULT_PASSWORD,
  remember: false,
  loginType: 1
})

const rules: Record<string, Rule[]> = {
  password: [{ required: true, message: '请输入密码', trigger: ['change', 'blur'] }],
  username: [{ required: true, message: '请输入账户', trigger: ['change', 'blur'] }]
}

const changeType = () => {
  formState.loginType = isOnProbation.value ? 1 : 0
  if (isOnProbation.value) {
    Object.assign(formState, { username: DEFAULT_USERNAME, password: DEFAULT_PASSWORD })
  } else {
    Object.assign(formState, { username: '', password: '' })
  }
  loginForm.value.clearValidate()
}

const onFinish = (values: any) => {
  loading.value = true
  const params = encryption(values)
  userLogin(params).then((res: any) => {
    if (res.responseStatus === 200) {
      appStore.setToken(res.data)
      setCookie('token', res.data, 0.25)
      message.success('登录成功！')
      router.push('/overview')
    } else {
      message.error(res.errorMsg)
    }
  }).finally(() => {
    loading.value = false
  })
}

const onFinishFailed = (errorInfo: any) => {
  console.log('Failed:', errorInfo)
}
</script>

<template>
  <div class="h100 index flex">
    <div class="flex1 flex fdc info">
      <div class="logo">
        <img src="@/assets/imgs/logo_apex.png" alt="">
      </div>
      <div class="flex1 flex aic">
        <div class="con">
          <div class="title">
            <img src="@/assets/imgs/logo_DATAVS.png" alt="">
          </div>
          <h4 class="ftitle mt30 mb20">湖仓一体低代码开发平台</h4>
          <p class="desc">DVS数据开发平台帮助企业高效、自主的利用企业自有数据资产，<br>并在有限的资源情况下，最大限度的帮助企业建立自有和便捷的数据研发能力。</p>
        </div>
      </div>
    </div>
    <div class="login flex aic">
      <div class="flex1 flex fdc">
        <h3 class="tit">登录<br>Log in</h3>
        <p class="ftit">欢迎注册体验 Dataverse 数据开发平台</p>
        <div class="flex1">
          <a-form :model="formState" :rules="rules" ref="loginForm" name="login" autocomplete="off" @finish="onFinish"
            @finishFailed="onFinishFailed">
            <p class="cw">账户</p>
            <a-form-item label="" name="username">
              <a-input v-model:value="formState.username" placeholder="请输入账户" />
            </a-form-item>

            <p class="cw">密码</p>
            <a-form-item label="" name="password">
              <a-input-password v-model:value="formState.password" placeholder="请输入密码" />
            </a-form-item>

            <div class="fxbw">
              <a-form-item name="remember">
                <a-checkbox v-model:checked="formState.remember" class="cw">记住密码</a-checkbox>
              </a-form-item>
              <a-form-item name="loginType">
                <a-checkbox v-model:checked="isOnProbation" class="cw" @change="changeType">选择试用账号登录</a-checkbox>
              </a-form-item>
            </div>

            <a-form-item>
              <a-button class="w100 btn" :loading="loading" size="large" type="primary" html-type="submit"
                style="background:#0057E6">登
                录</a-button>
              <!-- <p class="ar mt10">
              <RouterLink to="/resetPassword">忘记密码</RouterLink>
            </p> -->
            </a-form-item>
          </a-form>
        </div>
      </div>
      <!-- <p class="ac footer">
        <RouterLink class="register" to="/register">没有账号，请注册</RouterLink>
      </p> -->
    </div>
  </div>
</template>

<style lang="less" scoped>
.index {
  background: var(--bg-color-1);
  color: #fff;

  .info {
    background: var(--bg-color-1) url('@/assets/imgs/login_bg.png') 50% 50%/cover no-repeat;
    animation: 20s linear both bg-move;
    padding: 50px 70px;

    .con {
      animation: text-move 5s 0.5s both ease-in-out;

      .ftitle {
        font-size: 30px;
        line-height: 1;
      }

      .desc {
        font-size: 16px;
        line-height: 2.4;
      }
    }
  }

  .login {
    width: 33%;
    max-width: 620px;
    min-width: 400px;
    padding: 10px 50px;
    background: linear-gradient(179deg, #2B2B2B, #000000);
    position: relative;

    .tit {
      font-size: 36px;
      line-height: 40px;
    }

    .ftit {
      margin: 50px 0;
    }

    .footer {
      position: absolute;
      bottom: 20px;
      left: 50%;
      transform: translateX(-50%);
    }
  }

  .register {
    color: #fff;
  }
}

.cw {
  color: #fff !important;
}

@keyframes bg-move {
  0% {
    background-position: 100%;
  }

  100% {
    background-position: 50%;
  }
}

@keyframes text-move {
  0% {
    opacity: 0;
  }

  100% {
    opacity: 1;
  }
}

@media screen and (max-width: 1024px) {
  .index{
    background: #000 url('@/assets/imgs/bg_login_m.png') 50% 50%/cover no-repeat;
    align-items: center;
    justify-content: center;
    .info{display: none;}
    .login{
      width:60%;
      min-width: 300px;
      padding: 0;
      background:transparent;
      // .tit{font-size: 48px;}
      // .ftit{font-size: 30px;}
      .footer{
        transform: translate(-50%,20px);
      }
      :deep(.ant-input){
        background:rgba(30,30,30,.8) !important;
        border-color:#000 !important;
        padding: 8px 11px !important;
      }
      .ant-input-affix-wrapper{
        background:rgba(30,30,30,.8) !important;
        border-color:#000 !important;
        padding:0 11px!important;
        :deep(.ant-input){
          background-color: transparent !important;
          border-color:transparent !important;
          padding: 8px 0 !important;
        }
      }
      :deep(.ant-checkbox .ant-checkbox-inner){
        background:rgba(30,30,30,.8) !important;
      }
      .btn{
        background: linear-gradient( 90deg, #5B63E9 0%, #5B63E9 30%, #681E92 100%) !important;
        border-radius: 20px;
        border: none;
      }
    }
  }
}
</style>