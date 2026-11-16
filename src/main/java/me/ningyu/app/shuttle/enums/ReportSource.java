package me.ningyu.app.shuttle.enums;

import lombok.Getter;

/**
 * 发车/到站记录的上报来源（业务扩展枚举，非 GTFS 规范）。
 */
@Getter
public enum ReportSource
{
    MANUAL("手动上报"),
    DEVICE("设备上报");

    private final String name;

    ReportSource(String name)
    {
        this.name = name;
    }
}
