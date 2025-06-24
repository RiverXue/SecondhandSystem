import {defineStore} from 'pinia';
import {useUserStore} from './user';
import {clearAiChatHistory, sendAiChatMessage} from '../api/ai';
import {ElMessage} from 'element-plus';

interface Message {
    id?: number;
    content: string;
    isUser: boolean;
    timestamp: Date;
}

interface AiState {
    sessionId: string | null; // 明确为string类型
    messages: Message[];
    isLoading: boolean;
    recommendedGoods: any[];
}

export const useAiStore = defineStore('ai', {
    state: (): AiState => ({
        sessionId: null,
        messages: [],
        isLoading: false,
        recommendedGoods: []
    }),

    actions: {
        /**
         * 发送消息到AI服务
         */
        async sendMessage(content: string) {
            if (!content.trim()) return;

            const userStore = useUserStore();
            if (!userStore.userInfo.id) {
                ElMessage.error('用户未登录，无法发送消息');
                return;
            }

            // 1. 添加用户消息
            const userMessage: Message = {
                content,
                isUser: true,
                timestamp: new Date()
            };
            this.messages.push(userMessage);
            this.isLoading = true;

            try {
                // 2. 确保 sessionId（使用用户id作为初始sessionId）
                if (!this.sessionId) {
                    this.sessionId = userStore.userInfo.id.toString();
                }

                // 3. 请求后端
                const response = await sendAiChatMessage({
                    sessionId: this.sessionId,
                    message: content,
                    userId: userStore.userInfo.id
                });

                // 4. 后端返回sessionId（确保更新）
                this.sessionId = response.data.sessionId?.toString() || this.sessionId;

                // 5. 处理推荐商品
                this.recommendedGoods = response.data.recommendedGoods || [];

                // 6. 添加AI回复消息
                const aiMessage: Message = {
                    // content: response.data.reply || 'AI未返回内容',
                    content: response.data.data?.reply || 'AI未返回内容',
                    isUser: false,
                    timestamp: new Date()
                };
                this.messages.push(aiMessage);

                return response.data;
            } catch (error) {
                ElMessage.error('AI对话失败，请稍后重试');
                // 移除刚刚添加的用户消息（回滚）
                this.messages.pop();
                throw error;
            } finally {
                this.isLoading = false;
            }
        },

        /**
         * 获取聊天历史（本地，无后端接口）
         */
        async fetchHistory() {
            const userStore = useUserStore();
            this.messages = [];
            this.sessionId = userStore.userInfo.id?.toString() || null;
            this.recommendedGoods = [];
        },

        /**
         * 清除聊天历史（本地模拟）
         */
        async clearHistory() {
            try {
                await clearAiChatHistory();
                this.messages = [];
                this.sessionId = null;
                this.recommendedGoods = [];
                ElMessage.success('聊天历史已清除');
            } catch (error) {
                ElMessage.error('清除聊天历史失败');
                throw error;
            }
        }
    }
});
