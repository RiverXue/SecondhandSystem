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