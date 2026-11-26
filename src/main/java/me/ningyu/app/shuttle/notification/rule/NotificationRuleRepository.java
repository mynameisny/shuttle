package me.ningyu.app.shuttle.notification.rule;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRuleRepository extends JpaRepository<NotificationRule, Long>
{
    List<NotificationRule> findByEventTypeAndEnabledTrueOrderByPriorityAsc(String eventType);

    Optional<NotificationRule> findByRuleCode(String ruleCode);

    boolean existsByRuleCode(String ruleCode);

    boolean existsByTemplateCode(String templateCode);
}
