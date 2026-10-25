package me.ningyu.app.shuttle.enums;

import lombok.Getter;

/**
 * GTFS wheelchair_boarding / wheelchair_accessible
 * 0=无信息, 1=可无障碍使用, 2=不可无障碍使用
 */
@Getter
public enum WheelchairAccessibility
{
    NO_INFO(0, "无信息"),
    ACCESSIBLE(1, "可无障碍使用"),
    NOT_ACCESSIBLE(2, "不可无障碍使用");

    private final int code;
    private final String name;

    WheelchairAccessibility(int code, String name)
    {
        this.code = code;
        this.name = name;
    }

    public static WheelchairAccessibility fromValue(Integer code)
    {
        if (code == null) return NO_INFO;
        for (WheelchairAccessibility v : values())
        {
            if (v.code == code) return v;
        }
        throw new IllegalArgumentException("无效的 wheelchair_accessible 值: " + code);
    }
}
