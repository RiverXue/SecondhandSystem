package com.seateam.secondhand_system.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReplyMessageRequest {
    @NotBlank(message = "回复内容不能为空")
    private String content;
}