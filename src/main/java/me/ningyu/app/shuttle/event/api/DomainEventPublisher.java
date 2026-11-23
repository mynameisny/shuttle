package me.ningyu.app.shuttle.event.api;

import java.util.Collection;

/**
 * 领域事件发布器（业务模块唯一的事件入口）。
 *
 * <p>实现负责生成统一事件信封（事件 ID、来源、时间、关联信息），
 * 并把事件发布到应用内事件机制；由 {@code EventBoxListener} 在
 * <strong>同一数据库事务</strong>内同步写入 EventBox。
 */
public interface DomainEventPublisher
{
    void publish(DomainEvent event);

    void publishAll(Collection<DomainEvent> events);
}
