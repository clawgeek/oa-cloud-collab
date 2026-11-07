package com.yunshu.oa.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;
import com.yunshu.oa.entity.*;
@Mapper
public interface AttendanceMapper {
    Checkin findToday(@Param("userId") Long userId, @Param("day") String day);
    int insertCheckin(Checkin c);
    int updateCheckout(@Param("id") Long id, @Param("time") String time,
                       @Param("status") String status, @Param("hours") java.math.BigDecimal hours);
    List<Checkin> pageRecords(@Param("userId") Long userId, @Param("month") String month,
                              @Param("offset") int offset, @Param("size") int size);
    long countRecords(@Param("userId") Long userId, @Param("month") String month);
    List<Map<String, Object>> monthSummary(@Param("month") String month);
    LeaveBalance findBalance(@Param("userId") Long userId, @Param("year") String year);
    int consumeBalance(@Param("userId") Long userId, @Param("days") java.math.BigDecimal days);
}
