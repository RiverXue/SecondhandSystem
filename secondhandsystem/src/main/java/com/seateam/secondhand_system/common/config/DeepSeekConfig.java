package com.seateam.secondhand_system.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.ai.deepseek")
@Data
public class DeepSeekConfig {
    private String apiKey;
    private String endpoint;
}
