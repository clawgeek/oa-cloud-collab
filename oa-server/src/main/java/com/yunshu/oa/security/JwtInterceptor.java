package com.yunshu.oa.security;

import com.yunshu.oa.common.BizException;
import com.yunshu.oa.common.LoginUser;
import com.yunshu.oa.common.UserContext;
import com.yunshu.oa.mapper.AuthMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {
    private final JwtUtil jwt;
    private final AuthMapper authMapper;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) {
        UserContext.clear();
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) return true;
        if (!(handler instanceof HandlerMethod hm)) return true;
        String token = req.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) token = token.substring(7);
        Map<String, Object> claims = token == null ? null : jwt.verify(token);
        if (claims == null) throw new BizException(401, "登录已失效，请重新登录");

        LoginUser u = new LoginUser();
        u.setUserId(((Number) claims.get("uid")).longValue());
        u.setUsername(String.valueOf(claims.get("username")));
        u.setRealName(String.valueOf(claims.get("realName")));
        if (claims.get("deptId") instanceof Number n) u.setDeptId(n.longValue());
        UserContext.set(u);

        RequirePerm ann = hm.getMethodAnnotation(RequirePerm.class);
        if (ann == null) ann = hm.getBeanType().getAnnotation(RequirePerm.class);
        if (ann != null) {
            List<String> perms = authMapper.listPermCodes(u.getUserId());
            if (!perms.contains("*:*") && !perms.contains(ann.value())) {
                throw new BizException(403, "无权限执行该操作：" + ann.value());
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse resp, Object h, Exception e) {
        UserContext.clear();
    }
}
