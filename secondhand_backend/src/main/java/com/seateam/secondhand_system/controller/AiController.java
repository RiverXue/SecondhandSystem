/**
 * AI控制器 - 处理AI对话相关的HTTP请求
 * 提供与AI智能助手交互的API接口，接收用户消息并返回AI响应
 */
package com.seateam.secondhand_system.controller;

import com.seateam.secondhand_system.common.Result;
import com.seateam.secondhand_system.entity.AiChatRequest;
import com.seateam.secondhand_system.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    /**
     * 接收用户对话请求，返回AI回复及推荐商品
     *
     * @param request 包含用户ID、会话ID和消息内容的请求体
     * @return Result对象，包含AI回复内容和推荐商品列表
     */
    @PostMapping("/chat")
    public Result chat(@RequestBody AiChatRequest request) {

        System.out.println("前端正在发送ai请求" + request);
        return aiService.chat(request);
    }
}
