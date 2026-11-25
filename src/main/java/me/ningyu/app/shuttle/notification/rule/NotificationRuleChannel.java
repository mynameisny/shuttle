package me.ningyu.app.shuttle.notification.rule;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import me.ningyu.app.shuttle.notification.ChannelType;

/**
 * 规则与渠道关系（一条规则可选多个渠道，按 sort_order 编排）。
 */
@Entity(name = "sys_notification_rule_channel")
@Table(name = "sys_notification_rule_channel")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRuleChannel extends AbstractAuditable
{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false, columnDefinition = "BIGINT COMMENT '规则'")
    private NotificationRule rule;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 16, columnDefinition = "VARCHAR(16) COMMENT '渠道'")
    private ChannelType channel;

    @Column(name = "sort_order", columnDefinition = "INT DEFAULT 0 COMMENT '渠道顺序'")
    @Builder.Default
    private Integer sortOrder = 0;
}
