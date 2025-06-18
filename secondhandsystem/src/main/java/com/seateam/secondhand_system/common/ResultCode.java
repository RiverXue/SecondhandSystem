package com.seateam.secondhand_system.common;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 统一响应状态码枚举
 */
public enum ResultCode {
    // 成功状态码
    SUCCESS(200, "操作成功"),
    // 客户端错误
    ERROR(400, "操作失败"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权访问"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    // 服务器错误
    SERVER_ERROR(500, "服务器内部错误");

    private final int code;
    @Getter
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * JSON序列化时返回状态码数值
     */
    @JsonValue
    public int getCode() {
        return code;
    }

}