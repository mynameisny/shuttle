package me.ningyu.app.shuttle.enums;

/**
 * GPS数据优先级枚举
 * 用于确定不同来源GPS数据的可信度
 */
public enum GPSPriority
{
    /**
     * 司机手动上报（最高优先级）
     */
    DRIVER_MANUAL(3, "司机手动上报"),

    /**
     * 乘客扫码时附带的GPS（中等优先级）
     */
    PASSENGER_SCAN(2, "乘客扫码"),

    /**
     * 车机定时上报（最低优先级）
     */
    VEHICLE_AUTO(1, "车机自动上报");

    private final int priority;
    private final String description;

    GPSPriority(int priority, String description)
    {
        this.priority = priority;
        this.description = description;
    }

    public int getPriority()
    {
        return priority;
    }

    public String getDescription()
    {
        return description;
    }

    /**
     * 比较两个GPS优先级
     * @param other 另一个优先级
     * @return true 如果当前优先级更高
     */
    public boolean isHigherThan(GPSPriority other)
    {
        return this.priority > other.priority;
    }
}
