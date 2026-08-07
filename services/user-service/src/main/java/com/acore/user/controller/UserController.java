package com.acore.user.controller;

import com.acore.shared.dto.Result;
import com.acore.user.dto.*;
import com.acore.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 *
 * @author acore
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterReq req) {
        return Result.success("注册成功", userService.register(req));
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResp> login(@Valid @RequestBody LoginReq req) {
        return Result.success("登录成功", userService.login(req));
    }

    /**
     * 刷新令牌
     */
    @PostMapping("/refresh")
    public Result<LoginResp> refreshToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return Result.success(userService.refreshToken(token));
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public Result<UserVO> getCurrentUser(@RequestHeader("X-User-Id") Long userId) {
        return Result.success(userService.getCurrentUser(userId));
    }

    /**
     * 根据 ID 获取用户（管理员）
     */
    @GetMapping("/{id}")
    public Result<UserVO> getById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    /**
     * 更新用户信息（管理员）
     */
    @PutMapping("/{id}")
    public Result<UserVO> update(@PathVariable Long id, @Valid @RequestBody UserUpdateReq req) {
        return Result.success(userService.update(id, req));
    }

    /**
     * 删除用户（管理员）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success("删除成功");
    }
}