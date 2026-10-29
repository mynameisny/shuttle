package me.ningyu.app.shuttle.enums;

import lombok.Getter;

/**
 * GTFS booking_type
 * 预订方式
 */
@Getter
public enum BookingType
{
    REALTIME(0, "实时预订"),
    SAME_DAY(1, "当日预订"),
    ADVANCE(2, "提前预约");

    private final int code;
    private final String name;

    BookingType(int code, String name)
    {
        this.code = code;
        this.name = name;
    }

    public static BookingType fromValue(Integer code)
    {
        if (code == null) return REALTIME;
        for (BookingType v : values())
        {
            if (v.code == code) return v;
        }
        throw new IllegalArgumentException("无效的 booking_type 值: " + code);
    }
}
