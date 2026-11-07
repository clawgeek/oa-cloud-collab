package com.yunshu.oa.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;
import com.yunshu.oa.entity.*;
@Mapper
public interface AuthMapper {
    User findByUsername(@Param("username") String username);
    User findById(@Param("id") Long id);
    List<String> listPermCodes(@Param("userId") Long userId);
    List<String> listRoleCodes(@Param("userId") Long userId);
    List<Menu> listMenus(@Param("userId") Long userId);
    List<String> listRoleNames(@Param("userId") Long userId);
    int updatePassword(@Param("id") Long id, @Param("password") String password);
    int touchLogin(@Param("id") Long id);
}
