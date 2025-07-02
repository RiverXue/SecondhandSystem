<template>
  <div class="message-board-container">
    <el-card class="detail-card"> <!-- 正确包裹所有内容 -->

      <!-- 留言板头部 -->
      <div class="message-board-header">
        <h2 class="board-title">商品留言区</h2>
        <div class="board-stats">
          <span class="stats-item">{{ totalMessages }} 条留言</span>
          <el-divider class="divider" direction="vertical"/>
          <el-select v-model="sortOption" size="small" @change="handleSortChange">
            <el-option label="最新优先" value="newest"/>
            <el-option label="最早优先" value="oldest"/>
          </el-select>
        </div>
      </div>

      <!-- 留言输入区域 -->
      <MessageInput
          v-if="userStore.accessToken"
          :goods-id="goodsId"
          @message-sent="handleMessageSent"
      />
      <LoginPrompt v-else/>

      <!-- 留言列表区域 -->
      <div class="message-list-container">
        <ErrorState v-if="messageStore.error" :message="messageStore.error" @retry="loadMessages"/>
        <el-skeleton v-else-if="messageStore.loading && !messageStore.messages.length" :count="3"/>
        <EmptyState v-else-if="!messageStore.loading && !messageStore.messages.length"/>

        <div v-else class="message-list">
          <MessageItem
              v-for="message in sortedMessages"
              :key="message.id"
              :is-seller="isSeller"
              :message="message"
              :goods-id="goodsId"
              @reply-sent="handleReplySent"
          />
        </div>
      </div>

      <!-- 分页加载 -->
      <div v-if="!messageStore.loading && messageStore.hasMore" class="load-more-container">
        <el-button
            :disabled="loadingMore"
            :loading="loadingMore"
            class="load-more-btn"
            size="small"
            @click="loadMoreMessages"
        >
          加载更多
        </el-button>
      </div>

    </el-card>
  </div>
</template>

<script lang="ts" setup>
import {computed, onMounted, ref} from 'vue';
import {useMessageStore} from '../store/message';
import {useUserStore} from '../store/user';
import {useGoodsStore} from '../store/goods';
import MessageInput from './MessageInput.vue';
import MessageItem from './MessageItem.vue';
import LoginPrompt from './LoginPrompt.vue';
import EmptyState from './EmptyState.vue';
import ErrorState from './ErrorState.vue';
import {ElMessage} from 'element-plus';

const props = defineProps<{
  goodsId: number;
}>();

const messageStore = useMessageStore();
const userStore = useUserStore();
const goodsStore = useGoodsStore();

const sortOption = ref<'newest' | 'oldest'>('newest');
const loadingMore = ref(false);
const currentPage = ref(1);
const pageSize = 10;

const isSeller = computed(() => userStore.userInfo?.id === goodsStore.currentGoods?.userId);

const sortedMessages = computed(() => {
  return [...messageStore.messages].sort((a, b) => {
    const dateA = new Date(a.createTime).getTime();
    const dateB = new Date(b.createTime).getTime();
    return sortOption.value === 'newest' ? dateB - dateA : dateA - dateB;
  });
});

const totalMessages = computed(() => messageStore.messages.length);

onMounted(() => {
  loadMessages();
});

const loadMessages = async (page = 1, reset = false) => {
  try {
    if (reset) {
      currentPage.value = 1;
      messageStore.clearMessages(); // 清空旧数据
    }
    await messageStore.getMessageList(props.goodsId, page, pageSize);
  } catch (error) {
    console.error('加载留言失败:', error);
    ElMessage.error('加载留言失败，请稍后重试');
  }
};

const loadMoreMessages = async () => {
  if (loadingMore.value || !messageStore.hasMore) return;
  loadingMore.value = true;
  try {
    currentPage.value++;
    await loadMessages(currentPage.value);
  } finally {
    loadingMore.value = false;
  }
};

const handleSortChange = () => {
  // 本地排序无需额外处理
};

const handleMessageSent = () => {
  loadMessages(1, true);
  ElMessage.success('留言发布成功');
};

const handleReplySent = async () => {
  await loadMessages(1, true); // 保持分页一致
  ElMessage.success('回复成功');
};
</script>

<style lang="scss" scoped>
.message-board-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 0;
}

.detail-card {
  padding: 20px;
  background: var(--glass-bg);
  backdrop-filter: var(--glass-backdrop);
  border: var(--glass-border);
  box-shadow: var(--glass-shadow);
  border-radius: 20px;
  margin-bottom: 30px;
  transition: all 0.5s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.message-board-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--border-color);
}

.board-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.board-stats {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stats-item {
  font-size: 14px;
  color: var(--text-secondary);
}

.divider {
  height: 16px;
}

.message-list-container {
  margin-top: 24px;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.load-more-container {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.load-more-btn {
  width: 160px;
}

@media (max-width: 768px) {
  .message-board-container {
    padding: 16px;
    border-radius: 8px;
  }

  .message-board-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>
