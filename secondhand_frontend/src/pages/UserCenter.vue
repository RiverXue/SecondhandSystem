<template>
  <div class="user-center-container">
    <el-card class="user-card">
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
        <el-card>
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
        <el-card>
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
}

.user-card {
  margin-bottom: 20px;
  padding: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 30px;
}

.avatar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.avatar {
  border: 2px solid #f0f0f0;
}

.info-details {
  flex: 1;
}

.user-tabs {
  margin-top: 20px;
}

.empty-favorites {
  text-align: center;
  padding: 50px 0;
  color: #888;
}

.favorites-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 15px;
}

.favorite-card {
  height: 100%;
}

.favorite-item {
  display: flex;
  align-items: center;
  gap: 15px;
}

.favorite-image {
  width: 80px;
  height: 80px;
  flex-shrink: 0;
}

.favorite-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.favorite-info {
  flex: 1;
  min-width: 0;
}

.favorite-title {
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 5px;
}

.favorite-price {
  font-size: 16px;
  color: #ff4d4f;
  font-weight: bold;
}

.loading-skeleton {
  padding: 15px;
}
</style>
