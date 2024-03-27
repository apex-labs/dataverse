<script lang="ts" setup>
import { ref, reactive } from 'vue'
import type { Rule } from 'ant-design-vue/es/form';

interface FormState {
  username: string
  password: string
  checkPass: string
}

const formState = reactive<FormState>({
  username: '',
  password: '',
  checkPass: ''
})

const validateName = async (_rule: Rule, value: string) => {
  if (value === '') {
    return Promise.reject('请输入账户');
  } else {
    const reg = /^[\u4E00-\u9FA5A-Za-z0-9]{2,20}$/
    if (reg.test(value)) {
      return Promise.resolve()
    } else {
      return Promise.reject('由中文、英文或数字组成的字符串，且长度在20以内')
    }
  }
}
const validatePass = async (_rule: Rule, value: string) => {
  if (value === '') {
    return Promise.reject('请输入密码')
  } else {
    const reg = /^[a-zA-Z]\w{5,17}$/ // 弱密码 以字母开头，长度在6~18之间，只能包含字母、数字和下划线
    // const reg = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,10}$/ // 强密码 必须包含大小写字母和数字的组合，可以使用特殊字符，长度在8-10之间
    if (reg.test(value)) {
      return Promise.resolve()
    } else {
      return Promise.reject('以字母开头，长度在6~18之间，只能包含字母、数字和下划线')
    }
  }
}
const validatePass2 = async (_rule: Rule, value: string) => {
  if (value === '') {
    return Promise.reject('请再次输入密码');
  } else if (value !== formState.password) {
    return Promise.reject('两次密码不一致，请重新输入')
  } else {
    return Promise.resolve();
  }
}
const rules: Record<string, Rule[]> = {
  username: [{ required: true, validator: validateName, trigger: ['change', 'blur'] }],
  password: [{ required: true, validator: validatePass, trigger: ['change', 'blur'] }],
  checkPass: [{ required: true, validator: validatePass2, trigger: ['change', 'blur'] }]
}

const loading = ref(false)
const onFinish = (values: any) => {
  loading.value = true
  console.log('Success:', values)
  loading.value = false
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
        <h3 class="tit">注册<br>Sign up</h3>
        <p class="ftit">欢迎注册体验 Dataverse 数据开发平台</p>
        <div class="flex1">
          <a-form :model="formState" :rules="rules" name="login" autocomplete="off" @finish="onFinish"
            @finishFailed="onFinishFailed">
            <p class="cw">账户</p>
            <a-form-item label="" name="username">
              <a-input v-model:value="formState.username" placeholder="请输入账户" />
            </a-form-item>

            <p class="cw">密码</p>
            <a-form-item label="" name="password">
              <a-input-password v-model:value="formState.password" placeholder="请输入密码" />
            </a-form-item>

            <p class="cw">确认密码</p>
            <a-form-item label="" name="checkPass">
              <a-input-password v-model:value="formState.checkPass" placeholder="请输入密码" />
            </a-form-item>

            <a-form-item class="mt50">
              <a-button :loading="loading" class="w100 btn" size="large" type="primary" html-type="submit"
                style="background:#0057E6">注册</a-button>
            </a-form-item>
          </a-form>
        </div>
        <p class="ac footer">
          <RouterLink class="register" to="/login">已有账号，请登录</RouterLink>
        </p>
      </div>
    </div>
  </div>
</template>

<style lang="less" scoped>
.index {
  background: var(--bg-color-1);
  color: #fff;

  .info {
    background: var(--bg-color-1) url('@/assets/imgs/login_bg.png') 50% 50%/cover no-repeat;
    animation: 20s linear infinite alternate both bg-move;
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