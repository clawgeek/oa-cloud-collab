package com.yunshu.oa.entity;

import lombok.Data;

/** DocFolder 实体（带 *Name 的字段为联表查询冗余列） */
@Data
public class DocFolder {
    private Long id;
    private Long parentId;
    private String name;
    private Long ownerId;
    private String createTime;
}
