<template>
  <div class="app-container">
    <Navbar v-if="!isMobile" />
    <BottomNavbar v-else />
    <transition name="glass-fade">
      <router-view v-slot="{ Component }">
        <component :is="Component" />
      </router-view>
    </transition>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, onUnmounted } from 'vue';
import Navbar from "./components/Navbar.vue"
import BottomNavbar from "./components/BottomNavbar.vue"

const isMobile = ref(window.innerWidth < 768);

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
  background-color: #F8FAFC;
  min-height: 100vh;
}

.glass-fade-enter-active, .glass-fade-leave-active {
  transition: opacity 0.5s ease, transform 0.5s ease;
}

.glass-fade-enter-from, .glass-fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}
</style>


