package me.ningyu.app.shuttle.event.core;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 事件类型注册表（第一阶段为代码级注册，先不落库）。
 *
 * <p>发布事件前校验事件编码是否已注册，保证事件编码受控、稳定；
 * 后续引入可视化配置界面时，再评估是否需要 {@code sys_event_type} 表承载 Schema 与字段元数据。
 */
@Component
public class EventTypeRegistry
{
    private final Set<String> registeredTypes = new LinkedHashSet<>();

    public EventTypeRegistry()
    {
        register(EventTypes.TRIP_CANCELLED);
        register(EventTypes.DRIVER_STATUS_CHANGED);
    }

    public void register(String eventType)
    {
        if (eventType == null || eventType.isBlank())
        {
            throw new IllegalArgumentException("事件编码不能为空");
        }
        registeredTypes.add(eventType.trim());
    }

    public boolean isRegistered(String eventType)
    {
        return eventType != null && registeredTypes.contains(eventType.trim());
    }

    public Set<String> all()
    {
        return Collections.unmodifiableSet(registeredTypes);
    }
}
