package me.ningyu.app.shuttle.facade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.notification.rule.NotificationRule;
import me.ningyu.app.shuttle.notification.rule.NotificationRuleChannel;
import me.ningyu.app.shuttle.notification.rule.NotificationRuleChannelRepository;
import me.ningyu.app.shuttle.notification.rule.NotificationRuleRecipient;
import me.ningyu.app.shuttle.notification.rule.NotificationRuleRecipientRepository;
import me.ningyu.app.shuttle.notification.rule.NotificationRuleRepository;
import me.ningyu.app.shuttle.notification.template.NotificationTemplate;
import me.ningyu.app.shuttle.notification.template.NotificationTemplateRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "通知规则")
@SecurityRequirement(name = "TONY")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationRuleController
{
    private final NotificationRuleRepository ruleRepository;
    private final NotificationRuleChannelRepository channelRepository;
    private final NotificationRuleRecipientRepository recipientRepository;
    private final NotificationTemplateRepository templateRepository;

    @GetMapping("/notification-rules")
    @Operation(summary = "通知规则列表")
    public ResponseEntity<List<NotificationRule>> list()
    {
        return ResponseEntity.ok(ruleRepository.findAll());
    }

    @GetMapping("/notification-rules/{id}")
    @Operation(summary = "通知规则详情")
    public ResponseEntity<NotificationRule> detail(@Parameter(description = "规则主键") @PathVariable Long id)
    {
        return ResponseEntity.ok(ruleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("规则不存在: id=" + id)));
    }

    @GetMapping("/notification-rules/{id}/channels")
    @Operation(summary = "规则渠道列表")
    public ResponseEntity<List<NotificationRuleChannel>> channels(@PathVariable Long id)
    {
        return ResponseEntity.ok(channelRepository.findByRuleIdOrderBySortOrderAsc(id));
    }

    @GetMapping("/notification-rules/{id}/recipients")
    @Operation(summary = "规则接收人配置列表")
    public ResponseEntity<List<NotificationRuleRecipient>> recipients(@PathVariable Long id)
    {
        return ResponseEntity.ok(recipientRepository.findByRuleIdOrderBySortOrderAsc(id));
    }

    @GetMapping("/notification-templates")
    @Operation(summary = "通知模板列表")
    public ResponseEntity<List<NotificationTemplate>> templates()
    {
        return ResponseEntity.ok(templateRepository.findAll());
    }
}
