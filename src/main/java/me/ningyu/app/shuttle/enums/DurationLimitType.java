package me.ningyu.app.shuttle.enums;

import lombok.Getter;

/**
 * GTFS duration_limit_type（Fares V2）
 * 换乘时限的计时锚点
 */
@Getter
public enum DurationLimitType
{
    FROM_DEPARTURE(0, "换乘前区段出发时刻"),
    FROM_ARRIVAL(1, "换乘前区段到达时刻"),
    TO_DEPARTURE(2, "换乘后区段出发时刻"),
    TO_ARRIVAL(3, "换乘后区段到达时刻");

    private final int code;
    private final String name;

    DurationLimitType(int code, String name)
    {
        this.code = code;
        this.name = name;
    }

    public static DurationLimitType fromValue(Integer code)
    {
        if (code == null) return FROM_ARRIVAL;
        for (DurationLimitType v : values())
        {
            if (v.code == code) return v;
        }
        throw new IllegalArgumentException("无效的 duration_limit_type 值: " + code);
    }
}
