<template>
  <div class="error-state">
    <el-icon class="error-icon"><WarningFilled /></el-icon>
    <p class="error-message">{{ message }}</p>
    <el-button
      type="text"
      class="retry-button"
      @click="handleRetry"
    >
      重试
    </el-button>
  </div>
</template>

<script setup lang="ts">
import { defineProps, emit } from 'vue';
import { ElIcon, ElButton } from 'element-plus';
import { WarningFilled } from '@element-plus/icons-vue';

const props = defineProps<{
  message: string;
}>();

const emit = defineEmits<{
  (e: 'retry'): void;
}>();

const handleRetry = () => {
  emit('retry');
};
</script>

<style scoped lang="scss">
.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  background-color: #fff6f6;
  border: 1px solid #ffccc7;
  border-radius: 12px;
  color: #f56c6c;
}

.error-icon {
  font-size: 32px;
  margin-bottom: 12px;
}

.error-message {
  font-size: 14px;
  margin: 0 0 16px 0;
}

.retry-button {
  color: #f56c6c;
  padding: 0;
}

.retry-button:hover {
  color: #e4393c;
  background-color: transparent;
}
</style>