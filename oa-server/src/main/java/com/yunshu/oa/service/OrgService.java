package com.yunshu.oa.service;

import com.yunshu.oa.common.BizException;
import com.yunshu.oa.common.PageResult;
import com.yunshu.oa.common.Params;
import com.yunshu.oa.common.UserContext;
import com.yunshu.oa.entity.*;
import com.yunshu.oa.mapper.AuthMapper;
import com.yunshu.oa.mapper.OrgMapper;
import com.yunshu.oa.security.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/** 组织架构与 RBAC */
@Service
@RequiredArgsConstructor
public class OrgService {
    private final OrgMapper orgMapper;
    private final AuthMapper authMapper;

    public List<Dept> depts() { return orgMapper.listDepts(); }
    public List<Post> posts() { return orgMapper.listPosts(); }
    public List<Role> roles() { return orgMapper.listRoles(); }
    public List<Menu> menus() { return orgMapper.listAllMenus(); }

    public PageResult<User> users(String kw, Long deptId, Integer status, int page, int size) {
        int p = Math.max(page, 1), s = Math.min(Math.max(size, 1), 100);
        long total = orgMapper.countUsers(kw, deptId, status);
        return PageResult.of(total, orgMapper.pageUsers(kw, deptId, status, (p - 1) * s, s), p, s);
    }

    @Transactional
    public User saveUser(Map<String, Object> body) {
        Long id = Params.l(body, "id");
        String username = Params.s(body, "username");
        User u = new User();
        u.setId(id);
        u.setUsername(username);
        u.setRealName(Params.s(body, "realName"));
        u.setDeptId(Params.l(body, "deptId"));
        u.setPostId(Params.l(body, "postId"));
        u.setPhone(Params.s(body, "phone"));
        u.setEmail(Params.s(body, "email"));
        u.setStatus(Params.i(body, "status", 1));
        u.setHireDate(Params.s(body, "hireDate"));
        if (id == null) {
            if (authMapper.findByUsername(username) != null) throw new BizException("登录账号已存在");
            u.setPassword(PasswordUtil.hash(Params.s(body, "password", "123456")));
            orgMapper.insertUser(u);
        } else {
            orgMapper.updateUser(u);
        }
        return u;
    }

    @Transactional
    public void deleteUser(Long id) {
        if (id != null && id == 1L) throw new BizException("内置管理员账号不可删除");
        orgMapper.deleteUser(id);
    }

    @Transactional
    public Dept saveDept(Map<String, Object> body) {
        Dept d = new Dept();
        d.setId(Params.l(body, "id"));
        d.setParentId(Params.l(body, "parentId", 0L) == null ? 0L : Params.l(body, "parentId"));
        d.setDeptName(Params.s(body, "deptName"));
        d.setDeptCode(Params.s(body, "deptCode"));
        d.setLeaderId(Params.l(body, "leaderId"));
        d.setSortNo(Params.i(body, "sortNo", 0));
        d.setStatus(Params.i(body, "status", 1));
        if (d.getId() == null) orgMapper.insertDept(d); else orgMapper.updateDept(d);
        return d;
    }

    public void deleteDept(Long id) {
        if (orgMapper.listDepts().stream().anyMatch(x -> Objects.equals(x.getParentId(), id)))
            throw new BizException("存在子部门，不能直接删除");
        orgMapper.deleteDept(id);
    }

    @Transactional
    public List<Long> saveRoleMenus(Long roleId, List<Long> menuIds) {
        orgMapper.deleteRoleMenus(roleId);
        if (menuIds != null && !menuIds.isEmpty()) orgMapper.insertRoleMenus(roleId, menuIds);
        return menuIds == null ? List.of() : menuIds;
    }

    public List<Long> roleMenus(Long roleId) { return orgMapper.listMenuIdsOfRole(roleId); }

    /** 按角色解析审批人：leader 取部门负责人，hr/finance/gm 取固定岗位人 */
    public Long resolveAssignee(String roleKey, Long deptId) {
        Long id = switch (roleKey == null ? "" : roleKey) {
            case "hr" -> 2L;
            case "finance" -> 6L;
            case "gm" -> 1L;
            case "leader" -> {
                Long leader = deptId == null ? null : orgMapper.findDeptLeader(deptId);
                yield leader == null ? 3L : leader;
            }
            default -> 3L;
        };
        return id;
    }
}
