package com.yunshu.oa.entity;

import lombok.Data;

/** MeetingRoom 实体（带 *Name 的字段为联表查询冗余列） */
@Data
public class MeetingRoom {
    private Long id;
    private String name;
    private Integer capacity;
    private String location;
    private String equipment;
    private Integer status;
}
