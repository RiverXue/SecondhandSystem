package com.seateam.secondhand_system.ai.impl;

import com.seateam.secondhand_system.ai.DeepSeekService;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class DeepSeekServiceImpl implements DeepSeekService {

    @Resource
    private OpenAiChatModel chatModel;

    @Override
    public String chat(String message) {
        return chatModel.call(message);
    }

    @Override
    public Flux<String> chatFlux(String message) {
        UserMessage um = new UserMessage(message);
        return chatModel.stream(um);
    }
}
