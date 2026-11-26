package me.ningyu.app.shuttle.facade.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.notification.rule.NotificationRuleRepository;
import me.ningyu.app.shuttle.notification.template.NotificationTemplate;
import me.ningyu.app.shuttle.notification.template.NotificationTemplateRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * 通知模板写服务：创建 / 更新 / 删除。
 *
 * <p>删除时校验是否存在规则引用（规则以 templateCode 引用模板），有引用则拒绝，
 * 避免生成通知时因模板缺失导致标题/正文为空。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationTemplateService
{
    private final NotificationTemplateRepository templateRepository;
    private final NotificationRuleRepository ruleRepository;

    @Transactional
    public NotificationTemplate create(NotificationTemplate template)
    {
        validate(template, null);
        template.setId(null);
        NotificationTemplate saved = templateRepository.save(template);
        log.info("已创建通知模板: {}", saved.getTemplateCode());
        return saved;
    }

    @Transactional
    public NotificationTemplate update(Long id, NotificationTemplate request)
    {
        NotificationTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "模板不存在: id=" + id));
        validate(request, id);
        template.setTemplateCode(request.getTemplateCode());
        template.setName(request.getName());
        template.setTitle(request.getTitle());
        template.setContent(request.getContent());
        template.setChannel(request.getChannel());
        log.info("已更新通知模板: {}", template.getTemplateCode());
        return templateRepository.save(template);
    }

    @Transactional
    public void delete(Long id)
    {
        NotificationTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "模板不存在: id=" + id));
        if (ruleRepository.existsByTemplateCode(template.getTemplateCode()))
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "模板仍被通知规则引用，无法删除: " + template.getTemplateCode());
        }
        templateRepository.delete(template);
        log.info("已删除通知模板: {}", template.getTemplateCode());
    }

    private void validate(NotificationTemplate template, Long selfId)
    {
        String code = template.getTemplateCode();
        if (code == null || code.isBlank())
        {
            throw new IllegalArgumentException("模板编码不能为空");
        }
        if (template.getName() == null || template.getName().isBlank())
        {
            throw new IllegalArgumentException("模板名称不能为空");
        }
        if (template.getContent() == null || template.getContent().isBlank())
        {
            throw new IllegalArgumentException("模板正文不能为空");
        }
        templateRepository.findByTemplateCode(code).ifPresent(existing -> {
            if (selfId == null || !existing.getId().equals(selfId))
            {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "模板编码已存在: " + code);
            }
        });
    }
}
