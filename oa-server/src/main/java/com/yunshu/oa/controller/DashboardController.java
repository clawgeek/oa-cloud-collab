package com.yunshu.oa.controller;

import com.yunshu.oa.common.R;
import com.yunshu.oa.common.UserContext;
import com.yunshu.oa.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService service;
    @GetMapping("/overview")
    public R<Map<String, Object>> overview() { return R.ok(service.overview(UserContext.userId())); }
}
