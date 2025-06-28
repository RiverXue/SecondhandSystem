<template>
  <div class="login-container">
    <el-card class="login-card glass-card">
      <h2 class="title">用户登录</h2>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="loginForm.password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" class="login-btn">登录</el-button>
          <el-button @click="goToRegister">没有账号？去注册</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { ElMessage, ElForm, ElFormItem, ElInput, ElButton, ElCard } from 'element-plus'
import type { AxiosError } from 'axios'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref<InstanceType<typeof ElForm>>()

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  try {
    await loginFormRef.value.validate()
    const res = await userStore.login(loginForm)
    if (res.code === 200) {
      ElMessage.success('登录成功')
      router.push('/')
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch (error) {
    const axiosError = error as AxiosError
    if (axiosError.response) {
      const data = axiosError.response.data as any
      ElMessage.error(data.message || '登录失败')
    } else {
      ElMessage.error('网络错误，请稍后重试')
    }
  }
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #F8FAFC;
  background-image: radial-gradient(circle at 25% 50%, rgba(22, 93, 255, 0.05) 0%, transparent 50%);
}

.login-card {
  width: 400px;
  padding: 30px;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(8px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}

.title {
  text-align: center;
  margin-bottom: 20px;
  color: #F8FAFC;
  font-size: 24px;
}

.el-input {
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  color: #F8FAFC;
  transition: all 0.3s ease;
}

.el-input__wrapper {
  background: transparent !important;
  box-shadow: none !important;
}

.el-input:focus-within {
  border-color: #9D4EDD;
  box-shadow: 0 0 0 2px rgba(157, 78, 221, 0.2);
}

.login-btn {
  width: 100%;
  margin-bottom: 10px;
  background: linear-gradient(135deg, #165DFF 0%, #4080FF 100%);
  border: none;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.login-btn:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(22, 93, 255, 0.3);
}

.el-button--default {
  color: #CBD5E1;
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.el-button--default:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #F8FAFC;
}
</style>
