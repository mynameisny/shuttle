package me.ningyu.app.shuttle.event.domain;

import me.ningyu.app.shuttle.event.api.DomainEvent;
import me.ningyu.app.shuttle.event.core.EventTypes;

/**
 * 司机状态变化事件（入职、离职、停用、恢复等）。
 */
public final class DriverStatusChangedEvent implements DomainEvent
{
    private final DriverStatusChangedData data;

    public DriverStatusChangedEvent(DriverStatusChangedData data)
    {
        this.data = data;
    }

    @Override
    public String getEventType()
    {
        return EventTypes.DRIVER_STATUS_CHANGED;
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
     * 事件业务数据快照（不含电话号码等个人联系方式）。
     */
    public record DriverStatusChangedData(
            Long driverId,
            String name,
            String oldStatus,
            String newStatus)
    {
    }
}
