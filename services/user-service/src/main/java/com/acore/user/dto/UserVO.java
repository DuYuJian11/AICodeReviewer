package com.acore.user.dto;

import com.acore.user.entity.SysUserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户视图对象（返回给前端，不含密码）
 *
 * @author acore
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {

    private Long id;
    private String username;
    private String email;
    private String avatar;
    private String role;
    private Integer status;

    /**
     * 从实体转换
     */
    public static UserVO fromEntity(SysUserEntity entity) {
        return UserVO.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .avatar(entity.getAvatar())
                .role(entity.getRole())
                .status(entity.getStatus())
                .build();
    }
}