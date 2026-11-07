package com.yunshu.oa.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;
import com.yunshu.oa.entity.*;
@Mapper
public interface WorkMapper {
    List<Project> listProjects(@Param("status") String status);
    Project findProject(@Param("id") Long id);
    int insertProject(Project p);
    int updateProject(Project p);
    List<Task> listTasks(@Param("projectId") Long projectId, @Param("status") String status,
                         @Param("kw") String kw);
    Task findTask(@Param("id") Long id);
    int insertTask(Task t);
    int updateTask(Task t);
    int moveTask(@Param("id") Long id, @Param("status") String status, @Param("progress") Integer progress);
    int deleteTask(@Param("id") Long id);
    List<TaskComment> listComments(@Param("taskId") Long taskId);
    int insertComment(TaskComment c);
}
