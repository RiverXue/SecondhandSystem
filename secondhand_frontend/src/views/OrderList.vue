<template>
  <div class="order-list-container">
    <h2 class="page-title">我的交易</h2>
    <div class="total-amount">
      <span class="total-label">订单总价: </span>
      <span class="total-price">{{ totalPrice.toFixed(2) }}</span>
    </div>
    <el-tabs v-model="activeTab" class="order-tabs">
      <el-tab-pane label="我的购买" name="buyer">
        <div v-if="orderStore.loading" class="loading">加载中...</div>
        <div v-else-if="!orderStore.loading && orderStore.buyerOrders.length === 0" class="empty-tip">
          <p>您还没有购买订单</p>
          <router-link class="go-shopping-btn" to="/">去购物</router-link>
        </div>
        <div v-else class="orders-list">
          <div v-for="order in orderStore.buyerOrders" :key="order.id" class="order-item glass-card">
            <div class="order-header">
              <span class="order-number">订单编号: {{ order.id }}</span>
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
              <span>订单价格   : <span class="total-price">{{ order.price?.toFixed(2) || '0.00' }}</span></span>
            </div>
            <div class="order-actions">
              <el-button
                  v-if="order.status === 0"
                  size="small"
                  type="primary"
                  @click="handlePay(order.id)">
                支付
              </el-button>
              <el-button
                  v-if="order.status === 0 && userStore.userInfo?.id === order.buyerId"
                  size="small"
                  type="danger"
                  @click="handleCancel(order.id)">
                取消订单
              </el-button>
              <el-button
                  v-if="order.buyerId && Number(order.status) === 4 && userStore.userInfo && Number(userStore.userInfo.id) === Number(order.buyerId)"
                  type="text"
                  @click="handleDelete(order.id)">
                删除订单
              </el-button>
              <el-button
                  v-if="order.sellerId && Number(order.status) === 1 && userStore.userInfo && Number(userStore.userInfo.id) === Number(order.sellerId)"
                  size="small"
                  type="text"
                  @click="handleShip(order.id)">
                发货
              </el-button>
              <el-button
                  v-if="order.status === 2"
                  size="small"
                  type="success"
                  @click="handleComplete(order.id)">
                确认收货
              </el-button>
            </div>
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="我的卖出" name="seller">
        <div v-if="orderStore.loading" class="loading">加载中...</div>
        <div v-else-if="!orderStore.loading && orderStore.sellerOrders.length === 0" class="empty-tip">
          <p>您还没有卖出订单</p>
        </div>
        <div v-else class="orders-list">
          <div v-for="order in orderStore.sellerOrders" :key="order.id" class="order-item glass-card">
            <div class="order-header">
              <span class="order-number">订单编号: {{ order.id }}</span>
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
              <span>订单价格: <span class="total-price">{{ order.price?.toFixed(2) || '0.00' }}</span></span>
            </div>
            <div class="order-actions">
              <el-button
                  v-if="order.status === 1"
                  size="small"
                  type="primary"
                  @click="handleShip(order.id)">
                发货
              </el-button>
              <el-button
                  v-if="order.status === 4 && userStore.userInfo && Number(userStore.userInfo.id) === Number(order.sellerId)"
                  type="text"
                  @click="handleDelete(order.id)">
                删除订单
              </el-button>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script lang="ts" setup>
import {computed, onMounted, ref, watch} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import {useOrderStore} from '../store/order';
import {useUserStore} from '../store/user';
import defaultGoodsImage from '../assets/codelogo.png';

const userStore = useUserStore();
const orderStore = useOrderStore();
const activeTab = ref('buyer');

// 添加获取订单数据的函数
const fetchOrders = async () => {
  await orderStore.getMyOrders();
};

const fetchSellerOrders = async () => {
  await orderStore.getSellerOrders();
};

// 在组件挂载时加载数据
onMounted(() => {
  fetchOrders();
  fetchSellerOrders();
});

// 监听标签页切换，重新加载对应数据
watch(activeTab, (newVal) => {
  if (newVal === 'buyer') {
    fetchOrders();
  } else {
    fetchSellerOrders();
  }
});

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

const totalPrice = computed(() => {
  const orders = activeTab.value === 'buyer' ? orderStore.buyerOrders : orderStore.sellerOrders;
  return orders.reduce((sum, order) => sum + (order.price || 0), 0);
});


