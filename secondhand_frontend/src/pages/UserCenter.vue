<template>
  <div class="user-center-container">
    <el-card class="user-card glass-card">
      <div class="user-info">
        <div class="avatar-wrapper">
          <div class="avatar-container">
            <el-avatar :size="100" :src="getAvatarUrl(userStore.userInfo?.avatar)" class="avatar"></el-avatar>
          </div>
          <el-upload
              ref="avatarUpload"
              :action="uploadUrl"
              :before-upload="beforeAvatarUpload"
              :headers="headers"
              :on-success="handleAvatarSuccess"
              :show-file-list="false"
              class="avatar-uploader"
          >
            <el-button class="edit-avatar-btn" type="primary">更换头像</el-button>
          </el-upload>
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
        <el-card class="glass-card content-card">
          <Favorites/>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="我的交易" name="orders">
        <el-card class="glass-card content-card">
          <OrderList/>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script lang="ts" setup>
// import {onMounted, reactive, ref} from 'vue';
import {computed, onMounted, reactive, ref} from 'vue';
import {ElForm, ElMessage, ElUpload} from 'element-plus';
import {useUserStore} from '../store/user';
import {useGoodsStore} from '../store/goods';
import {useFavoriteStore} from '../store/favorite';
import OrderList from '../views/OrderList.vue';
import Favorites from "../views/Favorites.vue";
import defaultAvatar from '../assets/codelogo.png';

const avatarUpload = ref(null);
const uploadUrl = (import.meta.env.VITE_APP_API_URL || 'http://localhost:7272') + '/user/avatar';
const userStore = useUserStore();
const goodsStore = useGoodsStore();
const favoriteStore = useFavoriteStore();
const profileFormRef = ref<InstanceType<typeof ElForm>>();
const activeTab = ref('profile');
const userAvatar = ref('');


const headers = computed(() => ({
  Authorization: 'Bearer ' + userStore.accessToken
}));


// 添加日期格式化函数
const formatDate = (dateString: string) => {
  if (!dateString) return '未知时间';
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  });
};

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

onMounted(async () => {
  await userStore.fetchUserInfo();
  profileForm.username = userStore.userInfo?.username || '';
  profileForm.phone = userStore.userInfo?.phone || '';
  userAvatar.value = userStore.userInfo?.avatar || '';
});

const getAvatarUrl = (avatarPath: string | undefined) => {
  if (!avatarPath || avatarPath.trim() === '') return defaultAvatar;
  const baseUrl = import.meta.env.VITE_APP_BASE_URL;
  if (avatarPath.startsWith('http://') || avatarPath.startsWith('https://')) {
    return avatarPath;
  }
  if (avatarPath.startsWith('/uploads/')) {
    return `${baseUrl}${avatarPath}`;
  }
  return `${baseUrl}/uploads/${avatarPath}`;
};

const handleAvatarSuccess = (response: any) => {
  userAvatar.value = response.data;
  userStore.updateUserInfo({avatar: response.data.data});
  ElMessage.success('头像上传成功');
};

const beforeAvatarUpload = (file: File) => {
  // 检查是否已登录
  console.log('Uploading with token:', userStore.accessToken);
  if (!userStore.accessToken) {
    ElMessage.error('请先登录后再上传头像');
    return false;
  }

  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isJPG) {
    ElMessage.error('上传头像图片只能是 JPG/PNG 格式!');
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!');
  }
  return isJPG && isLt2M;
};

const removeAvatar = () => {
  userAvatar.value = '';
  userStore.updateUserInfo({avatar: ''});
  ElMessage.success('头像已移除');
};

const handleUpdateProfile = async () => {
  if (!profileFormRef.value) return;
  try {
    await profileFormRef.value.validate();
    await userStore.updateUserInfo(profileForm);
    ElMessage.success('个人资料更新成功');
  } catch (error) {
    ElMessage.error('请完善个人资料');
  }
};
</script>

<style scoped>
.avatar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-container {
  position: relative;
  width: 150px;
  height: 150px;
  border-radius: 50%;
  overflow: hidden;
  border: 1px solid var(--border-color);
  margin-bottom: 10px;
}

.edit-avatar-btn {
  margin-top: 10px;
  width: auto;
  height: auto;
  border-radius: 4px;
  background-color: var(--primary-blue);
  color: white;
  border: none;
  cursor: pointer;
  padding: 8px 16px;
  font-size: 14px;
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.default-avatar {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--bg-color);
}

.avatar-icon {
  font-size: 48px;
  color: var(--text-secondary);
}

.remove-avatar-btn {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: var(--overlay-bg-color);
  color: white;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-center-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  background-color: transparent;
}

.glass-card {
  background: var(--glass-bg);
  backdrop-filter: var(--glass-backdrop);
  border: var(--glass-border);
  border-radius: 20px;
  box-shadow: var(--glass-shadow);
  background-image: var(--glass-highlight);
  transform: perspective(1000px) rotateY(var(--glass-distortion)) scale(var(--glass-scale));
  transition: var(--glass-transition);
}

.glass-card:hover {
  border-color: rgba(255, 255, 255, 0.4);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.2);
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
  color: var(--text-primary);
}

/* 表单样式优化 */
.el-form-item__label {
  color: var(--text-secondary);
}

.el-input {
  background: var(--glass-bg);
  border: var(--glass-border);
  border-radius: 8px;
  color: var(--text-primary);
}

.el-input__wrapper {
  background: transparent !important;
  box-shadow: none !important;
}

.el-button--primary {
  background: linear-gradient(135deg, var(--primary-blue) 0%, var(--primary-dark) 100%);
  border: none;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.el-button--primary:hover {
  transform: scale(1.02);
  box-shadow: var(--glass-shadow);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .glass-card {
    backdrop-filter: blur(6px);
    border-radius: 10px;
  }
}

.page-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: var(--text-primary);
}

::v-deep .el-tabs__item {
  color: var(--text-primary) !important;
}

::v-deep .el-tabs__item.is-active {
  color: var(--primary-blue) !important;
}
</style>
