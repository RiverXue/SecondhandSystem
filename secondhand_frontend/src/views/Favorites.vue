<template>
  <div class="favorites-container">
    <h2 class="page-title">我的收藏</h2>
    <div v-if="favoriteStore.loading" class="loading">加载中...</div>
    <div v-else-if="!favoriteStore.loading && favoriteStore.favorites.length === 0" class="empty-tip">
      <p>您还没有收藏任何商品</p>
      <router-link class="go-shopping-btn" to="/home">去逛逛</router-link>
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
            @click="removeFavorite(goods.goodsId)"
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

const favoriteStore = useFavoriteStore();

const getImageUrl = (imagePath: string | undefined) => {
  if (!imagePath || typeof imagePath !== 'string') {
    return defaultGoodsImage;
  }
  const normalizedPath = imagePath.replace(/\\/g, '/');
  return `/uploads/${normalizedPath}`;
};

// 页面加载时获取收藏列表
onMounted(() => {
  favoriteStore.getFavoriteList();
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
  color: #333;
}

.loading,
.empty-tip {
  text-align: center;
  padding: 50px 0;
  color: #666;
}

.go-shopping-btn {
  display: inline-block;
  margin-top: 15px;
  padding: 8px 16px;
  background-color: #409eff;
  color: white;
  text-decoration: none;
  border-radius: 4px;
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
  border: 1px solid #eee;
  border-radius: 8px;
  transition: box-shadow 0.3s;
}

.favorite-item:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
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
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.goods-price {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
}

.remove-btn {
  padding: 6px 12px;
  background-color: #f56c6c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.remove-btn:hover {
  background-color: #e4393c;
}

.remove-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>