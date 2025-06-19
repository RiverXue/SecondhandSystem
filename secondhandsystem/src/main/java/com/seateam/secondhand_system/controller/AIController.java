package com.seateam.secondhand_system.controller;

import com.seateam.secondhand_system.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/chat")
    public String chat(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        return aiService.chat(userMessage);
    }
}
