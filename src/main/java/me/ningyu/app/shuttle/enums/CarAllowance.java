package me.ningyu.app.shuttle.enums;

import lombok.Getter;

/**
 * GTFS cars_allowed
 * 0=无信息, 1=可搭载汽车, 2=禁止搭载汽车
 */
@Getter
public enum CarAllowance
{
    NO_INFO(0, "无信息"),
    ALLOWED(1, "可搭载汽车"),
    NOT_ALLOWED(2, "禁止搭载汽车");

    private final int code;
    private final String name;

    CarAllowance(int code, String name)
    {
        this.code = code;
        this.name = name;
    }

    public static CarAllowance fromValue(Integer code)
    {
        if (code == null) return NO_INFO;
        for (CarAllowance v : values())
        {
            if (v.code == code) return v;
        }
        throw new IllegalArgumentException("无效的 cars_allowed 值: " + code);
    }
}
