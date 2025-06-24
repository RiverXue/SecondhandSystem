<template>
  <div class="message-input-container">
    <el-avatar :size="40" class="user-avatar">
      <img :src="userStore.userInfo?.avatar || '/default-avatar.jpg'" alt="用户头像"/>
    </el-avatar>
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
        <el-button
            :disabled="!messageContent.trim() || loading || messageContent.length > 200"
            size="small"
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
import {ElAvatar, ElButton, ElMessage} from 'element-plus';

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

.user-avatar {
  flex-shrink: 0;
}

.input-wrapper {
  flex: 1;
  width: calc(100% + 30px);
  margin-right: -30px;
}

.message-input {
  width: 100%;
  min-height: 100px;
  border: 1px solid #e5e6eb;
  border-radius: 8px;
  resize: none;
  font-size: 14px;
  transition: all 0.2s;
  line-height: 1.5;
}

.message-input:focus {
  outline: none;
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(55, 169, 250, 0.1);
}

.input-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.char-count {
  font-size: 12px;
  color: #86909c;
}

@media (max-width: 768px) {
  .input-wrapper {
    width: 100%;
    margin-right: 0;
  }
}
</style>