package me.ningyu.app.shuttle.facade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.notification.application.NotificationQueryService;
import me.ningyu.app.shuttle.notification.domain.Notification;
import me.ningyu.app.shuttle.notification.domain.NotificationInbox;
import me.ningyu.app.shuttle.notification.domain.NotificationRecipient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "通知中心")
@SecurityRequirement(name = "TONY")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController
{
    private final NotificationQueryService queryService;

    @GetMapping
    @Operation(summary = "通知列表")
    public ResponseEntity<List<Notification>> list()
    {
        return ResponseEntity.ok(queryService.listNotifications());
    }

    @GetMapping("/{id}")
    @Operation(summary = "通知详情")
    public ResponseEntity<Notification> detail(@Parameter(description = "通知主键") @PathVariable Long id)
    {
        return ResponseEntity.ok(queryService.getNotification(id));
    }

    @GetMapping("/{id}/recipients")
    @Operation(summary = "通知接收人列表")
    public ResponseEntity<List<NotificationRecipient>> recipients(@Parameter(description = "通知主键") @PathVariable Long id)
    {
        return ResponseEntity.ok(queryService.listRecipients(id));
    }

    @GetMapping("/inbox")
    @Operation(summary = "站内信列表", description = "按接收人标识查询站内信")
    public ResponseEntity<List<NotificationInbox>> inbox(@Parameter(description = "接收人标识（手机号）") @RequestParam String recipientId)
    {
        return ResponseEntity.ok(queryService.listInbox(recipientId));
    }

    @GetMapping("/inbox/unread-count")
    @Operation(summary = "站内信未读数")
    public ResponseEntity<Long> unreadCount(@Parameter(description = "接收人标识（手机号）") @RequestParam String recipientId)
    {
        return ResponseEntity.ok(queryService.unreadCount(recipientId));
    }

    @PutMapping("/inbox/{id}/read")
    @Operation(summary = "标记站内信已读")
    public ResponseEntity<NotificationInbox> markRead(@Parameter(description = "站内信主键") @PathVariable Long id)
    {
        return ResponseEntity.ok(queryService.markRead(id));
    }
}
