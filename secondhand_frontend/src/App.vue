<template>
  <div class="app-container">
    <!-- 导航栏响应式切换逻辑 -->
    <!-- 当屏幕宽度 >= 768px 时显示顶部导航栏 -->
    <Navbar v-if="!isMobile"/>
    <!-- 当屏幕宽度 < 768px 时显示底部导航栏 -->
    <BottomNavbar v-else/>
    <transition name="glass-fade">
      <router-view v-slot="{ Component }">
        <component :is="Component"/>
      </router-view>
    </transition>
  </div>
</template>

<script lang="ts" setup>
import {onMounted, onUnmounted, ref} from 'vue';
import Navbar from "./components/Navbar.vue"
import BottomNavbar from "./components/BottomNavbar.vue"

// 响应式导航栏状态变量
// 根据窗口宽度判断设备类型，小于768px为移动设备
const isMobile = ref(window.innerWidth < 768);

// 窗口大小变化处理函数
// 当窗口宽度改变时更新设备类型状态
const handleResize = () => {
  isMobile.value = window.innerWidth < 768;
};

onMounted(() => {
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
});
</script>

<style scoped>
.app-container {
  background-color: var(--glass-bg);
  min-height: 100vh;
  border: rgba(40, 190, 215, 0.09) 1px solid;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.17), 0 1px 0 rgba(22, 119, 255, 0.1) inset;
  border-radius: 16px;
}

.glass-fade-enter-active, .glass-fade-leave-active {
  transition: opacity 0.5s ease, transform 0.5s ease;
}

.glass-fade-enter-from, .glass-fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}
</style>


