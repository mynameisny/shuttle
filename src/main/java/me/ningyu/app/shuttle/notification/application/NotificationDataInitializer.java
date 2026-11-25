package me.ningyu.app.shuttle.notification.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.notification.ChannelType;
import me.ningyu.app.shuttle.notification.DeliveryPolicy;
import me.ningyu.app.shuttle.notification.Severity;
import me.ningyu.app.shuttle.notification.TriggerMode;
import me.ningyu.app.shuttle.notification.rule.NotificationRule;
import me.ningyu.app.shuttle.notification.rule.NotificationRuleChannel;
import me.ningyu.app.shuttle.notification.rule.NotificationRuleChannelRepository;
import me.ningyu.app.shuttle.notification.rule.NotificationRuleRecipient;
import me.ningyu.app.shuttle.notification.rule.NotificationRuleRecipientRepository;
import me.ningyu.app.shuttle.notification.rule.NotificationRuleRepository;
import me.ningyu.app.shuttle.notification.template.NotificationTemplate;
import me.ningyu.app.shuttle.notification.template.NotificationTemplateRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 通知中心种子数据：启动时初始化示例规则与模板（幂等，已存在则跳过）。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationDataInitializer implements ApplicationRunner
{
    private final NotificationRuleRepository ruleRepository;
    private final NotificationRuleChannelRepository channelRepository;
    private final NotificationRuleRecipientRepository recipientRepository;
    private final NotificationTemplateRepository templateRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args)
    {
        initTemplate("trip_cancelled_tpl", "班次停运通知模板",
                "【班车停运通知】",
                "线路 {{routeShortName}} 的班次 {{tripLabel}} 已取消/停运，请合理安排出行。");

        initTemplate("driver_status_tpl", "司机状态变更通知模板",
                "【司机状态变更】",
                "司机 {{name}} 状态由「{{oldStatus}}」变更为「{{newStatus}}」。");

        // 班次停运：紧急，自动，通知班次司机 + 车长，渠道站内信 + 短信
        initRule("trip.cancelled", "TRIP_CANCELLED_RULE", "班次停运紧急通知",
                Severity.CRITICAL, TriggerMode.AUTO, DeliveryPolicy.ALL,
                List.of(ChannelType.INBOX, ChannelType.SMS),
                List.of(new RecipientConfig("TRIP_DRIVER", null), new RecipientConfig("TRIP_CONDUCTOR", null)),
                "trip_cancelled_tpl", null);

        // 司机状态变更：普通，自动，通知指定管理员（手机号可改），渠道站内信
        initRule("driver.status_changed", "DRIVER_STATUS_RULE", "司机状态变更通知",
                Severity.NORMAL, TriggerMode.AUTO, DeliveryPolicy.ALL,
                List.of(ChannelType.INBOX),
                List.of(new RecipientConfig("CUSTOM_USERS", "13800000000")),
                "driver_status_tpl", null);
    }

    private void initTemplate(String code, String name, String title, String content)
    {
        if (templateRepository.findByTemplateCode(code).isPresent())
        {
            return;
        }
        templateRepository.save(NotificationTemplate.builder()
                .templateCode(code)
                .name(name)
                .title(title)
                .content(content)
                .build());
        log.info("已初始化通知模板: {}", code);
    }

    private void initRule(String eventType, String ruleCode, String ruleName,
                          Severity severity, TriggerMode triggerMode, DeliveryPolicy policy,
                          List<ChannelType> channels, List<RecipientConfig> recipients,
                          String templateCode, String conditionExpression)
    {
        if (ruleRepository.existsByRuleCode(ruleCode))
        {
            return;
        }
        NotificationRule rule = ruleRepository.save(NotificationRule.builder()
                .ruleCode(ruleCode)
                .ruleName(ruleName)
                .eventType(eventType)
                .triggerMode(triggerMode)
                .severity(severity)
                .deliveryPolicy(policy)
                .conditionExpression(conditionExpression)
                .enabled(true)
                .priority(0)
                .version(1)
                .templateCode(templateCode)
                .build());

        int order = 0;
        for (ChannelType channel : channels)
        {
            channelRepository.save(NotificationRuleChannel.builder()
                    .rule(rule)
                    .channel(channel)
                    .sortOrder(order++)
                    .build());
        }

        order = 0;
        for (RecipientConfig rc : recipients)
        {
            recipientRepository.save(NotificationRuleRecipient.builder()
                    .rule(rule)
                    .resolverType(rc.resolverType())
                    .customValue(rc.customValue())
                    .sortOrder(order++)
                    .build());
        }
        log.info("已初始化通知规则: {}", ruleCode);
    }

    private record RecipientConfig(String resolverType, String customValue)
    {
    }
}
