package com.yunshu.oa.entity;

import lombok.Data;

/** User 实体（带 *Name 的字段为联表查询冗余列） */
@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String avatar;
    private Long deptId;
    private Long postId;
    private String phone;
    private String email;
    private Integer status;
    private String hireDate;
    private String createTime;
    private String deptName;
    private String postName;
    private String roles;
}
