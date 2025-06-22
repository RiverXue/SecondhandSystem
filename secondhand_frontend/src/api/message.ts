import request from './request';

export const leaveMessage = (goodsId: number, content: string) => {
    return request.post('/message/leave', null, {
        params: { goodsId, content }
    });
};

export const getMessageList = (goodsId: number, page: number = 1, pageSize: number = 10) => {
    return request.get(`/message/list/${goodsId}`, { params: { page, pageSize } });
};