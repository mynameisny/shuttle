package me.ningyu.app.shuttle.notification.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.event.core.EventEnvelope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 通知事件监听器：在业务事务提交后异步消费事件信封，触发通知编排。
 *
 * <p>使用 {@code AFTER_COMMIT} 确保仅在业务事务与 EventBox 已提交后才处理通知，
 * 通知处理失败不影响已提交的业务事务。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener
{
    private final NotificationService notificationService;

    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEvent(EventEnvelope envelope)
    {
        try
        {
            notificationService.process(envelope);
        }
        catch (Exception e)
        {
            log.error("通知处理失败: eventType={}, eventId={}", envelope.getEventType(), envelope.getEventId(), e);
        }
    }
}
