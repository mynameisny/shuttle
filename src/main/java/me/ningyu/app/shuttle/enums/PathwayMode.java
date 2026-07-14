package me.ningyu.app.shuttle.enums;

import lombok.Getter;

/**
 * GTFS pathway_mode
 * 对应 pathways.txt 中的 pathway_mode 字段
 */
@Getter
public enum PathwayMode
{
    WALKWAY(1, "步道"),
    STAIRS(2, "楼梯"),
    MOVING_SIDEWALK(3, "自动步道"),
    ESCALATOR(4, "扶梯"),
    ELEVATOR(5, "电梯"),
    FARE_GATE(6, "检票闸机"),
    EXIT_GATE(7, "出站闸机");

    private final int code;
    private final String name;

    PathwayMode(int code, String name)
    {
        this.code = code;
        this.name = name;
    }
}
