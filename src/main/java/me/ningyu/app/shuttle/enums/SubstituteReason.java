package me.ningyu.app.shuttle.enums;

import lombok.Getter;

/**
 * 班次代班原因（业务扩展枚举，非 GTFS 规范）。
 */
@Getter
public enum SubstituteReason
{
    LEAVE("请假"),
    BUSY("有事"),
    OTHER("其他");

    private final String name;

    SubstituteReason(String name)
    {
        this.name = name;
    }
}
