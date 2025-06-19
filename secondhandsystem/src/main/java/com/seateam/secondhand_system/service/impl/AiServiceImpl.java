package com.seateam.secondhand_system.service.impl;

import com.seateam.secondhand_system.ai.DeepSeekApiClient;
import com.seateam.secondhand_system.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final DeepSeekApiClient deepSeekApiClient;

    @Override
    public String chat(String userMessage) {
        return deepSeekApiClient.chat(userMessage);
    }
}
