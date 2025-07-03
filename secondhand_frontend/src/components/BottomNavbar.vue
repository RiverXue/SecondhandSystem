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
  background: var(--glass-bg);
  backdrop-filter: var(--glass-backdrop);
  border-top: var(--glass-border);
  background-image: var(--glass-highlight);
  transform: perspective(1000px) rotateY(var(--glass-distortion)) scale(var(--glass-scale));
  transition: var(--glass-transition);
  animation: var(--glass-animation);
  box-shadow: var(--glass-shadow);
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