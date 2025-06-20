package com.seateam.secondhand_system.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * AI对话请求实体
 */
@Data
public class AiChatRequest {
    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 对话会话ID（多轮对话标识）
     */
    @NotBlank(message = "会话ID不能为空")
    private String sessionId;

    /**
     * 用户输入内容
     */
    @NotBlank(message = "消息内容不能为空")
    private String message;
}