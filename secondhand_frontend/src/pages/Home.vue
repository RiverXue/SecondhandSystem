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

</script>

<style scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 10px 20px;
}

.search-container {
  background: var(--glass-bg);
  backdrop-filter: var(--glass-backdrop);
  border: var(--glass-border);
  padding: 15px 20px;
  border-radius: 16px;
  box-shadow: var(--glass-shadow);
  margin: 7px 0 23px 0;
  position: relative;
  transform: perspective(1000px) rotateY(var(--glass-distortion)) scale(var(--glass-scale));
  transition: var(--glass-transition);
  overflow: hidden;
  width: 100%;
  box-sizing: border-box;
}

.search-container::after {
  content: '';
  position: absolute;
  bottom: -5px;
  left: 0;
  right: 0;
  height: 5px;
  background: linear-gradient(0deg, var(--primary-gradient-light) 0%, transparent 100%);
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
  color: var(--text-on-primary);
  width: 100px;
}

.floating-ai-btn {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: var(--primary-blue-transparent);
  color: var(--text-on-primary);
  box-shadow: 0 0 12px var(--primary-shadow);
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
    box-shadow: 0 0 0 0 var(--primary-blue-transparent);
  }
  70% {
    box-shadow: 0 0 0 15px var(--primary-blue-transparent);
  }
  100% {
    box-shadow: 0 0 0 0 var(--primary-blue-transparent);
  }
}

.goods-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 15px;
  margin-bottom: 30px;
}

.goods-card.glass-card {
  background-image: var(--glass-highlight);
  transform: perspective(1200px) rotateY(var(--glass-distortion)) scale(var(--glass-scale));
  transition: var(--glass-transition);
  animation: var(--glass-animation);
  backdrop-filter: var(--glass-backdrop);
  box-shadow: var(--glass-shadow), inset 0 0 30px rgba(255, 255, 255, 0.15);
  background: var(--glass-bg);
  border: var(--glass-border);
  border-radius: 24px;
  overflow: hidden;
  position: relative;
  will-change: transform, box-shadow;
}

.goods-card.glass-card::before {
  content: '';
  position: absolute;
  top: -50px;
  left: -50px;
  width: calc(100% + 100px);
  height: calc(100% + 100px);
  background: radial-gradient(circle at 15% 25%, rgba(255, 255, 255, 0.4), transparent 50%), radial-gradient(circle at 85% 75%, rgba(100, 160, 255, 0.45), transparent 55%), radial-gradient(circle at 50% 50%, rgba(255, 255, 255, 0.25), transparent 65%), radial-gradient(circle at 30% 70%, rgba(200, 220, 255, 0.3), transparent 60%), inherit;
  background-size: 200% 200%, 200% 200%, 200% 200%, 200% 200%;
  filter: blur(80px);
  opacity: 0.98;
  z-index: -1;
  transition: filter 0.5s ease, opacity 0.5s ease, transform 0.5s ease;
  transform: translateY(0);
  box-shadow: 0 0 200px rgba(255, 255, 255, 0.4), 0 0 60px rgba(100, 160, 255, 0.25) inset, 0 0 20px rgba(255, 255, 255, 0.3) inset;
  animation: glassFlow 15s infinite ease-in-out;
}

.goods-card:hover {
  transform: perspective(1200px) rotateY(5deg) scale(1.08) translateY(-12px);
  box-shadow: 0 30px 60px rgba(0, 0, 0, 0.35), 0 0 50px rgba(160, 190, 255, 0.5) inset;
}

.goods-card:hover::before {
  filter: blur(75px);
  opacity: 1;
  transform: translateY(-10px);
  box-shadow: 0 0 180px rgba(160, 190, 255, 0.45);
  animation: glassFlow 6s infinite ease-in-out;
}

.goods-title {
  color: var(--text-primary);
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.goods-price {
  color: var(--primary-blue);
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
  box-shadow: 0 10px 20px var(--primary-gradient-light);
  transition: all 0.3s ease;
}

::v-deep .el-card__body {
  background-color: var(--glass-bg) !important;
}

.goods-card:active {
  transform: translateY(1px);
  box-shadow: 0 4px 12px var(--primary-gradient-light);
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
  background-color: var(--surface);
  position: relative;
}

.goods-image::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 0;
  background: linear-gradient(to top, var(--primary-gradient-light), transparent);
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
  color: var(--text-primary);
}

.goods-price {
  font-size: 18px;
  color: var(--primary-blue);
  margin-bottom: 10px;
  font-weight: bold;
}

.sold-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--overlay-bg-color);
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
  color: var(--text-secondary);
  font-size: 12px;
}

.goods-location {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.favorite-btn {
  color: var(--text-secondary);
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
