<template>
  <div class="home-container">
    <div class="search-container">
      <div class="search-bar">
        <el-input v-model="searchKeyword" class="search-input" placeholder="搜索你想要的宝贝..."></el-input>
        <el-button class="search-btn" @click="handleSearch">搜索</el-button>
        <el-button class="ai-chat-btn" @click="handleShowAiChat">
          <Message />
          AI助手
        </el-button>
      </div>
    </div>

    <div class="goods-list">
      <el-card v-for="goods in goodsStore.goodsList" :key="goods.id" class="goods-card">
        <router-link :to="`/goods/detail/${goods.id}`" class="goods-link">
          <div class="goods-image">
            <img :src="getImageUrl(goods.image) || defaultGoodsImage" alt="{{ goods.title }}" class="goods-img">
          </div>
          <div class="goods-info">
            <h3 class="goods-title">{{ goods.title }}</h3>
            <p class="goods-price">¥{{ goods.price.toFixed(2) }}</p>
            <div class="goods-footer">
              <el-button
                  :icon="goodsStore.favorites.includes(goods.id) ? StarFilled : Star"
                  class="favorite-btn"
                  link
                  @click.stop="toggleFavorite(goods.id)">
              </el-button>
            </div>
          </div>
        </router-link>
      </el-card>
    </div>

    <AiChat v-model:visible="showAiChat" />
<pre>showAiChat value: {{ showAiChat }}</pre>

    <div class="pagination">
      <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="goodsStore.total"
          layout="prev, pager, next"
          @current-change="handlePageChange"
      ></el-pagination>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {onMounted, ref} from 'vue';
import {useGoodsStore} from '../store/goods';
import {useUserStore} from '../store/user';
import {Star, StarFilled, Message} from '@element-plus/icons-vue';
import AiChat from '../components/AiChat.vue';

import defaultGoodsImage from '../assets/codelogo.png';

const goodsStore = useGoodsStore();
const userStore = useUserStore();
const searchKeyword = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const showAiChat = ref(false);

onMounted(async () => {
  await userStore.fetchUserInfo();
  await fetchGoods();
  if (userStore.accessToken) {
    await goodsStore.fetchFavorites();
  }
});

const fetchGoods = async () => {
  await goodsStore.fetchGoodsList({
    pageNum: currentPage.value,
    pageSize: pageSize.value
  });
};

const handleSearch = async () => {
  currentPage.value = 1;
  if (searchKeyword.value.trim()) {
    await goodsStore.searchGoods(
        searchKeyword.value.trim(),
        currentPage.value,
        pageSize.value
    );
  } else {
    await fetchGoods();
  }
};

const handlePageChange = async (page: number) => {
  currentPage.value = page;
  if (searchKeyword.value.trim()) {
    await goodsStore.searchGoods(
        searchKeyword.value.trim(),
        currentPage.value,
        pageSize.value
    );
  } else {
    await fetchGoods();
  }
};

const getImageUrl = (imagePath: string | undefined) => {
  console.log('生成图片URL，输入路径:', imagePath);
  const baseUrl = import.meta.env.VITE_APP_BASE_URL;
  
  if (!imagePath || typeof imagePath !== 'string') {
    console.log('无效的图片路径，使用默认图片');
    return defaultGoodsImage;
  }
  
  // 如果路径已经是完整URL，直接返回
  if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
    console.log('使用完整URL图片路径:', imagePath);
    return imagePath;
  }
  
  // 如果路径已经包含/uploads/，直接使用baseUrl拼接
  if (imagePath.startsWith('/uploads/')) {
    const result = `${baseUrl}${imagePath}`;
    console.log('使用已包含/uploads/的路径:', result);
    return result;
  }
  
  const normalizedPath = imagePath.replace(/\\/g, '/');
  const result = `${baseUrl}/uploads/${normalizedPath}`;
  console.log('生成图片URL:', result);
  
  return result;
};

const handleShowAiChat = () => {
  showAiChat.value = true;
  console.log('AI助手按钮被点击，showAiChat:', showAiChat.value);
};

const toggleFavorite = async (goodsId: number) => {
  if (!userStore.accessToken) {
    // 未登录处理
    return;
  }

  if (goodsStore.favorites.includes(goodsId)) {
    await goodsStore.removeFavorite(goodsId);
  } else {
    await goodsStore.addFavorite(goodsId);
  }
};
</script>

<style scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 10px 20px;
}

.search-container {
  background-color: white;
  padding: 15px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
}

.search-bar {
  display: flex;
  gap: 10px;
}

.search-input {
  flex: 1;
  max-width: none;
  --el-input-border-radius: 4px;
}

.search-btn {
  background-color: var(--primary-orange);
  color: white;
  width: 100px;
}

.ai-chat-btn {
  background-color: #409EFF;
  color: white;
  width: 100px;
}

.goods-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 15px;
  margin-bottom: 30px;
}

.goods-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  border-radius: 8px;
  overflow: hidden;
  border: none;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  transition: all 0.2s ease;
}

.goods-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.goods-link {
  text-decoration: none;
  color: inherit;
}

.goods-image {
  width: 100%;
  aspect-ratio: 1/1;
  overflow: hidden;
  background-color: #f9f9f9;
}

.goods-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.goods-card:hover .goods-img {
  transform: scale(1.03);
}

.goods-info {
  padding: 12px 10px 10px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.goods-title {
  font-size: 14px;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #333;
}

.goods-price {
  font-size: 18px;
  color: var(--primary-orange);
  margin-bottom: 10px;
  font-weight: bold;
}

.goods-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  color: var(--dark-gray);
  font-size: 12px;
}

.goods-location {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.favorite-btn {
  color: var(--dark-gray);
}

.favorite-btn:hover {
  color: var(--primary-orange);
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.el-pagination .btn-next, .el-pagination .btn-prev {
  color: var(--primary-blue);
}

.el-pagination .el-pager li.active {
  color: var(--primary-blue);
  font-weight: 500;
}
</style>
