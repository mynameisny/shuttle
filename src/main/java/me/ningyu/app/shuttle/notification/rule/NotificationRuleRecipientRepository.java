package me.ningyu.app.shuttle.notification.rule;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRuleRecipientRepository extends JpaRepository<NotificationRuleRecipient, Long>
{
    List<NotificationRuleRecipient> findByRuleIdOrderBySortOrderAsc(Long ruleId);
}
