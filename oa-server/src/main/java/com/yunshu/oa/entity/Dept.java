package com.yunshu.oa.entity;

import lombok.Data;

/** Dept 实体（带 *Name 的字段为联表查询冗余列） */
@Data
public class Dept {
    private Long id;
    private Long parentId;
    private String deptName;
    private String deptCode;
    private Long leaderId;
    private Integer sortNo;
    private Integer status;
    private String createTime;
    private String leaderName;
    private Integer staffCount;
}
