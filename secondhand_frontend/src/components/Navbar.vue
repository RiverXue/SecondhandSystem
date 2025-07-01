<template>
  <div class="nav-wrapper">
    <!-- 左侧菜单 -->
    <el-menu
        :default-active="activeIndex"
        class="el-menu-demo glass-nav"
        mode="horizontal"
        @select="handleSelect"
    >
      <el-menu-item index="/">首页</el-menu-item>
      <el-menu-item index="/publish">发布商品</el-menu-item>
      <el-menu-item index="/favorites">我的收藏</el-menu-item>
      <el-menu-item index="/orders">我的交易</el-menu-item>
    </el-menu>

    <!-- 右侧：未登录时显示登录/注册 -->
    <div v-if="!userStore.accessToken" class="auth-buttons">
      <el-button link @click="router.push('/login')">登录</el-button>
      <el-button link @click="router.push('/register')">注册</el-button>
    </div>

    <!-- 右侧：登录后显示头像和下拉菜单 -->
    <el-dropdown v-else>
      <span class="el-dropdown-link">
        <el-avatar
            :src="getAvatarUrl(userStore.userInfo?.avatar)"
            size="small"
            @error="handleAvatarError"
        />
        <span class="username">{{ userStore.userInfo?.username || '用户' }}</span>
        <el-icon><arrow-down/></el-icon>
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="goToUserCenter">个人中心</el-dropdown-item>
          <!--          <el-dropdown-item @click="goToFavorites">我的收藏</el-dropdown-item>-->
          <!--          <el-dropdown-item @click="goToOrders">我的订单</el-dropdown-item>-->
          <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script lang="ts" setup>
import {computed} from 'vue';
import {useRouter} from 'vue-router';
import {useUserStore} from '../store/user';
import {ElMessage} from 'element-plus';
import {ArrowDown} from '@element-plus/icons-vue';
import defaultAvatarlogo from '../assets/codelogo.png';

const router = useRouter();
const userStore = useUserStore();

const defaultAvatar = defaultAvatarlogo; // 默认头像

const getAvatarUrl = (avatarPath: string | undefined) => {
  if (!avatarPath || avatarPath.trim() === '') return defaultAvatar;
  const baseUrl = import.meta.env.VITE_APP_BASE_URL || '';
  if (avatarPath.startsWith('http://') || avatarPath.startsWith('https://')) {
    return avatarPath;
  }
  if (avatarPath.startsWith('/uploads/')) {
    return `${baseUrl}${avatarPath}`;
  }
  return `${baseUrl}/uploads/${avatarPath}`;
};

const handleAvatarError = (e: Event) => {
  const img = e.target as HTMLImageElement;
  img.src = defaultAvatar;
};

const activeIndex = computed(() => router.currentRoute.value.path);

const handleSelect = (key: string) => {
  router.push(key);
};

const goToUserCenter = () => {
  router.push('/user-center');
};

const goToFavorites = () => {
  router.push('/favorites');
};

const goToOrders = () => {
  router.push('/orders');
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
.nav-wrapper {
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
  padding: 0 20px;
  border-radius: 16px 16px 0 0;
  box-shadow: 0 5px 12px rgba(22, 119, 255, 0.15), 0 1px 0 rgba(22, 119, 255, 0.1) inset;
}


.el-menu-demo {
  background: transparent;
  border-bottom: none;
  flex: 1;
}

/* 登录/注册按钮 */
.auth-buttons {
  display: flex;
  gap: 10px;
  align-items: center;
}

/* 用户头像和用户名 */
.el-dropdown-link {
  display: flex;
  align-items: center;
  cursor: pointer;
  font-weight: bold;
  color: #333;
  gap: 8px;
  padding: 0 8px;
}

.el-dropdown-link:hover {
  opacity: 0.85;
}

.username {
  margin-left: 4px;
}
</style>
