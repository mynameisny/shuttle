package me.ningyu.app.shuttle.enums;

import lombok.Getter;

/**
 * 线路方向（Route）状态
 */
@Getter
public enum RouteStatus
{
    ACTIVE("启用"),
    INACTIVE("停用");

    private final String name;

    RouteStatus(String name)
    {
        this.name = name;
    }
}
