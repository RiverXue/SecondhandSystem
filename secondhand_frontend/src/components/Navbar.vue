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
import {computed} from 'vue';
import {useRouter} from 'vue-router';
import {useUserStore} from '../store/user';
import {ElMessage} from 'element-plus';

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
  background: rgba(248, 250, 252, 0.85);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(220, 220, 220, 0.3);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  color: #333333;
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
  border-bottom: 2px solid #9D4EDD;
  color: #fff;
}
</style>