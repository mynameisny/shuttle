package me.ningyu.app.shuttle.notification.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationInboxRepository extends JpaRepository<NotificationInbox, Long>
{
    List<NotificationInbox> findByRecipientIdOrderByCreatedDateDesc(String recipientId);

    long countByRecipientIdAndIsReadFalse(String recipientId);
}
