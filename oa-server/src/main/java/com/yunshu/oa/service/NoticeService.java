package com.yunshu.oa.service;

import com.yunshu.oa.common.BizException;
import com.yunshu.oa.common.PageResult;
import com.yunshu.oa.common.Params;
import com.yunshu.oa.common.UserContext;
import com.yunshu.oa.entity.Notice;
import com.yunshu.oa.mapper.NoticeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/** 公告通知：发布、分页、置顶、阅读回执与未读数 */
@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeMapper mapper;

    public PageResult<Notice> list(String kw, String category, Long userId, int page, int size) {
        int p = Math.max(page, 1), s = Math.min(Math.max(size, 1), 100);
        return PageResult.of(mapper.countNotices(kw, category, userId),
                mapper.pageNotices(kw, category, userId, (p - 1) * s, s), p, s);
    }

    public Notice read(Long id, Long userId) {
        Notice n = mapper.findNotice(id);
        if (n == null) throw new BizException("公告不存在");
        mapper.addView(id);
        if (mapper.countRead(id, userId) == 0) mapper.markRead(id, userId);
        return mapper.findNotice(id);
    }

    public Notice save(Map<String, Object> body) {
        Notice n = new Notice();
        n.setId(Params.l(body, "id"));
        n.setTitle(Params.s(body, "title"));
        n.setContent(Params.s(body, "content"));
        n.setCategory(Params.s(body, "category", "公告"));
        n.setPinned(Params.i(body, "pinned", 0));
        n.setPublisherId(UserContext.userId());
        n.setPublisherName(UserContext.name());
        if (n.getTitle().isEmpty()) throw new BizException("公告标题不能为空");
        if (n.getId() == null) mapper.insertNotice(n); else mapper.updateNotice(n);
        return n;
    }

    public void delete(Long id) { mapper.deleteNotice(id); }
    public long unread(Long userId) { return mapper.countUnread(userId); }
}
