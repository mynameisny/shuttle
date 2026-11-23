package me.ningyu.app.shuttle.event.eventbox;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventBoxRecordRepository extends JpaRepository<EventBoxRecord, Long>
{
    boolean existsByEventId(String eventId);
}
