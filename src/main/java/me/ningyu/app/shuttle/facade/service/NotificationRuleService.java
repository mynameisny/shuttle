package me.ningyu.app.shuttle.facade.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.event.core.EventTypeRegistry;
import me.ningyu.app.shuttle.event.core.EventTypes;
import me.ningyu.app.shuttle.model.notification.NotificationRuleRecipientRequest;
import me.ningyu.app.shuttle.model.notification.NotificationRuleRequest;
import me.ningyu.app.shuttle.notification.ChannelType;
import me.ningyu.app.shuttle.notification.DeliveryPolicy;
import me.ningyu.app.shuttle.notification.Severity;
import me.ningyu.app.shuttle.notification.TriggerMode;
import me.ningyu.app.shuttle.notification.recipient.RecipientResolver;
import me.ningyu.app.shuttle.notification.rule.NotificationRule;
import me.ningyu.app.shuttle.notification.rule.NotificationRuleChannel;
import me.ningyu.app.shuttle.notification.rule.NotificationRuleChannelRepository;
import me.ningyu.app.shuttle.notification.rule.NotificationRuleRecipient;
import me.ningyu.app.shuttle.notification.rule.NotificationRuleRecipientRepository;
import me.ningyu.app.shuttle.notification.rule.NotificationRuleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知规则写服务：创建 / 更新 / 删除 / 启停，以及可视化配置所需的元数据。
 *
 * <p>渠道与接收人为关联表，随规则整体覆盖式提交（先清后插）。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationRuleService
{
    private final NotificationRuleRepository ruleRepository;
    private final NotificationRuleChannelRepository channelRepository;
    private final NotificationRuleRecipientRepository recipientRepository;
    private final EventTypeRegistry eventTypeRegistry;
    private final ObjectMapper objectMapper;
    private final List<RecipientResolver> recipientResolvers;

    /** 事件编码 → 中文名。 */
    private static final Map<String, String> EVENT_NAMES = new LinkedHashMap<>();

    /** 事件编码 → 可用字段元数据。 */
    private static final Map<String, List<FieldMeta>> EVENT_FIELDS = new LinkedHashMap<>();

    /** 接收人解析器编码 → 中文名与是否需要自定义值。 */
    private static final Map<String, ResolverMeta> RESOLVER_META = new LinkedHashMap<>();

    static
    {
        EVENT_NAMES.put(EventTypes.TRIP_CANCELLED, "班次停运");
        EVENT_NAMES.put(EventTypes.DRIVER_STATUS_CHANGED, "司机状态变更");
        EVENT_NAMES.put(EventTypes.DRIVER_UPDATED, "司机信息更新");
        EVENT_NAMES.put(EventTypes.CAPTAIN_UPDATED, "车长信息更新");

        EVENT_FIELDS.put(EventTypes.TRIP_CANCELLED, List.of(
                field("tripId", "班次ID", "LONG"),
                field("gtfsTripId", "GTFS班次ID", "STRING"),
                field("tripLabel", "班次标识", "STRING"),
                field("routeId", "线路ID", "STRING"),
                field("routeShortName", "线路短名", "STRING"),
                field("directionId", "方向(0去/1返)", "INTEGER")));

        EVENT_FIELDS.put(EventTypes.DRIVER_STATUS_CHANGED, List.of(
                field("driverId", "司机ID", "LONG"),
                field("name", "司机姓名", "STRING"),
                field("oldStatus", "原状态", "STRING", List.of("在岗", "休假", "停用")),
                field("newStatus", "新状态", "STRING", List.of("在岗", "休假", "停用"))));

        EVENT_FIELDS.put(EventTypes.DRIVER_UPDATED, List.of(
                field("driverId", "司机ID", "LONG"),
                field("name", "司机姓名", "STRING")));

        EVENT_FIELDS.put(EventTypes.CAPTAIN_UPDATED, List.of(
                field("captainId", "车长ID", "LONG"),
                field("name", "车长姓名", "STRING")));

        RESOLVER_META.put("TRIP_DRIVER", new ResolverMeta("班次司机", false));
        RESOLVER_META.put("TRIP_CONDUCTOR", new ResolverMeta("班次车长", false));
        RESOLVER_META.put("CUSTOM_USERS", new ResolverMeta("指定人员(手机号)", true));
    }

    @Transactional
    public NotificationRule create(NotificationRuleRequest request)
    {
        if (ruleRepository.existsByRuleCode(request.getRuleCode()))
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "规则编码已存在: " + request.getRuleCode());
        }
        validateEventType(request.getEventType());
        validateCondition(request.getConditionExpression());

        NotificationRule rule = ruleRepository.save(toEntity(request, null));
        replaceChildren(rule, request);
        log.info("已创建通知规则: {}", rule.getRuleCode());
        return rule;
    }

    @Transactional
    public NotificationRule update(Long id, NotificationRuleRequest request)
    {
        NotificationRule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "规则不存在: id=" + id));

        ruleRepository.findByRuleCode(request.getRuleCode()).ifPresent(existing -> {
            if (!existing.getId().equals(id))
            {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "规则编码已存在: " + request.getRuleCode());
            }
        });
        validateEventType(request.getEventType());
        validateCondition(request.getConditionExpression());

        toEntity(request, rule);
        ruleRepository.save(rule);
        replaceChildren(rule, request);
        log.info("已更新通知规则: {}", rule.getRuleCode());
        return rule;
    }

    @Transactional
    public void delete(Long id)
    {
        NotificationRule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "规则不存在: id=" + id));
        channelRepository.deleteAll(channelRepository.findByRuleIdOrderBySortOrderAsc(id));
        recipientRepository.deleteAll(recipientRepository.findByRuleIdOrderBySortOrderAsc(id));
        ruleRepository.delete(rule);
        log.info("已删除通知规则: {}", rule.getRuleCode());
    }

    @Transactional
    public NotificationRule toggle(Long id, boolean enabled)
    {
        NotificationRule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "规则不存在: id=" + id));
        rule.setEnabled(enabled);
        return ruleRepository.save(rule);
    }

    /**
     * 可视化配置元数据：事件类型与字段、运算符、枚举、接收人解析器。
     */
    public Map<String, Object> meta()
    {
        Map<String, Object> meta = new LinkedHashMap<>();

        List<Map<String, Object>> eventTypes = new ArrayList<>();
        for (Map.Entry<String, String> e : EVENT_NAMES.entrySet())
        {
            Map<String, Object> et = new LinkedHashMap<>();
            et.put("code", e.getKey());
            et.put("name", e.getValue());
            et.put("fields", EVENT_FIELDS.getOrDefault(e.getKey(), List.of()));
            eventTypes.add(et);
        }
        meta.put("eventTypes", eventTypes);

        meta.put("operators", List.of(
                op("EQ", "等于", "single"),
                op("NE", "不等于", "single"),
                op("GT", "大于", "single"),
                op("LT", "小于", "single"),
                op("GTE", "大于等于", "single"),
                op("LTE", "小于等于", "single"),
                op("IN", "属于(多选)", "multi"),
                op("NOT_IN", "不属于(多选)", "multi"),
                op("CONTAINS", "包含", "single"),
                op("NOT_CONTAINS", "不包含", "single"),
                op("IS_NULL", "为空", "none"),
                op("IS_NOT_NULL", "不为空", "none")));

        meta.put("channels", enumValues(ChannelType.values()));
        meta.put("severities", enumValues(Severity.values()));
        meta.put("triggerModes", enumValues(TriggerMode.values()));
        meta.put("deliveryPolicies", enumValues(DeliveryPolicy.values()));

        List<Map<String, Object>> resolvers = new ArrayList<>();
        for (RecipientResolver resolver : recipientResolvers)
        {
            ResolverMeta rm = RESOLVER_META.getOrDefault(resolver.getType(),
                    new ResolverMeta(resolver.getType(), false));
            Map<String, Object> r = new LinkedHashMap<>();
            r.put("code", resolver.getType());
            r.put("name", rm.name());
            r.put("needCustomValue", rm.needCustomValue());
            resolvers.add(r);
        }
        meta.put("resolvers", resolvers);

        return meta;
    }

    private NotificationRule toEntity(NotificationRuleRequest request, NotificationRule target)
    {
        NotificationRule rule = target == null ? new NotificationRule() : target;
        rule.setRuleCode(request.getRuleCode());
        rule.setRuleName(request.getRuleName());
        rule.setEventType(request.getEventType());
        rule.setTriggerMode(request.getTriggerMode() != null ? request.getTriggerMode() : TriggerMode.AUTO);
        rule.setSeverity(request.getSeverity() != null ? request.getSeverity() : Severity.NORMAL);
        rule.setDeliveryPolicy(request.getDeliveryPolicy());
        rule.setConditionExpression(request.getConditionExpression());
        rule.setEnabled(request.getEnabled() != null ? request.getEnabled() : Boolean.TRUE);
        rule.setPriority(request.getPriority() != null ? request.getPriority() : 0);
        rule.setEffectiveStart(request.getEffectiveStart());
        rule.setEffectiveEnd(request.getEffectiveEnd());
        rule.setTemplateCode(request.getTemplateCode());
        if (target == null)
        {
            rule.setVersion(1);
        }
        return rule;
    }

    private void replaceChildren(NotificationRule rule, NotificationRuleRequest request)
    {
        channelRepository.deleteAll(channelRepository.findByRuleIdOrderBySortOrderAsc(rule.getId()));
        recipientRepository.deleteAll(recipientRepository.findByRuleIdOrderBySortOrderAsc(rule.getId()));

        int order = 0;
        if (request.getChannels() != null)
        {
            for (ChannelType channel : request.getChannels())
            {
                channelRepository.save(NotificationRuleChannel.builder()
                        .rule(rule)
                        .channel(channel)
                        .sortOrder(order++)
                        .build());
            }
        }

        order = 0;
        if (request.getRecipients() != null)
        {
            for (NotificationRuleRecipientRequest rc : request.getRecipients())
            {
                recipientRepository.save(NotificationRuleRecipient.builder()
                        .rule(rule)
                        .resolverType(rc.getResolverType())
                        .customValue(rc.getCustomValue())
                        .sortOrder(order++)
                        .build());
            }
        }
    }

    private void validateEventType(String eventType)
    {
        if (!eventTypeRegistry.isRegistered(eventType))
        {
            throw new IllegalArgumentException("未注册的事件类型: " + eventType);
        }
    }

    private void validateCondition(String conditionExpression)
    {
        if (conditionExpression == null || conditionExpression.isBlank())
        {
            return;
        }
        try
        {
            objectMapper.readTree(conditionExpression);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("条件表达式不是合法的 JSON: " + e.getMessage(), e);
        }
    }

    private static FieldMeta field(String name, String label, String type)
    {
        return new FieldMeta(name, label, type, null);
    }

    private static FieldMeta field(String name, String label, String type, List<String> enumValues)
    {
        return new FieldMeta(name, label, type, enumValues);
    }

    private static Map<String, Object> op(String code, String name, String valueType)
    {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("code", code);
        m.put("name", name);
        m.put("valueType", valueType);
        return m;
    }

    private static List<Map<String, String>> enumValues(Enum<?>[] values)
    {
        List<Map<String, String>> list = new ArrayList<>();
        for (Enum<?> e : values)
        {
            Map<String, String> m = new LinkedHashMap<>();
            m.put("code", e.name());
            String name = e.name();
            try
            {
                name = (String) e.getClass().getMethod("getName").invoke(e);
            }
            catch (Exception ignore)
            {
                // 无 getName 时用枚举名
            }
            m.put("name", name);
            list.add(m);
        }
        return list;
    }

    /** 条件字段元数据。 */
    public record FieldMeta(String name, String label, String type, List<String> enumValues)
    {
    }

    /** 接收人解析器元数据。 */
    public record ResolverMeta(String name, boolean needCustomValue)
    {
    }
}
