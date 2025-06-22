<template>
  <div class="message-board-container">
    <!-- 留言板头部 -->
    <div class="message-board-header">
      <h2 class="board-title">商品留言区</h2>
      <div class="board-stats">
        <span class="stats-item">{{ totalMessages }} 条留言</span>
        <el-divider direction="vertical" class="divider" />
        <el-select v-model="sortOption" size="small" @change="handleSortChange">
          <el-option label="最新优先" value="newest" />
          <el-option label="最早优先" value="oldest" />
        </el-select>
      </div>
    </div>

    <!-- 留言输入区域 -->
    <MessageInput
      v-if="userStore.accessToken"
      :goods-id="goodsId"
      @message-sent="handleMessageSent"
    />
    <LoginPrompt v-else />

    <!-- 留言列表区域 -->
    <div class="message-list-container">
      <el-skeleton v-if="messageStore.loading && !messageStore.messages.length" :count="3" />
      <ErrorState v-if="messageStore.error" :message="messageStore.error" @retry="loadMessages" />
      <EmptyState v-if="!messageStore.loading && !messageStore.messages.length && !messageStore.error" />

      <div class="message-list" v-else>
        <MessageItem
          v-for="message in sortedMessages"
          :key="message.id"
          :message="message"
          :is-seller="userStore.userInfo?.isSeller || false"
          @reply-sent="handleReplySent"
        />
      </div>

      <!-- 分页加载 -->
      <div class="load-more-container" v-if="!messageStore.loading && messageStore.hasMore">
        <el-button
          size="small"
          :loading="loadingMore"
          @click="loadMoreMessages"
          class="load-more-btn"
        >
          加载更多
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useMessageStore } from '../store/message';
import { useUserStore } from '../store/user';
import MessageInput from './MessageInput.vue';
import MessageItem from './MessageItem.vue';
import LoginPrompt from './LoginPrompt.vue';
import EmptyState from './EmptyState.vue';
import ErrorState from './ErrorState.vue';
import { ElMessage, ElDivider, ElSelect, ElOption, ElButton } from 'element-plus';

// 定义组件参数
const props = defineProps<{
  goodsId: number;
}>();

// 状态管理
const messageStore = useMessageStore();
const userStore = useUserStore();
const sortOption = ref<'newest' | 'oldest'>('newest');
const loadingMore = ref(false);
const currentPage = ref(1);
const pageSize = 10;

// 计算属性：排序后的留言列表
const sortedMessages = computed(() => {
  return [...messageStore.messages].sort((a, b) => {
    const dateA = new Date(a.createTime).getTime();
    const dateB = new Date(b.createTime).getTime();
    return sortOption.value === 'newest' ? dateB - dateA : dateA - dateB;
  });
});

// 计算属性：总留言数
const totalMessages = computed(() => messageStore.messages.length);

// 页面加载时获取留言
onMounted(() => {
  loadMessages();
});

// 加载留言列表
const loadMessages = async (page = 1, reset = false) => {
  try {
    if (reset) currentPage.value = 1;
    await messageStore.getMessageList(props.goodsId, page, pageSize);
  } catch (error) {
    console.error('加载留言失败:', error);
    ElMessage.error('加载留言失败，请稍后重试');
  }
};

// 加载更多留言
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

// 处理排序变更
const handleSortChange = () => {
  // 排序变更时无需重新请求，仅客户端排序
};

// 处理留言发送成功
const handleMessageSent = () => {
  // 留言成功后重置页码并重新加载第一页
  loadMessages(1, true);
  ElMessage.success('留言发布成功');
};

// 处理回复发送成功
const handleReplySent = (messageId: number) => {
  // 找到对应留言并更新UI
  const messageIndex = messageStore.messages.findIndex(m => m.id === messageId);
  if (messageIndex !== -1) {
    // 这里可以优化为只更新单条留言，减少重渲染
    loadMessages(currentPage.value);
  }
  ElMessage.success('回复发布成功');
};
</script>

<style scoped lang="scss">
.message-board-container {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.06);
}

.message-board-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f5f5f5;
}

.board-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.board-stats {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stats-item {
  font-size: 14px;
  color: #606266;
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

/* 响应式调整 */
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