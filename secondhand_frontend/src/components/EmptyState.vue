<template>
  <div class="empty-state">
    <el-icon class="empty-icon"><ChatDotRound /></el-icon>
    <p class="empty-text">暂无留言，快来发表第一条留言吧~</p>
  </div>
</template>

<script setup lang="ts">
import { ElIcon } from 'element-plus';
import { ChatDotRound } from '@element-plus/icons-vue';
</script>

<style scoped lang="scss">
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: var(--text-color-secondary);
  background-color: var(--bg-color);
  border-radius: 12px;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  color: var(--text-color-disabled);
}

.empty-text {
  font-size: 14px;
  margin: 0;
}
</style>