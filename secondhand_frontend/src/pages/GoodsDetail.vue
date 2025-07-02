<script lang="ts" setup>
import {computed, onMounted, ref} from 'vue';
import {useRoute, useRouter} from 'vue-router';
import {useGoodsStore} from '../store/goods';
import {useUserStore} from '../store/user';
import {useFavoriteStore} from '../store/favorite';
import {useOrderStore} from '../store/order';
import MessageBoard from '../components/MessageBoard.vue';
import {ElCarousel, ElCarouselItem, ElIcon, ElImageViewer, ElMessage} from 'element-plus';
import {Star, StarFilled} from '@element-plus/icons-vue';
import defaultGoodsImage from '../assets/codelogo.png';

const baseUrl = import.meta.env.VITE_APP_BASE_URL || '';

// 状态
const route = useRoute();
const router = useRouter();
const goodsStore = useGoodsStore();
const userStore = useUserStore();
const favoriteStore = useFavoriteStore();
const orderStore = useOrderStore();
const currentGoodsany = ref<any>(null);
const currentGoods = computed(() => goodsStore.currentGoods);
// 第一版逻辑：直接使用 computed 来判断是否收藏
const isFavorite = ref(false);


// 判断是否收藏
// const updateFavoriteStatus = () => {
//   if (!userId.value || !currentGoods.value) return;
//   isFavorite.value = favoriteStore.favoriteList.some(
//       item => item.goodsId === currentGoods.value?.id
//   );
// };

onMounted(async () => {
  const goodsId = Number(route.params.id);
  await fetchGoodsDetail(goodsId);
  // 检查用户登录状态并加载用户信息
  if (userStore.accessToken && !userStore.userInfo) {
    await userStore.fetchUserInfo();
  }
  await favoriteStore.getFavoriteList();
});

const fetchGoodsDetail = async (goodsId: number) => {
  try {
    await goodsStore.fetchGoodsDetail(goodsId);
    currentGoodsany.value = parseGoodsImages(goodsStore.currentGoods);
    // 确保至少有一张默认图片
    if (!currentGoodsany.value.images || currentGoodsany.value.images.length === 0) {
      currentGoodsany.value.images = currentGoodsany.value.image ? [currentGoodsany.value.image] : [];
    }
  } catch (error) {
    console.error('获取商品详情失败:', error);
    // 错误状态下显示默认图片
    currentGoodsany.value = {
      images: [],
      title: '商品加载失败',
      price: 0,
      description: '无法加载商品详情，请稍后重试'
    }
  }
};
// 收藏按钮逻辑（第一版）
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
  return date.toLocaleDateString('zh-CN', {year: 'numeric', month: 'long', day: 'numeric'});
};

// 解析商品图片数据
// 处理主图和多图数据，确保始终返回图片数组
const parseGoodsImages = (goods: any) => {
  // 如果没有图片数据，初始化空数组
  if (!goods) return {images: []};

  // 处理多图数据
  if (goods.images) {
    try {
      // 尝试解析JSON格式的多图数据
      const parsedImages = JSON.parse(goods.images);
      goods.images = Array.isArray(parsedImages) ? parsedImages : [];
    } catch (e) {
      console.error('解析多图数据失败', e);
      goods.images = [];
    }
  } else {
    goods.images = [];
  }

  // 如果有多图数据，直接使用
  if (goods.images.length > 0) {
    return goods;
  }

  // 如果没有多图数据但有主图，将主图包装成数组
  if (goods.image) {
    goods.images = [goods.image];
    // 兼容可能的字符串数组格式
    if (typeof goods.images[0] === 'string' && goods.images[0].startsWith('[')) {
      try {
        goods.images = JSON.parse(goods.images[0]);
      } catch (e) {
        console.error('解析主图数据失败', e);
      }
    }
  }

  return goods;
};
// 在获取商品详情的地方调用解析函数
const getImageUrl = (imagePath: string | undefined) => {
  console.log('生成图片URL，输入路径:', imagePath);

  if (!imagePath || typeof imagePath !== 'string') {
    console.log('无效的图片路径，使用默认图片');
    return defaultGoodsImage;
  }

  // 如果路径是前端资产或完整URL，直接返回
  if (imagePath.startsWith('/src/assets/') || imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
    console.log('使用前端资产或完整URL图片路径:', imagePath);
    return imagePath;
  }

  // 如果路径已经包含/uploads/，直接使用
  if (imagePath.startsWith('/uploads/')) {
    console.log('使用已包含/uploads/的路径:', imagePath);
    return imagePath;
  }

  const normalizedPath = imagePath.replace(/\\/g, '/');
  const result = `${baseUrl}/uploads/${normalizedPath}`;
  console.log('生成图片URL:', result);

  return result;
};
</script>

