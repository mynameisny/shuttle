package me.ningyu.app.shuttle.notification.application;

import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.notification.domain.Notification;
import me.ningyu.app.shuttle.notification.domain.NotificationInbox;
import me.ningyu.app.shuttle.notification.domain.NotificationInboxRepository;
import me.ningyu.app.shuttle.notification.domain.NotificationRecipient;
import me.ningyu.app.shuttle.notification.domain.NotificationRecipientRepository;
import me.ningyu.app.shuttle.notification.domain.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知查询服务（供运营管理端读取通知与站内信）。
 */
@Service
@RequiredArgsConstructor
public class NotificationQueryService
{
    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository notificationRecipientRepository;
    private final NotificationInboxRepository inboxRepository;

    @Transactional(readOnly = true)
    public List<Notification> listNotifications()
    {
        return notificationRepository.findAllByOrderByCreatedDateDesc();
    }

    @Transactional(readOnly = true)
    public Notification getNotification(Long id)
    {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("通知不存在: id=" + id));
    }

    @Transactional(readOnly = true)
    public List<NotificationRecipient> listRecipients(Long notificationId)
    {
        return notificationRecipientRepository.findByNotificationId(notificationId);
    }

    @Transactional(readOnly = true)
    public List<NotificationInbox> listInbox(String recipientId)
    {
        return inboxRepository.findByRecipientIdOrderByCreatedDateDesc(recipientId);
    }

    @Transactional(readOnly = true)
    public long unreadCount(String recipientId)
    {
        return inboxRepository.countByRecipientIdAndIsReadFalse(recipientId);
    }

    @Transactional
    public NotificationInbox markRead(Long inboxId)
    {
        NotificationInbox inbox = inboxRepository.findById(inboxId)
                .orElseThrow(() -> new IllegalArgumentException("站内信不存在: id=" + inboxId));
        inbox.setIsRead(true);
        inbox.setReadAt(LocalDateTime.now());
        return inboxRepository.save(inbox);
    }
}