// 格式化订单状态
const formatStatus = (status: number) => {
  switch (status) {
    case 0:
      return '待付款';
    case 1:
      return '已付款';
    case 2:
      return '已发货';
    case 3:
      return '已完成';
    case 4:
      return '已取消';
    default:
      return '未知状态';
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

// 处理支付
const handlePay = async (orderId: number) => {
  try {
    await orderStore.payOrder(orderId);
  } catch (error) {
    console.error('支付失败:', error);
  }
};

// 处理取消订单
const handleCancel = async (orderId: number) => {
  try {
    await ElMessageBox.confirm(
        '确定要取消订单吗？',
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
    );
    await orderStore.cancelOrder(orderId);
  } catch (error) {
    if (error === 'cancel') {
      ElMessage.info('已取消操作');
    } else {
      console.error('取消订单失败:', error);
    }
  }
};

const handleDelete = async (orderId: number) => {
  try {
    await ElMessageBox.confirm(
        '确定要删除该订单吗？',
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
    );
    await orderStore.deleteOrder(orderId);
    ElMessage.success('订单删除成功');
    if (userStore.userInfo?.role?.toLowerCase() === 'buyer') {
      fetchOrders();
    } else {
      fetchSellerOrders();
    }
  } catch (error) {
    if (error === 'cancel') {
      ElMessage.info('已取消操作');
    } else {
      ElMessage.error('订单删除失败');
      console.error('删除订单失败:', error);
    }
  }
};

// 处理发货
const handleShip = async (orderId: number) => {
  try {
    await ElMessageBox.confirm(
        '确定要发货吗？',
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'info',
        }
    );
    await orderStore.shipOrder(orderId);
  } catch (error) {
    if (error === 'cancel') {
      ElMessage.info('已取消操作');
    } else {
      console.error('发货失败:', error);
    }
  }
};

// 处理确认收货
const handleComplete = async (orderId: number) => {
  try {
    await ElMessageBox.confirm(
        '确定已收到商品并完成订单吗？',
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'info',
        }
    );
    await orderStore.completeOrder(orderId);
  } catch (error) {
    if (error === 'cancel') {
      ElMessage.info('已取消操作');
    } else {
      console.error('确认订单失败:', error);
    }
  }
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
  color: var(--text-primary);
}

.loading,
.empty-tip {
  text-align: center;
  padding: 50px 0;
  color: var(--text-secondary);
}

.go-shopping-btn {
  display: inline-block;
  margin-top: 15px;
  padding: 8px 16px;
  background: linear-gradient(135deg, var(--primary-blue) 0%, var(--primary-dark) 100%);
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
  --glass-shadow: 0 8px 32px rgba(0, 0, 0, 0.08), 0 2px 10px rgba(255, 255, 255, 0.3) inset;

  background-image: var(--glass-highlight);
  transform: perspective(1000px) rotateY(var(--glass-distortion)) scale(var(--glass-scale));
  transition: var(--glass-transition);
}

.glass-card:hover {
  border-color: rgba(255, 255, 255, 0.4);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.2);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px dashed var(--border-color);
}

.order-number {
  color: var(--text-secondary);
  font-size: 14px;
}

.order-date {
  color: var(--text-secondary);
  font-size: 14px;
}

/* 订单状态样式 */
.status-pending {
  background: var(--warning-bg-color);
  color: var(--warning-text-color);
  backdrop-filter: blur(4px);
}

.status-paid {
  background: var(--info-bg-color);
  color: var(--info-text-color);
  backdrop-filter: blur(4px);
}

.status-shipped {
  background: var(--info-bg-color);
  color: var(--info-text-color);
  backdrop-filter: blur(4px);
}

.status-completed {
  background: var(--success-bg-color);
  color: var(--success-text-color);
}

.status-canceled {
  background: var(--danger-bg-color);
  color: var(--danger-color);
}

.order-status {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.total-amount.order-total {
  margin: 15px 0;
  font-size: 16px;
  font-weight: bold;
  color: var(--danger-color);
}

.order-actions {
  display: flex;
  gap: 10px;
  margin-top: 15px;
  justify-content: flex-end;
}

.order-actions .el-button {
  padding: 6px 12px;
  font-size: 12px;
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
  background-image: var(--glass-highlight);
  transform: perspective(1000px) rotateY(var(--glass-distortion)) scale(var(--glass-scale));
  transition: var(--glass-transition);
}

.total-price {
  color: var(--danger-color);
  font-weight: bold;
  margin-left: 8px;
}

::v-deep .el-tabs__item {
  color: var(--text-primary) !important;
}

::v-deep .el-tabs__item.is-active {
  color: var(--primary-blue) !important;
}

.total-label {
  color: var(--text-secondary);
}

.status-pending {
  background: var(--warning-bg-color);
  color: var(--warning-text-color);
  backdrop-filter: blur(4px);
}

.status-paid {
  background: var(--success-bg-color);
  color: var(--success-text-color);
  backdrop-filter: blur(4px);
}

.status-shipped {
  background: var(--info-bg-color);
  color: var(--info-text-color);
  backdrop-filter: blur(4px);
}

.status-completed {
  background: var(--success-bg-color);
  color: var(--success-text-color);
  backdrop-filter: blur(4px);
}

.status-canceled {
  background: var(--danger-bg-color);
  color: var(--danger-text-color);
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
  color: var(--text-primary);
}

.goods-price {
  font-size: 18px;
  color: var(--danger-color);
  font-weight: bold;
}

.order-total {
  text-align: right;
  font-size: 16px;
  color: var(--text-primary);
}

.total-price {
  color: var(--danger-color);
  font-weight: bold;
}
</style>