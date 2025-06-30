<template>
  <div class="home-container">
    <div class="search-container">
      <div class="search-bar">
        <el-input v-model="searchKeyword" class="search-input" placeholder="🔎 搜索你想要的宝贝..."></el-input>
        <el-button class="search-btn" @click="handleSearch">搜索</el-button>
        <el-button class="ai-chat-btn" @click="handleShowAiChat">
          <Message/>
          🤖 AI助手
        </el-button>
      </div>
    </div>

    <div class="goods-list">
      <el-card v-for="goods in goodsStore.goodsList" :key="goods.id" class="goods-card glass-card">
        <router-link :to="`/goods/detail/${goods.id}`" class="goods-link">
          <div class="goods-image">
            <img :src="getImageUrl(goods.image) || defaultGoodsImage" alt="{{ goods.title }}" class="goods-img">
            <div v-if="goods.status === 'sold'" class="sold-overlay">
              <div class="sold-badge">已售出</div>
            </div>
          </div>
          <div class="goods-info">
            <h3 class="goods-title">{{ goods.title }}</h3>
            <p class="goods-price">¥{{ goods.price.toFixed(2) }}</p>
            <div class="goods-footer">
            </div>
          </div>
        </router-link>
      </el-card>
    </div>

    <AiChat v-model:visible="showAiChat"/>

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

  <button class="floating-ai-btn" @click="handleShowAiChat">
    <Robot/>
  </button>

  <pre> Disgined by RiverX </pre>

</template>

<script lang="ts" setup>
import {onMounted, ref} from 'vue';
import {useGoodsStore} from '../store/goods';
import {useUserStore} from '../store/user';
import {Message, UserFilled as Robot} from '@element-plus/icons-vue'; // 假设使用 UserFilled 作为 Robot 替代，实际可根据需求更换为合适的图标
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
  background: var(--glass-bg);
  backdrop-filter: blur(16px);
  border: var(--glass-border);
  padding: 15px 20px;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(22, 119, 255, 0.1), 0 1px 0 rgba(22, 119, 255, 0.1) inset;
  margin-bottom: 20px;
  position: relative;
}

.search-container::after {
  content: '';
  position: absolute;
  bottom: -5px;
  left: 0;
  right: 0;
  height: 5px;
  background: linear-gradient(0deg, rgba(22, 119, 255, 0.1) 0%, transparent 100%);
  border-radius: 0 0 16px 16px;
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
  background-color: var(--primary-blue);
  color: white;
  width: 100px;
}

.floating-ai-btn {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: rgba(16, 105, 255, 0.8);
  color: white;
  box-shadow: 0 0 12px #69B1FF;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  cursor: pointer;
  z-index: 1000;
  transition: all 0.3s ease;
}

.floating-ai-btn:hover {
  transform: scale(1.05);
}

.floating-ai-btn:active {
  animation: ripple 0.6s ease;
}

@keyframes ripple {
  0% {
    box-shadow: 0 0 0 0 rgba(105, 177, 255, 0.7);
  }
  70% {
    box-shadow: 0 0 0 15px rgba(105, 177, 255, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(105, 177, 255, 0);
  }
}

.goods-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 15px;
  margin-bottom: 30px;
}

.goods-card {
  background: var(--glass-bg);
  backdrop-filter: blur(16px);
  border: var(--glass-border);
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s ease;
  will-change: transform, opacity;
}

.goods-card:hover {
  border-radius: 18px;
  opacity: 0.9;
  transform: translateY(-5px);
  box-shadow: 0 12px 24px rgba(22, 119, 255, 0.15);
}

.goods-title {
  color: #F8FAFC;
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.goods-price {
  color: #165DFF;
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 4px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .goods-card {
    backdrop-filter: blur(6px);
    border-radius: 10px;
  }
}

.goods-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px #40a0ffae;
  transition: all 0.3s ease;
}

.goods-card:active {
  transform: translateY(1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.goods-link {
  text-decoration: none;
  color: inherit;
}

.goods-image {
  width: 100%;
  aspect-ratio: 1/1;
  overflow: hidden;
}

.goods-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  aspect-ratio: 1/1;
  overflow: hidden;
  background-color: #f9f9f9;
  position: relative;
}

.goods-image::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 0;
  background: linear-gradient(to top, rgba(64, 160, 255, 0.199), transparent);
  transition: height 0.3s ease;
}

.goods-card:hover .goods-image::after {
  height: 40%;
}

.goods-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
  animation: imageLoad 0.5s ease-out;
}

@keyframes imageLoad {
  from {
    filter: blur(10px);
  }
  to {
    filter: blur(0);
  }
}

.goods-card:hover .goods-img {
  transform: scale(1.03);
}

.goods-info {
  padding: 12px;
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
  color: #409eff;
  margin-bottom: 10px;
  font-weight: bold;
}

.sold-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

.sold-badge {
  background: var(--glass-gradient-orange);
  color: white;
  padding: 8px 16px;
  border-radius: 8px 8px 0 0;
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
  }
  to {
    transform: translateY(0);
  }
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
  color: var(--primary-blue);
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
