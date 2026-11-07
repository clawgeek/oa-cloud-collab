package com.yunshu.oa.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;
import com.yunshu.oa.entity.*;
@Mapper
public interface LogMapper {
    int insert(Map<String, Object> m);
    long countLogs(@Param("kw") String kw, @Param("module") String module);
    List<OperLog> pageLogs(@Param("kw") String kw, @Param("module") String module,
                           @Param("offset") int offset, @Param("size") int size);
}
