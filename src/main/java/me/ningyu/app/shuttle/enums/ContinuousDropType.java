package me.ningyu.app.shuttle.enums;

import lombok.Getter;

/**
 * GTFS continuous_pickup / continuous_drop_off
 * 0=允许中途上下车, 1=不允许, 2=电话预约, 3=司机协商
 */
@Getter
public enum ContinuousDropType
{
    ALLOWED(0, "允许中途上下车"),
    NOT_ALLOWED(1, "不允许"),
    PHONE_AGENCY(2, "需电话预约"),
    COORDINATE_WITH_DRIVER(3, "需与司机协商");

    private final int code;
    private final String name;

    ContinuousDropType(int code, String name)
    {
        this.code = code;
        this.name = name;
    }

    public static ContinuousDropType fromValue(Integer code)
    {
        if (code == null) return NOT_ALLOWED;
        for (ContinuousDropType v : values())
        {
            if (v.code == code) return v;
        }
        throw new IllegalArgumentException("无效的 continuous_pickup/drop_off 值: " + code);
    }
}
