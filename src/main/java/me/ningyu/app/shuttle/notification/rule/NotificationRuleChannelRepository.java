package me.ningyu.app.shuttle.notification.rule;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRuleChannelRepository extends JpaRepository<NotificationRuleChannel, Long>
{
    List<NotificationRuleChannel> findByRuleIdOrderBySortOrderAsc(Long ruleId);
}
