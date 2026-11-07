package com.yunshu.oa.config;

import com.yunshu.oa.common.UserContext;
import com.yunshu.oa.mapper.LogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/** 操作审计切面：所有写操作自动落 sys_oper_log */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {
    private final LogMapper logMapper;
    private final HttpServletRequest request;

    @Around("execution(* com.yunshu.oa.controller..*.*(..)) && "
          + "(@annotation(org.springframework.web.bind.annotation.PostMapping) || "
          + "@annotation(org.springframework.web.bind.annotation.PutMapping) || "
          + "@annotation(org.springframework.web.bind.annotation.DeleteMapping))")
    public Object around(ProceedingJoinPoint p) throws Throwable {
        long t = System.currentTimeMillis();
        boolean ok = true;
        try { return p.proceed(); }
        catch (Throwable e) { ok = false; throw e; }
        finally {
            try {
                Map<String, Object> m = new HashMap<>();
                m.put("userId", UserContext.userId());
                m.put("userName", UserContext.name());
                m.put("module", p.getSignature().getDeclaringType().getSimpleName().replace("Controller", ""));
                m.put("operation", p.getSignature().getName());
                m.put("method", p.getSignature().getDeclaringTypeName() + "." + p.getSignature().getName());
                m.put("requestUri", request.getRequestURI());
                m.put("ip", request.getRemoteAddr());
                m.put("costMs", System.currentTimeMillis() - t);
                m.put("success", ok ? 1 : 0);
                logMapper.insert(m);
            } catch (Exception e) { log.warn("审计日志写入失败：{}", e.getMessage()); }
        }
    }
}
