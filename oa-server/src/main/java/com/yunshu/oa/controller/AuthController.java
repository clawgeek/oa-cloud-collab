package com.yunshu.oa.controller;

import com.yunshu.oa.common.R;
import com.yunshu.oa.common.UserContext;
import com.yunshu.oa.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody Map<String, Object> body) {
        return R.ok(authService.login(String.valueOf(body.get("username")),
                                      String.valueOf(body.get("password"))));
    }

    @GetMapping("/info")
    public R<Map<String, Object>> info() { return R.ok(authService.info(UserContext.userId())); }

    @PutMapping("/password")
    public R<?> password(@RequestBody Map<String, Object> body) {
        authService.changePassword(UserContext.userId(),
                String.valueOf(body.get("oldPassword")), String.valueOf(body.get("newPassword")));
        return R.ok();
    }

    @PostMapping("/logout")
    public R<?> logout() { return R.ok(); }
}
