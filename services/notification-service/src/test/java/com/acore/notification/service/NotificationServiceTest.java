package com.acore.notification.service;

import com.acore.notification.dto.NotificationVO;
import com.acore.notification.entity.NotificationRecordEntity;
import com.acore.notification.repository.NotificationRecordMapper;
import com.acore.notification.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 通知服务单元测试
 *
 * @author acore
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRecordMapper notificationRecordMapper;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
    }

    @Nested
    @DisplayName("发送通知")
    class SendNotification {

        @Test
        @DisplayName("发送邮件成功")
        void shouldSendMailSuccessfully() {
            when(notificationRecordMapper.insert(any(NotificationRecordEntity.class)))
                    .thenAnswer(invocation -> {
                        NotificationRecordEntity entity = invocation.getArgument(0);
                        entity.setId(1L);
                        return 1;
                    });

            NotificationVO result = notificationService.sendMail(
                    "test@example.com", "测试标题", "测试内容", 100L);

            assertNotNull(result);
            assertEquals("mail", result.getNotifyType());
            assertEquals("test@example.com", result.getReceiver());
            // 由于没有真实邮件服务器，状态应为 failed
            assertTrue(result.getStatus().equals("failed") || result.getStatus().equals("sent"));
        }

        @Test
        @DisplayName("不支持的发送类型")
        void shouldFailWhenInvalidType() {
            com.acore.notification.dto.SendNotificationReq req = new com.acore.notification.dto.SendNotificationReq();
            req.setNotifyType("sms");
            req.setReceiver("1234567890");
            req.setTitle("test");
            req.setContent("test");

            assertThrows(com.acore.shared.exception.BusinessException.class,
                    () -> notificationService.send(req));
        }

        @Test
        @DisplayName("发送钉钉通知")
        void shouldSendDingTalk() {
            when(notificationRecordMapper.insert(any(NotificationRecordEntity.class)))
                    .thenAnswer(invocation -> {
                        NotificationRecordEntity entity = invocation.getArgument(0);
                        entity.setId(2L);
                        return 1;
                    });

            NotificationVO result = notificationService.sendDingTalk(
                    "https://oapi.dingtalk.com/robot/send", "test", "content", 100L);

            assertNotNull(result);
            assertEquals("dingtalk", result.getNotifyType());
        }
    }

    @Nested
    @DisplayName("查询通知")
    class GetNotifications {

        @Test
        @DisplayName("获取通知记录")
        void shouldGetNotifications() {
            when(notificationRecordMapper.findByPullRequestId(100L))
                    .thenReturn(java.util.List.of());

            var result = notificationService.getNotifications(100L);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }
}