package com.acore.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一响应体
 *
 * <p>所有 Controller 接口统一返回此结构。</p>
 *
 * @param <T> 数据类型
 * @author acore
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 状态码 */
    private int code;

    /** 提示信息 */
    private String message;

    /** 数据 */
    private T data;

    // ==================== 成功 ====================

    public static <T> Result<T> success() {
        return Result.<T>builder()
                .code(200)
                .message("操作成功")
                .build();
    }

    public static <T> Result<T> success(T data) {
        return Result.<T>builder()
                .code(200)
                .message("操作成功")
                .data(data)
                .build();
    }

    public static <T> Result<T> success(String message, T data) {
        return Result.<T>builder()
                .code(200)
                .message(message)
                .data(data)
                .build();
    }

    // ==================== 失败 ====================

    public static <T> Result<T> error(int code, String message) {
        return Result.<T>builder()
                .code(code)
                .message(message)
                .build();
    }

    public static <T> Result<T> error(String message) {
        return Result.<T>builder()
                .code(500)
                .message(message)
                .build();
    }

    // ==================== 快捷方法 ====================

    public static <T> Result<T> unauthorized(String message) {
        return Result.<T>builder()
                .code(401)
                .message(message)
                .build();
    }

    public static <T> Result<T> forbidden(String message) {
        return Result.<T>builder()
                .code(403)
                .message(message)
                .build();
    }

    public static <T> Result<T> notFound(String message) {
        return Result.<T>builder()
                .code(404)
                .message(message)
                .build();
    }

    public static <T> Result<T> badRequest(String message) {
        return Result.<T>builder()
                .code(400)
                .message(message)
                .build();
    }
}