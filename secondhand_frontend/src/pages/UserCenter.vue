<template>
  <div class="user-center-container">
    <el-card class="user-card glass-card">
      <div class="user-info">
        <div class="avatar-container">
          <el-avatar :size="100" class="avatar">
            <img :src="userStore.userInfo?.avatar || '/default-avatar.jpg'" alt="用户头像"/>
          </el-avatar>
          <el-button class="edit-avatar-btn" type="primary" @click="handleAvatarUpload">更换头像</el-button>
        </div>
        <div class="info-details">
          <h2>{{ userStore.userInfo?.username || '未设置' }}</h2>
          <p>注册时间: {{ formatDate(userStore.userInfo?.createTime) }}</p>
        </div>
      </div>
    </el-card>

    <el-tabs v-model="activeTab" class="user-tabs">
      <el-tab-pane label="个人资料" name="profile">
        <el-card class="glass-card content-card">
          <el-form
              ref="profileFormRef"
              :model="profileForm"
              :rules="profileRules"
              label-width="100px"
          >
            <el-form-item label="用户名" prop="username">
              <el-input v-model="profileForm.username" disabled></el-input>
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="profileForm.phone" placeholder="请输入手机号"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdateProfile">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="我的收藏" name="favorites">
        <!--        <el-card>-->
        <!--          <div v-if="favoriteStore.favorites.length === 0" class="empty-favorites">-->
        <!--            <p>您暂无收藏的商品</p>-->
        <!--          </div>-->
        <!--          <div v-else class="favorites-list">-->
        <!--            <el-card v-for="item in favoriteStore.favorites" :key="item.id" class="favorite-card">-->
        <!--              <div class="favorite-item">-->
        <!--                <router-link :to="`/goods/detail/${item.goods_id}`">-->
        <!--                  <div class="favorite-image">-->
        <!--                    <img :src="item.image_url || '/default-goods.jpg'" alt="{{ item.title }}">-->
        <!--                  </div>-->
        <!--                  <div class="favorite-info">-->
        <!--                    <h3 class="favorite-title">{{ item.title }}</h3>-->
        <!--                    <p class="favorite-price">¥{{ item.price.toFixed(2) }}</p>-->
        <!--                  </div>-->
        <!--                </router-link>-->
        <!--                <el-button icon="Delete" link @click="handleRemoveFavorite(item.goods_id)"></el-button>-->
        <!--              </div>-->
        <!--            </el-card>-->
        <!--          </div>-->
        <!--        </el-card>-->
        <el-card>
          <Favorites/>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="我的订单" name="orders">
        <el-card class="glass-card content-card">
          <OrderList/>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script lang="ts" setup>
import {onMounted, reactive, ref} from 'vue';
import {ElForm, ElMessage} from 'element-plus';
import {useUserStore} from '../store/user';
import {useGoodsStore} from '../store/goods';
import {useFavoriteStore} from '../store/favorite';
import OrderList from '../views/OrderList.vue';
import Favorites from "../views/Favorites.vue";
// 由于 getGoodsDetail 已声明但未使用，暂时移除该导入
// import {getGoodsDetail} from '../api/goods';

const userStore = useUserStore();
const goodsStore = useGoodsStore();
const favoriteStore = useFavoriteStore();
const profileFormRef = ref<InstanceType<typeof ElForm>>();
const activeTab = ref('profile');

// 个人资料表单
const profileForm = reactive({
  username: '',
  phone: ''
});

// 表单验证规则
const profileRules = reactive({
  phone: [
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入有效的手机号（以1开头，11位数字）',
      trigger: 'blur'
    }
  ]
});

// 收藏相关
// const favoritesMap = ref<Record<number, any>>({});

onMounted(async () => {
  await userStore.fetchUserInfo();
  // 初始化表单数据
  if (userStore.userInfo) {
    profileForm.username = userStore.userInfo.username || '';
    profileForm.phone = userStore.userInfo.phone || '';
  }

  // 获取收藏列表
  if (userStore.accessToken) {
    await favoriteStore.getFavoriteList();
    console.log('UserCenter获取收藏列表后的数据:', favoriteStore.favorites);
    console.log('UserCenter收藏列表长度:', favoriteStore.favorites.length);
    // 获取收藏商品详情
    // await fetchFavoritesDetail();
  }
});

// 格式化日期
const formatDate = (dateString: string) => {
  if (!dateString) return '未知';
  const date = new Date(dateString);
  return date.toLocaleDateString();
};

// 更新个人资料
const handleUpdateProfile = async () => {
  if (!profileFormRef.value) return;

  try {
    await profileFormRef.value.validate();
    console.log('当前用户信息:', userStore.userInfo);
    console.log('当前用户ID:', userStore.userInfo?.id);
    console.log('更新用户信息参数:', {phone: profileForm.phone, username: userStore.userInfo.username});
    const result = await userStore.updateUserInfo({phone: profileForm.phone});
    console.log('更新用户信息响应详情:', result);
    if (result.code === 200 && userStore.userInfo.phone === profileForm.phone) {
      ElMessage.success('个人资料更新成功');
    } else {
      ElMessage.error(result.message || '更新失败，请检查手机号格式或联系管理员');
    }
  } catch (error) {
    console.error('更新失败:', error);
    ElMessage.error('更新失败，请重试');
  }
};

// 更换头像
const handleAvatarUpload = () => {
  ElMessage.info('头像上传功能待实现');
};

// 获取收藏商品详情
// const fetchFavoritesDetail = async () => {
//   for (const goodsId of goodsStore.favorites) {
//     try {
//       const res = await getGoodsDetail(goodsId);
//       if (res.data.code === 200) {
//         favoritesMap.value[goodsId] = res.data.data;
//       }
//     } catch (error) {
//       console.error(`获取商品${goodsId}详情失败:`, error);
//     }
//   }
// };

// 取消收藏
const handleRemoveFavorite = async (goodsId: number) => {
  try {
    await favoriteStore.removeFavorite(goodsId);
    ElMessage.success('取消收藏成功');
  } catch (error) {
    ElMessage.error('取消收藏失败');
  }
};
</script>

<style scoped>
.user-center-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  background-color: transparent;
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
}

.user-card {
  margin-bottom: 20px;
  padding: 20px;
}

.content-card {
  margin-top: 20px;
  padding: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 30px;
  color: #F8FAFC;
}

/* 表单样式优化 */
.el-form-item__label {
  color: #94A3B8;
}

.el-input {
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  color: #F8FAFC;
}

.el-input__wrapper {
  background: transparent !important;
  box-shadow: none !important;
}

.el-button--primary {
  background: linear-gradient(135deg, #165DFF 0%, #4080FF 100%);
  border: none;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.el-button--primary:hover {
  transform: scale(1.02);
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
