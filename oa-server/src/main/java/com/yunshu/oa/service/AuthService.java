package com.yunshu.oa.service;

import com.yunshu.oa.common.BizException;
import com.yunshu.oa.common.UserContext;
import com.yunshu.oa.entity.Menu;
import com.yunshu.oa.entity.User;
import com.yunshu.oa.jsoncodec.JsonCodec;
import com.yunshu.oa.mapper.AuthMapper;
import com.yunshu.oa.security.JwtUtil;
import com.yunshu.oa.security.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/** 认证与授权：PBKDF2 口令校验 → 签发 JWT → 下发菜单与权限码 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthMapper authMapper;
    private final JwtUtil jwt;

    public Map<String, Object> login(String username, String password) {
        User u = authMapper.findByUsername(username);
        if (u == null || u.getStatus() == null || u.getStatus() != 1
                || !PasswordUtil.matches(password, u.getPassword())) {
            throw new BizException(401, "账号或密码错误，或账号已被停用");
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("token", jwt.issue(u.getId(), u.getUsername(), u.getRealName(), u.getDeptId()));
        result.put("user", safe(u));
        result.put("roles", authMapper.listRoleCodes(u.getId()));
        result.put("roleNames", authMapper.listRoleNames(u.getId()));
        result.put("perms", authMapper.listPermCodes(u.getId()));
        result.put("menus", authMapper.listMenus(u.getId()));
        return result;
    }

    public Map<String, Object> info(Long userId) {
        User u = authMapper.findById(userId);
        if (u == null) throw new BizException(401, "登录状态已失效");
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("user", safe(u));
        m.put("roles", authMapper.listRoleCodes(userId));
        m.put("roleNames", authMapper.listRoleNames(userId));
        m.put("perms", authMapper.listPermCodes(userId));
        m.put("menus", authMapper.listMenus(userId));
        return m;
    }

    public void changePassword(Long userId, String oldPwd, String newPwd) {
        User u = authMapper.findById(userId);
        if (u == null || !PasswordUtil.matches(oldPwd, u.getPassword())) {
            throw new BizException("原密码不正确");
        }
        if (newPwd == null || newPwd.length() < 6) throw new BizException("新密码长度不能小于 6 位");
        authMapper.updatePassword(userId, PasswordUtil.hash(newPwd));
    }

    private User safe(User u) { u.setPassword(null); return u; }
}
