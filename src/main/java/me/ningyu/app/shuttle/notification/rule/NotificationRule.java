package me.ningyu.app.shuttle.notification.rule;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import me.ningyu.app.shuttle.notification.DeliveryPolicy;
import me.ningyu.app.shuttle.notification.Severity;
import me.ningyu.app.shuttle.notification.TriggerMode;

import java.time.LocalDateTime;

/**
 * 通知规则主表。
 *
 * <p>一条规则由「事件类型 + 触发模式 + 生效时间 + 条件表达式 + 通知级别 + 发送策略 +
 * 渠道 + 接收人 + 模板」构成。渠道与接收人分别存于
 * {@link NotificationRuleChannel} / {@link NotificationRuleRecipient}。
 */
@Entity(name = "sys_notification_rule")
@Table(name = "sys_notification_rule",
        uniqueConstraints = @UniqueConstraint(name = "uk_notification_rule_code", columnNames = "rule_code"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRule extends AbstractAuditable
{
    /** 规则编码（唯一）。 */
    @Column(name = "rule_code", nullable = false, length = 64, columnDefinition = "VARCHAR(64) COMMENT '规则编码'")
    private String ruleCode;

    /** 规则名称。 */
    @Column(name = "rule_name", nullable = false, length = 128, columnDefinition = "VARCHAR(128) COMMENT '规则名称'")
    private String ruleName;

    /** 事件编码，如 trip.cancelled / driver.status_changed。 */
    @Column(name = "event_type", nullable = false, length = 128, columnDefinition = "VARCHAR(128) COMMENT '事件编码'")
    private String eventType;

    /** 触发模式（AUTO / MANUAL）。 */
    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_mode", nullable = false, length = 16, columnDefinition = "VARCHAR(16) COMMENT '触发模式'")
    private TriggerMode triggerMode;

    /** 通知级别（业务紧急程度，不决定渠道）。 */
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 16, columnDefinition = "VARCHAR(16) COMMENT '通知级别'")
    private Severity severity;

    /** 发送策略。 */
    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_policy", length = 32, columnDefinition = "VARCHAR(32) COMMENT '发送策略'")
    private DeliveryPolicy deliveryPolicy;

    /** 条件表达式（JSON AST，可为空表示无条件）。 */
    @Column(name = "condition_expression", columnDefinition = "TEXT COMMENT '条件表达式(JSON AST)'")
    private String conditionExpression;

    /** 启用状态。 */
    @Column(name = "enabled", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1 COMMENT '是否启用'")
    @Builder.Default
    private Boolean enabled = true;

    /** 规则匹配优先级（越小越先）。 */
    @Column(name = "priority", columnDefinition = "INT DEFAULT 0 COMMENT '匹配优先级'")
    @Builder.Default
    private Integer priority = 0;

    /** 生效开始时间（空 = 不限）。 */
    @Column(name = "effective_start", columnDefinition = "DATETIME COMMENT '生效开始时间'")
    private LocalDateTime effectiveStart;

    /** 生效结束时间（空 = 不限）。 */
    @Column(name = "effective_end", columnDefinition = "DATETIME COMMENT '生效结束时间'")
    private LocalDateTime effectiveEnd;

    /** 规则版本。 */
    @Column(name = "version", columnDefinition = "INT DEFAULT 1 COMMENT '规则版本'")
    @Builder.Default
    private Integer version = 1;

    /** 引用的消息模板编码。 */
    @Column(name = "template_code", length = 64, columnDefinition = "VARCHAR(64) COMMENT '模板编码'")
    private String templateCode;
}
