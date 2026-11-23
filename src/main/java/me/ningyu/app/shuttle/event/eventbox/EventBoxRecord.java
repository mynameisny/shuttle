package me.ningyu.app.shuttle.event.eventbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import me.ningyu.app.shuttle.event.core.EventEnvelope;

import java.time.LocalDateTime;

/**
 * 事务性事件记录（Transactional Outbox）。
 *
 * <p>业务数据与 EventBox 记录在同一个本地数据库事务中提交或回滚；
 * 记录一旦提交即视为不可变事件事实。后续由 CDC 将已提交事件广播到 Kafka。
 *
 * <p>{@code created_date}（继承自 {@link AbstractAuditable}）即记录创建时间，
 * 对应设计文档的 {@code created_at}。
 */
@Entity(name = "sys_event_box")
@Table(name = "sys_event_box",
        indexes = {
                @Index(name = "idx_sys_event_box_type_created", columnList = "event_type, created_date"),
                @Index(name = "idx_sys_event_box_subject", columnList = "subject")
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventBoxRecord extends AbstractAuditable
{
    @Column(name = "event_id", nullable = false, unique = true, length = 64,
            columnDefinition = "VARCHAR(64) COMMENT '全局事件ID'")
    private String eventId;

    @Column(name = "event_type", nullable = false, length = 128,
            columnDefinition = "VARCHAR(128) COMMENT '事件编码'")
    private String eventType;

    @Column(name = "event_version", columnDefinition = "INT COMMENT '事件版本'")
    private Integer eventVersion;

    @Column(name = "source", length = 128, columnDefinition = "VARCHAR(128) COMMENT '来源系统'")
    private String source;

    @Column(name = "subject", length = 256, columnDefinition = "VARCHAR(256) COMMENT '业务实体标识'")
    private String subject;

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT COMMENT '事件信封JSON'")
    private String payload;

    @Column(name = "operator", length = 128, columnDefinition = "VARCHAR(128) COMMENT '操作人'")
    private String operator;

    @Column(name = "trigger_mode", length = 16, columnDefinition = "VARCHAR(16) COMMENT '触发模式(AUTO/MANUAL)'")
    private String triggerMode;

    @Column(name = "tenant_id", length = 64, columnDefinition = "VARCHAR(64) COMMENT '租户标识'")
    private String tenantId;

    @Column(name = "correlation_id", length = 64, columnDefinition = "VARCHAR(64) COMMENT '关联ID'")
    private String correlationId;

    @Column(name = "occurred_at", columnDefinition = "DATETIME COMMENT '业务发生时间'")
    private LocalDateTime occurredAt;

    public static EventBoxRecord from(EventEnvelope envelope, String payloadJson)
    {
        return EventBoxRecord.builder()
                .eventId(envelope.getEventId())
                .eventType(envelope.getEventType())
                .eventVersion(envelope.getEventVersion())
                .source(envelope.getSource())
                .subject(envelope.getSubject())
                .payload(payloadJson)
                .operator(envelope.getOperator())
                .triggerMode(envelope.getTriggerMode())
                .tenantId(envelope.getTenantId())
                .correlationId(envelope.getCorrelationId())
                .occurredAt(envelope.getOccurredAt())
                .build();
    }
}
