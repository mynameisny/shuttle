package me.ningyu.app.shuttle.enums;

import lombok.Getter;

/**
 * GTFS route_type
 * 对应 routes.txt 中的 route_type 字段
 */
@Getter
public enum RouteType
{
    TRAM(0, "有轨电车"),
    SUBWAY(1, "地铁"),
    RAIL(2, "铁路"),
    BUS(3, "公交"),
    FERRY(4, "轮渡"),
    CABLE_TRAM(5, "缆车"),
    AERIAL_LIFT(6, "索道"),
    FUNICULAR(7, "缆索铁路"),
    TROLLEYBUS(11, "无轨电车"),
    MONORAIL(12, "单轨"),
    SHUTTLE(100, "班车"),
    COMMUTER(200, "通勤车");

    private final int code;
    private final String name;

    RouteType(int code, String name)
    {
        this.code = code;
        this.name = name;
    }
}
