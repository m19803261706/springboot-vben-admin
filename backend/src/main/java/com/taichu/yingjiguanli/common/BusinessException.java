package com.taichu.yingjiguanli.common;

import lombok.Getter;

/**
 * 业务异常
 *
 * @author CX
 * @since 2026-01-13
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;

    /**
     * 构造函数（默认错误码 500）
     *
     * @param message 错误消息
     */
    public BusinessException(String message) {
        this(500, message);
    }

    /**
     * 构造函数（自定义错误码）
     *
     * @param code 错误码
     * @param message 错误消息
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 构造函数（带原因）
     *
     * @param code 错误码
     * @param message 错误消息
     * @param cause 原因
     */
    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
