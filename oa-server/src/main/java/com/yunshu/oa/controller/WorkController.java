package com.yunshu.oa.controller;

import com.yunshu.oa.common.R;
import com.yunshu.oa.entity.Project;
import com.yunshu.oa.entity.Task;
import com.yunshu.oa.entity.TaskComment;
import com.yunshu.oa.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/work")
@RequiredArgsConstructor
public class WorkController {
    private final WorkService service;

    @GetMapping("/projects") public R<List<Project>> projects(@RequestParam(required = false) String status) {
        return R.ok(service.projects(status));
    }
    @PostMapping("/projects") public R<Project> saveProject(@RequestBody Map<String, Object> body) {
        return R.ok(service.saveProject(body));
    }
    @GetMapping("/board") public R<Map<String, Object>> board(@RequestParam(required = false) Long projectId) {
        return R.ok(service.board(projectId));
    }
    @GetMapping("/tasks") public R<List<Task>> tasks(@RequestParam(required = false) Long projectId,
                                                     @RequestParam(required = false) String status,
                                                     @RequestParam(required = false) String kw) {
        return R.ok(service.tasks(projectId, status, kw));
    }
    @PostMapping("/tasks") public R<Task> saveTask(@RequestBody Map<String, Object> body) {
        return R.ok(service.saveTask(body));
    }
    @PutMapping("/tasks/{id}/status") public R<?> move(@PathVariable Long id,
                                                       @RequestBody Map<String, Object> body) {
        service.move(id, String.valueOf(body.get("status"))); return R.ok();
    }
    @DeleteMapping("/tasks/{id}") public R<?> deleteTask(@PathVariable Long id) {
        service.deleteTask(id); return R.ok();
    }
    @GetMapping("/tasks/{id}/comments") public R<List<TaskComment>> comments(@PathVariable Long id) {
        return R.ok(service.comments(id));
    }
    @PostMapping("/tasks/{id}/comments") public R<TaskComment> comment(@PathVariable Long id,
                                                                       @RequestBody Map<String, Object> body) {
        return R.ok(service.comment(id, String.valueOf(body.get("content"))));
    }
}
