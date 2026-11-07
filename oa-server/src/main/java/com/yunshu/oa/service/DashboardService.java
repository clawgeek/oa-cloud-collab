package com.yunshu.oa.service;

import com.yunshu.oa.mapper.ReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 工作台聚合：一次请求返回概览、四类图表与待办清单。
 *
 * resultType=map 的列名在不同数据库里大小写不一致：H2(DATABASE_TO_LOWER) 会把
 * runningApprovals 变成 runningapprovals，MySQL 保留别名原样。这里统一归一化成
 * camelCase / 固定键，前端只依赖这一层契约，换库不用改前端。
 */
@Service
@RequiredArgsConstructor
public class DashboardService {
    private final ReportMapper reportMapper;

    private static final String[] STAT_KEYS = {
        "staff", "depts", "runningApprovals", "myTodo", "activeProjects",
        "openTasks", "todayCheckins", "monthLate", "docs", "notices"
    };

    public Map<String, Object> overview(Long userId) {
        Map<String, Object> raw = reportMapper.overview(userId);
        Map<String, Object> stats = new LinkedHashMap<>();
        for (String key : STAT_KEYS) {
            Object v = pick(raw, key);
            stats.put(key, v instanceof Number n ? n.longValue() : 0L);
        }

        List<Map<String, Object>> todos = new ArrayList<>();
        for (Map<String, Object> row : reportMapper.todoList(userId)) {
            Map<String, Object> t = new LinkedHashMap<>();
            t.put("type", pick(row, "bizType"));
            t.put("title", pick(row, "title"));
            t.put("sortTime", pick(row, "sortTime"));
            t.put("refId", pick(row, "refId"));
            todos.add(t);
        }

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("stats", stats);
        m.put("attendanceTrend", reportMapper.attendanceTrend());
        m.put("taskDistribution", reportMapper.taskDistribution());
        m.put("approvalDistribution", reportMapper.approvalDistribution());
        m.put("deptStaff", reportMapper.deptStaff());
        m.put("todos", todos);
        m.put("serverTime", new Date());
        return m;
    }

    /** 忽略大小写与下划线查找 map 的键，兼容 H2 全小写与 MySQL 别名原样两种行为 */
    private static Object pick(Map<String, Object> m, String camel) {
        if (m == null) return null;
        String norm = camel.replace("_", "").toLowerCase();
        for (Map.Entry<String, Object> e : m.entrySet()) {
            if (e.getKey().replace("_", "").toLowerCase().equals(norm)) return e.getValue();
        }
        return null;
    }
}
