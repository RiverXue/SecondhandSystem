<template>
  <div class="message-input-container">
    <div class="input-wrapper">
      <textarea
          v-model="messageContent"
          :disabled="loading"
          class="message-input"
          placeholder="分享你的想法..."
          @keydown.enter.prevent="submitMessage"
      ></textarea>
      <div class="input-footer">
        <span class="char-count">{{ messageContent.length }}/200</span>
        <!-- 提交按钮 -->
        <el-button
            :disabled="!messageContent.trim() || loading || messageContent.length > 200"
            :loading="loading"
            class="send-button"
            type="primary"
            @click="submitMessage"
        >
          {{ loading ? '发布中...' : '发布留言' }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {defineProps, ref} from 'vue';
import {useUserStore} from '../store/user';
import {useMessageStore} from '../store/message';
import {ElButton, ElMessage} from 'element-plus';

const props = defineProps<{
  goodsId: number;
}>();

const emit = defineEmits<{
  (e: 'message-sent'): void;
}>();

const userStore = useUserStore();
const messageStore = useMessageStore();
const messageContent = ref('');
const loading = ref(false);

const submitMessage = async () => {
  if (!messageContent.value.trim() || messageContent.value.length > 200) return;

  try {
    loading.value = true;
    await messageStore.leaveMessage(props.goodsId, messageContent.value.trim());
    messageContent.value = '';
    emit('message-sent');
  } catch (error) {
    ElMessage.error('留言发布失败，请重试');
  } finally {
    loading.value = false;
  }
};
</script>

<style lang="scss" scoped>
.message-input-container {
  display: flex;
  gap: 12px;
  margin-bottom: 25px;
  position: relative;
}


.input-wrapper {
  flex: 1;
  width: calc(100% + 30px);
  margin-right: -30px;
}

.message-input {
  width: 95.5%;
  min-height: 100px;
  max-height: 200px;
  resize: vertical;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 12px;
  margin-right: 20px;
  font-size: 14px;
  background-color: var(--card-bg-color);
  color: var(--text-color-primary);
  outline: none;
  transition: border-color 0.2s;
  line-height: 1.5;
}

.message-input:focus {
  outline: none;
  border-color: var(--primary-blue);
  box-shadow: 0 0 0 2px var(--primary-gradient-light);
}

.input-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
  margin-right: 20px;
  color: var(--text-color-secondary);
}

.char-count {
  font-size: 12px;
  color: var(--text-secondary);
}

@media (max-width: 768px) {
  .input-wrapper {
    width: 100%;
    margin-right: 0;
  }
}
</style>