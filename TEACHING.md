# 二手交易系统项目设计与实现

## 项目概述
本项目是一个基于前后端分离架构的二手交易平台，旨在为用户提供便捷的商品发布、浏览、购买和交易管理功能。系统采用现代化的技术栈和设计理念，确保良好的用户体验和系统可维护性。

## 技术架构

### 前端技术栈
- **核心框架**: Vue 3 + TypeScript
- **构建工具**: Vite
- **状态管理**: Pinia
- **UI组件库**: Element Plus
- **路由管理**: Vue Router
- **HTTP客户端**: Axios

### 后端技术栈
- **核心框架**: Spring Boot
- **数据库**: MySQL
- **ORM框架**: MyBatis
- **API风格**: RESTful

### 项目结构
```
SecondhandSystem/
├── secondhand_backend/  # 后端Spring Boot项目
├── secondhand_frontend/ # 前端Vue项目
└── uploads/             # 商品图片存储目录
```

## 核心功能模块

### 1. 用户认证与授权
- 用户注册/登录功能
- JWT令牌认证
- 基于角色的权限控制

### 2. 商品管理
- 商品发布与编辑
- 商品分类与搜索
- 商品详情展示

### 3. 交易流程
- 订单创建与管理
- 商品收藏功能
- 交易状态追踪

### 4. 用户中心
- 个人资料管理
- 头像上传功能
- 我的订单与收藏

## 关键代码解析

### 前端核心组件

#### 导航栏组件 (Navbar.vue)
导航栏实现了路由导航、主题切换和用户菜单功能，采用了玻璃态设计风格：

```vue
<template>
  <div class="nav-wrapper">
    <!-- 左侧菜单 -->
    <el-menu
        :default-active="activeIndex"
        class="el-menu-demo glass-nav"
        mode="horizontal"
        @select="handleSelect"
    >
      <el-menu-item index="/">首页</el-menu-item>
      <el-menu-item index="/publish">发布商品</el-menu-item>
      <el-menu-item index="/favorites">我的收藏</el-menu-item>
      <el-menu-item index="/orders">我的交易</el-menu-item>
    </el-menu>

    <!-- 主题切换 -->
    <div class="theme-switch">
      <el-switch
          :active-icon="Moon"
          :inactive-icon="Sunny"
          :model-value="themeStore.isDark"
          @update:model-value="themeStore.toggleTheme"
      />
    </div>

    <!-- 用户菜单 -->
    <el-dropdown v-else>
      <span class="el-dropdown-link">
        <el-avatar :src="getAvatarUrl(userStore.userInfo?.avatar)"/>
        <span class="username">{{ userStore.userInfo?.username }}</span>
        <el-icon><arrow-down/></el-icon>
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="goToUserCenter">个人中心</el-dropdown-item>
          <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>
```

#### 用户中心组件 (UserCenter.vue)
用户中心实现了个人资料管理和头像上传功能：

```vue
<template>
  <div class="user-center-container">
    <el-card class="user-card glass-card">
      <div class="user-info">
        <div class="avatar-wrapper">
          <el-avatar :size="100" :src="getAvatarUrl(userStore.userInfo?.avatar)" class="avatar"></el-avatar>
          <el-upload
              :action="uploadUrl"
              :before-upload="beforeAvatarUpload"
              :headers="headers"
              :on-success="handleAvatarSuccess"
              class="avatar-uploader"
          >
            <el-button type="primary">更换头像</el-button>
          </el-upload>
        </div>
        <div class="info-details">
          <h2>{{ userStore.userInfo?.username }}</h2>
          <p>注册时间: {{ formatDate(userStore.userInfo?.createTime) }}</p>
        </div>
      </div>
    </el-card>

    <!-- 个人资料表单 -->
    <el-tabs v-model="activeTab">
      <el-tab-pane label="个人资料" name="profile">
        <el-form :model="profileForm" :rules="profileRules">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="profileForm.username" disabled></el-input>
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="profileForm.phone"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleUpdateProfile">保存修改</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
```

### 核心业务逻辑

#### 头像上传功能
```typescript
// 头像上传前验证
const beforeAvatarUpload = (file: File) => {
  // 检查是否已登录
  if (!userStore.accessToken) {
    ElMessage.error('请先登录后再上传头像');
    return false;
  }

  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isJPG) ElMessage.error('上传头像图片只能是 JPG/PNG 格式!');
  if (!isLt2M) ElMessage.error('上传头像图片大小不能超过 2MB!');
  return isJPG && isLt2M;
};

// 头像上传成功处理
const handleAvatarSuccess = (response: any) => {
  userAvatar.value = response.data;
  userStore.updateUserInfo({avatar: response.data.data});
  ElMessage.success('头像上传成功');
};
```

#### 商品发布功能
```typescript
// 表单提交处理
const handleSubmit = async () => {
  if (!publishFormRef.value) return;
  try {
    await publishFormRef.value.validate();
    // 上传图片
    if (fileList.value.length > 0) {
      await uploadRef.value?.submit();
    }
    // 提交商品信息
    const res = await publishGoods(formData);
    if (res.code === 200) {
      ElMessage.success('商品发布成功');
      router.push('/');
    } else {
      ElMessage.error(res.message || '商品发布失败');
    }
  } catch (error) {
    ElMessage.error('请完善商品信息');
  }
};
```

## 状态管理设计
采用Pinia进行状态管理，按功能模块划分store：

```typescript
// userStore.ts - 用户状态管理
import { defineStore } from 'pinia';

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null,
    accessToken: localStorage.getItem('accessToken') || '',
  }),
  actions: {
    async login(loginForm) {
      const res = await loginApi(loginForm);
      if (res.code === 200) {
        this.accessToken = res.data.token;
        this.userInfo = res.data.user;
        localStorage.setItem('accessToken', res.data.token);
      }
      return res;
    },
    async logout() {
      await logoutApi();
      this.accessToken = '';
      this.userInfo = null;
      localStorage.removeItem('accessToken');
    },
    // 其他用户相关操作...
  },
});
```

## 玻璃态设计实现
系统UI采用了现代玻璃态(Glass Morphism)设计风格，主要通过CSS变量和样式实现：

```css
.glass-card {
  background: var(--glass-bg);
  backdrop-filter: var(--glass-backdrop);
  border: var(--glass-border);
  box-shadow: var(--glass-shadow);
  transform: perspective(1000px) rotateY(var(--glass-distortion)) scale(var(--glass-scale));
  transition: var(--glass-transition);
}
```

## 部署与运行
### 前端运行
```bash
cd secondhand_frontend
npm install
npm run dev
```

### 后端运行
```bash
cd secondhand_backend
mvn spring-boot:run
```

## 总结
本项目采用前后端分离架构，通过Vue 3和Spring Boot构建了一个功能完善的二手交易平台。系统设计遵循了模块化、组件化的原则，实现了用户认证、商品管理、订单交易等核心功能，并采用了现代化的UI设计提升用户体验。