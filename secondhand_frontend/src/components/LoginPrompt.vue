<template>
  <div class="login-prompt">
    <div class="prompt-content">
      <el-icon class="login-icon"><User /></el-icon>
      <p class="prompt-text">登录后即可参与留言讨论</p>
      <router-link to="/login" class="login-button">
        <el-button type="primary" size="small">立即登录</el-button>
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ElButton, ElIcon } from 'element-plus';
import { User } from '@element-plus/icons-vue';
import { RouterLink } from 'vue-router';
</script>

<style scoped lang="scss">
.login-prompt {
  margin-bottom: 20px;
  padding: 24px 16px;
  background-color: #f5f7fa;
  border-radius: 12px;
  text-align: center;
}

.prompt-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.login-icon {
  font-size: 28px;
  color: #c0c6cf;
}

.prompt-text {
  font-size: 14px;
  color: #606266;
  margin: 0;
}

.login-button {
  margin-top: 8px;
}
</style>