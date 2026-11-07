package com.yunshu.oa.service;

import com.yunshu.oa.common.PageResult;
import com.yunshu.oa.entity.OperLog;
import com.yunshu.oa.mapper.LogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {
    private final LogMapper mapper;
    public PageResult<OperLog> page(String kw, String module, int page, int size) {
        int p = Math.max(page, 1), s = Math.min(Math.max(size, 1), 100);
        return PageResult.of(mapper.countLogs(kw, module),
                mapper.pageLogs(kw, module, (p - 1) * s, s), p, s);
    }
}
