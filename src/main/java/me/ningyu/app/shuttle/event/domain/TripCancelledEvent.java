package me.ningyu.app.shuttle.event.domain;

import me.ningyu.app.shuttle.event.api.DomainEvent;
import me.ningyu.app.shuttle.event.core.EventTypes;

/**
 * 班次取消 / 停运事件。
 */
public final class TripCancelledEvent implements DomainEvent
{
    private final TripCancelledData data;

    public TripCancelledEvent(TripCancelledData data)
    {
        this.data = data;
    }

    @Override
    public String getEventType()
    {
        return EventTypes.TRIP_CANCELLED;
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
        return "trip/" + data.tripId();
    }

    /**
     * 事件业务数据快照（不含个人联系方式等敏感信息）。
     */
    public record TripCancelledData(
            Long tripId,
            String gtfsTripId,
            String tripLabel,
            String routeId,
            String routeShortName,
            Integer directionId)
    {
    }
}
