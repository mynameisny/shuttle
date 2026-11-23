package me.ningyu.app.shuttle.event.eventbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.event.core.EventEnvelope;
import me.ningyu.app.shuttle.event.core.EventSerializer;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * EventBox 监听器：在业务事务内同步写入事件记录。
 *
 * <p>约束（与设计文档一致）：
 * <ul>
 *   <li>发布事件的业务方法必须处于 {@code @Transactional} 事务中；</li>
 *   <li>本监听器同步执行，<strong>不使用</strong> {@code @Async}；</li>
 *   <li>EventBox 与业务数据共用同一数据库与事务管理器；</li>
 *   <li>不使用 {@code REQUIRES_NEW}；</li>
 *   <li>写入异常向上传播，使业务事务回滚；</li>
 *   <li>不使用 {@code @TransactionalEventListener(AFTER_COMMIT)}（那无法保证原子性）。</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EventBoxListener
{
    private final EventBoxRecordRepository repository;
    private final EventSerializer serializer;

    @EventListener
    public void onEnvelope(EventEnvelope envelope)
    {
        EventBoxRecord record = EventBoxRecord.from(envelope, serializer.toJson(envelope));
        repository.save(record);
        log.debug("EventBox 已写入事件: type={}, eventId={}", envelope.getEventType(), envelope.getEventId());
    }
}
