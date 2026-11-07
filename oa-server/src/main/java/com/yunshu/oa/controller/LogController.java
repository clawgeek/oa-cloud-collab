package com.yunshu.oa.controller;

import com.yunshu.oa.common.PageResult;
import com.yunshu.oa.common.R;
import com.yunshu.oa.entity.OperLog;
import com.yunshu.oa.security.RequirePerm;
import com.yunshu.oa.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {
    private final LogService service;
    @GetMapping
    @RequirePerm("log:view")
    public R<PageResult<OperLog>> page(@RequestParam(required = false) String kw,
                                       @RequestParam(required = false) String module,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        return R.ok(service.page(kw, module, page, size));
    }
}
