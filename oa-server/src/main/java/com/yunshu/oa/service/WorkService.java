package com.yunshu.oa.service;

import com.yunshu.oa.common.BizException;
import com.yunshu.oa.common.Params;
import com.yunshu.oa.common.UserContext;
import com.yunshu.oa.entity.*;
import com.yunshu.oa.mapper.AuthMapper;
import com.yunshu.oa.mapper.WorkMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/** 项目与任务协作：项目台账、四列看板、状态流转与评论 */
@Service
@RequiredArgsConstructor
public class WorkService {
    private static final String[] COLUMNS = {"TODO", "DOING", "REVIEW", "DONE"};
    private final WorkMapper mapper;
    private final AuthMapper authMapper;

    public List<Project> projects(String status) { return mapper.listProjects(status); }

    public Map<String, Object> board(Long projectId) {
        Map<String, Object> board = new LinkedHashMap<>();
        for (String col : COLUMNS) {
            board.put(col, mapper.listTasks(projectId, col, ""));
        }
        board.put("columns", List.of(COLUMNS));
        return board;
    }

    public List<Task> tasks(Long projectId, String status, String kw) {
        return mapper.listTasks(projectId, status, kw);
    }

    @Transactional
    public Task saveTask(Map<String, Object> body) {
        Task t = new Task();
        t.setId(Params.l(body, "id"));
        t.setProjectId(Params.l(body, "projectId"));
        t.setTitle(Params.s(body, "title"));
        t.setDescription(Params.s(body, "description"));
        t.setPriority(Params.s(body, "priority", "MEDIUM"));
        t.setStatus(Params.s(body, "status", "TODO"));
        t.setProgress(Params.i(body, "progress", 0));
        t.setDueDate(Params.s(body, "dueDate"));
        Long assigneeId = Params.l(body, "assigneeId");
        User assignee = assigneeId == null ? null : authMapper.findById(assigneeId);
        t.setAssigneeId(assigneeId);
        t.setAssigneeName(assignee == null ? "" : assignee.getRealName());
        if (t.getTitle().isEmpty()) throw new BizException("任务标题不能为空");
        if (t.getProjectId() == null) throw new BizException("请选择所属项目");
        if (t.getId() == null) mapper.insertTask(t); else mapper.updateTask(t);
        return t;
    }

    public void move(Long id, String status) {
        if (!Arrays.asList(COLUMNS).contains(status)) throw new BizException("非法任务状态：" + status);
        int progress = switch (status) {
            case "TODO" -> 0; case "DOING" -> 50; case "REVIEW" -> 90; default -> 100;
        };
        mapper.moveTask(id, status, progress);
    }

    public void deleteTask(Long id) { mapper.deleteTask(id); }

    public List<TaskComment> comments(Long taskId) { return mapper.listComments(taskId); }

    public TaskComment comment(Long taskId, String content) {
        if (content == null || content.isBlank()) throw new BizException("评论内容不能为空");
        TaskComment c = new TaskComment();
        c.setTaskId(taskId); c.setUserId(UserContext.userId());
        c.setUserName(UserContext.name()); c.setContent(content);
        mapper.insertComment(c);
        return c;
    }

    public Project saveProject(Map<String, Object> body) {
        Project p = new Project();
        p.setId(Params.l(body, "id"));
        p.setProjectCode(Params.s(body, "projectCode"));
        p.setName(Params.s(body, "name"));
        p.setStatus(Params.s(body, "status", "ACTIVE"));
        p.setStartDate(Params.s(body, "startDate"));
        p.setEndDate(Params.s(body, "endDate"));
        p.setDescription(Params.s(body, "description"));
        p.setOwnerId(UserContext.userId());
        p.setOwnerName(UserContext.name());
        if (p.getName().isEmpty()) throw new BizException("项目名称不能为空");
        if (p.getId() == null) mapper.insertProject(p); else mapper.updateProject(p);
        return p;
    }
}
