package me.ningyu.app.shuttle.enums;

import lombok.Getter;

/**
 * GTFS pickup_type / drop_off_type
 * 上下车方式：0=正常, 1=不可, 2=电话预约, 3=司机协商
 */
@Getter
public enum PickupDropOffType
{
    SCHEDULED(0, "正常上下车"),
    NONE(1, "不可上下车"),
    PHONE_AGENCY(2, "需电话预约"),
    COORDINATE_WITH_DRIVER(3, "需与司机协商");

    private final int code;
    private final String name;

    PickupDropOffType(int code, String name)
    {
        this.code = code;
        this.name = name;
    }

    public static PickupDropOffType fromValue(Integer code)
    {
        if (code == null) return SCHEDULED;
        for (PickupDropOffType v : values())
        {
            if (v.code == code) return v;
        }
        throw new IllegalArgumentException("无效的 pickup/drop_off_type 值: " + code);
    }
}
