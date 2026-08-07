package com.acore.shared.exception;

import lombok.Getter;

/**
 * 业务异常
 *
 * <p>在 Service 层抛出，由全局异常处理器统一捕获并返回 {@link com.acore.shared.dto.Result}。</p>
 *
 * @author acore
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = errorCode.getCode();
    }
}