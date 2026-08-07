package com.acore.notification.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import com.acore.shared.exception.BusinessException;
import com.acore.shared.exception.ErrorCode;
import com.acore.notification.dto.NotificationVO;
import com.acore.notification.dto.SendNotificationReq;
import com.acore.notification.entity.NotificationRecordEntity;
import com.acore.notification.repository.NotificationRecordMapper;
import com.acore.notification.service.NotificationService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 通知服务实现
 *
 * @author acore
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRecordMapper notificationRecordMapper;
    private final JavaMailSender mailSender;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NotificationVO send(SendNotificationReq req) {
        log.info("发送通知: type={}, receiver={}, title={}", req.getNotifyType(), req.getReceiver(), req.getTitle());

        return switch (req.getNotifyType()) {
            case "mail" -> sendMail(req.getReceiver(), req.getTitle(), req.getContent(), req.getPullRequestId());
            case "dingtalk" -> sendDingTalk(req.getReceiver(), req.getTitle(), req.getContent(), req.getPullRequestId());
            case "wechat" -> sendWeChat(req.getReceiver(), req.getTitle(), req.getContent(), req.getPullRequestId());
            default -> throw new BusinessException(ErrorCode.NOTIFICATION_SEND_FAILED.getCode(), "不支持的通知类型: " + req.getNotifyType());
        };
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NotificationVO sendMail(String to, String title, String content, Long pullRequestId) {
        log.info("发送邮件通知: to={}, title={}", to, title);

        NotificationRecordEntity record = createRecord(pullRequestId, "mail", to, title, content);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(title);
            helper.setText(content, true);
            mailSender.send(message);

            record.setStatus("sent");
            notificationRecordMapper.updateById(record);
            log.info("邮件发送成功: to={}", to);

        } catch (Exception e) {
            log.error("邮件发送失败: {}", e.getMessage());
            record.setStatus("failed");
            record.setErrorMessage(e.getMessage());
            notificationRecordMapper.updateById(record);
        }

        return NotificationVO.fromEntity(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NotificationVO sendDingTalk(String webhookUrl, String title, String content, Long pullRequestId) {
        log.info("发送钉钉通知: webhookUrl={}", webhookUrl);

        NotificationRecordEntity record = createRecord(pullRequestId, "dingtalk", webhookUrl, title, content);

        try {
            JSONObject body = new JSONObject();
            JSONObject markdown = new JSONObject();
            markdown.set("title", title);
            markdown.set("text", content);
            body.set("msgtype", "markdown");
            body.set("markdown", markdown);

            String response = HttpRequest.post(webhookUrl)
                    .header("Content-Type", "application/json")
                    .body(body.toString())
                    .timeout(10000)
                    .execute()
                    .body();

            log.info("钉钉通知响应: {}", response);
            record.setStatus("sent");
            notificationRecordMapper.updateById(record);

        } catch (Exception e) {
            log.error("钉钉通知发送失败: {}", e.getMessage());
            record.setStatus("failed");
            record.setErrorMessage(e.getMessage());
            notificationRecordMapper.updateById(record);
        }

        return NotificationVO.fromEntity(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NotificationVO sendWeChat(String webhookUrl, String title, String content, Long pullRequestId) {
        log.info("发送企业微信通知: webhookUrl={}", webhookUrl);

        NotificationRecordEntity record = createRecord(pullRequestId, "wechat", webhookUrl, title, content);

        try {
            JSONObject body = new JSONObject();
            JSONObject markdown = new JSONObject();
            markdown.set("content", content);
            body.set("msgtype", "markdown");
            body.set("markdown", markdown);

            String response = HttpRequest.post(webhookUrl)
                    .header("Content-Type", "application/json")
                    .body(body.toString())
                    .timeout(10000)
                    .execute()
                    .body();

            log.info("企业微信通知响应: {}", response);
            record.setStatus("sent");
            notificationRecordMapper.updateById(record);

        } catch (Exception e) {
            log.error("企业微信通知发送失败: {}", e.getMessage());
            record.setStatus("failed");
            record.setErrorMessage(e.getMessage());
            notificationRecordMapper.updateById(record);
        }

        return NotificationVO.fromEntity(record);
    }

    @Override
    public List<NotificationVO> getNotifications(Long pullRequestId) {
        return notificationRecordMapper.findByPullRequestId(pullRequestId).stream()
                .map(NotificationVO::fromEntity)
                .toList();
    }

    private NotificationRecordEntity createRecord(Long pullRequestId, String type, String receiver, String title, String content) {
        NotificationRecordEntity record = new NotificationRecordEntity();
        record.setPullRequestId(pullRequestId);
        record.setNotifyType(type);
        record.setReceiver(receiver);
        record.setTitle(title);
        record.setContent(content);
        record.setStatus("pending");
        notificationRecordMapper.insert(record);
        return record;
    }
}