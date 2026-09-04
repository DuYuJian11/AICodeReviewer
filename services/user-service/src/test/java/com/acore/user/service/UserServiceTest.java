package com.acore.user.service;

import com.acore.shared.exception.BusinessException;
import com.acore.shared.exception.ErrorCode;
import com.acore.user.UserServiceApplication;
import com.acore.user.config.JwtProperties;
import com.acore.user.dto.LoginReq;
import com.acore.user.dto.LoginResp;
import com.acore.user.dto.RegisterReq;
import com.acore.user.dto.UserUpdateReq;
import com.acore.user.dto.UserVO;
import com.acore.user.entity.SysUserEntity;
import com.acore.user.repository.UserMapper;
import com.acore.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 用户服务单元测试
 *
 * @author acore
 */
@SpringBootTest(classes = UserServiceApplication.class)
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    private UserServiceImpl userService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private JwtProperties jwtProperties;

    private SysUserEntity mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new SysUserEntity();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setPassword("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"); // BCrypt: test123
        mockUser.setEmail("test@example.com");
        mockUser.setRole("user");
        mockUser.setStatus(1);

        when(jwtProperties.getSecret()).thenReturn("test-secret-key-for-unit-testing-only");
        when(jwtProperties.getExpiration()).thenReturn(86400000L);
    }

    @Nested
    @DisplayName("注册")
    class Register {

        @Test
        @DisplayName("注册成功")
        void shouldRegisterSuccessfully() {
            RegisterReq req = new RegisterReq();
            req.setUsername("newuser");
            req.setPassword("test123");
            req.setEmail("new@example.com");

            when(userMapper.existsByUsername("newuser")).thenReturn(false);
            when(userMapper.insert(any(SysUserEntity.class))).thenAnswer(invocation -> {
                SysUserEntity entity = invocation.getArgument(0);
                entity.setId(2L);
                return 1;
            });

            UserVO result = userService.register(req);

            assertNotNull(result);
            assertEquals("newuser", result.getUsername());
            assertEquals("new@example.com", result.getEmail());
            verify(userMapper).insert(any(SysUserEntity.class));
        }

        @Test
        @DisplayName("注册失败：用户名已存在")
        void shouldFailWhenUsernameExists() {
            RegisterReq req = new RegisterReq();
            req.setUsername("testuser");
            req.setPassword("test123");

            when(userMapper.existsByUsername("testuser")).thenReturn(true);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> userService.register(req));
            assertEquals(ErrorCode.USER_EXISTS.getCode(), ex.getCode());
        }
    }

    @Nested
    @DisplayName("登录")
    class Login {

        @Test
        @DisplayName("登录成功")
        void shouldLoginSuccessfully() {
            LoginReq req = new LoginReq();
            req.setUsername("testuser");
            req.setPassword("test123");

            when(userMapper.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

            LoginResp result = userService.login(req);

            assertNotNull(result);
            assertNotNull(result.getAccessToken());
            assertEquals("Bearer", result.getTokenType());
            assertEquals("testuser", result.getUser().getUsername());
        }

        @Test
        @DisplayName("登录失败：用户不存在")
        void shouldFailWhenUserNotFound() {
            LoginReq req = new LoginReq();
            req.setUsername("nonexistent");
            req.setPassword("test123");

            when(userMapper.findByUsername("nonexistent")).thenReturn(Optional.empty());

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> userService.login(req));
            assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("登录失败：密码错误")
        void shouldFailWhenPasswordWrong() {
            LoginReq req = new LoginReq();
            req.setUsername("testuser");
            req.setPassword("wrongpassword");

            when(userMapper.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> userService.login(req));
            assertEquals(ErrorCode.USER_PASSWORD_ERROR.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("登录失败：账号被禁用")
        void shouldFailWhenUserDisabled() {
            mockUser.setStatus(0);

            LoginReq req = new LoginReq();
            req.setUsername("testuser");
            req.setPassword("test123");

            when(userMapper.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> userService.login(req));
            assertEquals(ErrorCode.USER_DISABLED.getCode(), ex.getCode());
        }
    }

    @Nested
    @DisplayName("用户查询")
    class GetUser {

        @Test
        @DisplayName("根据 ID 查询成功")
        void shouldGetById() {
            when(userMapper.selectById(1L)).thenReturn(mockUser);

            UserVO result = userService.getById(1L);
            assertNotNull(result);
            assertEquals("testuser", result.getUsername());
        }

        @Test
        @DisplayName("根据 ID 查询失败：不存在")
        void shouldFailWhenNotFound() {
            when(userMapper.selectById(999L)).thenReturn(null);

            assertThrows(BusinessException.class, () -> userService.getById(999L));
        }
    }

    @Nested
    @DisplayName("用户更新")
    class UpdateUser {

        @Test
        @DisplayName("更新成功")
        void shouldUpdateSuccessfully() {
            UserUpdateReq req = new UserUpdateReq();
            req.setEmail("new@example.com");
            req.setRole("admin");

            when(userMapper.selectById(1L)).thenReturn(mockUser);

            UserVO result = userService.update(1L, req);

            assertEquals("new@example.com", result.getEmail());
            assertEquals("admin", result.getRole());
            verify(userMapper).updateById(any(SysUserEntity.class));
        }
    }

    @Nested
    @DisplayName("删除用户")
    class DeleteUser {

        @Test
        @DisplayName("删除成功")
        void shouldDeleteSuccessfully() {
            when(userMapper.selectById(1L)).thenReturn(mockUser);

            userService.delete(1L);

            verify(userMapper).deleteById(1L);
        }

        @Test
        @DisplayName("删除失败：用户不存在")
        void shouldFailWhenNotFound() {
            when(userMapper.selectById(999L)).thenReturn(null);

            assertThrows(BusinessException.class, () -> userService.delete(999L));
        }
    }
}