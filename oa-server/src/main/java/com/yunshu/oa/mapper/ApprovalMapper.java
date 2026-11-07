package com.yunshu.oa.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;
import com.yunshu.oa.entity.*;
@Mapper
public interface ApprovalMapper {
    List<ProcessDef> listDefs();
    ProcessDef findDef(@Param("procKey") String procKey);
    long countInstances(@Param("scope") String scope, @Param("userId") Long userId,
                        @Param("status") String status, @Param("kw") String kw);
    List<FlowInstance> pageInstances(@Param("scope") String scope, @Param("userId") Long userId,
                                     @Param("status") String status, @Param("kw") String kw,
                                     @Param("offset") int offset, @Param("size") int size);
    FlowInstance findInstance(@Param("id") Long id);
    int insertInstance(FlowInstance i);
    int updateInstance(@Param("id") Long id, @Param("nodeKey") String nodeKey,
                       @Param("nodeName") String nodeName, @Param("status") String status);
    List<FlowTask> listTasks(@Param("instanceId") Long instanceId);
    FlowTask findPendingTask(@Param("instanceId") Long instanceId, @Param("assigneeId") Long assigneeId);
    long countTodo(@Param("assigneeId") Long assigneeId);
    List<FlowTask> pageTodo(@Param("assigneeId") Long assigneeId, @Param("offset") int offset, @Param("size") int size);
    int insertTask(FlowTask t);
    int finishTask(@Param("id") Long id, @Param("action") String action, @Param("comment") String comment);
    int cancelTasks(@Param("instanceId") Long instanceId);
}
