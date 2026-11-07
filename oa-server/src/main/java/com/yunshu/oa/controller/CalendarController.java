package com.yunshu.oa.controller;

import com.yunshu.oa.common.R;
import com.yunshu.oa.entity.CalendarEvent;
import com.yunshu.oa.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {
    private final CalendarService service;

    @GetMapping("/rooms") public R<?> rooms() { return R.ok(service.rooms()); }
    @GetMapping("/events") public R<Map<String, Object>> events(@RequestParam(required = false) String start,
                                                                 @RequestParam(required = false) String end) {
        return R.ok(service.events(start, end));
    }
    @PostMapping("/events") public R<CalendarEvent> save(@RequestBody Map<String, Object> body) {
        return R.ok(service.save(body));
    }
    @DeleteMapping("/events/{id}") public R<?> delete(@PathVariable Long id) {
        service.delete(id); return R.ok();
    }
}
