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
            <div v-if="aiStore.messages.length === 0" class="welcome-message">
              <p>您好！我是您的AI智能助手，有什么可以帮您找到合适的商品吗？</p>
            </div>

            <!-- 消息列表 -->
            <div v-for="(msg, index) in aiStore.messages" :key="index"
                 :class="['message-item', msg.isUser ? 'user-message' : 'ai-message']"
                 class="message-item">
              <!--              <div :class="['message-bubble', msg.isUser ? 'user-message' : 'ai-message']">-->
              <!--                &lt;!&ndash; 用户消息：纯文本 &ndash;&gt;-->
              <!--                <div v-if="msg.isUser" class="message-content">{{ msg.content }}</div>-->

              <!--                &lt;!&ndash; AI消息：Markdown渲染 &ndash;&gt;-->
              <!--                <div v-else class="message-content" v-html="renderMarkdown(msg.content)"></div>-->

              <!--                <div class="message-time">{{ formatTime(msg.timestamp) }}</div>-->
              <!--              </div>-->
              <!--              <div :class="['message-bubble', msg.isUser ? 'user-message' : 'ai-message']">-->
              <!--                <div class="message-header">-->
              <!--                  <span class="message-time">{{ formatTime(msg.timestamp) }}</span>-->
              <!--                </div>-->
              <!--                <div class="message-content">-->
              <!--                  <div v-if="msg.isUser">{{ msg.content }}</div>-->
              <!--                  <div v-else v-html="renderMarkdown(msg.content)"></div>-->
              <!--                </div>-->
              <!--              </div>-->

              <div :class="['message-item', msg.isUser ? 'user-message' : 'ai-message']">
                <div class="message-row">
                  <div class="message-bubble">
                    <div v-if="msg.isUser">{{ msg.content }}</div>
                    <!--                    <div v-else>-->
                    <!--                      <div v-html="renderMarkdown(msg.content)"></div>-->
                    <!--                      &lt;!&ndash; 推荐商品展示 &ndash;&gt;-->
                    <!--                      <div v-if="!msg.isUser && msg.type === 'recommend'" class="recommended-goods">-->
                    <!--                        <div class="goods-list">-->
                    <!--                          <div v-for="goods in msg.content" :key="goods.id" class="goods-item">-->
                    <!--                            <img :alt="goods.title" :src="getImageUrl(goods.image)" class="goods-image"-->
                    <!--                                 @error="onImageError"/>-->
                    <!--                            <div class="goods-info">-->
                    <!--                              <h4 class="goods-title">{{ goods.title }}</h4>-->
                    <!--                              <p class="goods-price">¥{{ goods.price.toFixed(2) }}</p>-->
                    <!--                            </div>-->
                    <!--                          </div>-->
                    <!--                        </div>-->
                    <!--                      </div>-->
                    <!--                    </div>-->
                    <div v-else>
                      <!-- 推荐商品消息 -->
                      <template v-if="msg.type === 'recommend'">
                        <div class="recommended-goods">
                          <div class="goods-list">
                            <div
                                v-for="goods in JSON.parse(msg.content)"
                                :key="goods.id"
                                class="goods-item"
                                @click="gotoGoodsDetail(goods.id)"
                            >
                              <img
                                  :alt="goods.title"
                                  :src="getImageUrl(goods.image)"
                                  class="goods-image"
                                  @error="onImageError"
                              />
                              <div class="goods-info">
                                <h4 class="goods-name">{{ goods.title }}</h4>
                                <p class="goods-price">¥{{ goods.price.toFixed(2) }}</p>
                              </div>
                            </div>
                          </div>
                        </div>
                      </template>

                      <!-- 普通AI文本消息 -->
                      <template v-else>
                        <div v-html="renderMarkdown(msg.content)"></div>
                      </template>
                    </div>

                  </div>
                  <span class="message-time-inline">{{ formatTime(msg.timestamp) }}</span>
                </div>
              </div>

            </div>

            <!--            &lt;!&ndash; 推荐商品展示 &ndash;&gt;-->
            <!--            <div v-if="isLoading" class="recommended-loading">-->
            <!--              <el-skeleton :rows="3" width="100%"/>-->
            <!--            </div>-->
            <!--            <div v-if="recommendedGoods.length" class="recommended-goods">-->
            <!--              <h3 class="section-title">推荐商品</h3>-->
            <!--              <div class="goods-list">-->
            <!--                <div-->
            <!--                    v-for="goods in recommendedGoods"-->
            <!--                    :key="goods.id"-->
            <!--                    class="goods-item"-->
            <!--                    @click="gotoGoodsDetail(goods.id)"-->
            <!--                >-->
            <!--                  <img-->
            <!--                      :alt="goods.title"-->
            <!--                      :src="getImageUrl(goods.image)"-->
            <!--                      class="goods-image"-->
            <!--                      @error="e => e.target.src = '/default-goods.jpg'"-->
            <!--                  />-->
            <!--                  <div class="goods-info">-->
            <!--                    <h4 class="goods-name">{{ goods.title }}</h4>-->
            <!--                    <p class="goods-price">¥{{ goods.price.toFixed(2) }}</p>-->
            <!--                  </div>-->
            <!--                </div>-->
            <!--              </div>-->
            <!--            </div>-->
            <!--            <div v-else-if="!recommendedGoods.length && messages.length" class="ai-reply">-->
            <!--              &lt;!&ndash;              <p>暂无推荐商品</p>&ndash;&gt;-->
            <!--            </div>-->
            <!--            &lt;!&ndash; 加载中提示 &ndash;&gt;-->
            <!--            <div v-if="isLoading" class="loading-message">-->
            <!--              <el-skeleton :rows="2" width="80%"/>-->
            <!--            </div>-->
            <!--          </div>-->
            <!-- 推荐商品展示区域 -->
            <template v-if="aiStore.messages.length > 0">
              <!-- 加载状态下骨架屏 -->
              <div v-if="aiStore.isLoading" class="recommended-loading">
                <el-skeleton :rows="3" width="100%"/>
              </div>

              <!--              &lt;!&ndash; 推荐商品存在时 &ndash;&gt;-->
              <!--              <div v-else-if="aiStore.recommendedGoods.length > 0" class="recommended-goods">-->
              <!--                &lt;!&ndash;                <h3 class="section-title">推荐商品</h3>&ndash;&gt;-->
              <!--                <div class="goods-list">-->
              <!--                  <div-->
              <!--                      v-for="goods in aiStore.recommendedGoods"-->
              <!--                      :key="goods.id"-->
              <!--                      class="goods-item"-->
              <!--                      @click="gotoGoodsDetail(goods.id)"-->
              <!--                  >-->
              <!--                    &lt;!&ndash;                    <img&ndash;&gt;-->
              <!--                    &lt;!&ndash;                        :alt="goods.title"&ndash;&gt;-->
              <!--                    &lt;!&ndash;                        :src="getImageUrl(goods.image)"&ndash;&gt;-->
              <!--                    &lt;!&ndash;                        class="goods-image"&ndash;&gt;-->
              <!--                    &lt;!&ndash;                        @error="e => e.target.src = '/default-goods.jpg'"&ndash;&gt;-->
              <!--                    &lt;!&ndash;                    />&ndash;&gt;-->
              <!--                    <img-->
              <!--                        :alt="goods.title"-->
              <!--                        :src="getImageUrl(goods.image)"-->
              <!--                        class="goods-image"-->
              <!--                        @error="onImageError"-->
              <!--                    />-->
              <!--                    <div class="goods-info">-->
              <!--                      <h4 class="goods-name">{{ goods.title }}</h4>-->
              <!--                      <p class="goods-price">¥{{ goods.price.toFixed(2) }}</p>-->
              <!--                    </div>-->
              <!--                  </div>-->
              <!--                </div>-->
              <!--              </div>-->

              <!-- 无推荐商品时的提示 -->
              <div v-else class="ai-reply">
              </div>
            </template>
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
            :loading="aiStore.isLoading"
            class="send-button"
            type="primary"
            @click="handleSendMessage"
        >
          发送
        </el-button>
      </div>
    </div>
    <!-- 调试信息 -->
    <p style="color: gray; font-size: 12px">
      推荐商品数：{{ aiStore.recommendedGoods.length }}，
      消息数：{{ aiStore.messages.length }}
    </p>

  </el-dialog>
