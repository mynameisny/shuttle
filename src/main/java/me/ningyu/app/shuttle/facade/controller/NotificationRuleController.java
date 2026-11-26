package me.ningyu.app.shuttle.facade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.facade.service.NotificationRuleService;
import me.ningyu.app.shuttle.model.notification.NotificationRuleRequest;
import me.ningyu.app.shuttle.notification.rule.NotificationRule;
import me.ningyu.app.shuttle.notification.rule.NotificationRuleChannel;
import me.ningyu.app.shuttle.notification.rule.NotificationRuleChannelRepository;
import me.ningyu.app.shuttle.notification.rule.NotificationRuleRecipient;
import me.ningyu.app.shuttle.notification.rule.NotificationRuleRecipientRepository;
import me.ningyu.app.shuttle.notification.rule.NotificationRuleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
    private final NotificationRuleService ruleService;

    @GetMapping("/notification-rules")
    @Operation(summary = "通知规则列表")
    public ResponseEntity<List<NotificationRule>> list()
    {
        return ResponseEntity.ok(ruleRepository.findAll());
    }

    @GetMapping("/notification-rules/meta")
    @Operation(summary = "规则配置元数据", description = "事件类型与字段、运算符、渠道/级别/触发模式/发送策略枚举、接收人解析器")
    public ResponseEntity<Map<String, Object>> meta()
    {
        return ResponseEntity.ok(ruleService.meta());
    }

    @PostMapping("/notification-rules")
    @Operation(summary = "创建通知规则")
    public ResponseEntity<NotificationRule> create(@Valid @RequestBody NotificationRuleRequest request)
    {
        return ResponseEntity.ok(ruleService.create(request));
    }

    @PutMapping("/notification-rules/{id}")
    @Operation(summary = "更新通知规则")
    public ResponseEntity<NotificationRule> update(
            @Parameter(description = "规则主键") @PathVariable Long id,
            @Valid @RequestBody NotificationRuleRequest request)
    {
        return ResponseEntity.ok(ruleService.update(id, request));
    }

    @PutMapping("/notification-rules/{id}/enabled")
    @Operation(summary = "启停通知规则")
    public ResponseEntity<NotificationRule> toggle(
            @Parameter(description = "规则主键") @PathVariable Long id,
            @Parameter(description = "是否启用") @RequestParam boolean enabled)
    {
        return ResponseEntity.ok(ruleService.toggle(id, enabled));
    }

    @DeleteMapping("/notification-rules/{id}")
    @Operation(summary = "删除通知规则")
    public ResponseEntity<Void> delete(@Parameter(description = "规则主键") @PathVariable Long id)
    {
        ruleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/notification-rules/{id}")
    @Operation(summary = "通知规则详情")
    public ResponseEntity<NotificationRule> detail(@Parameter(description = "规则主键") @PathVariable Long id)
    {
        return ResponseEntity.ok(ruleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "规则不存在: id=" + id)));
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
}
