import {defineStore} from 'pinia';
import {getMessageList, leaveMessage} from '../api/message';

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