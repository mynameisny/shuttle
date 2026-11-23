package me.ningyu.app.shuttle.event;

import me.ningyu.app.shuttle.event.api.DomainEvent;
import me.ningyu.app.shuttle.event.api.DomainEventPublisher;
import me.ningyu.app.shuttle.event.domain.TripCancelledEvent;
import me.ningyu.app.shuttle.event.eventbox.EventBoxRecord;
import me.ningyu.app.shuttle.event.eventbox.EventBoxRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * EventBox 事务性 Outbox 核心验收测试：
 * 1) 业务成功 → EventBox 记录与业务同事务提交；
 * 2) 发布后抛异常 → 整个事务回滚，EventBox 不留记录；
 * 3) 未注册事件编码 → 拒绝发布且不落库。
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(EventBoxPublishTests.TestConfig.class)
class EventBoxPublishTests
{
    @Autowired
    private EventPublishingHelper helper;

    @Autowired
    private EventBoxRecordRepository repository;

    @BeforeEach
    void cleanEventBox()
    {
        repository.deleteAll();
    }

    @Test
    void publish_commits_eventbox_in_same_transaction()
    {
        helper.publishOk(new TripCancelledEvent(new TripCancelledEvent.TripCancelledData(
                1L, "1_outbound", "东三旗早班", "1", "东三旗-昌平", 0)));

        assertEquals(1, repository.count());
        EventBoxRecord record = repository.findAll().get(0);
        assertEquals("trip.cancelled", record.getEventType());
        assertEquals("trip/1", record.getSubject());
        assertEquals("shuttle-management", record.getSource());
        assertEquals("AUTO", record.getTriggerMode());
        assertNotNull(record.getEventId());
        assertNotNull(record.getPayload());
        assertTrue(record.getPayload().contains("\"eventType\":\"trip.cancelled\""));
    }

    @Test
    void publish_failure_rolls_back_eventbox()
    {
        long before = repository.count();

        assertThrows(RuntimeException.class, () -> helper.publishThenFail(
                new TripCancelledEvent(new TripCancelledEvent.TripCancelledData(
                        2L, "1_inbound", "东三旗晚班", "1", "东三旗-昌平", 1))));

        assertEquals(before, repository.count());
    }

    @Test
    void unregistered_event_type_is_rejected()
    {
        DomainEvent unknown = new DomainEvent()
        {
            @Override
            public String getEventType()
            {
                return "foo.bar";
            }

            @Override
            public int getEventVersion()
            {
                return 1;
            }

            @Override
            public Object getData()
            {
                return Map.of();
            }
        };

        assertThrows(IllegalArgumentException.class, () -> helper.publishOk(unknown));
        assertEquals(0, repository.count());
    }

    @TestConfiguration
    static class TestConfig
    {
        @Bean
        EventPublishingHelper eventPublishingHelper(DomainEventPublisher publisher)
        {
            return new EventPublishingHelper(publisher);
        }
    }
}
