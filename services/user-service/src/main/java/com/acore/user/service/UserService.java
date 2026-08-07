package com.acore.user.service;

import com.acore.user.dto.*;
import com.acore.user.entity.SysUserEntity;

/**
 * 用户服务接口
 *
 * @author acore
 */
public interface UserService {

    /**
     * 用户注册
     */
    UserVO register(RegisterReq req);

    /**
     * 用户登录
     */
    LoginResp login(LoginReq req);

    /**
     * 刷新令牌
     */
    LoginResp refreshToken(String token);

    /**
     * 根据 ID 获取用户
     */
    UserVO getById(Long id);

    /**
     * 根据用户名获取用户
     */
    UserVO getByUsername(String username);

    /**
     * 更新用户信息
     */
    UserVO update(Long id, UserUpdateReq req);

    /**
     * 删除用户
     */
    void delete(Long id);

    /**
     * 获取当前登录用户信息
     */
    UserVO getCurrentUser(Long userId);
}