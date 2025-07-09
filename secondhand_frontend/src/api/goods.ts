import request from './request';

/**
 * 发布新商品
 * @param data 商品信息
 * @returns 发布结果
 */
export const publishGoods = (data: any) => {
    return request.post('/goods/publish', data);
};

/**
 * 获取商品分页列表
 * @param pageNum 页码
 * @param pageSize 每页条数
 * @returns 商品列表
 */
export const getGoodsList = (pageNum: number, pageSize: number) => {
    return request.get('/goods/list', {params: {pageNum, pageSize}});
};

/**
 * 获取商品详情
 * @param id 商品ID
 * @returns 商品详情
 */
export const getGoodsDetail = (id: number) => {
    return request.get(`/goods/detail/${id}`);
};

/**
 * 搜索商品
 * @param keyword 搜索关键词
 * @param pageNum 页码
 * @param pageSize 每页条数
 * @returns 搜索结果
 */
export const searchGoods = (keyword: string, pageNum: number, pageSize: number) => {
    return request.get('/goods/search', {params: {keyword, pageNum, pageSize}});
};


/**
 * 获取我的收藏列表
 * @returns 收藏列表
 */
export const getFavoriteList = async () => {
    try {
        const res = await request.get('/favorite/list');
        const data = res.data.data;
        return Array.isArray(data) ? data : [];
    } catch (error) {
        console.error('获取收藏列表失败:', error);
        return [];
    }
}

/**
 * 获取我的发布商品列表
 * @param pageNum 页码
 * @param pageSize 每页条数
 * @returns 商品列表
 */
export const getMyPublishedGoods = (pageNum: number, pageSize: number) => {
    return request.get('/goods/my-published', { params: { pageNum, pageSize } });
}

/**
 * 删除商品
 * @param id 商品ID
 * @returns 删除结果
 */
export const deleteGoods = (id: number) => {
    return request.delete(`/goods/${id}`);
};