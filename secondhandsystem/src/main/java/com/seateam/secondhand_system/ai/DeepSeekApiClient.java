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

    public String chat(String userMessage) {
        String url = deepSeekConfig.getEndpoint();
        System.out.println("调用DeepSeek接口URL: [" + url + "]");

        // 请求体封装
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", "deepseek-v3");
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", userMessage));
        payload.put("input", Map.of("messages", messages));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(deepSeekConfig.getApiKey()); // 使用 Bearer Token
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        // 解析返回数据
        // 解析返回数据
        Map<String, Object> output = (Map<String, Object>) response.getBody().get("output");
        List<Map<String, Object>> respMessages = (List<Map<String, Object>>) output.get("choices");
        Map<String, Object> firstChoice = respMessages.get(0);
        Map<String, String> messageMap = (Map<String, String>) firstChoice.get("message");
        return messageMap.get("content");


    }
}
