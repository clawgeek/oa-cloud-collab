package com.yunshu.oa.entity;

import lombok.Data;

/** CalendarEvent 实体（带 *Name 的字段为联表查询冗余列） */
@Data
public class CalendarEvent {
    private Long id;
    private String title;
    private String eventType;
    private Long roomId;
    private Long organizerId;
    private String organizerName;
    private String startTime;
    private String endTime;
    private String attendees;
    private String remark;
    private String createTime;
    private String roomName;
}
