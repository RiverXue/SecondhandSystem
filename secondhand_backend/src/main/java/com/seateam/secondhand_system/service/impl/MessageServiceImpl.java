package com.seateam.secondhand_system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seateam.secondhand_system.common.Result;
import com.seateam.secondhand_system.common.ResultCode;
import com.seateam.secondhand_system.common.utils.JwtUtils;
import com.seateam.secondhand_system.entity.Goods;
import com.seateam.secondhand_system.entity.Message;
import com.seateam.secondhand_system.mapper.GoodsMapper;
import com.seateam.secondhand_system.mapper.MessageMapper;
import com.seateam.secondhand_system.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author HK
 * @description 针对表【message(留言表)】的数据库操作Service实现
 * @createDate 2025-06-17 10:50:27
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
        implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 发表留言
     */
    @Override
    public Result leaveMessage(Long goodsId, Long userId, String content) {
        // 验证参数
        if (goodsId == null || userId == null || content == null || content.trim().isEmpty()) {
            return Result.error(ResultCode.PARAM_ERROR, "商品ID、用户ID和留言内容不能为空");
        }

        // 验证商品是否存在
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            return Result.error(ResultCode.NOT_FOUND, "商品不存在");
        }

        // 创建留言记录
        Message message = new Message();
        message.setGoodsId(goodsId);
        message.setUserId(userId);
        message.setContent(content);
        message.setCreateTime(new Date());

        // 保存留言
        int rows = messageMapper.insert(message);
        return rows > 0 ? Result.success("留言成功") : Result.error("留言失败");
    }

    /**
     * 根据商品ID获取留言列表
     */
    @Override
    public Result getMessageListByGoodsId(Long goodsId) {
        // 验证参数
        if (goodsId == null) {
            return Result.error(ResultCode.PARAM_ERROR, "商品ID不能为空");
        }

        // 查询留言列表
        List<Message> messageList = messageMapper.selectByGoodsId(goodsId);
        return Result.success().put("messageList", messageList);
    }

    /**
     * 卖家回复留言
     */
    @Override
    public Result replyToMessage(Long messageId, String replyContent) {
        // 验证参数
        if (messageId == null || replyContent == null || replyContent.trim().isEmpty()) {
            return Result.error(ResultCode.PARAM_ERROR, "留言ID和回复内容不能为空");
        }

        // 获取当前登录用户ID
        Long currentUserId = jwtUtils.getCurrentUserId();

        // 查询留言信息
        Message message = messageMapper.selectById(messageId);
        if (message == null) {
            return Result.error(ResultCode.NOT_FOUND, "留言不存在");
        }

        // 查询商品信息
        Goods goods = goodsMapper.selectById(message.getGoodsId());
        if (goods == null) {
            return Result.error(ResultCode.NOT_FOUND, "商品不存在");
        }

        // 验证当前用户是否为商品卖家
        if (!goods.getUserId().equals(currentUserId)) {
            return Result.error(ResultCode.FORBIDDEN, "没有权限回复此留言");
        }

        // 检查是否已经回复过
        if (message.getReplyContent() != null && !message.getReplyContent().isEmpty()) {
            return Result.error(ResultCode.ERROR, "该留言已回复，无法重复回复");
        }

        // 更新留言回复
        int rows = messageMapper.updateReply(messageId, replyContent, new Date());
        return rows > 0 ? Result.success("回复成功") : Result.error("回复失败");
    }
}




