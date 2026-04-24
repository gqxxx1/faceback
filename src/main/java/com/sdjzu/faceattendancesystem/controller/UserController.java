package com.sdjzu.faceattendancesystem.controller;

import com.sdjzu.faceattendancesystem.common.PageResult;
import com.sdjzu.faceattendancesystem.common.Result;
import com.sdjzu.faceattendancesystem.dto.LoginRequest;
import com.sdjzu.faceattendancesystem.entity.User;
import com.sdjzu.faceattendancesystem.service.SysLogService;
import com.sdjzu.faceattendancesystem.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SysLogService sysLogService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        User user = userService.login(request.getUsername(), request.getPassword());
        if (user != null) {
            // 记录登录日志
            sysLogService.log(user.getUsername(), "login", "用户登录", "POST", "/api/user/login",
                    request.getUsername(), getClientIp(httpRequest), 1, null);

            // 返回用户信息（不含密码）
            Map<String, Object> result = new HashMap<>();
            result.put("token", "mock-token-" + user.getId());
            result.put("userId", user.getId());
            result.put("username", user.getUsername());
            result.put("realName", user.getRealName());
            result.put("role", user.getRole());

            return Result.success(result, "登录成功");
        }
        return Result.error("用户名或密码错误");
    }

    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(@RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> result = new HashMap<>();

        // 从token中解析用户ID（mock token格式：mock-token-{userId}）
        if (token != null && !token.isEmpty()) {
            try {
                String tokenValue = token.startsWith("Bearer ") ? token.substring(7) : token;
                if (tokenValue.startsWith("mock-token-")) {
                    Long userId = Long.parseLong(tokenValue.substring("mock-token-".length()));
                    User user = userService.getById(userId);
                    if (user != null) {
                        result.put("userId", user.getId());
                        result.put("username", user.getUsername());
                        result.put("realName", user.getRealName());
                        result.put("role", user.getRole());
                        result.put("avatar", user.getAvatar());
                        return Result.success(result);
                    }
                }
            } catch (NumberFormatException e) {
                // token解析失败，使用默认值
            }
        }

        // 如果无法解析token，返回默认值
        result.put("username", "admin");
        result.put("realName", "系统管理员");
        result.put("role", "admin");
        return Result.success(result);
    }

    @GetMapping("/list")
    public Result<PageResult<User>> list(@RequestParam(defaultValue = "1") Integer current,
                                          @RequestParam(defaultValue = "10") Integer size) {
        var page = userService.pageList(current, size);
        return Result.success(new PageResult<>(page.getTotal(), page.getRecords()));
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody User user) {
        if (userService.getByUsername(user.getUsername()) != null) {
            return Result.error("用户名已存在");
        }
        userService.register(user);
        return Result.success(null, "注册成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success(null, "删除成功");
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
