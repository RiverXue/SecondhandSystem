import request from './request';
/**
 * 留言功能
 * @param {number} goodsId - 商品ID
 * @param {string} content - 留言内容
 * @returns {Promise<Object>} 留言结果
 */
export const leaveMessage = (goodsId: number, content: string) => {
    return request.post('/message/leave', null, {
        params: { goodsId, content }
    });
};

/**
 * 获取商品留言列表
 * @param {number} goodsId - 商品ID
 * @returns {Promise<Array<Object>>} 留言列表
 */
export const getMessageList = (goodsId: number, page: number = 1, pageSize: number = 10) => {
    return request.get(`/message/list/${goodsId}`, { params: { page, pageSize } });
};

/**
 * 回复留言
 * @param {number} messageId - 留言ID
 * @param {string} content - 回复内容
 * @returns {Promise<Object>} 回复结果
 */
export const replyToMessage = (messageId: number, content: string) => {
    return request.post(`/message/reply/${messageId}`, { content });
};