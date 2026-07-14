package me.ningyu.app.shuttle.enums;

import lombok.Getter;

/**
 * GTFS bikes_allowed
 * 0=无信息, 1=可携带自行车, 2=不可携带自行车
 */
@Getter
public enum BikeAllowance
{
    NO_INFO(0, "无信息"),
    ALLOWED(1, "可携带自行车"),
    NOT_ALLOWED(2, "不可携带自行车");

    private final int code;
    private final String name;

    BikeAllowance(int code, String name)
    {
        this.code = code;
        this.name = name;
    }
}
