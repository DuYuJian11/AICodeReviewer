package com.acore.user.dto;

import lombok.Data;

/**
 * 用户更新请求
 *
 * @author acore
 */
@Data
public class UserUpdateReq {

    private String email;
    private String avatar;
    private String role;
    private Integer status;
}