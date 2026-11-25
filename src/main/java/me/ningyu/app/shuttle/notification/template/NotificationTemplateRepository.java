package me.ningyu.app.shuttle.notification.template;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long>
{
    Optional<NotificationTemplate> findByTemplateCode(String templateCode);
}
