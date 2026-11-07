package com.yunshu.oa.entity;

import lombok.Data;

/** Checkin 实体（带 *Name 的字段为联表查询冗余列） */
@Data
public class Checkin {
    private Long id;
    private Long userId;
    private String checkDate;
    private String checkIn;
    private String checkOut;
    private String inStatus;
    private String outStatus;
    private java.math.BigDecimal workHours;
    private String location;
    private String createTime;
    private String realName;
    private String deptName;
}
