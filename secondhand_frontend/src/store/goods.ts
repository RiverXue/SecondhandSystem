import {defineStore} from 'pinia';
import {getGoodsDetail, getGoodsList, searchGoods, getMyPublishedGoods, deleteGoods} from '../api/goods';
import {addFavorite, getFavoriteList, removeFavorite} from '../api/favorite';

/**
 * 商品状态
 */
interface GoodsState {
    goodsList: any[];
    currentGoods: any | null;
    total: number;
    loading: boolean;
    favorites: number[];
    myPublishedGoods: any[];
    myPublishedTotal: number;
}

/**
 * 商品 Store
 * @returns {GoodsState} 商品状态
 * @example
 * const goodsStore = useGoodsStore();
 * goodsStore.fetchGoodsList();
 * goodsStore.fetchGoodsDetail(1);
 */
export const useGoodsStore = defineStore('goods', {
    state: (): GoodsState => ({
        goodsList: [],
        currentGoods: null,
        total: 0,
        loading: false,
        favorites: [],
        myPublishedGoods: [],
        myPublishedTotal: 0
    }),

    actions: {
        /**
         * 获取商品列表
         */
        async fetchGoodsList({pageNum = 1, pageSize = 10}: { pageNum?: number; pageSize?: number }) {
            this.loading = true;
            try {
                const res = await getGoodsList(pageNum, pageSize);
                if (res.data && res.data.code === 200) {
                    this.goodsList = res.data.data.list;
                    this.total = res.data.data.total;
                }
                return res.data;
            } catch (error) {
                console.error('获取商品列表失败:', error);
                throw error;
            } finally {
                this.loading = false;
            }
        },

        /**
         * 获取商品详情
         */
        async fetchGoodsDetail(id: number) {
            this.loading = true;
            try {
                const res = await getGoodsDetail(id);
                if (res.data && res.data.code === 200) {
                    this.currentGoods = res.data.data.goods;
                }
                return res.data;
            } catch (error) {
                console.error('获取商品详情失败:', error);
                throw error;
            } finally {
                this.loading = false;
            }
        },

        /**
         * 搜索商品
         */
        async searchGoods(keyword: string, pageNum: number = 1, pageSize: number = 10) {
            this.loading = true;
            try {
                const res = await searchGoods(keyword, pageNum, pageSize);
                if (res.data && res.data.code === 200) {
                    this.goodsList = res.data.data.list;
                    this.total = res.data.data.total;
                }
                return res.data;
            } catch (error) {
                console.error('搜索商品失败:', error);
                throw error;
            } finally {
                this.loading = false;
            }
        },

        /**
         * 获取收藏列表
         */
        async fetchFavorites() {
            try {
                const res = await getFavoriteList();
                this.favorites = Array.isArray(res.data.data?.favoritesList) ? res.data.data.favoritesList.map((item: any) => item.goodsId) : [];
                return {data: {code: 200}};
            } catch (error) {
                console.error('获取收藏列表失败:', error);
                throw error;
            }
        },

        /**
         * 添加收藏
         */
        async addFavorite(goodsId: number) {
            try {
                const res = await addFavorite(goodsId);
                if (res.data && res.data.code === 200) {
                    this.favorites.push(goodsId);
                }
                return res.data;
            } catch (error) {
                console.error('添加收藏失败:', error);
                throw error;
            }
        },

        /**
         * 取消收藏
         */
        async removeFavorite(goodsId: number) {
            try {
                const res = await removeFavorite(goodsId);
                if (res.data && res.data.code === 200) {
                    this.favorites = this.favorites.filter(id => id !== goodsId);
                }
                return res.data;
            } catch (error) {
                console.error('取消收藏失败:', error);
                throw error;
            }
        },

        /**
         * 获取我的发布商品列表
         */
        async fetchMyPublishedGoods({pageNum = 1, pageSize = 10}: { pageNum?: number; pageSize?: number }) {
            this.loading = true;
            try {
                const res = await getMyPublishedGoods(pageNum, pageSize);
                if (res.data && res.data.code === 200) {
                    this.myPublishedGoods = res.data.data.list;
                    this.myPublishedTotal = res.data.data.total;
                }
                return res.data;
            } catch (error) {
                console.error('获取我的发布商品列表失败:', error);
                throw error;
            } finally {
                this.loading = false;
            }
        },

        /**
         * 删除我的发布商品
         */
        async deleteMyPublishedGoods(goodsId: number) {
            this.loading = true;
            try {
                const res = await deleteGoods(goodsId);
                return res.data;
            } catch (error) {
                console.error('删除商品失败:', error);
                throw error;
            } finally {
                this.loading = false;
            }
        }
    }
});