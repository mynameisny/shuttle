package me.ningyu.app.shuttle.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;

import java.time.LocalDateTime;

/**
 * 站内信（INBOX 渠道落库）。
 *
 * <p>{@code (notification_id, recipient_id)} 唯一，按用户与通知去重。
 */
@Entity(name = "sys_notification_inbox")
@Table(name = "sys_notification_inbox",
        uniqueConstraints = @UniqueConstraint(name = "uk_inbox_notification_recipient",
                columnNames = {"notification_id", "recipient_id"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationInbox extends AbstractAuditable
{
    /** 通知主键（引用 sys_notification.id）。 */
    @Column(name = "notification_id", nullable = false, columnDefinition = "BIGINT COMMENT '通知ID'")
    private Long notificationId;

    /** 接收人标识。 */
    @Column(name = "recipient_id", nullable = false, length = 64, columnDefinition = "VARCHAR(64) COMMENT '接收人标识'")
    private String recipientId;

    /** 接收人姓名。 */
    @Column(name = "recipient_name", length = 64, columnDefinition = "VARCHAR(64) COMMENT '接收人姓名'")
    private String recipientName;

    /** 标题。 */
    @Column(name = "title", length = 255, columnDefinition = "VARCHAR(255) COMMENT '标题'")
    private String title;

    /** 正文。 */
    @Column(name = "content", columnDefinition = "TEXT COMMENT '正文'")
    private String content;

    /** 是否已读。 */
    @Column(name = "is_read", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0 COMMENT '是否已读'")
    @Builder.Default
    private Boolean isRead = false;

    /** 已读时间。 */
    @Column(name = "read_at", columnDefinition = "DATETIME COMMENT '已读时间'")
    private LocalDateTime readAt;
}
