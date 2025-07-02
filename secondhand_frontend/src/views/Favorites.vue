<template>
  <div class="favorites-container">
    <h2 class="page-title">我的收藏</h2>
    <div v-if="favoriteStore.loading" class="loading">加载中...</div>
    <div v-else-if="!favoriteStore.loading && favoriteStore.favorites.length === 0" class="empty-tip">
      <p>您还没有收藏任何商品</p>
      <router-link class="go-shopping-btn" to="/">去逛逛</router-link>
    </div>
    <div v-else class="favorites-list">
      <div v-for="goods in favoriteStore.favorites" :key="goods.id" class="favorite-item">
        <router-link :to="`/goods/detail/${goods.id}`" class="item-link">
          <img :src="getImageUrl(goods.image) || defaultGoodsImage" alt="{{ goods.title }}" class="goods-img">
          <div class="goods-info">
            <h3 class="goods-title">{{ goods.title }}</h3>
            <p class="goods-price">{{ goods.price.toFixed(2) }}</p>
          </div>
        </router-link>
        <button
            :disabled="favoriteStore.loading"
            class="remove-btn"
            @click="removeFavorite(goods.id)"
        >
          {{ favoriteStore.loading ? '取消中...' : '取消收藏' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {onMounted} from 'vue';
import {useFavoriteStore} from '../store/favorite';
import defaultGoodsImage from '../assets/codelogo.png';
import {useUserStore} from "../store/user.ts";
import {ElMessage} from "element-plus";
import router from "../router";

const favoriteStore = useFavoriteStore();

const getImageUrl = (imagePath: string | undefined) => {
  if (!imagePath || typeof imagePath !== 'string') {
    return defaultGoodsImage;
  }
  // 如果已经是完整URL则直接使用
  if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
    return imagePath;
  }
  // 如果路径已经包含/uploads/，直接使用
  if (imagePath.startsWith('/uploads/')) {
    return imagePath;
  }
  const normalizedPath = imagePath.replace(/\\/g, '/');
  const baseUrl = import.meta.env.VITE_APP_BASE_URL || '';
  return `${baseUrl}/uploads/${normalizedPath}`;
};

// 页面加载时获取收藏列表
onMounted(() => {
  (async () => {
    const userStore = useUserStore();
    if (!userStore.accessToken) {
      ElMessage.warning('请先登录');
      router.push('/login');
      return;
    }

    // 用户信息未加载则加载
    if (!userStore.userInfo) {
      await userStore.fetchUserInfo();
    }

    await favoriteStore.getFavoriteList();
    console.log('测试收藏列表：', favoriteStore.favorites);

  })();
});

// 取消收藏
const removeFavorite = async (goodsId: number) => {
  if (confirm('确定要取消收藏这件商品吗？')) {
    try {
      const res = await favoriteStore.removeFavorite(goodsId);
      if (res.code === 200) {
        // 取消成功，刷新列表
        favoriteStore.getFavoriteList();
      }
    } catch (error) {
      console.error('取消收藏失败:', error);
    }
  }
};
</script>

<style scoped>
.favorites-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: var(--text-color-primary);
}

.loading,
.empty-tip {
  text-align: center;
  padding: 50px 0;
  color: var(--text-color-secondary);
  background: var(--glass-bg);
  backdrop-filter: var(--glass-backdrop);
  border: var(--glass-border);
  border-radius: 20px;
  box-shadow: var(--glass-shadow);
  margin: 20px 0;
}

.go-shopping-btn {
  display: inline-block;
  margin-top: 15px;
  padding: 8px 16px;
  background: var(--primary-color);
  color: white;
  text-decoration: none;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.go-shopping-btn:hover {
  transform: scale(1.02);
  box-shadow: var(--glass-shadow);
}

.favorites-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.favorite-item {
  display: flex;
  align-items: center;
  padding: 15px;
  background: var(--glass-bg);
  backdrop-filter: var(--glass-backdrop);
  border: var(--glass-border);
  border-radius: 20px;
  box-shadow: var(--glass-shadow);
  transition: all 0.5s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.favorite-item:hover {
  border-color: rgba(255, 255, 255, 0.2);
  box-shadow: var(--glass-shadow);
}

.item-link {
  display: flex;
  align-items: center;
  flex: 1;
  text-decoration: none;
  color: inherit;
}

.goods-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 15px;
}

.goods-info {
  flex: 1;
}

.goods-title {
  font-size: 16px;
  margin-bottom: 8px;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.goods-price {
  font-size: 18px;
  font-weight: 500;
  color: var(--danger-color);
}

.remove-btn {
  background-color: var(--bg-secondary);
  color: var(--text-secondary);
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.remove-btn:hover {
  background: linear-gradient(135deg, var(--danger-color) 0%, var(--danger-dark-color) 100%);
  transform: scale(1.02);
  box-shadow: var(--danger-shadow);
}

.remove-btn:disabled {
  background: var(--text-secondary);
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}
</style>