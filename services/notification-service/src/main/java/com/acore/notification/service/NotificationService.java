package com.acore.notification.service;

import com.acore.notification.dto.NotificationVO;
import com.acore.notification.dto.SendNotificationReq;

import java.util.List;

/**
 * 通知服务接口
 *
 * @author acore
 */
public interface NotificationService {

    /**
     * 发送通知
     */
    NotificationVO send(SendNotificationReq req);

    /**
     * 发送邮件通知
     */
    NotificationVO sendMail(String to, String title, String content, Long pullRequestId);

    /**
     * 发送钉钉通知
     */
    NotificationVO sendDingTalk(String webhookUrl, String title, String content, Long pullRequestId);

    /**
     * 发送企业微信通知
     */
    NotificationVO sendWeChat(String webhookUrl, String title, String content, Long pullRequestId);

    /**
     * 获取 PR 的通知记录
     */
    List<NotificationVO> getNotifications(Long pullRequestId);
}