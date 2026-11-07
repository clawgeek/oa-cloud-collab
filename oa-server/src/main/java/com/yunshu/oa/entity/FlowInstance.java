package com.yunshu.oa.entity;

import lombok.Data;

/** FlowInstance 实体（带 *Name 的字段为联表查询冗余列） */
@Data
public class FlowInstance {
    private Long id;
    private String procKey;
    private String bizType;
    private Long bizId;
    private String title;
    private Long applicantId;
    private String applicantName;
    private String currentNode;
    private String currentNodeName;
    private String status;
    private String formJson;
    private String createTime;
    private String finishTime;
}
