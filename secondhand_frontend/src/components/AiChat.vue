<template>
  <el-dialog
      v-if="visible"
      :close-on-click-modal="false"
      :model-value="visible"
      :show-close="true"
      append-to-body
      title="AI智能助手"
      width="420px"
      @update:model-value="(val) => emit('update:visible', val)"
  >
    <div class="chat-container">
      <div class="chat-messages">
        <el-scrollbar height="400px">
          <div class="message-list">
            <!-- 欢迎消息 -->
            <div v-if="messages.length === 0" class="welcome-message">
              <p>您好！我是您的AI智能助手，有什么可以帮您找到合适的商品吗？</p>
            </div>

            <!-- 消息列表 -->
            <div v-for="(msg, index) in messages" :key="index" class="message-item">
              <div :class="['message-bubble', msg.isUser ? 'user-message' : 'ai-message']">
                <!-- 用户消息：纯文本 -->
                <div v-if="msg.isUser" class="message-content">{{ msg.content }}</div>

                <!-- AI消息：Markdown渲染 -->
                <div v-else class="message-content" v-html="renderMarkdown(msg.content)"></div>

                <div class="message-time">{{ formatTime(msg.timestamp) }}</div>
              </div>
            </div>

            <!-- 推荐商品展示 -->
            <div v-if="isLoading" class="recommended-loading">
              <el-skeleton :rows="3" width="100%"/>
            </div>
            <div v-else-if="recommendedGoods.length" class="recommended-goods">
              <h3 class="section-title">推荐商品</h3>
              <div class="goods-list">
                <div
                    v-for="goods in recommendedGoods"
                    :key="goods.id"
                    class="goods-item"
                    @click="gotoGoodsDetail(goods.id)"
                >
                  <img :alt="goods.name" :src="goods.imageUrl || '/default-goods.jpg'" class="goods-image"
                       @error="e => e.target.src = '/default-goods.jpg'"/>
                  <div class="goods-info">
                    <h4 class="goods-name">{{ goods.name }}</h4>
                    <p class="goods-price">¥{{ goods.price.toFixed(2) }}</p>
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="no-recommendations">
              <!--              <p>暂无推荐商品</p>-->
            </div>

            <!-- 加载中提示 -->
            <div v-if="isLoading" class="loading-message">
              <el-skeleton :rows="2" width="80%"/>
            </div>
          </div>
        </el-scrollbar>
      </div>

      <!-- 输入区域 -->
      <div class="input-area">
        <el-input
            v-model="inputMessage"
            class="message-input"
            placeholder="请输入您的需求..."
            @keyup.enter="handleSendMessage"
        ></el-input>
        <el-button
            :loading="isLoading"
            class="send-button"
            type="primary"
            @click="handleSendMessage"
        >
          发送
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script lang="ts" setup>
import {ref, watch} from 'vue';
import {useRouter} from 'vue-router';
import {useAiStore} from '../store/ai';
import {useUserStore} from '../store/user';
import {ElMessage} from 'element-plus';
import markdownIt from 'markdown-it';

const md = new markdownIt();

const props = defineProps<{ visible: boolean }>();
const emit = defineEmits<{ (e: 'update:visible', value: boolean): void }>();

const inputMessage = ref('');
const aiStore = useAiStore();
const userStore = useUserStore();
const {messages, isLoading, recommendedGoods} = aiStore;
const router = useRouter();

const renderMarkdown = (content: string) => {
  return md.render(content);
};

const gotoGoodsDetail = (goodsId: number) => {
  emit('update:visible', false);
  router.push(`/goods/detail/${goodsId}`);
};

const formatTime = (date: Date) => {
  return new Intl.DateTimeFormat('zh-CN', {hour: '2-digit', minute: '2-digit'}).format(date);
};

const handleSendMessage = async () => {
  if (!userStore.accessToken) {
    ElMessage.warning('请先登录后使用AI助手');
    return;
  }
  const content = inputMessage.value.trim();
  if (!content) {
    ElMessage.warning('请输入消息内容');
    return;
  }
  if (content.length > 500) {
    ElMessage.warning('消息长度不能超过500个字符');
    return;
  }
  try {
    await aiStore.sendMessage(content);
    inputMessage.value = '';
    setTimeout(() => {
      const scrollContainer = document.querySelector('.el-scrollbar__wrap');
      if (scrollContainer) scrollContainer.scrollTop = scrollContainer.scrollHeight;
    }, 0);
  } catch (error) {
    console.error('发送消息失败:', error);
    ElMessage.error('消息发送失败，请稍后重试');
  }
};

watch(() => props.visible, (newVal) => {
  if (!newVal) inputMessage.value = '';
});
</script>

<style scoped>
</style>
