package com.acore.user.controller;

import com.acore.shared.dto.Result;
import com.acore.user.dto.*;
import com.acore.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 用户控制器单元测试
 *
 * @author acore
 */
@SpringBootTest(classes = com.acore.user.UserServiceApplication.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private UserController userController;

    @MockBean
    private UserService userService;

    private UserVO mockUserVO;

    @BeforeEach
    void setUp() {
        mockUserVO = UserVO.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .role("user")
                .status(1)
                .build();
    }

    @Test
    @DisplayName("注册接口")
    void register() {
        RegisterReq req = new RegisterReq();
        req.setUsername("newuser");
        req.setPassword("test123");
        req.setEmail("new@example.com");

        when(userService.register(any())).thenReturn(mockUserVO);

        Result<UserVO> result = userController.register(req);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals("testuser", result.getData().getUsername());
    }

    @Test
    @DisplayName("登录接口")
    void login() {
        LoginReq req = new LoginReq();
        req.setUsername("testuser");
        req.setPassword("test123");

        LoginResp resp = LoginResp.builder()
                .accessToken("test-token")
                .tokenType("Bearer")
                .expiresIn(86400L)
                .user(mockUserVO)
                .build();

        when(userService.login(any())).thenReturn(resp);

        Result<LoginResp> result = userController.login(req);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals("test-token", result.getData().getAccessToken());
    }

    @Test
    @DisplayName("获取当前用户信息")
    void getCurrentUser() {
        when(userService.getCurrentUser(1L)).thenReturn(mockUserVO);

        Result<UserVO> result = userController.getCurrentUser(1L);

        assertEquals(200, result.getCode());
        assertEquals("testuser", result.getData().getUsername());
    }

    @Test
    @DisplayName("根据 ID 获取用户")
    void getById() {
        when(userService.getById(1L)).thenReturn(mockUserVO);

        Result<UserVO> result = userController.getById(1L);

        assertEquals(200, result.getCode());
        assertEquals(1L, result.getData().getId());
    }

    @Test
    @DisplayName("更新用户")
    void update() {
        UserUpdateReq req = new UserUpdateReq();
        req.setEmail("new@example.com");

        when(userService.update(eq(1L), any())).thenReturn(mockUserVO);

        Result<UserVO> result = userController.update(1L, req);

        assertEquals(200, result.getCode());
    }

    @Test
    @DisplayName("删除用户")
    void delete() {
        doNothing().when(userService).delete(1L);

        Result<Void> result = userController.delete(1L);

        assertEquals(200, result.getCode());
        verify(userService).delete(1L);
    }
}