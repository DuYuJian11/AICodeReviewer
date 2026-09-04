package com.acore.notification.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知记录实体
 *
 * @author acore
 */
@Data
@TableName("notification_record")
public class NotificationRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联 PR ID */
    private Long pullRequestId;

    /** 通知类型：mail / dingtalk / wechat */
    private String notifyType;

    /** 接收人/群组 */
    private String receiver;

    /** 标题 */
    private String title;

    /** 内容 */
    private String content;

    /** 发送状态：pending / sent / failed */
    private String status;

    /** 错误信息 */
    private String errorMessage;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}