package com.yunshu.oa.common;

import lombok.Data;

/** 当前登录人（ThreadLocal 上下文），由 JwtInterceptor 填充 */
@Data
public class LoginUser {
    private Long userId;
    private String username;
    private String realName;
    private Long deptId;
}
