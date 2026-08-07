package com.acore.notification.dto;

import com.acore.notification.entity.NotificationRecordEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 通知记录视图对象
 *
 * @author acore
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationVO {

    private Long id;
    private Long pullRequestId;
    private String notifyType;
    private String receiver;
    private String title;
    private String content;
    private String status;
    private String errorMessage;
    private LocalDateTime createdAt;

    public static NotificationVO fromEntity(NotificationRecordEntity entity) {
        return NotificationVO.builder()
                .id(entity.getId())
                .pullRequestId(entity.getPullRequestId())
                .notifyType(entity.getNotifyType())
                .receiver(entity.getReceiver())
                .title(entity.getTitle())
                .content(entity.getContent())
                .status(entity.getStatus())
                .errorMessage(entity.getErrorMessage())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}