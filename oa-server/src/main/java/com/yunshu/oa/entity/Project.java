package com.yunshu.oa.entity;

import lombok.Data;

/** Project 实体（带 *Name 的字段为联表查询冗余列） */
@Data
public class Project {
    private Long id;
    private String projectCode;
    private String name;
    private Long ownerId;
    private String ownerName;
    private String status;
    private String startDate;
    private String endDate;
    private String description;
    private String createTime;
    private Integer taskCount;
    private Integer doneCount;
}
