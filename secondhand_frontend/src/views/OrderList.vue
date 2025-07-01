<template>
  <div class="order-list-container">
    <h2 class="page-title">我的订单</h2>
    <div class="total-amount">
      <span class="total-label">订单总价: </span>
      <span class="total-price">{{ totalPrice.toFixed(2) }}</span>
    </div>
    <div v-if="orderStore.loading" class="loading">加载中...</div>
    <div v-else-if="!orderStore.loading && orderStore.orders.length === 0" class="empty-tip">
      <p>您还没有任何订单</p>
      <router-link class="go-shopping-btn" to="/">去购物</router-link>
    </div>
    <div v-else class="orders-list">
      <div v-for="order in orderStore.orders" :key="order.id" class="order-item glass-card">
        <div class="order-header">
          <span class="order-number">订单编号: {{ order.orderNo }}</span>
          <span class="order-date">{{ formatDate(order.createTime) }}</span>
          <span :class="getStatusClass(order.status)" class="order-status">{{ formatStatus(order.status) }}</span>
        </div>
        <div class="order-goods">
          <router-link :to="order.goodsId != null ? `/goods/detail/${order.goodsId}` : '#'" class="goods-link">
            <img :src="getImageUrl(order.image) || defaultGoodsImage" alt="{{ order.title }}" class="goods-img">
            <div class="goods-info">
              <h3 class="goods-title">{{ order.title }}</h3>
              <p class="goods-price">{{ order.price?.toFixed(2) || '0.00' }}</p>
            </div>
          </router-link>
        </div>
        <div class="order-total">
          <!--          <span>订单总价: <span class="total-price">{{ order.totalPrice?.toFixed(2) || '0.00' }}</span></span>-->
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed, onMounted} from 'vue';
import {useOrderStore} from '../store/order';
import {ElMessage} from 'element-plus';
import defaultGoodsImage from '../assets/codelogo.png';

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

const orderStore = useOrderStore();

// const totalPrice = computed(() => {
//   return orderStore.orders.reduce((sum, order) => sum + (order.totalPrice || 0), 0);
// });
const totalPrice = computed(() => {
  return orderStore.orders.reduce((sum, order) => {
    return sum + (order.price || 0);
  }, 0);
});

onMounted(async () => {
  try {
    await orderStore.getMyOrders();
    if (orderStore.orders.length === 0) {
      ElMessage.info('暂无订单数据，请先创建订单');
    }
  } catch (error) {
    ElMessage.error('获取订单列表失败，请刷新页面重试');
    console.error('获取订单列表失败:', error);
  }
});

// 格式化订单状态
const formatStatus = (status: number) => {
  switch (status) {
    case 0:
      // return '待付款';
      return '已下单';
    case 1:
      return '已付款';
      // case 2:
      //   return '已发货';
      // case 3:
      //   return '已完成';
      // case 4:
      //   return '已取消';
      // default:
      //   return '未知状态';
  }
};

// 获取订单状态样式
const getStatusClass = (status: number) => {
  switch (status) {
    case 0:
      return 'status-pending';
    case 1:
      return 'status-paid';
    case 2:
      return 'status-shipped';
    case 3:
      return 'status-completed';
    case 4:
      return 'status-canceled';
    default:
      return '';
  }
};

// 格式化日期
const formatDate = (dateString: string) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleString();
};
</script>

<style scoped>
.order-list-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: #303133;
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
  background: linear-gradient(135deg, #165DFF 0%, #4080FF 100%);
  color: white;
  text-decoration: none;
  border-radius: 8px;
  transition: all 0.2s ease;
  border: none;
}

.go-shopping-btn:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(22, 93, 255, 0.3);
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-item {
  padding: 15px;
  margin-bottom: 15px;
}

.glass-card {
  background: var(--glass-bg);
  backdrop-filter: var(--glass-backdrop);
  border: var(--glass-border);
  border-radius: 20px;
  box-shadow: var(--glass-shadow);
  transition: all 0.5s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.glass-card:hover {
  border-color: rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px dashed #eee;
}

.order-number {
  color: #666;
  font-size: 14px;
}

.order-date {
  color: #999;
  font-size: 14px;
}

/* 订单状态样式 */
.status-pending {
  color: #ff9800;
  background-color: #fff8e1;
}

.status-paid {
  color: #2196f3;
  background-color: #e3f2fd;
}

.status-shipped {
  color: #4caf50;
  background-color: #e8f5e9;
}

.status-completed {
  color: #9c27b0;
  background-color: #f3e5f5;
}

.status-canceled {
  color: #f44336;
  background-color: #ffebee;
}

.order-status {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.total-amount {
  margin: 15px 0;
  font-size: 16px;
  font-weight: bold;
  color: #ff4d4f;
}

.total-amount {
  margin: 20px 0;
  padding: 15px;
  background: var(--glass-bg);
  backdrop-filter: var(--glass-backdrop);
  border: var(--glass-border);
  border-radius: 12px;
  text-align: right;
  font-size: 18px;
  box-shadow: var(--glass-shadow);
}

.total-price {
  color: #ff4d4f;
  font-weight: bold;
  margin-left: 8px;
}

.status-pending {
  background: rgba(255, 243, 205, 0.3);
  color: #856404;
  backdrop-filter: blur(4px);
}

.status-paid {
  background: rgba(209, 236, 241, 0.3);
  color: #0c5460;
  backdrop-filter: blur(4px);
}

.status-shipped {
  background: rgba(212, 237, 218, 0.3);
  color: #155724;
  backdrop-filter: blur(4px);
}

.status-completed {
  background-color: #c3e6cb;
  color: #155724;
}

.status-canceled {
  background: rgba(248, 215, 218, 0.3);
  color: #721c24;
  backdrop-filter: blur(4px);
}

.order-goods {
  margin-bottom: 15px;
}

.goods-link {
  display: flex;
  align-items: center;
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
}

.goods-price {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
}

.order-total {
  text-align: right;
  font-size: 16px;
  color: #333;
}

.total-price {
  color: #f56c6c;
  font-weight: bold;
}
</style>