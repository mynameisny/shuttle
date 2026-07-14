package me.ningyu.app.shuttle.enums;

import lombok.Getter;

/**
 * GTFS exception_type
 * 对应 calendar_dates.txt 中的 exception_type 字段
 */
@Getter
public enum ExceptionType
{
    ADDED(1, "加开"),
    REMOVED(2, "停运");

    private final int code;
    private final String name;

    ExceptionType(int code, String name)
    {
        this.code = code;
        this.name = name;
    }
}
