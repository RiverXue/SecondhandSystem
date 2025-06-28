import {defineStore} from 'pinia';
import {getMessageList, leaveMessage, replyToMessage} from '../api/message';
import { getUserById } from '../api/user';

interface MessageState {
    messages: any[];
    loading: boolean;
}

export const useMessageStore = defineStore('message', {
    state: (): MessageState => ({
        messages: [],
        loading: false
    }),

    actions: {
        /**
         * 回复留言
         */
        async replyMessage(messageId: number, content: string, goodsId: number) {
            this.loading = true;
            try {
                const res = await replyToMessage(messageId, content);
                if (res.data.code === 200) {
                    // 回复成功后刷新当前商品的留言列表
                    await this.getMessageList(goodsId);
                }
                return res.data;
            } catch (error) {
                console.error('回复留言失败:', error);
                throw error;
            } finally {
                this.loading = false;
            }
        },

        /**
         * 获取商品留言列表并补充用户信息
         */
        async getMessageList(goodsId: number, page: number = 1, pageSize: number = 10) {
            this.loading = true;
            try {
                const res = await getMessageList(goodsId, page, pageSize);
                if (res.data.code === 200) {
                    const messages = res.data.data.messageList || [];
                    // 获取每条留言的用户信息
                    for (const message of messages) {
                        const userInfo = await getUserById(message.userId);
                        message.username = userInfo.data.username;
                        message.userAvatar = userInfo.data.avatar;
                    }
                    this.messages = messages;
                    this.hasMore = res.data.data.total > page * pageSize;
                }
                return res.data;
            } catch (error) {
                console.error('获取留言列表失败:', error);
                throw error;
            } finally {
                this.loading = false;
            }
        },
        /**
         * 发表留言
         */
        async leaveMessage(goodsId: number, content: string) {
            this.loading = true;
            try {
                const res = await leaveMessage(goodsId, content);
                if (res.data.code === 200) {
                    // 发表成功后刷新留言列表
                    await this.getMessageList(goodsId);
                }
                return res.data;
            } catch (error) {
                console.error('发表留言失败:', error);
                throw error;
            } finally {
                this.loading = false;
            }
        },

        /**
         * 清空留言列表
         */
        clearMessages() {
            this.messages = [];
        },

        /**
         * 获取商品留言列表
         */
        async getMessageList(goodsId: number, page: number = 1, pageSize: number = 10) {
            this.loading = true;
            try {
                const res = await getMessageList(goodsId, page, pageSize);
                if (res.data.code === 200) {
                    this.messages = Array.isArray(res.data.data.messageList) ? res.data.data.messageList : [];
                    this.hasMore = res.data.data.total > page * pageSize;
                }
                return res.data;
            } catch (error) {
                console.error('获取留言列表失败:', error);
                throw error;
            } finally {
                this.loading = false;
            }
        }
    }
});