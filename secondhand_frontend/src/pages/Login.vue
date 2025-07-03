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
  background-color: var(--bg-color);
  background-image: radial-gradient(circle at 25% 50%, var(--primary-gradient-light) 0%, transparent 50%);
}

.login-card {
  width: 400px;
  padding: 30px;
  border-radius: 16px;
  border: var(--glass-border);
  background: var(--glass-bg);
  backdrop-filter: blur(16px);
  box-shadow: var(--glass-shadow);
  background-image: var(--glass-highlight);
  transform: perspective(1000px) rotateY(var(--glass-distortion)) scale(var(--glass-scale));
  transition: var(--glass-transition);
}

.title {
  text-align: center;
  margin-bottom: 20px;
  color: var(--text-primary);
  font-size: 24px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.el-input {
  background: var(--glass-bg);
  border: var(--glass-border);
  border-radius: 8px;
  color: var(--text-primary);
  transition: all 0.3s ease;
}

.el-input__wrapper {
  background: transparent !important;
  box-shadow: none !important;
}

.el-input:focus-within {
  border-color: var(--border-focus);
  box-shadow: 0 0 0 2px rgba(13, 150, 255, 0.2);
}

.login-btn {
  width: 100%;
  margin-bottom: 10px;
  background-color: var(--primary-blue);
  border: none;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(22, 93, 255, 0.3);
}

.el-button--default {
  color: var(--text-secondary);
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.el-button--default:hover {
  background: rgba(255, 255, 255, 0.1);
  color: var(--text-primary);
}
</style>
