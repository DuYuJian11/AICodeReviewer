package com.acore.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应
 *
 * @author acore
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResp {

    /** 访问令牌 */
    private String accessToken;

    /** 令牌类型 */
    private String tokenType;

    /** 过期时间（秒） */
    private long expiresIn;

    /** 用户信息 */
    private UserVO user;
}