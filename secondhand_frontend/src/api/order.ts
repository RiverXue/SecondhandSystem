import request from "./request.ts";

/**
 * 创建订单
 * @param goodsId 商品ID
 * @returns 订单创建结果
 */
export const createOrder = (goodsId: number) => {
    return request.post('/order/create', null, {params: {goodsId}});
};

/**
 * 获取我的订单列表
 * @returns 订单列表
 */
export const getMyOrders = (pageNum: number = 1, pageSize: number = 10) => {
    return request.get('/order/my', {params: {pageNum, pageSize}});
};

/**
 * 支付订单
 * @param orderId 订单ID
 * @returns 支付结果
 */
export const payOrder = (orderId: number) => {
    return request.post(`/order/pay/${orderId}`);
};

/**
 * 取消订单
 * @param orderId 订单ID
 * @returns 取消结果
 */
export const cancelOrder = (orderId: number) => {
    return request.post(`/order/cancel/${orderId}`);
};

export const deleteOrder = (orderId: number) => {
    return request.delete(`/order/${orderId}`);
};

/**
 * 完成订单
 * @param orderId 订单ID
 * @returns 完成结果
 */
export const completeOrder = (orderId: number) => {
    return request.post(`/order/complete/${orderId}`);
};

/**
 * 获取卖家订单列表
 * @returns 订单列表
 */
export const getSellerOrders = () => {
    return request.get('/order/seller');
};

/**
 * 发货订单
 * @param orderId 订单ID
 * @returns 发货结果
 */
export const shipOrder = (orderId: number) => {
    return request.post(`/order/ship/${orderId}`);
};