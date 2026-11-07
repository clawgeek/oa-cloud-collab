package com.yunshu.oa.entity;

import lombok.Data;

/** Menu 实体（带 *Name 的字段为联表查询冗余列） */
@Data
public class Menu {
    private Long id;
    private Long parentId;
    private String menuName;
    private String path;
    private String component;
    private String permCode;
    private String icon;
    private String menuType;
    private Integer sortNo;
    private Integer status;
}
