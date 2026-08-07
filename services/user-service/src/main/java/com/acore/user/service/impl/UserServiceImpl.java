package com.acore.user.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.acore.shared.exception.BusinessException;
import com.acore.shared.exception.ErrorCode;
import com.acore.user.config.JwtProperties;
import com.acore.user.dto.*;
import com.acore.user.entity.SysUserEntity;
import com.acore.user.repository.UserMapper;
import com.acore.user.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 用户服务实现
 *
 * @author acore
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtProperties jwtProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO register(RegisterReq req) {
        log.info("用户注册: username={}", req.getUsername());

        // 检查用户名是否已存在
        if (userMapper.existsByUsername(req.getUsername())) {
            throw new BusinessException(ErrorCode.USER_EXISTS);
        }

        // 创建用户
        SysUserEntity entity = new SysUserEntity();
        entity.setUsername(req.getUsername());
        entity.setPassword(BCrypt.hashpw(req.getPassword()));
        entity.setEmail(req.getEmail());
        entity.setRole("user");
        entity.setStatus(1);

        userMapper.insert(entity);
        log.info("用户注册成功: userId={}", entity.getId());

        return UserVO.fromEntity(entity);
    }

    @Override
    public LoginResp login(LoginReq req) {
        log.info("用户登录: username={}", req.getUsername());

        // 查询用户
        SysUserEntity user = userMapper.findByUsername(req.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 校验密码
        if (!BCrypt.checkpw(req.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.USER_PASSWORD_ERROR);
        }

        // 检查账号状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }

        // 生成 Token
        String token = generateToken(user);
        log.info("用户登录成功: userId={}", user.getId());

        return LoginResp.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getExpiration() / 1000)
                .user(UserVO.fromEntity(user))
                .build();
    }

    @Override
    public LoginResp refreshToken(String token) {
        log.info("刷新令牌");

        try {
            SecretKey key = Keys.hmacShaKeyFor(
                    jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Long userId = Long.parseLong(claims.getSubject());
            SysUserEntity user = userMapper.selectById(userId);
            if (user == null) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }

            String newToken = generateToken(user);
            log.info("令牌刷新成功: userId={}", userId);

            return LoginResp.builder()
                    .accessToken(newToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtProperties.getExpiration() / 1000)
                    .user(UserVO.fromEntity(user))
                    .build();

        } catch (JwtException e) {
            throw new BusinessException(ErrorCode.TOKEN_REFRESH_FAILED);
        }
    }

    @Override
    public UserVO getById(Long id) {
        SysUserEntity user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return UserVO.fromEntity(user);
    }

    @Override
    public UserVO getByUsername(String username) {
        SysUserEntity user = userMapper.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return UserVO.fromEntity(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO update(Long id, UserUpdateReq req) {
        log.info("更新用户信息: userId={}", id);

        SysUserEntity user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        if (req.getEmail() != null) {
            user.setEmail(req.getEmail());
        }
        if (req.getAvatar() != null) {
            user.setAvatar(req.getAvatar());
        }
        if (req.getRole() != null) {
            user.setRole(req.getRole());
        }
        if (req.getStatus() != null) {
            user.setStatus(req.getStatus());
        }

        userMapper.updateById(user);
        log.info("用户更新成功: userId={}", id);

        return UserVO.fromEntity(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        log.info("删除用户: userId={}", id);

        SysUserEntity user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        userMapper.deleteById(id);
        log.info("用户删除成功: userId={}", id);
    }

    @Override
    public UserVO getCurrentUser(Long userId) {
        return getById(userId);
    }

    /**
     * 生成 JWT Token
     */
    private String generateToken(SysUserEntity user) {
        SecretKey key = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("username", user.getUsername())
                .claim("role", user.getRole())
                .issuedAt(new Date(now))
                .expiration(new Date(now + jwtProperties.getExpiration()))
                .signWith(key)
                .compact();
    }
}