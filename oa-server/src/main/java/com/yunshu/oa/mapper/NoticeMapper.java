package com.yunshu.oa.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;
import com.yunshu.oa.entity.*;
@Mapper
public interface NoticeMapper {
    long countNotices(@Param("kw") String kw, @Param("category") String category, @Param("userId") Long userId);
    List<Notice> pageNotices(@Param("kw") String kw, @Param("category") String category,
                             @Param("userId") Long userId, @Param("offset") int offset, @Param("size") int size);
    Notice findNotice(@Param("id") Long id);
    int insertNotice(Notice n);
    int updateNotice(Notice n);
    int deleteNotice(@Param("id") Long id);
    int addView(@Param("id") Long id);
    int markRead(@Param("noticeId") Long noticeId, @Param("userId") Long userId);
    int countRead(@Param("noticeId") Long noticeId, @Param("userId") Long userId);
    long countUnread(@Param("userId") Long userId);
}
