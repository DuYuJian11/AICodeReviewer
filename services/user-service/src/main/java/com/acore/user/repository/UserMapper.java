package com.acore.user.repository;

import com.acore.user.entity.SysUserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

/**
 * 用户 Mapper
 *
 * @author acore
 */
@Mapper
public interface UserMapper extends BaseMapper<SysUserEntity> {

    /**
     * 根据用户名查询用户
     */
    default Optional<SysUserEntity> findByUsername(String username) {
        return Optional.ofNullable(
                selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUserEntity>()
                        .eq(SysUserEntity::getUsername, username)));
    }

    /**
     * 判断用户名是否已存在
     */
    default boolean existsByUsername(String username) {
        return selectCount(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUserEntity>()
                .eq(SysUserEntity::getUsername, username)) > 0;
    }
}