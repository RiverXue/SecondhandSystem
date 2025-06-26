package com.seateam.secondhand_system.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Result {
    private Integer code; // 状态码，使用ResultCode枚举管理
    private String message; // 提示信息
    private Map<String, Object> data = new HashMap<>(); // 返回数据

    // 私有构造方法
    private Result() {
    }

    // 成功静态方法
    public static Result success() {
        Result result = new Result();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        return result;
    }

    public static Result success(String message) {
        Result result = new Result();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(message);
        return result;
    }

    // 失败静态方法
    public static Result error() {
        Result result = new Result();
        result.setCode(ResultCode.ERROR.getCode());
        result.setMessage(ResultCode.ERROR.getMessage());
        return result;
    }

    public static Result error(String message) {
        Result result = new Result();
        result.setCode(ResultCode.ERROR.getCode());
        result.setMessage(message);
        return result;
    }

    /**
     * 根据ResultCode返回错误结果
     */
    public static Result error(ResultCode resultCode) {
        Result result = new Result();
        result.setCode(resultCode.getCode());
        result.setMessage(resultCode.getMessage());
        return result;
    }

    /**
     * 根据ResultCode返回自定义消息的错误结果
     */
    public static Result error(ResultCode resultCode, String message) {
        Result result = new Result();
        result.setCode(resultCode.getCode());
        result.setMessage(message);
        return result;
    }

    // 添加数据
    public Result put(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    // 新增：链式设置 data 的方法
    public Result data(Object value) {
        this.data.put("data", value);
        return this;
    }

}