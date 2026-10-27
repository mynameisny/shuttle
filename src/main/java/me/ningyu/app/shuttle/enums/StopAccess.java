package me.ningyu.app.shuttle.enums;

import lombok.Getter;

/**
 * GTFS stop_access
 * 指示站点如何进入。
 * 0 - 不可直接从街道网络进入，需经入口/路径
 * 1 - 可直接从街道网络进入
 */
@Getter
public enum StopAccess
{
    VIA_ENTRANCE(0, "需经车站入口或路径进入"),
    DIRECT_ACCESS(1, "可直接从街道网络进入");

    private final int code;
    private final String name;

    StopAccess(int code, String name)
    {
        this.code = code;
        this.name = name;
    }

    public static StopAccess fromValue(Integer code)
    {
        if (code == null) return null;
        for (StopAccess v : values())
        {
            if (v.code == code) return v;
        }
        throw new IllegalArgumentException("无效的 stop_access 值: " + code);
    }
}
