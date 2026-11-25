package me.ningyu.app.shuttle.event.core;

/**
 * 事件编码常量（{@code <domain>.<entity_action>} 稳定字符串）。
 *
 * <p>事件编码一旦对外发布即保持稳定，不与通知级别、渠道策略混在一起。
 */
public final class EventTypes
{
    /** 班次取消 / 停运。 */
    public static final String TRIP_CANCELLED = "trip.cancelled";

    /** 司机状态变化（入职、离职、停用、恢复等）。 */
    public static final String DRIVER_STATUS_CHANGED = "driver.status_changed";

    /** 司机信息更新（任意业务字段变化，供关注/审计/同步消费）。 */
    public static final String DRIVER_UPDATED = "driver.updated";

    /** 车长信息更新（任意业务字段变化）。 */
    public static final String CAPTAIN_UPDATED = "captain.updated";

    private EventTypes()
    {
    }
}
