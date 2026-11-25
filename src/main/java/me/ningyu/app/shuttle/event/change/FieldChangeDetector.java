package me.ningyu.app.shuttle.event.change;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 实体字段变更检测器：反射 diff 新旧实体的全部业务字段，生成变更记录。
 *
 * <p>默认（无策略配置）所有变化字段都纳入事件；策略 {@code EXCLUDE} 跳过、
 * {@code MASK} 脱敏、{@code INCLUDE} 原样。审计字段（id/createdBy/...）始终跳过。
 */
@Component
@RequiredArgsConstructor
public class FieldChangeDetector
{
    private static final Set<String> AUDIT_FIELDS = Set.of(
            "id", "createdBy", "createdDate", "lastModifiedBy", "lastModifiedDate");

    private final FieldPolicyRepository policyRepository;

    /**
     * diff 新旧实体，返回业务字段变更列表（空表示无变化）。
     *
     * @param entityType 实体类型编码（用于查询字段策略，如 DRIVER / CAPTAIN）
     * @param oldEntity  旧实体（set 前）
     * @param newEntity  新实体（请求体）
     */
    public List<FieldChange> diff(String entityType, Object oldEntity, Object newEntity)
    {
        if (oldEntity == null || newEntity == null)
        {
            return List.of();
        }
        Map<String, FieldPolicy> policyMap = policyRepository.findByEntityType(entityType).stream()
                .collect(Collectors.toMap(FieldPolicy::getFieldName, Function.identity()));

        List<FieldChange> changes = new ArrayList<>();
        for (Field field : newEntity.getClass().getDeclaredFields())
        {
            if (shouldSkip(field))
            {
                continue;
            }
            field.setAccessible(true);
            Object oldValue = read(field, oldEntity);
            Object newValue = read(field, newEntity);
            if (Objects.equals(oldValue, newValue))
            {
                continue;
            }
            FieldPolicy policy = policyMap.get(field.getName());
            FieldPolicyType type = policy == null ? FieldPolicyType.INCLUDE : policy.getPolicy();
            if (type == FieldPolicyType.EXCLUDE)
            {
                continue;
            }
            String label = (policy != null && policy.getFieldLabel() != null)
                    ? policy.getFieldLabel() : field.getName();
            changes.add(new FieldChange(
                    field.getName(),
                    label,
                    display(oldValue, type),
                    display(newValue, type)));
        }
        return changes;
    }

    private boolean shouldSkip(Field field)
    {
        int mod = field.getModifiers();
        if (Modifier.isStatic(mod) || Modifier.isTransient(mod))
        {
            return true;
        }
        return AUDIT_FIELDS.contains(field.getName());
    }

    private Object read(Field field, Object target)
    {
        try
        {
            return field.get(target);
        }
        catch (IllegalAccessException e)
        {
            return null;
        }
    }

    private String display(Object value, FieldPolicyType type)
    {
        String text = value == null ? "" : String.valueOf(value);
        return type == FieldPolicyType.MASK ? mask(text) : text;
    }

    /** 通用脱敏：保留前 3 后 4，其余打码（如 138****5678）。 */
    private String mask(String value)
    {
        if (value == null || value.isEmpty())
        {
            return value;
        }
        if (value.length() <= 7)
        {
            return "****";
        }
        return value.substring(0, 3) + "*".repeat(value.length() - 7) + value.substring(value.length() - 4);
    }
}
