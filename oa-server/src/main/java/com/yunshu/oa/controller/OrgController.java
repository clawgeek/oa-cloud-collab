package com.yunshu.oa.controller;

import com.yunshu.oa.common.PageResult;
import com.yunshu.oa.common.R;
import com.yunshu.oa.entity.*;
import com.yunshu.oa.security.RequirePerm;
import com.yunshu.oa.service.OrgService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/org")
@RequiredArgsConstructor
public class OrgController {
    private final OrgService service;

    @GetMapping("/depts") public R<List<Dept>> depts() { return R.ok(service.depts()); }
    @GetMapping("/posts") public R<List<Post>> posts() { return R.ok(service.posts()); }
    @GetMapping("/roles") public R<List<Role>> roles() { return R.ok(service.roles()); }
    @GetMapping("/menus") public R<List<Menu>> menus() { return R.ok(service.menus()); }

    @GetMapping("/users")
    public R<PageResult<User>> users(@RequestParam(required = false) String kw,
                                     @RequestParam(required = false) Long deptId,
                                     @RequestParam(required = false) Integer status,
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        return R.ok(service.users(kw, deptId, status, page, size));
    }

    @PostMapping("/users")
    @RequirePerm("org:user:list")
    public R<User> saveUser(@RequestBody Map<String, Object> body) { return R.ok(service.saveUser(body)); }

    @DeleteMapping("/users/{id}")
    @RequirePerm("org:user:list")
    public R<?> deleteUser(@PathVariable Long id) { service.deleteUser(id); return R.ok(); }

    @PostMapping("/depts")
    @RequirePerm("org:dept:list")
    public R<Dept> saveDept(@RequestBody Map<String, Object> body) { return R.ok(service.saveDept(body)); }

    @DeleteMapping("/depts/{id}")
    @RequirePerm("org:dept:list")
    public R<?> deleteDept(@PathVariable Long id) { service.deleteDept(id); return R.ok(); }

    @GetMapping("/role-menus")
    public R<List<Long>> roleMenus(@RequestParam Long roleId) { return R.ok(service.roleMenus(roleId)); }

    @PutMapping("/role-menus")
    @RequirePerm("org:role:list")
    public R<List<Long>> saveRoleMenus(@RequestBody Map<String, Object> body) {
        Object raw = body.get("menuIds");
        List<Long> ids = new java.util.ArrayList<>();
        if (raw instanceof List<?> list) for (Object x : list) ids.add(Long.valueOf(String.valueOf(x)));
        return R.ok(service.saveRoleMenus(Long.valueOf(String.valueOf(body.get("roleId"))), ids));
    }
}
