package com.seateam.secondhand_system.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Result {
    private Integer code; // 状态码：200成功，400失败
    private String message; // 提示信息
    private Map<String, Object> data = new HashMap<>(); // 返回数据

    // 私有构造方法
    private Result() {}

    // 成功静态方法
    public static Result success() {
        Result result = new Result();
        result.setCode(200);
        result.setMessage("成功");
        return result;
    }

    public static Result success(String message) {
        Result result = new Result();
        result.setCode(200);
        result.setMessage(message);
        return result;
    }

    // 失败静态方法
    public static Result error() {
        Result result = new Result();
        result.setCode(400);
        result.setMessage("失败");
        return result;
    }

    public static Result error(String message) {
        Result result = new Result();
        result.setCode(400);
        result.setMessage(message);
        return result;
    }

    // 添加数据
    public Result put(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}