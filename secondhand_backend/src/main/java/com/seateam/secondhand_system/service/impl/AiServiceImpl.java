package com.seateam.secondhand_system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seateam.secondhand_system.ai.DeepSeekApiClient;
import com.seateam.secondhand_system.common.Result;
import com.seateam.secondhand_system.entity.AiChat;
import com.seateam.secondhand_system.entity.AiChatRequest;
import com.seateam.secondhand_system.entity.Goods;
import com.seateam.secondhand_system.mapper.AiChatMapper;
import com.seateam.secondhand_system.service.AiService;
import com.seateam.secondhand_system.service.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * AI服务实现类 - 处理用户与AI的对话及商品推荐逻辑
 * 编码逻辑概述：
 * 1. 接收用户对话请求，包含用户ID、会话ID和消息内容
 * 2. 查询该会话的历史对话记录，构建多轮对话上下文
 * 3. 调用商品搜索服务，根据用户消息关键词搜索相关商品
 * 4. 若搜索到商品，直接返回商品推荐结果，不调用AI接口
 * 5. 若未搜索到商品，调用DeepSeek API获取AI回复
 * 6. 保存新的对话记录到数据库
 * 7. 返回AI回复和推荐商品列表（若有）
 */
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final DeepSeekApiClient deepSeekApiClient;
    private final AiChatMapper aiChatMapper;
    private final GoodsService goodsService;

    /**
     * 处理用户与AI的对话请求，返回AI回复及推荐商品
     *
     * @param request 包含用户ID、会话ID和消息内容的请求对象
     * @return 包含回复内容和推荐商品的Result对象
     */
    @Override
    public Result chat(AiChatRequest request) {
        // 获取历史对话（上下文）
        // 1. 查询历史对话 - 使用会话ID从数据库获取之前的对话记录，按时间排序
        List<AiChat> historyChats = aiChatMapper.selectList(
                new QueryWrapper<AiChat>()  // 使用QueryWrapper构建查询条件
                        .eq("session_id", request.getSessionId())  // 过滤条件：会话ID匹配
                        .orderByAsc("create_time")  // 按创建时间升序排序
        );

        // 2. 构建消息列表，构造上下文格式
        List<Map<String, String>> messages = new ArrayList<>();  // 用于存储多轮对话上下文
        for (AiChat chat : historyChats) {  // 遍历历史对话记录
            messages.add(Map.of("role", "user", "content", chat.getMessage()));  // 将用户消息加入上下文
            messages.add(Map.of("role", "assistant", "content", chat.getResponse()));  // 将AI回复加入上下文
        }
        // 当前用户消息
        messages.add(Map.of("role", "user", "content", request.getMessage()));  // 将当前用户消息加入上下文

        // 3. 先搜索商品
        Result goodsResult = goodsService.searchGoods(request.getMessage(), 1, 10);  // 搜索前10条相关商品
        System.out.println("商品搜索结果: " + goodsResult);  // 打印搜索结果
        Map<String, Object> dataMap = goodsResult.getData();  // 提取搜索结果数据
        List<Goods> recommendedGoods = new ArrayList<>();  // 用于存储推荐商品列表
        if (dataMap != null && dataMap.get("list") != null) {  // 检查搜索结果是否存在商品列表
            recommendedGoods = (List<Goods>) dataMap.get("list");  // 提取商品列表
        }

//        // 4. 判断是否有商品
//        if (recommendedGoods != null && !recommendedGoods.isEmpty()) {  // 检查是否有推荐商品
//            // 搜索到商品，直接返回，不调用AI
//
//            return Result.success()  // 返回成功结果
//                    .put("reply", "为您推荐以下商品：") // 自定义提示语
//                    .put("recommendedGoods", recommendedGoods)  // 返回推荐商品列表
//                    .put("sessionId", request.getSessionId());
//
//        }
//
//        // 5. 若无商品，再调用AI
//        String aiResponse = deepSeekApiClient.chat(messages);  // 调用DeepSeek API获取AI回复
//
//        // 6. 保存对话记录
//        AiChat newChat = new AiChat();  // 创建新的对话记录对象
//        newChat.setUserId(request.getUserId());  // 设置用户ID
//        newChat.setSessionId(request.getSessionId());  // 设置会话ID
//        newChat.setMessage(request.getMessage());  // 设置用户消息
//        newChat.setResponse(aiResponse);  // 设置AI回复
//        newChat.setCreateTime(new Date());  // 设置创建时间
//        aiChatMapper.insert(newChat);  // 插入新的对话记录到数据库
//
//        // 7. 返回AI回复，无推荐商品
//        System.out.println("AI回复内容: " + aiResponse);
//        return Result.success()  // 返回成功结果
//                .put("reply", aiResponse)  // 返回AI回复
//                .put("recommendedGoods", recommendedGoods) // 返回推荐商品列表
//                .put("sessionId", request.getSessionId());
//    }
        // 4. 构建统一响应数据结构
        Map<String, Object> responseData = new java.util.HashMap<>();
        responseData.put("sessionId", request.getSessionId());

        // 5. 如果有商品推荐，优先返回推荐商品
        if (!recommendedGoods.isEmpty()) {
            responseData.put("reply", "\uD83E\uDD16 根据您的需求，为您推荐以下商品：");
            responseData.put("list", recommendedGoods);
            return Result.success().data(responseData);
        }

        // 6. 调用AI获取回复
        String aiResponse = deepSeekApiClient.chat(messages);
        responseData.put("reply", aiResponse);
        responseData.put("list", recommendedGoods); // 为空数组

        // 7. 保存对话记录
        AiChat newChat = new AiChat();
        newChat.setUserId(request.getUserId());
        newChat.setSessionId(request.getSessionId());
        newChat.setMessage(request.getMessage());
        newChat.setResponse(aiResponse);
        newChat.setCreateTime(new Date());
        aiChatMapper.insert(newChat);

        System.out.println("AI回复内容: " + aiResponse);
        return Result.success().data(responseData);
    }

}
