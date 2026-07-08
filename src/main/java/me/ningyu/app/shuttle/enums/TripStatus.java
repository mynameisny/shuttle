package me.ningyu.app.shuttle.enums;

import lombok.Getter;

/**
 * 班次（Trip）状态
 */
@Getter
public enum TripStatus
{
    SCHEDULED("已排班"),
    IN_PROGRESS("运行中"),
    COMPLETED("已完成"),
    CANCELLED("已取消");

    private final String name;

    TripStatus(String name)
    {
        this.name = name;
    }
}
