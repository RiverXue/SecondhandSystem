<template>
  <div class="user-profile-container">
    <h2 class="page-title">个人资料</h2>
    <div class="profile-card glass-card">
      <div class="profile-avatar">
        <img :src="userStore.userInfo?.avatar || defaultAvatar" alt="用户头像" class="avatar-img">
      </div>
      <div class="profile-info">
        <div class="info-item">
          <span class="label">用户名:</span>
          <span class="value">{{ userStore.userInfo?.username || '未设置' }}</span>
        </div>
        <div class="info-item">
          <span class="label">昵称:</span>
          <span class="value">{{ userStore.userInfo?.nickname || '未设置' }}</span>
        </div>
        <div class="info-item">
          <span class="label">手机号:</span>
          <span class="value">{{ userStore.userInfo?.phone || '未设置' }}</span>
        </div>
        <div class="info-item">
          <span class="label">注册时间:</span>
          <span class="value">{{ formatDate(userStore.userInfo?.createTime) || '未知' }}</span>
        </div>
      </div>
      <button class="edit-btn" @click="goToEdit">编辑资料</button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {onMounted} from 'vue';
import {useUserStore} from '../store/user';
import {useRouter} from 'vue-router';
import defaultAvatarlogo from '../assets/codelogo.png';

const userStore = useUserStore();
const router = useRouter();
// const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';
const defaultAvatar = defaultAvatarlogo;

// 页面加载时获取用户信息
onMounted(async () => {
  if (!userStore.userInfo) {
    await userStore.fetchUserInfo();
  }
});

// 格式化日期
const formatDate = (dateString: string) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  });
};

// 跳转到编辑页面
const goToEdit = () => {
  router.push('/user/edit');
};
</script>

<style scoped>
.user-profile-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background: var(--glass-bg);
  backdrop-filter: var(--glass-backdrop);
  border: var(--glass-border);
  border-radius: 20px;
  background-image: var(--glass-highlight);
  transform: perspective(1000px) rotateY(var(--glass-distortion)) scale(var(--glass-scale));
  transition: var(--glass-transition);
  animation: var(--glass-animation);
}

.page-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: var(--text-primary);
}

.glass-card {
  background: var(--glass-bg);
  backdrop-filter: var(--glass-backdrop);
  border: var(--glass-border);
  border-radius: 20px;
  box-shadow: var(--glass-shadow);
  transition: all 0.5s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.glass-card:hover {
  border-color: var(--glass-border);
  box-shadow: var(--glass-shadow);
}

.profile-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background-color: var(--bg-color);
  border-radius: 8px;
  box-shadow: var(--glass-shadow);
}

.profile-avatar {
  margin-right: 30px;
}

.avatar-img {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid var(--border-color);
}

.profile-info {
  flex: 1;
}

.info-item {
  margin-bottom: 15px;
  font-size: 16px;
}

.label {
  display: inline-block;
  width: 100px;
  color: var(--text-secondary);
}

.value {
  color: var(--text-primary);
}

.edit-btn {
  padding: 8px 16px;
  background: linear-gradient(135deg, var(--primary-blue) 0%, var(--primary-dark) 100%);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.edit-btn:hover {
  background: linear-gradient(135deg, var(--primary-dark) 0%, var(--primary-blue) 100%);
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}
</style>