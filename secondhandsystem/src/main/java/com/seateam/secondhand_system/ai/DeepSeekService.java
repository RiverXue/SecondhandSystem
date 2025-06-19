// ai/DeepSeekService.java
package com.seateam.secondhand_system.ai;

import reactor.core.publisher.Flux;

public interface DeepSeekService {
    String chat(String message);

    Flux<String> chatFlux(String message);
}
