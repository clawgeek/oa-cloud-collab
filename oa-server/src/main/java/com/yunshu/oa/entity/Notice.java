package com.yunshu.oa.entity;

import lombok.Data;

/** Notice 实体（带 *Name 的字段为联表查询冗余列） */
@Data
public class Notice {
    private Long id;
    private String title;
    private String content;
    private String category;
    private Long publisherId;
    private String publisherName;
    private Integer pinned;
    private Integer viewCount;
    private String publishTime;
    private Integer isRead;
}
