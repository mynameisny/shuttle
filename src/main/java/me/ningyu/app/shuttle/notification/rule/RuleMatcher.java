package me.ningyu.app.shuttle.notification.rule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 规则匹配器：按事件类型取启用规则，过滤生效时间与条件表达式。
 */
@Component
@RequiredArgsConstructor
public class RuleMatcher
{
    private final NotificationRuleRepository ruleRepository;
    private final ConditionEvaluator conditionEvaluator;

    /**
     * 返回命中事件 {@code eventType} 的全部启用规则（按优先级升序）。
     */
    public List<NotificationRule> match(String eventType, Map<String, Object> data, LocalDateTime now)
    {
        List<NotificationRule> rules = ruleRepository.findByEventTypeAndEnabledTrueOrderByPriorityAsc(eventType);
        return rules.stream()
                .filter(r -> inEffective(r, now))
                .filter(r -> conditionEvaluator.evaluate(r.getConditionExpression(), data))
                .toList();
    }

    private boolean inEffective(NotificationRule rule, LocalDateTime now)
    {
        if (rule.getEffectiveStart() != null && now.isBefore(rule.getEffectiveStart()))
        {
            return false;
        }
        if (rule.getEffectiveEnd() != null && now.isAfter(rule.getEffectiveEnd()))
        {
            return false;
        }
        return true;
    }
}
