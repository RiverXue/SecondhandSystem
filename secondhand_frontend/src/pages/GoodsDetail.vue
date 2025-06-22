<script lang="ts" setup>
import {onMounted, ref} from 'vue';
import {useRoute, useRouter} from 'vue-router';
import {useGoodsStore} from '../store/goods';
import {useUserStore} from '../store/user';
import {useFavoriteStore} from '../store/favorite';
import {useOrderStore} from '../store/order';
import MessageBoard from '../components/MessageBoard.vue';
import {ElMessage} from 'element-plus';
import {ElCarousel, ElCarouselItem, ElImageViewer, ElIcon, ElBadge } from 'element-plus';
import { Star, StarFilled } from '@element-plus/icons-vue';


const route = useRoute();
const router = useRouter();
const goodsStore = useGoodsStore();
const userStore = useUserStore();
const favoriteStore = useFavoriteStore();
const orderStore = useOrderStore();
const currentGoods = ref<any>(null);
const isFavorite = ref(false);

onMounted(async () => {
  const goodsId = Number(route.params.id);
  await fetchGoodsDetail(goodsId);
  // 检查用户登录状态并加载用户信息
  if (userStore.accessToken && !userStore.userInfo) {
    await userStore.fetchUserInfo();
  }
  await checkFavoriteStatus(goodsId);
});

const fetchGoodsDetail = async (goodsId: number) => {
  try {
    await goodsStore.fetchGoodsDetail(goodsId);
    currentGoods.value = goodsStore.currentGoods;
  } catch (error) {
    console.error('获取商品详情失败:', error);
  }
};

const checkFavoriteStatus = async (goodsId: number) => {
  try {
    await favoriteStore.getFavoriteList();
    isFavorite.value = favoriteStore.favorites.some((item: any) => item.goodsId === goodsId);
  } catch (error) {
    console.error('检查收藏状态失败:', error);
  }
};

const toggleFavorite = async () => {
  if (!userStore.accessToken) {
    ElMessage.warning('请先登录');
    router.push('/login');
    return;
  }
  const goodsId = currentGoods.value.id;
  try {
    if (isFavorite.value) {
      await favoriteStore.removeFavorite(goodsId);
    } else {
      await favoriteStore.addFavorite(goodsId);
    }
    isFavorite.value = !isFavorite.value;
  } catch (error) {
    console.error('切换收藏状态失败:', error);
  }
};

const createOrder = async () => {
  if (!userStore.accessToken) {
    ElMessage.warning('请先登录');
    router.push('/login');
    return;
  }
  const goodsId = currentGoods.value.id;
  try {
    const res = await orderStore.createOrder(goodsId);
    if (res.code === 200) {
      ElMessage.success('订单创建成功！');
      await orderStore.getMyOrders(); // 刷新订单列表
      router.push('/orders');
    } else {
      ElMessage.error(res.message || '订单创建失败，请重试');
    }
  } catch (error) {
    console.error('创建订单失败:', error);
    ElMessage.error('创建订单失败，请重试');
  }
};
const activeThumbnail = ref(0);
const showViewer = ref(false);
const currentImage = ref('');

const showImagePreview = (img: string) => {
  currentImage.value = img;
  showViewer.value = true;
};

const formatDate = (dateString: string) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' });
};
</script>