</template>

<script lang="ts" setup>
import {ref, watch} from 'vue';
import {useRouter} from 'vue-router';
import {useAiStore} from '../store/ai';
import {useUserStore} from '../store/user';
import {ElMessage} from 'element-plus';
import markdownIt from 'markdown-it';
import defaultGoodsImage from '../assets/codelogo.png';

const md = new markdownIt();

const props = defineProps<{ visible: boolean }>();
const emit = defineEmits<{ (e: 'update:visible', value: boolean): void }>();

// const getImageUrl = (filename?: string) => {
//   return filename ? `http://localhost:7272/uploads/${filename}` : '/default-goods.jpg';
// };
const getImageUrl = (filename?: string) => {
  // 如果 filename 为空或是空字符串或为 null，返回默认图
  if (!filename || filename.trim() === '') {
    return defaultGoodsImage;
  }
  return `http://localhost:7272/uploads/${filename}`;
};

const onImageError = (e: Event) => {
  const target = e.target as HTMLImageElement;
  if (target.src !== defaultGoodsImage) {
    target.src = defaultGoodsImage;
  }
};


const inputMessage = ref('');
const aiStore = useAiStore();
const userStore = useUserStore();
// const {messages, isLoading, recommendedGoods} = aiStore;
const router = useRouter();

