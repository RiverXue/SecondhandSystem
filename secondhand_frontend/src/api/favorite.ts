import request from './request';

/**
 * 添加收藏
 * @param goodsId 商品ID
 * @returns 操作结果
 */
export const addFavorite = (goodsId: number) => {
    return request.post('/favorite/add', null, {
        params: {goodsId}
    });
};

/**
 * 取消收藏
 * @param goodsId 商品ID
 * @returns 操作结果
 */
export const removeFavorite = (goodsId: number) => {
    return request.delete('/favorite/remove', {
        params: {goodsId}
    });
};

/**
 * 获取收藏列表
 * @returns 收藏列表
 */
export const getFavoriteList = () => {
    return request.get('/favorite/list');
};

