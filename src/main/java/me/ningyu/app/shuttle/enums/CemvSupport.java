package me.ningyu.app.shuttle.enums;

import lombok.Getter;

/**
 * GTFS cemv_support
 * 0=无信息, 1=支持, 2=不支持
 */
@Getter
public enum CemvSupport
{
    NO_INFORMATION(0, "无信息"),
    SUPPORTED(1, "支持"),
    NOT_SUPPORTED(2, "不支持");

    private final int code;
    private final String name;

    CemvSupport(int code, String name)
    {
        this.code = code;
        this.name = name;
    }

    public static CemvSupport fromValue(Integer code)
    {
        if (code == null) return NO_INFORMATION;
        for (CemvSupport v : values())
        {
            if (v.code == code) return v;
        }
        throw new IllegalArgumentException("无效的 cemv_support 值: " + code);
    }
}
