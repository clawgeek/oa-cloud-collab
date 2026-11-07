package com.yunshu.oa.entity;

import lombok.Data;

/** OperLog 实体（带 *Name 的字段为联表查询冗余列） */
@Data
public class OperLog {
    private Long id;
    private Long userId;
    private String userName;
    private String module;
    private String operation;
    private String method;
    private String requestUri;
    private String ip;
    private Long costMs;
    private Integer success;
    private String createTime;
}
