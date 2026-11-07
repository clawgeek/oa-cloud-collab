package com.yunshu.oa.entity;

import lombok.Data;

/** DocFile 实体（带 *Name 的字段为联表查询冗余列） */
@Data
public class DocFile {
    private Long id;
    private Long folderId;
    private String name;
    private String ext;
    private Long fileSize;
    private Integer version;
    private String storagePath;
    private Long uploaderId;
    private String uploaderName;
    private Integer downloadCount;
    private String createTime;
}
