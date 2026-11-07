package com.yunshu.oa.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;
import com.yunshu.oa.entity.*;
@Mapper
public interface CalendarMapper {
    List<MeetingRoom> listRooms();
    int insertRoom(MeetingRoom r);
    List<CalendarEvent> listEvents(@Param("start") String start, @Param("end") String end);
    int countConflict(@Param("roomId") Long roomId, @Param("start") String start, @Param("end") String end,
                      @Param("excludeId") Long excludeId);
    int insertEvent(CalendarEvent e);
    int deleteEvent(@Param("id") Long id);
}
