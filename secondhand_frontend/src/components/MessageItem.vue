<template>
  <div class="message-item">
    <div class="message-content-wrapper">
      <div class="message-header">
        <span class="username">ID: {{ message.userId }}</span>
        <span class="create-time">{{ formatDate(message.createTime) }}</span>
      </div>
      <div class="message-bubble">{{ message.content }}</div>

      <!-- 回复显示区域 -->
      <div v-if="message.replyContent" class="reply-container">
        <div class="reply-header">
          <span class="reply-seller">卖家回复</span>
          <span class="reply-time">{{ formatDate(message.replyTime) }}</span>
        </div>
        <div class="reply-bubble">{{ message.replyContent }}</div>
      </div>

      <!-- 卖家回复输入框 -->
      <div v-if="isSeller && !message.replyContent" class="reply-input-container">
        <textarea
            v-model="replyContent"
            :disabled="replyLoading"
            class="reply-input"
            placeholder="输入回复内容..."
            @keydown.enter.prevent="submitReply"
        ></textarea>
        <div class="reply-input-footer">
          <span class="reply-char-count">{{ replyContent.length }}/100</span>
          <el-button
              :disabled="!replyContent.trim() || replyLoading || replyContent.length > 100"
              size="small"
              type="primary"
              @click="submitReply"
          >
            {{ replyLoading ? '回复中...' : '回复' }}
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {defineProps, ref} from 'vue';
import {useMessageStore} from '../store/message';
import {ElButton, ElMessage} from 'element-plus';

// 定义留言数据类型
interface Message {
  id: number;
  userId: number;
  username: string;
  userAvatar: string | null;
  content: string;
  createTime: string;
  replyContent?: string;
  replyTime?: string;
}

const props = defineProps<{
  message: Message;
  isSeller: boolean;
  goodsId: number;
}>();

const emit = defineEmits<{
  (e: 'reply-sent', messageId: number): void;
}>();

const messageStore = useMessageStore();
const replyContent = ref('');
const replyLoading = ref(false);

// 格式化日期
const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 提交回复
const submitReply = async () => {
  if (!replyContent.value.trim() || replyContent.value.length > 100) return;

  try {
    replyLoading.value = true;
    await messageStore.replyMessage(props.message.id, replyContent.value.trim(), props.goodsId);
    emit('reply-sent', props.message.id);
    replyContent.value = '';
  } catch (error) {
    ElMessage.error('回复失败，请重试');
  } finally {
    replyLoading.value = false;
  }
};
</script>

<style lang="scss" scoped>
.message-item {
  display: flex;
  gap: 12px;
  padding: 15px 0;
  border-bottom: 1px solid var(--border-color);
}

.message-item:last-child {
  border-bottom: none;
}


.message-content-wrapper {
  flex: 1;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.username {
  font-weight: 500;
  color: var(--text-primary);
  font-size: 14px;
}

.create-time {
  color: var(--text-secondary);
  font-size: 12px;
}

.message-bubble {
  background-color: var(--bg-secondary);
  padding: 10px 15px;
  border-radius: 8px;
  color: var(--text-primary);
  font-size: 14px;
  line-height: 1.5;
  max-width: 80%;
}

/* 回复样式 */
.reply-container {
  margin-top: 10px;
  margin-left: 20px;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
  font-size: 12px;
}

.reply-seller {
  color: var(--primary-blue);
  font-weight: 500;
}

.reply-time {
  color: var(--text-secondary);
}

.reply-bubble {
  background-color: var(--primary-light-bg);
  padding: 8px 12px;
  border-radius: 8px;
  color: var(--primary-dark-text);
  font-size: 13px;
  line-height: 1.5;
  max-width: 80%;
}

/* 回复输入框 */
.reply-input-container {
  margin-top: 12px;
  margin-left: 20px;
}

.reply-input {
  width: 100%;

  .reply-input {
    min-height: 60px;
    padding: 8px 12px;
    border: var(--glass-border);
    border-radius: 12px;
    resize: none;
    font-size: 13px;
    line-height: 1.5;
    background: var(--glass-bg);
    backdrop-filter: var(--glass-backdrop);
    color: var(--text-primary);
    background-image: var(--glass-highlight);
    transition: var(--glass-transition);
  }

  .reply-input:focus {
    outline: none;
    border-color: rgba(22, 119, 255, 0.5);
    transform: perspective(1000px) rotateY(1deg) scale(1.01);
    box-shadow: 0 5px 15px rgba(22, 119, 255, 0.15);
  }

  .reply-input-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 6px;
  }

  .reply-char-count {
    font-size: 12px;
    color: var(--text-secondary);
  }
}
</style>