package com.yunshu.oa.security;

import java.lang.annotation.*;

/** 按钮/接口级权限注解，配合 JwtInterceptor 做 RBAC 校验 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePerm {
    String value();
}
