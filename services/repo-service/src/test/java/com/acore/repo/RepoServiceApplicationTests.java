package com.acore.repo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 仓库服务启动测试
 *
 * @author acore
 */
@SpringBootTest
@ActiveProfiles("test")
class RepoServiceApplicationTests {

    @Test
    void contextLoads() {
        // 验证 Spring 上下文加载成功
    }
}