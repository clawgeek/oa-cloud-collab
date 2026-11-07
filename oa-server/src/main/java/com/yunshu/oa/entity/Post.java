package com.yunshu.oa.entity;

import lombok.Data;

/** Post 实体（带 *Name 的字段为联表查询冗余列） */
@Data
public class Post {
    private Long id;
    private String postName;
    private String postCode;
    private Integer sortNo;
    private Integer status;
}
