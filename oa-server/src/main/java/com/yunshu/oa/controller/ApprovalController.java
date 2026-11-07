package com.yunshu.oa.controller;

import com.yunshu.oa.common.PageResult;
import com.yunshu.oa.common.R;
import com.yunshu.oa.common.UserContext;
import com.yunshu.oa.entity.FlowInstance;
import com.yunshu.oa.entity.FlowTask;
import com.yunshu.oa.entity.ProcessDef;
import com.yunshu.oa.security.RequirePerm;
import com.yunshu.oa.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/approvals")
@RequiredArgsConstructor
public class ApprovalController {
    private final ApprovalService service;

    @GetMapping("/defs") public R<List<ProcessDef>> defs() { return R.ok(service.defs()); }

    @GetMapping("/instances")
    public R<PageResult<FlowInstance>> instances(@RequestParam(defaultValue = "mine") String scope,
                                                 @RequestParam(required = false) String status,
                                                 @RequestParam(required = false) String kw,
                                                 @RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        return R.ok(service.instances(scope, UserContext.userId(), status, kw, page, size));
    }

    @GetMapping("/todo")
    public R<PageResult<FlowTask>> todo(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        return R.ok(service.todo(UserContext.userId(), page, size));
    }

    @GetMapping("/instances/{id}") public R<Map<String, Object>> detail(@PathVariable Long id) {
        return R.ok(service.detail(id));
    }

    @PostMapping("/instances")
    @RequirePerm("approval:start")
    public R<FlowInstance> start(@RequestBody Map<String, Object> body) { return R.ok(service.start(body)); }

    @PostMapping("/{id}/approve")
    @RequirePerm("approval:audit")
    public R<?> approve(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> body) {
        service.audit(id, "APPROVE", body == null ? "同意" : String.valueOf(body.getOrDefault("comment", "同意")));
        return R.ok();
    }

    @PostMapping("/{id}/reject")
    @RequirePerm("approval:audit")
    public R<?> reject(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        service.audit(id, "REJECT", String.valueOf(body.getOrDefault("comment", "不同意")));
        return R.ok();
    }

    @PostMapping("/{id}/withdraw")
    public R<?> withdraw(@PathVariable Long id) { service.withdraw(id); return R.ok(); }
}
