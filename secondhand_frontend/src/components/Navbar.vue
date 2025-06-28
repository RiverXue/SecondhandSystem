<template>
  <el-menu :default-active="activeIndex" class="el-menu-demo glass-nav" mode="horizontal" @select="handleSelect">
    <el-menu-item index="/">首页</el-menu-item>
    <el-menu-item index="/publish">发布商品</el-menu-item>
    <el-menu-item index="/user-center">个人中心</el-menu-item>
    <el-menu-item v-if="!userStore.accessToken" index="/login">登录</el-menu-item>
    <el-menu-item v-if="!userStore.accessToken" index="/register">注册</el-menu-item>
    <el-menu-item v-if="userStore.accessToken" index="logout" @click="handleLogout">退出登录</el-menu-item>
  </el-menu>
</template>

<script lang="ts" setup>
import {computed, onMounted, onUnmounted, ref} from 'vue';
import {useRouter} from 'vue-router';
import {useUserStore} from '../store/user';
import {ElMessage} from 'element-plus';

const isScrolled = ref(false);

const handleScroll = () => {
  isScrolled.value = window.scrollY > 10;
};

onMounted(() => {
  window.addEventListener('scroll', handleScroll);
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});

const router = useRouter();
const userStore = useUserStore();

const activeIndex = computed(() => router.currentRoute.value.path);

const handleSelect = (key: string) => {
  if (key !== 'logout') {
    router.push(key);
  }
};

const handleLogout = async () => {
  try {
    await userStore.logout();
    ElMessage.success('退出登录成功');
    router.push('/login');
  } catch (error) {
    console.error('退出登录失败:', error);
    ElMessage.error('退出登录失败，请稍后再试');
  }
};
</script>

<style scoped>
.el-menu-demo {
  margin-bottom: 20px;
}

.glass-nav {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(255,255,255,0.8);
  backdrop-filter: blur(16px);
  border-bottom: var(--glass-border);
  box-shadow: 0 4px 12px rgba(22,119,255,0.1), 0 1px 0 rgba(22,119,255,0.1) inset;
  color: var(--text-primary);
  border-radius: 16px 16px 0 0;
  transition: background 0.3s ease;
}

.glass-nav.scrolled {
  background: rgba(255,255,255,0.95);
}

.el-menu-item {
  color: #333333 !important;
  transition: all 0.2s ease;
}

.el-menu-item:hover,
.el-menu-item.is-active {
  color: #165DFF !important;
  background-color: rgba(22, 93, 255, 0.05);
  transition: all 0.2s ease;
  border-bottom: 2px solid transparent;
}

.el-menu-item:hover {
  transform: scale(1.05);
  opacity: 0.9;
}

.el-menu-item.is-active {
  border-bottom: 2px solid var(--primary-blue);
  color: var(--primary-blue);
}
</style>