<template>
  <div class="goods-detail-container">
    <el-card class="detail-card">
      <div class="goods-detail">
        <!-- 商品图片轮播 -->
        <div class="goods-images">
          <el-carousel :loop="true" height="400px" indicator-position="outside">
            <el-carousel-item
                v-for="(img, index) in (currentGoodsany?.images?.length ? currentGoodsany.images : (currentGoodsany?.image ? [currentGoodsany.image] : []))"
                :key="index || 'main'">
              <img :src="getImageUrl(img)" alt="商品图片" class="carousel-img"
                   @click="showImagePreview(getImageUrl(img))">
            </el-carousel-item>
            <!--            <el-carousel-item v-if="!currentGoods?.images?.length && !currentGoods?.image">-->
            <!--              <img :src="defaultGoodsImage" alt="默认商品图片" class="carousel-img"-->
            <!--                   @click="showImagePreview(defaultGoodsImage)">-->
            <!--            </el-carousel-item>-->
            <el-carousel-item v-if="!currentGoodsany?.images?.length && !currentGoodsany?.image">
              <div class="carousel-img-wrapper">
                <img :src="defaultGoodsImage" alt="默认商品图片" class="carousel-img"
                     @click="showImagePreview(defaultGoodsImage)">
              </div>
            </el-carousel-item>


          </el-carousel>
          <!-- 缩略图预览 -->
          <div v-if="currentGoods?.images?.length > 1" class="thumbnails">
            <div v-for="(img, index) in currentGoods?.images" :key="index" :class="{active: activeThumbnail === index}"
                 class="thumbnail-item" @click="activeThumbnail = index">
              <img :src="getImageUrl(img)" alt="缩略图{{ index + 1 }}">
            </div>
          </div>
        </div>

        <!-- 商品信息 -->
        <div class="goods-info">
          <div v-if="currentGoods?.status === 'sold'" class="status-badge">
            <!--            {{ currentGoods?.status === 'sold' ? '已售出' : '可购买' }}-->
            {{ currentGoods?.status === 1 ? '已售出' : '可购买' }}
          </div>
          <h1 class="goods-title">{{ currentGoods?.title || '加载中...' }}</h1>
          <div v-if="!currentGoods" class="loading-placeholder">加载商品数据中...</div>
          <div class="price-section">
            <span class="current-price">¥{{ currentGoods?.price?.toFixed(2) || '0.00' }}</span>
            <span v-if="currentGoods?.originalPrice" class="original-price">¥{{
                currentGoods?.originalPrice.toFixed(2)
              }}</span>
          </div>

          <div class="goods-meta">
            <div class="meta-item">
              <span class="label">发布时间：</span>
              <span class="value">{{
                  currentGoods?.createTime ? formatDate(currentGoods.createTime) : '未知时间'
                }}</span>
            </div>
            <div class="meta-item">
              <span class="label">卖家ID：</span>
              <span class="value">{{ currentGoods?.userId || '未知卖家' }}</span>
            </div>
          </div>

          <div class="goods-description">
            <h3>商品描述</h3>
            <p>{{ currentGoods?.description || '暂无描述' }}</p>
          </div>

          <div class="goods-actions">
            <button :class="{active: isFavorite}" class="favorite-btn" @click="toggleFavorite">
              <el-icon :size="18">
                <StarFilled v-if="isFavorite"/>
                <Star v-else/>
              </el-icon>
              {{ isFavorite ? '已收藏' : '收藏' }}
            </button>
            <button :disabled="currentGoods?.status === 1" class="order-btn" @click="createOrder">
              {{ currentGoods?.status === 1 ? '已售出' : '立即购买' }}
            </button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 留言板区域 -->
    <MessageBoard v-if="currentGoods" :goods-id="currentGoods.id"/>

    <!-- 图片预览 -->
    <el-image-viewer v-if="showViewer" :url-list="[currentImage]" @close="showViewer = false"/>
  </div>
</template>

<style lang="scss" scoped>
.goods-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.carousel-img-wrapper {
  width: 100%;
  height: 400px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.carousel-img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}


.detail-card {
  padding: 20px;
  box-shadow: var(--glass-shadow);
  border-radius: 8px;
  margin-bottom: 30px;
  background-color: var(--bg-color) !important;
}

::v-deep .el-card__body {
  background-color: var(--bg-color) !important;
  padding: 0;
}

.goods-detail {
  display: flex;
  gap: 30px;
}

.goods-images {
  flex: 1;
}

.main-image {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 400px;
  background-color: var(--bg-color);

  object-fit: contain;
  cursor: zoom-in; //
  margin: 0 auto; /* 水平居中 */
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
  border-color: var(--primary-blue);
}

.thumbnail-item img {
  width: 100%;
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  margin: 0 auto; /* 水平居中 */
}

.goods-info {
  flex: 1;
}

.status-badge {
  display: inline-block;
  padding: 4px 8px;
  background-color: var(--danger-color);
  color: white;
  font-size: 12px;
  border-radius: 4px;
  margin-bottom: 10px;
}

.goods-title {
  font-size: 24px;
  margin-bottom: 15px;
  color: var(--text-primary);
  font-weight: 600;
}

.price-section {
  margin-bottom: 20px;
}

.current-price {
  font-size: 28px;
  color: var(--danger-color);
  font-weight: bold;
  margin-right: 10px;
}

.original-price {
  font-size: 16px;
  color: var(--text-secondary);
  text-decoration: line-through;
}

.goods-meta {
  border-top: 1px solid var(--border-color);
  border-bottom: 1px solid var(--border-color);
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
  color: var(--text-secondary);
}

.value {
  flex: 1;
  color: var(--text-secondary);
}

.goods-description {
  margin-bottom: 30px;
}

.goods-description h3 {
  font-size: 16px;
  margin-bottom: 10px;
  color: var(--text-primary);
}

.goods-description p {
  color: var(--text-secondary);
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
  background-color: var(--bg-color);
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: all 0.2s;
}

.favorite-btn:hover {
  background-color: var(--hover-bg-color);
}

.favorite-btn.active {
  background-color: var(--danger-bg-color);
  color: var(--danger-color);
  border-color: var(--danger-border-color);
}

.order-btn {
  padding: 10px 20px;
  background-color: var(--primary-blue);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  flex: 1;
  transition: all 0.2s;
}

.order-btn:hover {
  background-color: var(--primary-dark);
}

.order-btn:disabled {
  background-color: var(--text-secondary);
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