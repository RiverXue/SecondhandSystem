<template>
  <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal" @select="handleSelect">
    <el-menu-item index="/">首页</el-menu-item>
    <el-menu-item index="/publish">发布商品</el-menu-item>
    <el-menu-item index="/user-center">个人中心</el-menu-item>
    <el-menu-item v-if="!userStore.accessToken" index="/login">登录</el-menu-item>
    <el-menu-item v-if="!userStore.accessToken" index="/register">注册</el-menu-item>
    <el-menu-item v-if="userStore.accessToken" index="logout" @click="handleLogout">退出登录</el-menu-item>
  </el-menu>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '../store/user';
import { ElMessage } from 'element-plus';

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
</style>