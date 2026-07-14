package me.ningyu.app.shuttle.enums;

import lombok.Getter;

/**
 * GTFS timepoint
 * 对应 stop_times.txt 中的 timepoint 字段
 */
@Getter
public enum Timepoint
{
    APPROXIMATE(0, "近似时间"),
    EXACT(1, "精确时间");

    private final int code;
    private final String name;

    Timepoint(int code, String name)
    {
        this.code = code;
        this.name = name;
    }
}
