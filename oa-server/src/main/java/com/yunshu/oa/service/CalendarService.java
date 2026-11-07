package com.yunshu.oa.service;

import com.yunshu.oa.common.BizException;
import com.yunshu.oa.common.Params;
import com.yunshu.oa.common.UserContext;
import com.yunshu.oa.entity.CalendarEvent;
import com.yunshu.oa.entity.MeetingRoom;
import com.yunshu.oa.mapper.CalendarMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/** 日程会议：会议室台账 + 时间段冲突检测 */
@Service
@RequiredArgsConstructor
public class CalendarService {
    private final CalendarMapper mapper;

    public List<MeetingRoom> rooms() { return mapper.listRooms(); }

    public Map<String, Object> events(String start, String end) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("rooms", rooms());
        m.put("events", mapper.listEvents(start, end));
        return m;
    }

    public CalendarEvent save(Map<String, Object> body) {
        String start = Params.s(body, "startTime"), end = Params.s(body, "endTime");
        Long roomId = Params.l(body, "roomId");
        if (start.isEmpty() || end.isEmpty() || roomId == null) throw new BizException("时间范围与会议室不能为空");
        if (start.compareTo(end) >= 0) throw new BizException("结束时间必须晚于开始时间");
        if (mapper.countConflict(roomId, start, end, null) > 0)
            throw new BizException("会议室在该时段已被占用，请更换时间或会议室");
        CalendarEvent e = new CalendarEvent();
        e.setTitle(Params.s(body, "title"));
        e.setEventType(Params.s(body, "eventType", "MEETING"));
        e.setRoomId(roomId);
        e.setOrganizerId(UserContext.userId());
        e.setOrganizerName(UserContext.name());
        e.setStartTime(start); e.setEndTime(end);
        e.setAttendees(Params.s(body, "attendees"));
        e.setRemark(Params.s(body, "remark"));
        mapper.insertEvent(e);
        return e;
    }

    public void delete(Long id) { mapper.deleteEvent(id); }
}
