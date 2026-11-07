package com.yunshu.oa.service;

import com.yunshu.oa.common.BizException;
import com.yunshu.oa.common.PageResult;
import com.yunshu.oa.common.UserContext;
import com.yunshu.oa.entity.Checkin;
import com.yunshu.oa.entity.LeaveBalance;
import com.yunshu.oa.mapper.AttendanceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** 考勤：上班/下班打卡、迟到早退判定、月报统计与假期余额 */
@Service
@RequiredArgsConstructor
public class AttendanceService {
    private static final LocalTime WORK_START = LocalTime.of(9, 0);
    private static final LocalTime WORK_END = LocalTime.of(18, 0);
    private final AttendanceMapper mapper;

    public Map<String, Object> today(Long userId) {
        String day = LocalDate.now().toString();
        Checkin c = mapper.findToday(userId, day);
        LeaveBalance bal = mapper.findBalance(userId, String.valueOf(LocalDate.now().getYear()));
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("date", day);
        m.put("record", c);
        m.put("balance", bal);
        m.put("serverTime", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        return m;
    }

    public Checkin check(String location) {
        Long userId = UserContext.userId();
        String day = LocalDate.now().toString();
        String now = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        Checkin exist = mapper.findToday(userId, day);
        if (exist == null) {
            Checkin c = new Checkin();
            c.setUserId(userId); c.setCheckDate(day);
            c.setCheckIn(now.substring(0, 5));
            c.setInStatus(LocalTime.now().isAfter(WORK_START) ? "LATE" : "NORMAL");
            c.setLocation(location == null || location.isEmpty() ? "总部-5F WiFi" : location);
            mapper.insertCheckin(c);
            return mapper.findToday(userId, day);
        }
        if (exist.getCheckOut() != null) throw new BizException("今日已完成上下班打卡");
        LocalTime t = LocalTime.now();
        BigDecimal hours = BigDecimal.valueOf(
                Math.max(0, java.time.Duration.between(
                        LocalTime.parse(exist.getCheckIn() + ":00"), t).toMinutes() / 60.0 - 1))
                .setScale(2, java.math.RoundingMode.HALF_UP);
        mapper.updateCheckout(exist.getId(), now.substring(0, 5),
                t.isBefore(WORK_END) ? "EARLY" : "NORMAL", hours);
        return mapper.findToday(userId, day);
    }

    public PageResult<Checkin> records(Long userId, String month, int page, int size) {
        int p = Math.max(page, 1), s = Math.min(Math.max(size, 1), 100);
        return PageResult.of(mapper.countRecords(userId, month),
                mapper.pageRecords(userId, month, (p - 1) * s, s), p, s);
    }

    public List<Map<String, Object>> summary(String month) {
        if (month == null || month.isEmpty()) month = LocalDate.now().toString().substring(0, 7);
        return mapper.monthSummary(month);
    }
}
