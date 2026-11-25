package me.ningyu.app.shuttle.notification.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long>
{
    boolean existsByRuleIdAndEventId(Long ruleId, String eventId);

    Optional<Notification> findByNotificationId(String notificationId);

    List<Notification> findAllByOrderByCreatedDateDesc();
}
