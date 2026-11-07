package com.yunshu.oa.entity;

import lombok.Data;

/** LeaveBalance 实体（带 *Name 的字段为联表查询冗余列） */
@Data
public class LeaveBalance {
    private Long id;
    private Long userId;
    private String yearNo;
    private java.math.BigDecimal annualTotal;
    private java.math.BigDecimal annualUsed;
    private java.math.BigDecimal compTotal;
    private java.math.BigDecimal compUsed;
}
