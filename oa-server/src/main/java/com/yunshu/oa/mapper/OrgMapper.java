package com.yunshu.oa.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;
import com.yunshu.oa.entity.*;
@Mapper
public interface OrgMapper {
    List<Dept> listDepts();
    List<Post> listPosts();
    List<User> pageUsers(@Param("kw") String kw, @Param("deptId") Long deptId,
                         @Param("status") Integer status, @Param("offset") int offset, @Param("size") int size);
    long countUsers(@Param("kw") String kw, @Param("deptId") Long deptId, @Param("status") Integer status);
    User getUser(@Param("id") Long id);
    int insertUser(User u);
    int updateUser(User u);
    int deleteUser(@Param("id") Long id);
    int insertDept(Dept d);
    int updateDept(Dept d);
    int deleteDept(@Param("id") Long id);
    List<Role> listRoles();
    List<Menu> listAllMenus();
    List<Long> listMenuIdsOfRole(@Param("roleId") Long roleId);
    int deleteRoleMenus(@Param("roleId") Long roleId);
    int insertRoleMenus(@Param("roleId") Long roleId, @Param("menuIds") List<Long> menuIds);
    Long findDeptLeader(@Param("deptId") Long deptId);
}
