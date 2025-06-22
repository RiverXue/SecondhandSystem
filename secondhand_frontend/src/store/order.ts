import {defineStore} from 'pinia';
import {ElMessage} from 'element-plus';
import router from '../router';
import {createOrder, getMyOrders} from '../api/order';

interface OrderState {
    orders: any[];
    loading: boolean;
}

export const useOrderStore = defineStore('order', {
    state: (): OrderState => ({
        orders: [],
        loading: false
    }),

    actions: {
        /**
         * 创建订单
         */
        async createOrder(goodsId: number) {
            this.loading = true;
            try {
                const res = await createOrder(goodsId);
                if (res.data.code === 200) {
                    // 创建成功后刷新订单列表
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

        /**
         * 获取我的订单列表
         */
        async getMyOrders() {
            this.loading = true;
            try {
                const res = await getMyOrders(1, 10);
                if (res.data.code === 200) {
                    // 确保orders是数组
                    this.orders = Array.isArray(res.data.data.orderList) ? res.data.data.orderList : [];
                    // 验证订单数据结构
                    this.orders = this.orders.map(order => ({
                        ...order,
                        // 确保goodsId存在
                        goodsId: order.goodsId || null,
                        // 确保price和totalPrice是数字
                        price: typeof order.price === 'number' ? order.price : 0,
                        totalPrice: typeof order.totalPrice === 'number' ? order.totalPrice : 0
                    }));
                } else if (res.data.code === 401) {
                    // 处理未授权错误
                    ElMessage.error('登录已过期，请重新登录');
                    localStorage.removeItem('accessToken');
                    this.orders = [];
                    // 跳转到登录页
                    router.push('/login');
                } else {
                    ElMessage.error(res.data.message || '获取订单失败');
                    this.orders = [];
                }
                return res.data;
            } catch (error) {
                console.error('获取订单列表失败:', error);
                ElMessage.error('网络错误，无法获取订单列表');
                this.orders = [];
                throw error;
            } finally {
                this.loading = false;
            }
        }
    }
});