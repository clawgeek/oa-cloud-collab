package com.yunshu.oa.entity;

import lombok.Data;

/** Task 实体（带 *Name 的字段为联表查询冗余列） */
@Data
public class Task {
    private Long id;
    private Long projectId;
    private String title;
    private String description;
    private Long assigneeId;
    private String assigneeName;
    private String priority;
    private String status;
    private Integer progress;
    private String dueDate;
    private String createTime;
    private String finishTime;
    private String projectName;
}
