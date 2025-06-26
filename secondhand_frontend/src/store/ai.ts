import {defineStore} from 'pinia';
import {useUserStore} from './user';
import {clearAiChatHistory, sendAiChatMessage} from '../api/ai';
import {ElMessage} from 'element-plus';

interface Message {
    id?: number;
    content: string;
    isUser: boolean;
    timestamp: Date;
    type?: string;  // 新增，可选字段，标记消息类型
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


                // 4. 处理推荐商品
                // this.recommendedGoods = response.data.data?.list || [];
                console.log('AI 返回完整内容：', response.data);
                // const aiData = response.data.data || {};
                // console.log('解析后的 aiData：', aiData);
                // this.recommendedGoods = aiData.list || [];
                //
                // // 5. 后端返回sessionId（确保更新）
                // // this.sessionId = response.data.sessionId?.toString() || this.sessionId;
                // // this.sessionId = response.data.sessionId?.toString() || this.sessionId;
                // this.sessionId = aiData.sessionId?.toString() || this.sessionId;
                const outerData = response.data.data || {};
                const aiData = outerData.data || {};  // 取内层data
                // const aiData = response.data.data || {};
                console.log('解析后的 aiData：', aiData);

                console.log('response.data:', response.data);
                console.log('response.data.data:', response.data.data);
                console.log('response.data.data.list:', response.data.data?.list);

                this.recommendedGoods = Array.isArray(aiData.list) ? aiData.list : [];
                this.sessionId = aiData.sessionId?.toString() || this.sessionId;


                // 6. 添加AI回复消息
                const aiMessage: Message = {
                    // content: response.data.reply || 'AI未返回内容',
                    // content: response.data.data?.reply || 'AI未返回内容',
                    content: aiData.reply || 'AI未返回内容',
                    isUser: false,
                    timestamp: new Date()
                };
                this.messages.push(aiMessage);

                // 商品推荐消息（可选）
                if (Array.isArray(aiData.list) && aiData.list.length > 0) {
                    this.messages.push({
                        content: JSON.stringify(aiData.list), // 暂时存成字符串，模板中解析
                        isUser: false,
                        timestamp: new Date(),
                        type: 'recommend' // 标记为推荐类型
                    } as Message);
                }

                return response.data;
            } catch (error) {
                ElMessage.error('AI对话失败，请稍后重试');
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
