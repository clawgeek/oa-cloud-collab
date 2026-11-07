package com.yunshu.oa.common;

import lombok.Getter;

/** 业务异常，由 GlobalExceptionHandler 统一转成 R.fail */
@Getter
public class BizException extends RuntimeException {
    private final int code;
    public BizException(String message) { super(message); this.code = 400; }
    public BizException(int code, String message) { super(message); this.code = code; }
}
