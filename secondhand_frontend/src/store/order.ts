import {defineStore} from 'pinia';
import {ElMessage} from 'element-plus';
import router from '../router';
import {
    cancelOrder,
    completeOrder,
    createOrder,
    deleteOrder,
    getMyOrders,
    getSellerOrders,
    payOrder,
    shipOrder
} from '../api/order';
import {getGoodsDetail} from '../api/goods';

interface OrderState {
    buyerOrders: any[];
    sellerOrders: any[];
    loading: boolean;
}

export const useOrderStore = defineStore('order', {
    state: (): OrderState => ({
        buyerOrders: [],
        sellerOrders: [],
        loading: false
    }),

    actions: {
        async createOrder(goodsId: number) {
            this.loading = true;
            try {
                const res = await createOrder(goodsId);
                if (res.data.code === 200) {
                    await this.getMyOrders();
                }
                return res.data;
            } catch (error) {
                console.error('创建订单失败:', error);
                throw error;
            } finally {
                this.loading = false;
            }
        },

        async payOrder(orderId: number) {
            return this._handleOrderAction(() => payOrder(orderId), '订单支付成功', '订单支付失败');
        },

        async cancelOrder(orderId: number) {
            return this._handleOrderAction(() => cancelOrder(orderId), '订单取消成功', '订单取消失败');
        },

        async deleteOrder(orderId: number) {
            return this._handleOrderAction(() => deleteOrder(orderId), '订单删除成功', '订单删除失败');
        },

        async completeOrder(orderId: number) {
            return this._handleOrderAction(() => completeOrder(orderId), '订单已确认完成', '确认订单失败');
        },

        async shipOrder(orderId: number) {
            return this._handleOrderAction(() => shipOrder(orderId), '订单发货成功', '订单发货失败');
        },

        async getMyOrders(page = 1, pageSize = 10) {
            this.loading = true;
            try {
                const res = await getMyOrders(page, pageSize);
                if (res.data.code === 200) {
                    this.buyerOrders = await this._enrichOrders(res.data.data.orderList);
                } else if (res.data.code === 401) {
                    this._handleUnauthorized();
                    this.buyerOrders = [];
                } else {
                    ElMessage.error(res.data.message || '获取订单失败');
                    this.buyerOrders = [];
                }
            } catch (error) {
                console.error('获取订单失败:', error);
                ElMessage.error('获取订单失败，请重试');
                this.buyerOrders = [];
            } finally {
                this.loading = false;
            }
        },

        async getSellerOrders() {
            this.loading = true;
            try {
                const res = await getSellerOrders();
                if (res.data.code === 200) {
                    this.sellerOrders = await this._enrichOrders(res.data.data.orderList);
                } else if (res.data.code === 401) {
                    this._handleUnauthorized();
                    this.sellerOrders = [];
                } else {
                    ElMessage.error(res.data.message || '获取卖家订单失败');
                    this.sellerOrders = [];
                }
            } catch (error) {
                console.error('获取卖家订单失败:', error);
                ElMessage.error('获取卖家订单失败，请重试');
                this.sellerOrders = [];
            } finally {
                this.loading = false;
            }
        },

        /** 公共订单处理逻辑 */
        async _handleOrderAction(actionFn: () => Promise<any>, successMsg: string, errorMsg: string) {
            this.loading = true;
            try {
                const res = await actionFn();
                if (res.data.code === 200) {
                    ElMessage.success(successMsg);
                    await this.getMyOrders();
                } else {
                    ElMessage.error(res.data.message || errorMsg);
                }
                return res.data;
            } catch (error) {
                console.error(errorMsg, error);
                ElMessage.error(errorMsg);
                throw error;
            } finally {
                this.loading = false;
            }
        },

        /** 统一商品详情补全 */
        async _enrichOrders(orderList: any[] = []) {
            return Promise.all(
                orderList.map(async (order) => {
                    try {
                        const goodsRes = await getGoodsDetail(order.goodsId);
                        const goods = goodsRes.data.code === 200 ? goodsRes.data.data.goods : {};
                        return {
                            ...order,
                            goodsId: order.goodsId || null,
                            price: typeof order.price === 'number' ? order.price : 0,
                            title: goods.title || '未知商品',
                            image: goods.image || '',
                            totalPrice: typeof order.totalPrice === 'number' ? order.totalPrice : 0
                        };
                    } catch (error) {
                        console.error(`获取商品 ${order.goodsId} 详情失败`, error);
                        return {
                            ...order,
                            goodsId: order.goodsId || null,
                            price: typeof order.price === 'number' ? order.price : 0,
                            title: '未知商品',
                            image: '',
                            totalPrice: typeof order.totalPrice === 'number' ? order.totalPrice : 0
                        };
                    }
                })
            );
        },

        /** 登录失效统一处理 */
        _handleUnauthorized() {
            ElMessage.error('登录已过期，请重新登录');
            localStorage.removeItem('accessToken');
            router.push('/login');
        }
    }
});
