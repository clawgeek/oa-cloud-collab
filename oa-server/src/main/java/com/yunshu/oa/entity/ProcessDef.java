package com.yunshu.oa.entity;

import lombok.Data;

/** ProcessDef 实体（带 *Name 的字段为联表查询冗余列） */
@Data
public class ProcessDef {
    private Long id;
    private String procKey;
    private String procName;
    private String bizType;
    private String nodesJson;
    private Integer status;
    private String createTime;
}
