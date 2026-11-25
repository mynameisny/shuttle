package me.ningyu.app.shuttle.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import me.ningyu.app.shuttle.notification.NotificationStatus;
import me.ningyu.app.shuttle.notification.Severity;

import java.time.LocalDateTime;

/**
 * 通知主记录。
 *
 * <p>{@code (rule_id, event_id)} 唯一，作为规则执行幂等键，防止重复消费/重放导致重复生成通知。
 * 执行时保存规则编码/名称/版本与级别快照，规则后续变更不影响历史通知。
 */
@Entity(name = "sys_notification")
@Table(name = "sys_notification",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_notification_rule_event", columnNames = {"rule_id", "event_id"}),
                @UniqueConstraint(name = "uk_notification_notification_id", columnNames = "notification_id")
        },
        indexes = {
                @Index(name = "idx_notification_event_id", columnList = "event_id"),
                @Index(name = "idx_notification_event_type", columnList = "event_type")
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends AbstractAuditable
{
    /** 业务唯一通知 ID（UUID）。 */
    @Column(name = "notification_id", nullable = false, length = 64, columnDefinition = "VARCHAR(64) COMMENT '通知ID'")
    private String notificationId;

    /** 命中的规则主键。 */
    @Column(name = "rule_id", nullable = false, columnDefinition = "BIGINT COMMENT '规则ID'")
    private Long ruleId;

    /** 规则编码快照。 */
    @Column(name = "rule_code", length = 64, columnDefinition = "VARCHAR(64) COMMENT '规则编码快照'")
    private String ruleCode;

    /** 规则名称快照。 */
    @Column(name = "rule_name", length = 128, columnDefinition = "VARCHAR(128) COMMENT '规则名称快照'")
    private String ruleName;

    /** 规则版本快照。 */
    @Column(name = "rule_version", columnDefinition = "INT COMMENT '规则版本快照'")
    private Integer ruleVersion;

    /** 触发通知的事件 ID。 */
    @Column(name = "event_id", nullable = false, length = 64, columnDefinition = "VARCHAR(64) COMMENT '事件ID'")
    private String eventId;

    /** 事件编码。 */
    @Column(name = "event_type", length = 128, columnDefinition = "VARCHAR(128) COMMENT '事件编码'")
    private String eventType;

    /** 业务实体标识。 */
    @Column(name = "subject", length = 256, columnDefinition = "VARCHAR(256) COMMENT '业务实体标识'")
    private String subject;

    /** 通知级别快照。 */
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", length = 16, columnDefinition = "VARCHAR(16) COMMENT '通知级别'")
    private Severity severity;

    /** 通知状态。 */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32, columnDefinition = "VARCHAR(32) COMMENT '通知状态'")
    private NotificationStatus status;

    /** 渲染后的标题。 */
    @Column(name = "title", length = 255, columnDefinition = "VARCHAR(255) COMMENT '标题'")
    private String title;

    /** 渲染后的正文。 */
    @Column(name = "content", columnDefinition = "TEXT COMMENT '正文'")
    private String content;

    /** 业务事实发生时间。 */
    @Column(name = "occurred_at", columnDefinition = "DATETIME COMMENT '业务发生时间'")
    private LocalDateTime occurredAt;
}
