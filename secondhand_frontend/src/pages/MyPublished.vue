<template>
  <div class="my-published-container">
    <h2 class="page-title">我的发布</h2>
    <div v-if="goodsStore.loading" class="loading">加载中...</div>
    <div v-else-if="!goodsStore.loading && goodsStore.myPublishedGoods.length === 0" class="empty-tip">
      <p>您还没有发布任何商品</p>
      <router-link class="publish-btn" to="/publish">去发布</router-link>
    </div>
    <div v-else class="goods-list">
      <el-card v-for="goods in goodsStore.myPublishedGoods" :key="goods.id" class="goods-card glass-card">
        <router-link :to="`/goods/detail/${goods.id}`" class="goods-link">
          <div class="goods-image">
            <img :src="getImageUrl(goods.image) || defaultGoodsImage" alt="{{ goods.title }}" class="goods-img">
            <div v-if="goods.status === 1" class="sold-overlay">
              <div class="sold-badge">已售出</div>
            </div>
          </div>
          <div class="goods-info">
            <h3 class="goods-title">{{ goods.title }}</h3>
            <p class="goods-price">¥{{ goods.price.toFixed(2) }}</p>
            <div class="goods-status">
              <span :class="getStatusClass(goods.status)">{{ formatStatus(goods.status) }}</span>
            </div>
          </div>
        </router-link>
        <el-button
            class="delete-btn"
            size="small"
            type="danger"
            @click.stop="handleDelete(goods.id)"
        >删除
        </el-button>
      </el-card>
    </div>
    <div class="pagination">
      <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="goodsStore.myPublishedTotal"
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

import defaultGoodsImage from '../assets/codelogo.png';
import {ElMessage, ElMessageBox} from 'element-plus';

const goodsStore = useGoodsStore();
const userStore = useUserStore();
const currentPage = ref(1);
const pageSize = ref(10);

onMounted(async () => {
  if (!userStore.accessToken) {
    ElMessage.warning('请先登录');
    return;
  }
  await fetchMyPublishedGoods();
});

const fetchMyPublishedGoods = async () => {
  await goodsStore.fetchMyPublishedGoods({
    pageNum: currentPage.value,
    pageSize: pageSize.value
  });
};

const handlePageChange = async (page: number) => {
  currentPage.value = page;
  await fetchMyPublishedGoods();
};

const getImageUrl = (imagePath: string | undefined) => {
  if (!imagePath || typeof imagePath !== 'string') {
    return defaultGoodsImage;
  }
  if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
    return imagePath;
  }
  if (imagePath.startsWith('/uploads/')) {
    return imagePath;
  }
  const normalizedPath = imagePath.replace(/\\/g, '/');
  const baseUrl = import.meta.env.VITE_APP_BASE_URL || '';
  return `${baseUrl}/uploads/${normalizedPath}`;
};

const formatStatus = (status: number) => {
  switch (status) {
    case 0:
      return '可购买';
    case 1:
      return '已售出';
    default:
      return '未知状态';
  }
};

const getStatusClass = (status: number) => {
  return status === 1 ? 'status-sold' : 'status-available';
};

const handleDelete = async (goodsId: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });
    const res = await goodsStore.deleteMyPublishedGoods(goodsId);
    if (res.code === 200) {
      ElMessage.success('商品删除成功');
      await fetchMyPublishedGoods();
    } else {
      ElMessage.error(`删除失败: ${res.message || '未知错误'}`);
    }
  } catch (error) {
    if (error !== 'cancel') {
      // 增强错误信息捕获
      let errorMsg = '删除商品失败，请重试';
      if (error instanceof Error) {
        errorMsg = `错误: ${error.message}`;
      } else if (typeof error === 'object' && error && 'response' in error) {
        // 处理Axios错误
        const status = error.response?.status;
        const data = error.response?.data;
        errorMsg = `服务器错误 ${status || ''}: ${data?.message || '删除失败'}`;
      }
      console.error('删除商品失败详情:', error);
      ElMessage.error(errorMsg);
    }
  }
};
</script>

<style scoped>
.my-published-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  margin-bottom: 20px;
  color: var(--text-primary);
}

.loading {
  text-align: center;
  padding: 40px;
  color: var(--text-secondary);
}

.empty-tip {
  text-align: center;
  padding: 60px 20px;
  color: var(--text-secondary);
  background: var(--glass-bg);
  border-radius: 12px;
  margin-bottom: 20px;
}

.publish-btn {
  display: inline-block;
  margin-top: 15px;
  padding: 8px 16px;
  background-color: var(--primary-blue);
  color: white;
  border-radius: 4px;
  text-decoration: none;
  transition: all 0.2s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.publish-btn:hover {
  background-color: var(--primary-blue-dark);
  transform: translateY(-1px);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.15);
}

.goods-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.goods-card {
  transition: transform 0.2s, box-shadow 0.2s;
  position: relative;
  background: var(--glass-bg);
  backdrop-filter: var(--glass-backdrop);
  border: var(--glass-border);
  border-radius: 12px;
  box-shadow: var(--glass-shadow);
  background-image: var(--glass-highlight);
  overflow: hidden;
}

.goods-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.goods-image {
  position: relative;
  height: 180px;
  overflow: hidden;
  border-radius: 8px 8px 0 0;
}

.goods-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.sold-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
}

.sold-badge {
  background: var(--glass-gradient-orange);
  color: white;
  padding: 8px 16px;
  border-radius: 8px 8px 0 0;
  animation: slideUp 0.3s ease-out;
}

.goods-info {
  padding: 12px;
  display: flex;
  flex-direction: column;
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
}

.goods-status {
  font-size: 12px;
}

.status-sold {
  color: #ff4d4f;
}

.status-available {
  color: #52c41a;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  margin-bottom: 20px;
}

.delete-btn {
  position: absolute;
  bottom: 5px;
  right: 5px;
  z-index: 10;
  background-color: #ff4d4f;
  border-radius: 4px;
  padding: 4px 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.delete-btn:hover {
  background-color: #f5222d;
  transform: translateY(-1px);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.15);
  transition: all 0.2s ease;
}
</style>