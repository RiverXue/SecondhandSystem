import {defineStore} from 'pinia';
import {addFavorite, getFavoriteList, removeFavorite} from '../api/favorite';

interface FavoriteState {
    favorites: any[];
    loading: boolean;
}

export const useFavoriteStore = defineStore('favorite', {
    state: (): FavoriteState => ({
        favorites: [],
        loading: false
    }),

    actions: {
        /**
         * 添加收藏
         */
        async addFavorite(goodsId: number) {
            this.loading = true;
            try {
                const res = await addFavorite(goodsId);
                if (res.data.code === 200) {
                    await this.getFavoriteList();
                }
                console.log('add 返回：', res);
                return res.data;
            } catch (error) {
                console.error('添加收藏失败:', error);
                throw error;
            } finally {
                this.loading = false;
            }
        },

        /**
         * 取消收藏
         */
        async removeFavorite(goodsId: number) {
            this.loading = true;
            try {
                const res = await removeFavorite(goodsId);
                if (res.data.code === 200) {
                    await this.getFavoriteList();
                }
                return res.data;
            } catch (error) {
                console.error('取消收藏失败:', error);
                throw error;
            } finally {
                this.loading = false;
            }
        },

        /**
         * 获取我的收藏列表
         */
        async getFavoriteList() {
            this.loading = true;
            try {
                const res = await getFavoriteList();
                console.log('收藏列表完整响应:', res);
                if (res.data && res.data.code === 200) {
                    console.log('收藏列表数据:', res.data.data.data.favoritesList);
                    this.favorites = res.data.data.data.favoritesList || [];
                    console.log('收藏列表赋值结果:', this.favorites);
                    console.log('收藏列表长度:', this.favorites.length);
                } else {
                    this.favorites = [];
                    console.error('获取收藏列表失败:', res.data?.message || '未知错误');
                }
                return res.data;
            } catch (error) {
                console.error('获取收藏列表失败:', error);
                this.favorites = [];
                throw error;
            } finally {
                this.loading = false;
            }
        }
    }
});