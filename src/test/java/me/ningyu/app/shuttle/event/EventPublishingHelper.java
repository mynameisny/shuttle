package me.ningyu.app.shuttle.event;

import me.ningyu.app.shuttle.event.api.DomainEvent;
import me.ningyu.app.shuttle.event.api.DomainEventPublisher;
import org.springframework.transaction.annotation.Transactional;

/**
 * 测试专用：把「发布事件」放进独立事务，验证 EventBox 与业务在事务边界上的原子性。
 */
public class EventPublishingHelper
{
    private final DomainEventPublisher publisher;

    public EventPublishingHelper(DomainEventPublisher publisher)
    {
        this.publisher = publisher;
    }

    @Transactional
    public void publishOk(DomainEvent event)
    {
        publisher.publish(event);
    }

    @Transactional
    public void publishThenFail(DomainEvent event)
    {
        publisher.publish(event);
        throw new RuntimeException("simulated failure after publish");
    }
}
