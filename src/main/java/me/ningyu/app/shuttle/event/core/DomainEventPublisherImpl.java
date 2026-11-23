package me.ningyu.app.shuttle.event.core;

import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.event.api.DomainEvent;
import me.ningyu.app.shuttle.event.api.DomainEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

/**
 * 领域事件发布器实现。
 *
 * <p>负责生成统一事件信封（事件 ID、来源、时间、操作人等），
 * 并借助 Spring 应用内事件机制同步广播。{@code EventBoxListener}
 * 在同一业务事务内监听并写入 EventBox，保证业务数据与事件记录原子提交。
 *
 * <p>本项目单机构（Silo）、无登录用户上下文，operator 默认 {@code system}，
 * triggerMode 默认 {@code AUTO}，tenantId / correlationId 暂为空，后续按需扩展上下文注入。
 */
@Service
@RequiredArgsConstructor
public class DomainEventPublisherImpl implements DomainEventPublisher
{
    private static final String SOURCE = "shuttle-management";

    private final ApplicationEventPublisher applicationEventPublisher;
    private final EventTypeRegistry eventTypeRegistry;

    @Override
    public void publish(DomainEvent event)
    {
        applicationEventPublisher.publishEvent(buildEnvelope(event));
    }

    @Override
    public void publishAll(Collection<DomainEvent> events)
    {
        events.forEach(this::publish);
    }

    private EventEnvelope buildEnvelope(DomainEvent event)
    {
        String eventType = event.getEventType();
        if (!eventTypeRegistry.isRegistered(eventType))
        {
            throw new IllegalArgumentException("未注册的事件类型: " + eventType);
        }
        LocalDateTime now = LocalDateTime.now();
        return EventEnvelope.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .eventVersion(event.getEventVersion())
                .source(SOURCE)
                .subject(event.getSubject())
                .occurredAt(now)
                .publishedAt(now)
                .operator("system")
                .triggerMode("AUTO")
                .data(event.getData())
                .build();
    }
}
