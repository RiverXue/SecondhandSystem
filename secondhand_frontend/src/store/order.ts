import {defineStore} from 'pinia';
import {ElMessage} from 'element-plus';
import router from '../router';
import {createOrder, getMyOrders} from '../api/order';
import {getGoodsDetail} from '../api/goods';

interface OrderState {
    orders: any[]; // 建议后续替换为具体 Order 类型
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
         * 获取我的订单列表 + 补全商品详情
         */
        async getMyOrders() {
            this.loading = true;
            try {
                const res = await getMyOrders(1, 10); // 这里的 1 和 10 建议抽成参数
                if (res.data.code === 200) {
                    // 确保订单列表是数组，避免非数组类型导致后续遍历报错
                    const rawOrders = Array.isArray(res.data.data.orderList)
                        ? res.data.data.orderList
                        : [];

                    // 并行请求商品详情，优化性能
                    const updatedOrders = await Promise.all(
                        rawOrders.map(async (order) => {
                            try {
                                // 补全商品信息：通过 goodsId 查详情
                                const goodsRes = await getGoodsDetail(order.goodsId);
                                if (goodsRes.data.code === 200) {
                                    const goods = goodsRes.data.data.goods;
                                    return {
                                        ...order,
                                        goodsId: order.goodsId || null, // 兜底空值
                                        price: typeof order.price === 'number' ? order.price : 0,
                                        title: goods.title || '未知商品',
                                        image: goods.image || '',
                                        totalPrice: typeof order.totalPrice === 'number' ? order.totalPrice : 0
                                    };
                                }
                                // 商品详情接口异常，返回基础订单信息
                                return {
                                    ...order,
                                    goodsId: order.goodsId || null,
                                    price: typeof order.price === 'number' ? order.price : 0,
                                    title: '未知商品',
                                    image: '',
                                    totalPrice: typeof order.totalPrice === 'number' ? order.totalPrice : 0
                                };
                            } catch (error) {
                                // 捕获单个商品请求异常，不阻断整个流程
                                console.error(`商品 ${order.goodsId} 详情获取失败`, error);
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

                    // 更新订单列表
                    this.orders = updatedOrders;
                } else if (res.data.code === 401) {
                    // 登录态失效处理
                    ElMessage.error('登录已过期，请重新登录');
                    localStorage.removeItem('accessToken');
                    this.orders = [];
                    router.push('/login');
                } else {
                    // 其他业务错误
                    ElMessage.error(res.data.message || '获取订单失败');
                    this.orders = [];
                }
                return res.data;
            } catch (error) {
                // 网络等全局异常
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