watch(() => aiStore.recommendedGoods, (val) => {
  console.log('推荐商品变化：', val);
});

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
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}


.message-bubble {
  position: relative;
  padding: 10px 15px;
  border-radius: 18px;
  word-break: break-word;
  max-width: 100%;
}

.message-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-message .message-row {
  flex-direction: row-reverse;
}

.message-time-inline {
  font-size: 10px;
  color: #aaa;
  white-space: nowrap;
}

.message-header {
  position: absolute;
  top: 6px;
  right: 12px;
  font-size: 10px;
  color: #999;
}

.message-content {
  padding-top: 12px;
}


.user-message {
  align-self: flex-end;
}

.ai-message {
  align-self: flex-start;
}

.message-bubble {
  padding: 10px 15px;
  border-radius: 18px;
  word-break: break-word;
}

.user-message .message-bubble {
  background-color: #409eff;
  color: white;
}

.ai-message .message-bubble {
  background-color: #f2f3f5;
  color: #303133;
}

.message-time {
  font-size: 12px;
  margin-top: 5px;
  text-align: right;
  color: #86909c;
}

.welcome-message {
  text-align: center;
  padding: 20px;
  color: #86909c;
}

.input-area {
  display: flex;
  gap: 10px;
  padding: 10px;
  border-top: 1px solid #e5e6eb;
}

.message-input {
  flex: 1;
}

.send-button {
  width: 80px;
}

.loading-message {
  padding: 10px;
}

.recommended-goods {
  margin-top: 15px;
  padding: 10px;
}

.section-title {
  font-size: 16px;
  margin-bottom: 10px;
  color: #333;
  font-weight: 500;
}

.goods-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
  gap: 12px;
  padding: 8px 0;
}

.goods-item {
  border: 1px solid #e5e6eb;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s ease;
  padding: 8px;
  height: 180px;
  display: flex;
  flex-direction: column;
}

.goods-item:hover {
  transform: scale(1.02);
}

.goods-image {
  width: 100%;
  height: 100px;
  object-fit: cover;
}

.goods-info {
  padding: 8px;
}

.goods-name {
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 4px;
}

.goods-price {
  font-size: 16px;
  color: #ff4d4f;
  font-weight: bold;
}
</style>
