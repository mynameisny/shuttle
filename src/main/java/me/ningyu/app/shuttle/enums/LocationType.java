package me.ningyu.app.shuttle.enums;

import lombok.Getter;

/**
 * GTFS location_type
 * 对应 stops.txt 中的 location_type 字段
 */
@Getter
public enum LocationType
{
    STOP(0, "站点"),
    STATION(1, "车站"),
    ENTRANCE_EXIT(2, "出入口"),
    GENERIC_NODE(3, "通用节点"),
    BOARDING_AREA(4, "候车区域");

    private final int code;
    private final String name;

    LocationType(int code, String name)
    {
        this.code = code;
        this.name = name;
    }

    public static LocationType fromValue(Integer code)
    {
        if (code == null) return STOP;
        for (LocationType v : values())
        {
            if (v.code == code) return v;
        }
        throw new IllegalArgumentException("无效的 location_type 值: " + code);
    }
}
