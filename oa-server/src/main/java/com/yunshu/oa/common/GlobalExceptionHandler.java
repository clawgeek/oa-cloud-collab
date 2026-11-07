package com.yunshu.oa.common;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public R<?> biz(BizException e) { return R.fail(e.getCode(), e.getMessage()); }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public R<?> valid(BindException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .findFirst().map(f -> f.getField() + " " + f.getDefaultMessage())
                .orElse("参数校验失败");
        return R.fail(422, msg);
    }

    @ExceptionHandler(Exception.class)
    public R<?> other(Exception e, HttpServletRequest req) {
        log.error("请求异常 {} {}", req.getMethod(), req.getRequestURI(), e);
        return R.fail(500, "服务内部异常：" + e.getMessage());
    }
}
