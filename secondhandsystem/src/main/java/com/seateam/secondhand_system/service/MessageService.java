package com.seateam.secondhand_system.service;

import com.seateam.secondhand_system.entity.Message;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author HK
* @description 针对表【message(留言表)】的数据库操作Service
* @createDate 2025-06-17 10:50:27
*/
import com.seateam.secondhand_system.common.Result;

public interface MessageService extends IService<Message> {
    /**
     * 发表留言
     * @param goodsId 商品ID
     * @param userId 用户ID
     * @param content 留言内容
     * @return 操作结果
     */
    Result leaveMessage(Long goodsId, Long userId, String content);

    /**
     * 根据商品ID获取留言列表
     * @param goodsId 商品ID
     * @return 留言列表
     */
    Result getMessageListByGoodsId(Long goodsId);

    /**
     * 卖家回复留言
     * @param messageId 留言ID
     * @param replyContent 回复内容
     * @return 操作结果
     */
    Result replyToMessage(Long messageId, String replyContent);
}
