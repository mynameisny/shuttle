package me.ningyu.app.shuttle.enums;

import lombok.Getter;

/**
 * GTFS fare_media_type（Fares V2）
 * 票价媒介类型
 */
@Getter
public enum FareMediaType
{
    NONE(0, "无媒介"),
    PAPER_TICKET(1, "实体纸质车票"),
    TRANSIT_CARD(2, "实体交通卡"),
    CEMV(3, "非接触式EMV卡"),
    MOBILE_APP(4, "移动应用");

    private final int code;
    private final String name;

    FareMediaType(int code, String name)
    {
        this.code = code;
        this.name = name;
    }

    public static FareMediaType fromValue(Integer code)
    {
        if (code == null) return NONE;
        for (FareMediaType v : values())
        {
            if (v.code == code) return v;
        }
        throw new IllegalArgumentException("无效的 fare_media_type 值: " + code);
    }
}
