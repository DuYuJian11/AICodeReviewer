package com.acore.notification.controller;

import com.acore.notification.dto.NotificationVO;
import com.acore.notification.dto.SendNotificationReq;
import com.acore.notification.service.NotificationService;
import com.acore.shared.dto.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 通知控制器
 *
 * @author acore
 */
@Slf4j
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 发送通知
     */
    @PostMapping("/send")
    public Result<NotificationVO> send(@Valid @RequestBody SendNotificationReq req) {
        return Result.success(notificationService.send(req));
    }

    /**
     * 发送邮件
     */
    @PostMapping("/send/mail")
    public Result<NotificationVO> sendMail(@RequestParam String to,
                                            @RequestParam String title,
                                            @RequestParam String content,
                                            @RequestParam(required = false) Long pullRequestId) {
        return Result.success(notificationService.sendMail(to, title, content, pullRequestId));
    }

    /**
     * 发送钉钉通知
     */
    @PostMapping("/send/dingtalk")
    public Result<NotificationVO> sendDingTalk(@RequestParam String webhookUrl,
                                                @RequestParam String title,
                                                @RequestParam String content,
                                                @RequestParam(required = false) Long pullRequestId) {
        return Result.success(notificationService.sendDingTalk(webhookUrl, title, content, pullRequestId));
    }

    /**
     * 发送企业微信通知
     */
    @PostMapping("/send/wechat")
    public Result<NotificationVO> sendWeChat(@RequestParam String webhookUrl,
                                              @RequestParam String title,
                                              @RequestParam String content,
                                              @RequestParam(required = false) Long pullRequestId) {
        return Result.success(notificationService.sendWeChat(webhookUrl, title, content, pullRequestId));
    }

    /**
     * 获取 PR 的通知记录
     */
    @GetMapping("/list/{pullRequestId}")
    public Result<List<NotificationVO>> getNotifications(@PathVariable Long pullRequestId) {
        return Result.success(notificationService.getNotifications(pullRequestId));
    }
}