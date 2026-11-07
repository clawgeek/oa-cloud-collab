package com.yunshu.oa.controller;

import com.yunshu.oa.common.PageResult;
import com.yunshu.oa.common.R;
import com.yunshu.oa.common.UserContext;
import com.yunshu.oa.entity.Checkin;
import com.yunshu.oa.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService service;

    @GetMapping("/today") public R<Map<String, Object>> today() { return R.ok(service.today(UserContext.userId())); }

    @PostMapping("/checkin") public R<Checkin> checkin(@RequestBody(required = false) Map<String, Object> body) {
        return R.ok(service.check(body == null ? "" : String.valueOf(body.getOrDefault("location", ""))));
    }

    @GetMapping("/records")
    public R<PageResult<Checkin>> records(@RequestParam(required = false) Long userId,
                                          @RequestParam(required = false) String month,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        return R.ok(service.records(userId, month, page, size));
    }

    @GetMapping("/summary") public R<List<Map<String, Object>>> summary(@RequestParam(required = false) String month) {
        return R.ok(service.summary(month));
    }
}
