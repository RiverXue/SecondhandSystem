<template>
  <div class="bottom-nav glass-nav">
    <div class="nav-items">
      <router-link class="nav-item" to="/">
        <el-icon>
          <Home/>
        </el-icon>
        <span>首页</span>
      </router-link>
      <router-link class="nav-item" to="/publish">
        <el-icon>
          <Plus/>
        </el-icon>
        <span>发布</span>
      </router-link>
      <router-link class="nav-item" to="/user-center">
        <el-icon>
          <User/>
        </el-icon>
        <span>我的</span>
      </router-link>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {ElIcon} from 'element-plus';
import { House as Home, Plus, User } from '@element-plus/icons-vue';
import {RouterLink} from 'vue-router';
</script>

<style scoped>
.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(16px);
  box-shadow: 0 -4px 12px rgba(0, 0, 0, 0.05);
  z-index: 100;
}

.nav-items {
  display: flex;
  justify-content: space-around;
  height: 100%;
  align-items: center;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: var(--text-secondary);
  text-decoration: none;
  font-size: 12px;
  padding: 5px 0;
}

.nav-item.router-link-exact-active {
  color: var(--primary-blue);
}

.nav-item el-icon {
  font-size: 20px;
  margin-bottom: 2px;
}
</style>