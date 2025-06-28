<template>
  <div class="favorites-container">
    <h2 class="page-title">我的收藏</h2>
    <div v-if="favoriteStore.loading" class="loading">加载中...</div>
    <div v-else-if="!favoriteStore.loading && favoriteStore.favorites.length === 0" class="empty-tip glass-card">
      <p>您还没有收藏任何商品</p>
      <router-link class="go-shopping-btn" to="/">去逛逛</router-link>
    </div>
    <div v-else class="favorites-list">
      <div v-for="goods in favoriteStore.favorites" :key="goods.id" class="favorite-item glass-card">
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
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: #F8FAFC;
}

.glass-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.glass-card:hover {
  border-color: rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}

.favorite-item {
  display: flex;
  align-items: center;
  padding: 15px;
  margin-bottom: 15px;
}

.goods-title {
  color: #F8FAFC;
  font-size: 16px;
  margin-bottom: 5px;
}

.goods-price {
  color: #165DFF;
  font-weight: bold;
}

.remove-btn {
  background: rgba(239, 68, 68, 0.1);
  color: #EF4444;
  border: 1px solid rgba(239, 68, 68, 0.2);
  border-radius: 8px;
  padding: 6px 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.remove-btn:hover {
  background: rgba(239, 68, 68, 0.2);
  transform: scale(1.05);
}

.empty-tip {
  text-align: center;
  padding: 50px 20px;
  color: #94A3B8;
}

.go-shopping-btn {
  display: inline-block;
  margin-top: 15px;
  padding: 8px 16px;
  background: linear-gradient(135deg, #165DFF 0%, #4080FF 100%);
  color: white;
  border-radius: 8px;
  text-decoration: none;
  transition: all 0.2s ease;
}

.go-shopping-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(22, 93, 255, 0.3);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .glass-card {
    backdrop-filter: blur(6px);
    border-radius: 10px;
  }
}
</style>