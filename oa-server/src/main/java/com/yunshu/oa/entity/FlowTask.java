package com.yunshu.oa.entity;

import lombok.Data;

/** FlowTask 实体（带 *Name 的字段为联表查询冗余列） */
@Data
public class FlowTask {
    private Long id;
    private Long instanceId;
    private String nodeKey;
    private String nodeName;
    private Long assigneeId;
    private String assigneeName;
    private String action;
    private String comment;
    private String status;
    private String createTime;
    private String finishTime;
}
