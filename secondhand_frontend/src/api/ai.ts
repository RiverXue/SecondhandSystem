import request from './request';

/**
 * 发送AI聊天消息
 * @param data 聊天请求数据
 * @returns 聊天响应
 */
export const sendAiChatMessage = (data: {
    sessionId?: string;
    message: string;
    userId: number;
}) => {
    return request.post('/ai/chat', data);
};

/**
 * 清除本地AI聊天历史（无后端接口）
 */
export const clearAiChatHistory = () => {
    return new Promise((resolve) => {
        resolve({data: {code: 200, message: '聊天历史已清除'}});
    });
};

/**
 * 获取本地AI聊天历史（无后端接口）
 */
export const getAiChatHistory = () => {
    return new Promise((resolve) => {
        resolve({data: []});
    });
};