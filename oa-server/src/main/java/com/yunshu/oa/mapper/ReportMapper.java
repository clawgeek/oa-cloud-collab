package com.yunshu.oa.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;
import com.yunshu.oa.entity.*;
@Mapper
public interface ReportMapper {
    Map<String, Object> overview(@Param("userId") Long userId);
    List<Map<String, Object>> attendanceTrend();
    List<Map<String, Object>> taskDistribution();
    List<Map<String, Object>> approvalDistribution();
    List<Map<String, Object>> deptStaff();
    List<Map<String, Object>> todoList(@Param("userId") Long userId);
}
