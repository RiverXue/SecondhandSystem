/**
 * DeepSeek API客户端 - 负责与DeepSeek AI服务进行交互
 * 提供单轮和多轮对话功能，封装API请求构建、发送和响应解析逻辑
 */
package com.seateam.secondhand_system.ai;

import com.seateam.secondhand_system.common.config.DeepSeekConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DeepSeekApiClient {

    private final DeepSeekConfig deepSeekConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 支持多轮对话的AI调用方法
     *
     * @param messages 消息列表，包含多轮对话历史，每个消息包含role和content字段
     * @return AI生成的回复内容字符串
     */
    public String chat(List<Map<String, String>> messages) {
        // 准备 URL 和日志打印
        String url = deepSeekConfig.getEndpoint();
        System.out.println("调用DeepSeek接口URL: [" + url + "]");

        // 构造请求体 - 构建符合DeepSeek API要求的请求参数
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", "deepseek-v3");
        payload.put("input", Map.of("messages", messages));
        // 设置请求头 headers
        HttpHeaders headers = new HttpHeaders();  // 创建HttpHeaders对象
        headers.setContentType(MediaType.APPLICATION_JSON);  // 设置请求体类型为JSON
        headers.setBearerAuth(deepSeekConfig.getApiKey()); // 使用Bearer Token认证
        /*发送 HTTP 请求
          用 RestTemplate 发 POST 请求，把刚才构造的请求体和请求头发给 DeepSeek。
          结果返回的是 Map 类型，包含了 AI 的完整响应。
         */
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);  // 使用HttpEntity封装请求体
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);  // 发送POST请求
        System.out.println("DeepSeek API响应: " + response.getBody());  // 打印API响应

        // 解析返回数据 - 从API响应中提取AI生成的回复内容
        Map<String, Object> output = (Map<String, Object>) response.getBody().get("output");  // 从响应中取出 output 字段
        List<Map<String, Object>> respMessages = (List<Map<String, Object>>) output.get("choices");  // 提取choices字段
        Map<String, Object> firstChoice = respMessages.get(0);  // 拿数组中第一个 message
        Map<String, String> messageMap = (Map<String, String>) firstChoice.get("message");  // 取出 content 字段，作为 AI 的回复文本。
        return messageMap.get("content");  // 返回content字段
    }

    /**
     * 单轮对话的AI调用方法（兼容旧接口）
     *
     * @param userMessage 用户输入的单条消息内容
     * @return AI生成的回复内容字符串
     */
    public String chat(String userMessage) {
        List<Map<String, String>> messages = new ArrayList<>();  // 初始化消息列表
        messages.add(Map.of("role", "user", "content", userMessage));  // 添加用户消息
        return chat(messages);  // 调用多轮对话方法
    }
}
