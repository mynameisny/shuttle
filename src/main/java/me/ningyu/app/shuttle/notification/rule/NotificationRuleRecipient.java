package me.ningyu.app.shuttle.notification.rule;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;

/**
 * 规则与接收人配置关系。
 *
 * <p>{@code resolverType} 引用接收人解析器（如 TRIP_DRIVER / TRIP_CONDUCTOR / CUSTOM_USERS）；
 * {@code customValue} 用于解析器参数（如 CUSTOM_USERS 的手机号，逗号分隔）。
 */
@Entity(name = "sys_notification_rule_recipient")
@Table(name = "sys_notification_rule_recipient")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRuleRecipient extends AbstractAuditable
{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false, columnDefinition = "BIGINT COMMENT '规则'")
    private NotificationRule rule;

    /** 接收人解析器类型。 */
    @Column(name = "resolver_type", nullable = false, length = 64, columnDefinition = "VARCHAR(64) COMMENT '解析器类型'")
    private String resolverType;

    /** 解析器参数（如自定义手机号，逗号分隔）。 */
    @Column(name = "custom_value", length = 512, columnDefinition = "VARCHAR(512) COMMENT '解析器参数'")
    private String customValue;

    @Column(name = "sort_order", columnDefinition = "INT DEFAULT 0 COMMENT '顺序'")
    @Builder.Default
    private Integer sortOrder = 0;
}
