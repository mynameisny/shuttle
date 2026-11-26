package me.ningyu.app.shuttle.facade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.facade.service.NotificationTemplateService;
import me.ningyu.app.shuttle.notification.template.NotificationTemplate;
import me.ningyu.app.shuttle.notification.template.NotificationTemplateRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "通知模板")
@SecurityRequirement(name = "TONY")
@RestController
@RequestMapping("/api/notification-templates")
@RequiredArgsConstructor
public class NotificationTemplateController
{
    private final NotificationTemplateRepository templateRepository;
    private final NotificationTemplateService templateService;

    @GetMapping
    @Operation(summary = "通知模板列表")
    public ResponseEntity<List<NotificationTemplate>> list()
    {
        return ResponseEntity.ok(templateRepository.findAll());
    }

    @PostMapping
    @Operation(summary = "创建通知模板")
    public ResponseEntity<NotificationTemplate> create(@RequestBody NotificationTemplate template)
    {
        return ResponseEntity.ok(templateService.create(template));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新通知模板")
    public ResponseEntity<NotificationTemplate> update(
            @Parameter(description = "模板主键") @PathVariable Long id,
            @RequestBody NotificationTemplate template)
    {
        return ResponseEntity.ok(templateService.update(id, template));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除通知模板")
    public ResponseEntity<Void> delete(@Parameter(description = "模板主键") @PathVariable Long id)
    {
        templateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
