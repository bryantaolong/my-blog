package com.example.myblog.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class BaseResponse<T> implements Serializable {

    private int code;
    private String msg;
    private T data;

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(200, "success", data);
    }

    public static <T> BaseResponse<T> success(String msg, T data) {
        return new BaseResponse<>(200, msg, data);
    }

    public static <T> BaseResponse<T> fail(String msg) {
        return new BaseResponse<>(400, msg, null);
    }

    public static <T> BaseResponse<T> fail(int code, String msg) {
        return new BaseResponse<>(code, msg, null);
    }
}