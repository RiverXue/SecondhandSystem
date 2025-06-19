package com.seateam.secondhand_system.controller;

import com.seateam.secondhand_system.ai.DeepSeekService;
import com.seateam.secondhand_system.common.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Resource
    private DeepSeekService deepSeekService;

    @PostMapping("/chat")
    public Result chat(@RequestParam String message) {
        String reply = deepSeekService.chat(message);
        return Result.success().put("reply", reply);
    }

    @GetMapping("/chat/stream")
    public Object chatStream(@RequestParam String message) {
        return deepSeekService.chatFlux(message);
    }
}
