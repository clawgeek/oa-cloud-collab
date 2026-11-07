package com.yunshu.oa.entity;

import lombok.Data;

/** TaskComment 实体（带 *Name 的字段为联表查询冗余列） */
@Data
public class TaskComment {
    private Long id;
    private Long taskId;
    private Long userId;
    private String userName;
    private String content;
    private String createTime;
}
