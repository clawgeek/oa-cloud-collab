package com.yunshu.oa.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yunshu.oa.common.R;
import java.lang.management.ManagementFactory;
import java.util.LinkedHashMap;
import java.util.Map;

/** 健康检查：容器编排与部署脚本的探针入口（无需鉴权） */
@RestController
@RequestMapping("/api")
public class HealthController {
    @GetMapping("/health")
    public R<Map<String, Object>> health() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("status", "UP");
        m.put("app", "yunshu-oa-server");
        m.put("version", "1.0.0");
        m.put("uptimeMs", ManagementFactory.getRuntimeMXBean().getUptime());
        m.put("java", System.getProperty("java.version"));
        return R.ok(m);
    }
}
