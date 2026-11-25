package me.ningyu.app.shuttle.notification.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.event.core.EventEnvelope;
import me.ningyu.app.shuttle.notification.ChannelType;
import me.ningyu.app.shuttle.notification.NotificationStatus;
import me.ningyu.app.shuttle.notification.TriggerMode;
import me.ningyu.app.shuttle.notification.domain.Notification;
import me.ningyu.app.shuttle.notification.domain.NotificationInbox;
import me.ningyu.app.shuttle.notification.domain.NotificationInboxRepository;
import me.ningyu.app.shuttle.notification.domain.NotificationRecipient;
import me.ningyu.app.shuttle.notification.domain.NotificationRecipientRepository;
import me.ningyu.app.shuttle.notification.domain.NotificationRepository;
import me.ningyu.app.shuttle.notification.recipient.Recipient;
import me.ningyu.app.shuttle.notification.recipient.RecipientContext;
import me.ningyu.app.shuttle.notification.recipient.RecipientResolver;
import me.ningyu.app.shuttle.notification.recipient.RecipientResolverRegistry;
import me.ningyu.app.shuttle.notification.rule.NotificationRule;
import me.ningyu.app.shuttle.notification.rule.NotificationRuleChannel;
import me.ningyu.app.shuttle.notification.rule.NotificationRuleChannelRepository;
import me.ningyu.app.shuttle.notification.rule.NotificationRuleRecipient;
import me.ningyu.app.shuttle.notification.rule.NotificationRuleRecipientRepository;
import me.ningyu.app.shuttle.notification.rule.RuleMatcher;
import me.ningyu.app.shuttle.notification.template.NotificationTemplate;
import me.ningyu.app.shuttle.notification.template.NotificationTemplateRepository;
import me.ningyu.app.shuttle.notification.template.TemplateRenderer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 通知编排服务：规则匹配 → 接收人解析 → 模板渲染 → 生成通知/接收人/站内信。
 *
 * <p>在独立事务中执行（由异步监听器调用），业务事务不受通知失败影响。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService
{
    private final RuleMatcher ruleMatcher;
    private final RecipientResolverRegistry resolverRegistry;
    private final NotificationRuleChannelRepository ruleChannelRepository;
    private final NotificationRuleRecipientRepository ruleRecipientRepository;
    private final NotificationTemplateRepository templateRepository;
    private final TemplateRenderer templateRenderer;
    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository notificationRecipientRepository;
    private final NotificationInboxRepository inboxRepository;
    private final ObjectMapper objectMapper;

    /**
     * 处理一个事件信封：匹配启用规则并逐个生成通知。
     */
    @Transactional
    public void process(EventEnvelope envelope)
    {
        String eventType = envelope.getEventType();
        Map<String, Object> data = toMap(envelope.getData());
        List<NotificationRule> rules = ruleMatcher.match(eventType, data, LocalDateTime.now());
        for (NotificationRule rule : rules)
        {
            processRule(rule, envelope, data);
        }
    }

    private void processRule(NotificationRule rule, EventEnvelope envelope, Map<String, Object> data)
    {
        // 规则执行幂等：(rule_id, event_id) 去重
        if (notificationRepository.existsByRuleIdAndEventId(rule.getId(), envelope.getEventId()))
        {
            log.debug("通知已存在，跳过: rule={}, event={}", rule.getRuleCode(), envelope.getEventId());
            return;
        }

        List<Recipient> recipients = resolveRecipients(rule, envelope, data);

        NotificationTemplate template = rule.getTemplateCode() == null ? null
                : templateRepository.findByTemplateCode(rule.getTemplateCode()).orElse(null);
        String title = template == null ? null : templateRenderer.render(template.getTitle(), data);
        String content = template == null ? null : templateRenderer.render(template.getContent(), data);

        List<ChannelType> channels = ruleChannelRepository.findByRuleIdOrderBySortOrderAsc(rule.getId())
                .stream()
                .map(NotificationRuleChannel::getChannel)
                .toList();
        boolean inboxEnabled = channels.contains(ChannelType.INBOX);

        NotificationStatus status;
        if (rule.getTriggerMode() == TriggerMode.MANUAL)
        {
            status = NotificationStatus.PENDING_CONFIRMATION;
        }
        else
        {
            status = inboxEnabled ? NotificationStatus.COMPLETED : NotificationStatus.PENDING_DELIVERY;
        }

        Notification saved = notificationRepository.save(Notification.builder()
                .notificationId(UUID.randomUUID().toString())
                .ruleId(rule.getId())
                .ruleCode(rule.getRuleCode())
                .ruleName(rule.getRuleName())
                .ruleVersion(rule.getVersion())
                .eventId(envelope.getEventId())
                .eventType(envelope.getEventType())
                .subject(envelope.getSubject())
                .severity(rule.getSeverity())
                .status(status)
                .title(title)
                .content(content)
                .occurredAt(envelope.getOccurredAt())
                .build());

        for (Recipient recipient : recipients)
        {
            notificationRecipientRepository.save(NotificationRecipient.builder()
                    .notification(saved)
                    .recipientId(recipient.recipientId())
                    .recipientType(recipient.recipientType())
                    .recipientName(recipient.recipientName())
                    .contacts(toJson(recipient.contacts()))
                    .build());
        }

        // 站内信：自动规则且渠道含 INBOX 时落库
        if (rule.getTriggerMode() == TriggerMode.AUTO && inboxEnabled)
        {
            for (Recipient recipient : recipients)
            {
                inboxRepository.save(NotificationInbox.builder()
                        .notificationId(saved.getId())
                        .recipientId(recipient.recipientId())
                        .recipientName(recipient.recipientName())
                        .title(title)
                        .content(content)
                        .isRead(false)
                        .build());
            }
        }

        log.info("通知已生成: rule={}, event={}, status={}, recipients={}",
                rule.getRuleCode(), envelope.getEventId(), status, recipients.size());
    }

    private List<Recipient> resolveRecipients(NotificationRule rule, EventEnvelope envelope, Map<String, Object> data)
    {
        List<NotificationRuleRecipient> configs = ruleRecipientRepository.findByRuleIdOrderBySortOrderAsc(rule.getId());
        List<Recipient> result = new ArrayList<>();
        for (NotificationRuleRecipient config : configs)
        {
            RecipientResolver resolver = resolverRegistry.get(config.getResolverType());
            if (resolver == null)
            {
                log.warn("未找到接收人解析器: {}, rule={}", config.getResolverType(), rule.getRuleCode());
                continue;
            }
            RecipientContext context = new RecipientContext(
                    envelope.getEventType(), envelope.getSubject(), data, config.getCustomValue());
            List<Recipient> resolved = resolver.resolve(context);
            if (resolved != null)
            {
                result.addAll(resolved);
            }
        }
        return result;
    }

    private Map<String, Object> toMap(Object data)
    {
        if (data == null)
        {
            return Map.of();
        }
        return objectMapper.convertValue(data, new TypeReference<Map<String, Object>>()
        {
        });
    }

    private String toJson(Object value)
    {
        if (value == null)
        {
            return null;
        }
        try
        {
            return objectMapper.writeValueAsString(value);
        }
        catch (JsonProcessingException e)
        {
            throw new IllegalStateException("序列化失败: " + value, e);
        }
    }
}
