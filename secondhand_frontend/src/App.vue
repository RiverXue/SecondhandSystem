<template>
  <div class="app-container">
    <Navbar />
    <transition name="glass-fade">
      <router-view/>
    </transition>
  </div>
</template>

<script lang="ts" setup>
import Navbar from "./components/Navbar.vue"
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


