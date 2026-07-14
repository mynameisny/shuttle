package me.ningyu.app.shuttle.enums;

import lombok.Getter;

/**
 * GTFS exact_times
 * 对应 frequencies.txt 中的 exact_times 字段
 */
@Getter
public enum ExactTimes
{
    FREQUENCY_BASED(0, "基于频率"),
    SCHEDULE_BASED(1, "基于时刻表");

    private final int code;
    private final String name;

    ExactTimes(int code, String name)
    {
        this.code = code;
        this.name = name;
    }
}
