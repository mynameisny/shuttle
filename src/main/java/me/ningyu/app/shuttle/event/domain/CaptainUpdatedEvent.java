package me.ningyu.app.shuttle.event.domain;

import me.ningyu.app.shuttle.event.api.DomainEvent;
import me.ningyu.app.shuttle.event.change.FieldChange;
import me.ningyu.app.shuttle.event.core.EventTypes;

import java.util.List;

/**
 * 车长信息更新事件（任意业务字段变化均发布，供关注/审计/同步等消费）。
 */
public final class CaptainUpdatedEvent implements DomainEvent
{
    private final CaptainUpdatedData data;

    public CaptainUpdatedEvent(CaptainUpdatedData data)
    {
        this.data = data;
    }

    @Override
    public String getEventType()
    {
        return EventTypes.CAPTAIN_UPDATED;
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
        return "captain/" + data.captainId();
    }

    /**
     * 事件业务数据快照（变更字段已脱敏）。
     */
    public record CaptainUpdatedData(Long captainId, String name, List<FieldChange> changes)
    {
    }
}