<template>
  <div class="goods-detail-container">
    <el-card class="detail-card">
      <div class="goods-detail">
        <!-- 商品图片轮播 -->
        <div class="goods-images">
          <el-carousel height="400px" :loop="true" indicator-position="outside">
            <el-carousel-item v-for="(img, index) in currentGoods?.images || [currentGoods?.image]" :key="index">
              <img :src="img || '/default-goods.jpg'" alt="{{ currentGoods?.title }}" class="main-image" @click="showImagePreview(img)">
            </el-carousel-item>
          </el-carousel>
          <!-- 缩略图预览 -->
          <div class="thumbnails" v-if="currentGoods?.images?.length > 1">
            <div v-for="(img, index) in currentGoods?.images" :key="index" class="thumbnail-item" :class="{active: activeThumbnail === index}" @click="activeThumbnail = index">
              <img :src="img" alt="缩略图{{ index + 1 }}">
            </div>
          </div>
        </div>

        <!-- 商品信息 -->
        <div class="goods-info">
          <div class="status-badge" v-if="currentGoods?.status === 'sold'">{{ currentGoods?.status === 'sold' ? '已售出' : '可购买' }}</div>
          <h1 class="goods-title">{{ currentGoods?.title || '加载中...' }}</h1>
          <div class="price-section">
            <span class="current-price">¥{{ currentGoods?.price?.toFixed(2) || '0.00' }}</span>
            <span class="original-price" v-if="currentGoods?.originalPrice">¥{{ currentGoods?.originalPrice.toFixed(2) }}</span>
          </div>

          <div class="goods-meta">
            <div class="meta-item">
              <span class="label">新旧程度：</span>
              <span class="value">{{ currentGoods?.condition || '未填写' }}</span>
            </div>
            <div class="meta-item">
              <span class="label">发布时间：</span>
              <span class="value">{{ formatDate(currentGoods?.createTime) }}</span>
            </div>
            <div class="meta-item">
              <span class="label">卖家：</span>
              <span class="value">{{ currentGoods?.sellerName || '未知卖家' }}</span>
            </div>
          </div>

          <div class="goods-description">
            <h3>商品描述</h3>
            <p>{{ currentGoods?.description || '暂无描述' }}</p>
          </div>

          <div class="goods-actions">
            <button class="favorite-btn" @click="toggleFavorite" :class="{active: isFavorite}">
              <el-icon :size="18"><StarFilled v-if="isFavorite"/><Star v-else/></el-icon>
              {{ isFavorite ? '已收藏' : '收藏' }}
            </button>
            <button class="order-btn" @click="createOrder" :disabled="currentGoods?.status === 'sold'">
              {{ currentGoods?.status === 'sold' ? '已售出' : '立即购买' }}
            </button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 留言板区域 -->
    <MessageBoard v-if="currentGoods" :goods-id="currentGoods.id" />

    <!-- 图片预览 -->
    <el-image-viewer v-if="showViewer" :url-list="[currentImage]" @close="showViewer = false"/>
  </div>
</template>

<style scoped lang="scss">
.goods-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.detail-card {
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  border-radius: 8px;
  margin-bottom: 30px;
}

.goods-detail {
  display: flex;
  gap: 30px;
}

.goods-images {
  flex: 1;
}

.main-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  cursor: zoom-in;
}

.thumbnails {
  display: flex;
  gap: 10px;
  margin-top: 10px;
  overflow-x: auto;
  padding-bottom: 5px;
}

.thumbnail-item {
  width: 80px;
  height: 80px;
  border: 2px solid transparent;
  cursor: pointer;
}

.thumbnail-item.active {
  border-color: #409eff;
}

.thumbnail-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.goods-info {
  flex: 1;
}

.status-badge {
  display: inline-block;
  padding: 4px 8px;
  background-color: #f56c6c;
  color: white;
  font-size: 12px;
  border-radius: 4px;
  margin-bottom: 10px;
}

.goods-title {
  font-size: 24px;
  margin-bottom: 15px;
  color: #333;
  font-weight: 600;
}

.price-section {
  margin-bottom: 20px;
}

.current-price {
  font-size: 28px;
  color: #ff4d4f;
  font-weight: bold;
  margin-right: 10px;
}

.original-price {
  font-size: 16px;
  color: #909399;
  text-decoration: line-through;
}

.goods-meta {
  border-top: 1px solid #f5f5f5;
  border-bottom: 1px solid #f5f5f5;
  padding: 15px 0;
  margin-bottom: 20px;
}

.meta-item {
  display: flex;
  margin-bottom: 10px;
}

.meta-item:last-child {
  margin-bottom: 0;
}

.label {
  width: 100px;
  color: #909399;
}

.value {
  flex: 1;
  color: #606266;
}

.goods-description {
  margin-bottom: 30px;
}

.goods-description h3 {
  font-size: 16px;
  margin-bottom: 10px;
  color: #333;
}

.goods-description p {
  color: #606266;
  line-height: 1.6;
  white-space: pre-line;
}

.goods-actions {
  display: flex;
  gap: 15px;
  margin-top: 20px;
}

.favorite-btn {
  padding: 10px 20px;
  background-color: #f5f5f5;
  color: #666;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: all 0.2s;
}

.favorite-btn:hover {
  background-color: #f0f0f0;
}

.favorite-btn.active {
  background-color: #fef0f0;
  color: #f56c6c;
  border-color: #fbc4c4;
}

.order-btn {
  padding: 10px 20px;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  flex: 1;
  transition: all 0.2s;
}

.order-btn:hover {
  background-color: #3391e8;
}

.order-btn:disabled {
  background-color: #c0c4cc;
  cursor: not-allowed;
}

.message-board {
  margin-top: 30px;
}

@media (max-width: 768px) {
  .goods-detail {
    flex-direction: column;
  }

  .goods-title {
    font-size: 20px;
  }

  .current-price {
    font-size: 24px;
  }

  .goods-actions {
    flex-direction: column;
  }
}
</style>