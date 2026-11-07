package com.yunshu.oa.controller;

import com.yunshu.oa.common.PageResult;
import com.yunshu.oa.common.R;
import com.yunshu.oa.common.UserContext;
import com.yunshu.oa.entity.Notice;
import com.yunshu.oa.security.RequirePerm;
import com.yunshu.oa.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService service;

    @GetMapping
    public R<PageResult<Notice>> list(@RequestParam(required = false) String kw,
                                      @RequestParam(required = false) String category,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        return R.ok(service.list(kw, category, UserContext.userId(), page, size));
    }

    @GetMapping("/unread") public R<Long> unread() { return R.ok(service.unread(UserContext.userId())); }

    @GetMapping("/{id}") public R<Notice> detail(@PathVariable Long id) {
        return R.ok(service.read(id, UserContext.userId()));
    }

    @PostMapping
    @RequirePerm("notice:view")
    public R<Notice> save(@RequestBody Map<String, Object> body) { return R.ok(service.save(body)); }

    @DeleteMapping("/{id}")
    @RequirePerm("notice:view")
    public R<?> delete(@PathVariable Long id) { service.delete(id); return R.ok(); }
}
