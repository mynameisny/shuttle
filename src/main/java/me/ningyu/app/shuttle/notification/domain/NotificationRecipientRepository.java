package me.ningyu.app.shuttle.notification.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRecipientRepository extends JpaRepository<NotificationRecipient, Long>
{
    List<NotificationRecipient> findByNotificationId(Long notificationId);
}
