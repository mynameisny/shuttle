package me.ningyu.app.shuttle.event.domain;

import me.ningyu.app.shuttle.event.api.DomainEvent;
import me.ningyu.app.shuttle.event.change.FieldChange;
import me.ningyu.app.shuttle.event.core.EventTypes;

import java.util.List;

/**
 * 司机信息更新事件（任意业务字段变化均发布，供关注/审计/同步等消费）。
 *
 * <p>{@code changes} 为变更字段列表，已按字段策略脱敏/排除；为空则不发布。
 */
public final class DriverUpdatedEvent implements DomainEvent
{
    private final DriverUpdatedData data;

    public DriverUpdatedEvent(DriverUpdatedData data)
    {
        this.data = data;
    }

    @Override
    public String getEventType()
    {
        return EventTypes.DRIVER_UPDATED;
    }

    @Override
    public int getEventVersion()
    {
        return 1;
    }

    @Override
    public Object getData()
    {
        return data;
    }

    @Override
    public String getSubject()
    {
        return "driver/" + data.driverId();
    }

    /**
     * 事件业务数据快照（变更字段已脱敏，不含身份证/驾驶证等敏感值）。
     */
    public record DriverUpdatedData(Long driverId, String name, List<FieldChange> changes)
    {
    }
}
