package me.ningyu.app.shuttle.event.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 统一事件信封（借鉴 CloudEvents 设计思想）。
 *
 * <p>{@code eventType} 使用稳定字符串编码；{@code data} 为业务事实快照，
 * 消费方据此理解事件，无需回查可能已变化的实体当前状态。
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventEnvelope
{
    /** 全局唯一事件 ID。 */
    private String eventId;

    /** 事件编码，例如 {@code trip.cancelled}。 */
    private String eventType;

    /** 事件数据版本。 */
    private int eventVersion;

    /** 事件来源系统。 */
    private String source;

    /** 业务实体标识，例如 {@code trip/10001}。 */
    private String subject;

    /** 业务事实发生时间。 */
    private LocalDateTime occurredAt;

    /** 事件发布到应用内事件机制的时间。 */
    private LocalDateTime publishedAt;

    /** 操作人或系统主体。 */
    private String operator;

    /** 事件产生方式：AUTO / MANUAL。 */
    private String triggerMode;

    /** 租户标识（本项目单机构，通常为空）。 */
    private String tenantId;

    /** 关联业务操作 ID。 */
    private String correlationId;

    /** 事件业务数据快照。 */
    private Object data;
}
