package com.seateam.secondhand_system.controller;

import com.seateam.secondhand_system.common.Result;
import com.seateam.secondhand_system.service.MessageService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 留言模块控制器
 */
@RestController
@RequestMapping("/api/message")
@Validated
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 发表留言
     */
    @PostMapping("/leave")
    public Result leaveMessage(
            @RequestParam(name = "goodsId") @NotNull(message = "商品ID不能为空") Long goodsId,
            @RequestParam(name = "content") @NotBlank(message = "留言内容不能为空") String content) {
        System.out.println("前端传来的数据：" + goodsId + "，" + content + "，正在发表留言");
        Long userId = getCurrentUserId();
        return messageService.leaveMessage(goodsId, userId, content);
    }

    /**
     * 获取某商品留言列表
     */
    @GetMapping("/list/{goodsId}")
    public Result getMessageList(@PathVariable(name = "goodsId") @NotNull(message = "商品ID不能为空") Long goodsId) {
        System.out.println(goodsId + "正在获取留言列表");
        return messageService.getMessageListByGoodsId(goodsId);
    }

    /**
     * 卖家回复留言
     */
    @PostMapping("/reply")
    public Result replyToMessage(
            @RequestParam(name = "messageId") @NotNull(message = "留言ID不能为空") Long messageId,
            @RequestParam(name = "replyContent") @NotBlank(message = "回复内容不能为空") String replyContent) {
        System.out.println(messageId + "和" + replyContent + "正在回复留言");
        return messageService.replyToMessage(messageId, replyContent);
    }

    /**
     * 从安全上下文获取当前用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null) {
            throw new RuntimeException("用户未登录");
        }
        try {
            return Long.valueOf(authentication.getName());
        } catch (NumberFormatException e) {
            throw new RuntimeException("无效的用户ID");
        }
    }
}