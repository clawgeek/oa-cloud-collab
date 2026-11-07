package com.yunshu.oa.entity;

import lombok.Data;

/** Role 实体（带 *Name 的字段为联表查询冗余列） */
@Data
public class Role {
    private Long id;
    private String roleCode;
    private String roleName;
    private String dataScope;
    private String remark;
    private String createTime;
}
