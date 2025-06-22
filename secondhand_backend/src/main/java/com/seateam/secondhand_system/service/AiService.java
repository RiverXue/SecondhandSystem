/**
 * AI服务接口 - 定义AI对话及商品推荐功能的规范
 * 负责处理用户与AI的交互逻辑，包括对话管理和商品推荐
 */
package com.seateam.secondhand_system.service;

import com.seateam.secondhand_system.common.Result;
import com.seateam.secondhand_system.entity.AiChatRequest;

public interface AiService {
    /**
     * 处理用户与AI的对话请求
     * @param request 对话请求对象，包含用户ID、会话ID和消息内容
     * @return Result对象，包含AI回复内容和推荐商品列表
     */
    Result chat(AiChatRequest request);
}